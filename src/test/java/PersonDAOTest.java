import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jdbi3.strategies.TimedAnnotationNameStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.team.jdbi.db.PersonDAO;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class PersonDAOTest {


    private Environment environment;
    private MetricRegistry metricRegistry = new MetricRegistry();
    private Jdbi dbi;
    private PersonDAO personDAO;


    // 1. Need to set up DataSource
    // 2. Set up schema, posible incompatibilities with auto increment and db coupled functions.
    @Before
    public void setUp() throws Exception {
        environment = new Environment("test", new ObjectMapper(), Validators.newValidator(),
                metricRegistry, ClassLoader.getSystemClassLoader());

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setUrl("jdbc:h2:mem:jdbi3-test");
        dataSourceFactory.setUser("sa");
        dataSourceFactory.setDriverClass("org.h2.Driver");
        dataSourceFactory.asSingleConnectionPool();

        dbi = new JdbiFactory(new TimedAnnotationNameStrategy()).build(environment, dataSourceFactory, "h2");
        dbi.useTransaction(handlerConsumer -> {
            handlerConsumer.createScript(Resources.toString(Resources.getResource("schema.sql"), Charsets.UTF_8)).execute();
            handlerConsumer.createScript(Resources.toString(Resources.getResource("data.sql"), Charsets.UTF_8)).execute();
        });
        personDAO = dbi.onDemand(PersonDAO.class);

        // RESEARCH IF THIS IS REQUIRED...
        for (LifeCycle lifeCycle : environment.lifecycle().getManagedObjects()) {
            lifeCycle.start();
        }
    }

    @After
    public void tearDown() throws Exception {
        for (LifeCycle lifeCycle : environment.lifecycle().getManagedObjects()) {
            lifeCycle.stop();
        }
    }

    @Test
    public void canAcceptOptionalParams() {
        assertThat(personDAO.findNameById(1)).contains("Juan");
    }

    @Test
    public void deletePerson() {
        personDAO.deleteById(1);
        assertThat(personDAO.findNameById(1)).isNull();
    }
}
