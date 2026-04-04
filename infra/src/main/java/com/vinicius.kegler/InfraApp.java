package com.vinicius.kegler;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class InfraApp {
    public static void main(final String[] args) {
        App app = new App();

        StackProps env = StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                .build();

        InfraStack infra = new InfraStack(app, "InfraStack", env);
        new AppStack(app, "AppStack", infra, env);

        app.synth();
    }
}
