package company.jobs;

import io.quarkus.runtime.StartupEvent;
import java.time.DayOfWeek;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.utils.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexander Hofmeister
 */
public class BackgroundJobServerConfig {

  @Inject
  BackgroundJobServer backgroundJobServer;

  @Inject
  JobScheduler jobScheduler;

  @Inject
  UserService userService;

  @Inject
  StorageProvider storageProvider;

  @Inject
  JsonMapper jsonMapper;

  @Inject
  @ConfigProperty(name = "TYPE")
  ApplicationType type;

  private static final Logger LOGGER =
      new JobRunrDashboardLogger(LoggerFactory.getLogger(BackgroundJobServerConfig.class));

  void onStart(@Observes StartupEvent ev) {
    LOGGER.info("The application is starting...");

    if (type == ApplicationType.JOB) {
      backgroundJobServer.start();
      new JobRunrDashboardWebServer(storageProvider, jsonMapper, 1234);
      BackgroundJob.scheduleRecurringly("print-all-user", () -> userService.printAll(),
          Cron.weekly(DayOfWeek.FRIDAY, 22));
    }
  }
}
