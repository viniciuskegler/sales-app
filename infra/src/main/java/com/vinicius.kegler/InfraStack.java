package com.vinicius.kegler;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.elasticache.CfnCacheCluster;
import software.amazon.awscdk.services.elasticache.CfnSubnetGroup;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretStringGenerator;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.apprunner.CfnVpcConnector;
import software.constructs.Construct;

import java.util.List;
import java.util.stream.Collectors;

public class InfraStack extends Stack {

    // Exposed for AppStack
    final Repository ecrRepo;
    final Repository frontendEcrRepo;
    final DatabaseInstance db;
    final CfnCacheCluster cache;
    final Queue paymentQueue;
    final Secret dbPasswordSecret;
    final Secret jwtSecret;
    final Secret internalApiSecret;
    final Role instanceRole;
    final Role ecrAccessRole;
    final CfnVpcConnector vpcConnector;
    final List<String> subnetIds;

    public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Vpc vpc = Vpc.Builder.create(this, "Vpc")
                .maxAzs(2)
                .natGateways(0)
                .subnetConfiguration(List.of(
                        SubnetConfiguration.builder()
                                .name("public")
                                .subnetType(SubnetType.PUBLIC)
                                .cidrMask(24)
                                .build()
                ))
                .build();

        SecurityGroup appRunnerSg = SecurityGroup.Builder.create(this, "AppRunnerSg")
                .vpc(vpc)
                .description("App Runner VPC connector egress")
                .build();

        SecurityGroup rdsSg = SecurityGroup.Builder.create(this, "RdsSg")
                .vpc(vpc)
                .build();
        rdsSg.addIngressRule(appRunnerSg, Port.tcp(5432));

        SecurityGroup cacheSg = SecurityGroup.Builder.create(this, "CacheSg")
                .vpc(vpc)
                .build();
        cacheSg.addIngressRule(appRunnerSg, Port.tcp(6379));

        dbPasswordSecret = Secret.Builder.create(this, "DbPasswordSecret")
                .secretName("salesapp/db-password")
                .generateSecretString(SecretStringGenerator.builder()
                        .excludePunctuation(true)
                        .passwordLength(32)
                        .build())
                .build();

        jwtSecret = Secret.Builder.create(this, "JwtSecret")
                .secretName("salesapp/jwt-secret")
                .generateSecretString(SecretStringGenerator.builder()
                        .passwordLength(64)
                        .build())
                .build();

        internalApiSecret = Secret.Builder.create(this, "InternalApiSecret")
                .secretName("salesapp/internal-api-secret")
                .generateSecretString(SecretStringGenerator.builder()
                        .excludePunctuation(true)
                        .passwordLength(32)
                        .build())
                .build();

        db = DatabaseInstance.Builder.create(this, "Database")
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                        .version(PostgresEngineVersion.VER_16)
                        .build()))
                .instanceType(InstanceType.of(InstanceClass.T4G, InstanceSize.MICRO))
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder()
                        .subnetType(SubnetType.PUBLIC)
                        .build())
                .securityGroups(List.of(rdsSg))
                .databaseName("salesapp")
                .credentials(Credentials.fromPassword("salesapp", dbPasswordSecret.getSecretValue()))
                .multiAz(false)
                .publiclyAccessible(false)
                .deletionProtection(false)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        subnetIds = vpc.getPublicSubnets().stream()
                .map(ISubnet::getSubnetId)
                .collect(Collectors.toList());

        CfnSubnetGroup cacheSubnetGroup = CfnSubnetGroup.Builder.create(this, "CacheSubnetGroup")
                .description("Subnet group for salesapp cache")
                .subnetIds(subnetIds)
                .build();

        cache = CfnCacheCluster.Builder.create(this, "Cache")
                .engine("redis")
                .cacheNodeType("cache.t4g.micro")
                .numCacheNodes(1)
                .cacheSubnetGroupName(cacheSubnetGroup.getRef())
                .vpcSecurityGroupIds(List.of(cacheSg.getSecurityGroupId()))
                .build();

        paymentQueue = Queue.Builder.create(this, "PaymentQueue")
                .queueName("salesapp-payment-events")
                .visibilityTimeout(Duration.seconds(30))
                .build();

        ecrRepo = Repository.Builder.create(this, "BackendRepo")
                .repositoryName("salesapp-backend")
                .removalPolicy(RemovalPolicy.DESTROY)
                .emptyOnDelete(true)
                .build();

        frontendEcrRepo = Repository.Builder.create(this, "FrontendRepo")
                .repositoryName("salesapp-frontend")
                .removalPolicy(RemovalPolicy.DESTROY)
                .emptyOnDelete(true)
                .build();

        instanceRole = Role.Builder.create(this, "AppRunnerInstanceRole")
                .assumedBy(new ServicePrincipal("tasks.apprunner.amazonaws.com"))
                .build();
        dbPasswordSecret.grantRead(instanceRole);
        jwtSecret.grantRead(instanceRole);
        internalApiSecret.grantRead(instanceRole);
        paymentQueue.grantConsumeMessages(instanceRole);
        paymentQueue.grantSendMessages(instanceRole);

        ecrAccessRole = Role.Builder.create(this, "AppRunnerEcrRole")
                .assumedBy(new ServicePrincipal("build.apprunner.amazonaws.com"))
                .build();
        ecrRepo.grantPull(ecrAccessRole);
        frontendEcrRepo.grantPull(ecrAccessRole);

        vpcConnector = CfnVpcConnector.Builder.create(this, "VpcConnector")
                .vpcConnectorName("salesapp-connector")
                .subnets(subnetIds)
                .securityGroups(List.of(appRunnerSg.getSecurityGroupId()))
                .build();

        new CfnOutput(this, "BackendEcrUri", CfnOutputProps.builder()
                .value(ecrRepo.getRepositoryUri())
                .description("Push backend Docker image here, then run: cdk deploy AppStack")
                .build());

        new CfnOutput(this, "FrontendEcrUri", CfnOutputProps.builder()
                .value(frontendEcrRepo.getRepositoryUri())
                .description("Push frontend Docker image here, then run: cdk deploy AppStack")
                .build());

        new CfnOutput(this, "PaymentQueueUrl", CfnOutputProps.builder()
                .value(paymentQueue.getQueueUrl())
                .build());
    }
}
