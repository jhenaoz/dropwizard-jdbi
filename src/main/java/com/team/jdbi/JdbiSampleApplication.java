package com.team.jdbi;

import com.team.jdbi.resources.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

public class JdbiSampleApplication extends Application<JdbiSampleConfiguration> {

    public static void main(final String[] args) throws Exception {
        new JdbiSampleApplication().run(args);
    }

    @Override
    public String getName() {
        return "JdbiSampleApp";
    }

    @Override
    public void initialize(final Bootstrap<JdbiSampleConfiguration> bootstrap) {
    }

    @Override
    public void run(final JdbiSampleConfiguration configuration,
                    final Environment environment) {

        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");
        // environment.jersey().register(new UserResource(jdbi));

        final HelloWorldResource helloWorldResource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );

        environment.jersey().register(helloWorldResource);
    }

}
