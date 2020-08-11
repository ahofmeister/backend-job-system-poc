package company.jobs;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;
import javax.sql.DataSource;
import javax.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.postgres.PostgresStorageProvider;
import org.jobrunr.utils.mapper.JsonMapper;
import org.jobrunr.utils.mapper.jsonb.JsonbJsonMapper;
import org.postgresql.ds.PGSimpleDataSource;

/**
 * @author Alexander Hofmeister
 */

public class JobRunrProvider {

  @ConfigProperty(name = "quarkus.datasource.url")
  String url;

  @ConfigProperty(name = "quarkus.datasource.username")
  String username;

  @ConfigProperty(name = "quarkus.datasource.password")
  String password;

  @Produces
  @Singleton
  public BackgroundJobServer backgroundJobServer(StorageProvider storageProvider,
      JobActivator jobActivator) {
    return new BackgroundJobServer(storageProvider, jobActivator);
  }

  @Produces
  @Singleton
  public JobActivator jobActivator() {
    return new JobActivator() {
      @Override
      public <T> T activateJob(Class<T> aClass) {
        return CDI.current().select(aClass).get();
      }
    };
  }

  @Produces
  @Singleton
  public JobScheduler jobScheduler(StorageProvider storageProvider) {
    final JobScheduler jobScheduler = new JobScheduler(storageProvider);
    BackgroundJob.setJobScheduler(jobScheduler);
    return jobScheduler;
  }

  @Produces
  @Singleton
  public StorageProvider storageProvider(@CustomDatasource DataSource dataSource,
      JobMapper jobMapper) {
    PostgresStorageProvider storageProvider = new PostgresStorageProvider(dataSource);
    storageProvider.setJobMapper(jobMapper);
    return storageProvider;
  }

  @Produces
  @Singleton
  @CustomDatasource
  public DataSource createDataSource() {
    PGSimpleDataSource jdbcDataSource = new PGSimpleDataSource();
    jdbcDataSource.setURL(this.url);
    jdbcDataSource.setUser(this.username);
    jdbcDataSource.setPassword(this.password);
    return jdbcDataSource;
  }

  @Produces
  @Singleton
  public JobMapper jobMapper(JsonMapper jsonMapper) {
    return new JobMapper(jsonMapper);
  }

  @Produces
  @Singleton
  public JsonMapper jsonMapper() {
    return new JsonbJsonMapper();
  }
}
