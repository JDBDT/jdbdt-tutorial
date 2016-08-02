package org.jdbdt.tutorial;

import java.sql.Date;
import java.util.Arrays;

/**
 * POJO for user data.
 */
@SuppressWarnings("javadoc")
public final class User {

  private int id;
  private String login;
  private String name;
  private String password;
  private Role role;
  private Date created;

  
  public User(int id, String login, String name, String password, Role role, Date created) {
    setId(id);
    setLogin(login);
    setName(name);
    setPassword(password);
    setCreated(created);
    setRole(role);
  }

  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public void setRole(Role r) {
    role = r;
  }
  
  public Role getRole() {
    return role;
  }
  
  public Date getCreated() {
    return created;
  }

  public void setCreated(Date date) {
    this.created = date;
  }
  
  public boolean sameAs(User u) {
    return Arrays.equals(toArray(), u.toArray());
  }
  
  @Override 
  public boolean equals(Object o) {
    return o == this || (o instanceof User && sameAs((User) o));
  }
  
  @Override 
  public String toString() {
    return String.format("id=%d login=%s name=%s password=%s role=%s created=%s",
                         toArray());
  }

  @Override 
  public int hashCode() {
    return 
      Arrays.hashCode(toArray());
  }
  
  private Object[] toArray() {
    return new Object[] { id, role, login, name, password, role, created };
  }
}
