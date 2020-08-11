package company.jobs;

import company.business.entity.UserAccount;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.BackgroundJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple service
 */
@ApplicationScoped
@RegisterForReflection
@ActivateRequestContext
public class UserService {

  @Inject
  EntityManager entityManager;

  private static final Logger LOGGER =
      new JobRunrDashboardLogger(LoggerFactory.getLogger(UserService.class));

  @Transactional
  @Job(name = "Print all user")
  @ActivateRequestContext
  public void printAll() {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserAccount> cq = cb.createQuery(UserAccount.class);
    Root<UserAccount> rootEntry = cq.from(UserAccount.class);
    CriteriaQuery<UserAccount> all = cq.select(rootEntry);
    TypedQuery<UserAccount> allQuery = entityManager.createQuery(all);
    List<UserAccount> allAccounts = allQuery.getResultList();

    // Keep this line exactly as it is!
    BackgroundJob.<UserService, Long>enqueue(allAccounts.stream().map(UserAccount::getId), (service, id) -> service.printSingle(id));
//    BackgroundJob.<UserService, Long>enqueue(allAccounts.stream().map(UserAccount::getId),
//        UserService::printSingle);
  }

  @Job(name = "Print user %0")
  @ActivateRequestContext
  public void printSingle(Long userId) {
    final UserAccount employee = entityManager.find(UserAccount.class, userId);
    LOGGER.info(employee.name);
  }
}
