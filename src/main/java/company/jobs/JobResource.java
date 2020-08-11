package company.jobs;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jobrunr.scheduling.BackgroundJob;

@Path("jobs")
@ApplicationScoped
public class JobResource {

  @GET
  @Path("/add")
  @Transactional
  @Produces(MediaType.APPLICATION_JSON)
  public void addJob() {
    BackgroundJob.enqueue(() -> System.out.println("Simple!"));
  }

}
