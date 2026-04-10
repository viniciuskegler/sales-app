package com.vinicius.kegler;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apprunner.CfnService;
import software.amazon.awscdk.services.events.Rule;
import software.amazon.awscdk.services.events.Schedule;
import software.amazon.awscdk.services.events.targets.LambdaFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

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
                                                kvp("SPRING_PROFILES_ACTIVE", "prod"),
                                                kvp("DB_HOST", infra.db.getDbInstanceEndpointAddress()),
                                                kvp("DB_NAME", "salesapp"),
                                                kvp("DB_USER", "salesapp"),
                                                kvp("REDIS_HOST", infra.cache.getAttrRedisEndpointAddress()),
                                                kvp("JWT_EXPIRATION", "36000"),
                                                kvp("SQS_QUEUE_URL", infra.paymentQueue.getQueueUrl())
                                        ))
                                        .runtimeEnvironmentSecrets(List.of(
                                                kvp("DB_PASSWORD", infra.dbPasswordSecret.getSecretArn()),
                                                kvp("JWT_SECRET", infra.jwtSecret.getSecretArn()),
                                                kvp("INTERNAL_API_SECRET", infra.internalApiSecret.getSecretArn())
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

        String backendUrl = "https://" + service.getAttrServiceUrl();

        Function advancerLambda = Function.Builder.create(this, "OrderStatusAdvancer")
                .functionName("salesapp-order-status-advancer")
                .runtime(Runtime.NODEJS_22_X)
                .handler("index.handler")
                .timeout(Duration.seconds(10))
                .code(Code.fromInline("""
                        exports.handler = async () => {
                            const res = await fetch(
                                process.env.BACKEND_URL + '/api/internal/advance-statuses',
                                {
                                    method: 'POST',
                                    headers: { 'X-Internal-Secret': process.env.INTERNAL_API_SECRET }
                                }
                            );
                            if (!res.ok) throw new Error('advance-statuses failed: ' + res.status);
                            console.log('Order statuses advanced successfully');
                        };
                        """))
                .environment(Map.of(
                        "BACKEND_URL", backendUrl,
                        "INTERNAL_API_SECRET", infra.internalApiSecret.secretValue().unsafeUnwrap()
                ))
                .build();

        Rule.Builder.create(this, "OrderStatusAdvancerRule")
                .schedule(Schedule.rate(Duration.minutes(2)))
                .targets(List.of(new LambdaFunction(advancerLambda)))
                .build();

        CfnService frontendService = CfnService.Builder.create(this, "FrontendService")
                .serviceName("salesapp-frontend")
                .sourceConfiguration(CfnService.SourceConfigurationProperty.builder()
                        .authenticationConfiguration(CfnService.AuthenticationConfigurationProperty.builder()
                                .accessRoleArn(infra.ecrAccessRole.getRoleArn())
                                .build())
                        .autoDeploymentsEnabled(true)
                        .imageRepository(CfnService.ImageRepositoryProperty.builder()
                                .imageIdentifier(infra.frontendEcrRepo.getRepositoryUri() + ":latest")
                                .imageRepositoryType("ECR")
                                .imageConfiguration(CfnService.ImageConfigurationProperty.builder()
                                        .port("4000")
                                        .runtimeEnvironmentVariables(List.of(
                                                kvp("BACKEND_URL", backendUrl)
                                        ))
                                        .build())
                                .build())
                        .build())
                .build();

        new CfnOutput(this, "BackendUrl", CfnOutputProps.builder()
                .value(backendUrl)
                .description("Backend URL")
                .build());

        new CfnOutput(this, "FrontendUrl", CfnOutputProps.builder()
                .value("https://" + frontendService.getAttrServiceUrl())
                .description("Frontend URL")
                .build());
    }

    private static CfnService.KeyValuePairProperty kvp(String name, String value) {
        return CfnService.KeyValuePairProperty.builder().name(name).value(value).build();
    }
}
