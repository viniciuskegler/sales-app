package com.vinicius.kegler;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apprunner.CfnService;
import software.constructs.Construct;

import java.util.List;

public class AppStack extends Stack {

    public AppStack(final Construct scope, final String id, final InfraStack infra) {
        this(scope, id, infra, null);
    }

    public AppStack(final Construct scope, final String id, final InfraStack infra, final StackProps props) {
        super(scope, id, props);

        CfnService service = CfnService.Builder.create(this, "AppRunnerService")
                .serviceName("salesapp-backend")
                .sourceConfiguration(CfnService.SourceConfigurationProperty.builder()
                        .authenticationConfiguration(CfnService.AuthenticationConfigurationProperty.builder()
                                .accessRoleArn(infra.ecrAccessRole.getRoleArn())
                                .build())
                        .autoDeploymentsEnabled(true)
                        .imageRepository(CfnService.ImageRepositoryProperty.builder()
                                .imageIdentifier(infra.ecrRepo.getRepositoryUri() + ":latest")
                                .imageRepositoryType("ECR")
                                .imageConfiguration(CfnService.ImageConfigurationProperty.builder()
                                        .port("8080")
                                        .runtimeEnvironmentVariables(List.of(
                                                kvp("DB_HOST", infra.db.getDbInstanceEndpointAddress()),
                                                kvp("DB_NAME", "salesapp"),
                                                kvp("DB_USER", "salesapp"),
                                                kvp("REDIS_HOST", infra.cache.getAttrEndpointAddress()),
                                                kvp("JWT_EXPIRATION", "36000"),
                                                kvp("SQS_QUEUE_URL", infra.paymentQueue.getQueueUrl())
                                        ))
                                        .runtimeEnvironmentSecrets(List.of(
                                                kvp("DB_PASSWORD", infra.dbPasswordSecret.getSecretArn()),
                                                kvp("JWT_SECRET", infra.jwtSecret.getSecretArn())
                                        ))
                                        .build())
                                .build())
                        .build())
                .instanceConfiguration(CfnService.InstanceConfigurationProperty.builder()
                        .instanceRoleArn(infra.instanceRole.getRoleArn())
                        .build())
                .networkConfiguration(CfnService.NetworkConfigurationProperty.builder()
                        .egressConfiguration(CfnService.EgressConfigurationProperty.builder()
                                .egressType("VPC")
                                .vpcConnectorArn(infra.vpcConnector.getAttrVpcConnectorArn())
                                .build())
                        .build())
                .build();

        new CfnOutput(this, "AppUrl", CfnOutputProps.builder()
                .value("https://" + service.getAttrServiceUrl())
                .description("Backend URL")
                .build());
    }

    private static CfnService.KeyValuePairProperty kvp(String name, String value) {
        return CfnService.KeyValuePairProperty.builder().name(name).value(value).build();
    }
}
