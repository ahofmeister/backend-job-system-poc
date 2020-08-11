package company.jobs;

import company.business.entity.UserAccount;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import net.bytebuddy.utility.RandomString;

/**
 * @author Alexander Hofmeister
 */
@Path("user")
public class UserResource {

  @Inject
  EntityManager entityManager;


  @GET
  @Transactional
  @Path("add")
  public void add() {
    for (int i = 0; i < 10; i++) {
      UserAccount userAccount = new UserAccount();
      userAccount.name = RandomString.make(10);
      entityManager.persist(userAccount);
    }
  }

}
