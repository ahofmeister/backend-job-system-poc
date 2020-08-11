package company.business.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Alexander Hofmeister
 */
@Entity
public class UserAccount {

  @Id
  @GeneratedValue
  public Long id;

  public String name;

  public Long getId() {
    return id;
  }
}
