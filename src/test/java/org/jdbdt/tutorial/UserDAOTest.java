package org.jdbdt.tutorial;

// Java/JDBC API imports
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

//JUnit imports
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// JDBDT import
import static org.jdbdt.JDBDT.*; 
import org.jdbdt.Conversion;
import org.jdbdt.DB;
import org.jdbdt.DataSet;
import org.jdbdt.Table;

// Static import for user roles
import static org.jdbdt.tutorial.Role.*;

@SuppressWarnings("javadoc")
public class UserDAOTest {
  
  // We'll make use of a H2 database 
  private static final String 
    JDBC_DRIVER_CLASS = "org.h2.Driver";
  
  // H2 database URL 
  private static final String 
    DATABASE_URL = "jdbc:h2:./jdbdtTutorialDB";
  
  // JDBDT handle for the database 
  static DB theDB; 
  
  // DAO (the SUT)
  static UserDAO theDAO;
  
  // User table.
  static Table theTable;
  
  // Fixed date used for test data
  static final Date FIXED_DATE = Date.valueOf("2016-01-01");
  
  // Data set that we use for populating the table.
  // (we need this as a field so it can be referred to in a few test methods)
  static DataSet theInitialData;
  
  @BeforeClass
  public static void globalSetup() throws Throwable {
    // Setup 
    Class.forName(JDBC_DRIVER_CLASS);
    theDB = database(DATABASE_URL);
    theDB.getConnection().setAutoCommit(false);
    theDAO = new UserDAO(theDB.getConnection());
    theDAO.createTable();
    theTable = table(theDB, "USERS")
              .columns("ID",
                       "LOGIN", 
                       "NAME", 
                       "PASSWORD",
                       "ROLE",
                       "CREATED" );
    // Initial data
    theInitialData
      =  builder(theTable)
        .sequence("ID", 0)
        .sequence("PASSWORD", i -> "pass" + i)
        .value("LOGIN", "root")
        .value("NAME", "Root user")
        .value("CREATED", FIXED_DATE)
        .value("ROLE", ADMIN.toString())
        .generate(1)
        .sequence("LOGIN", "alice", "bob", "charles")
        .sequence("NAME",  "Alice", "Bob", "Charles")
        .value("ROLE", REGULAR.toString())
        .generate(3)
        .sequence("LOGIN", i -> "guest" + i)
        .sequence("NAME",  i -> "Guest User " + i)
        .value("ROLE", GUEST.toString())
        .generate(2)
        .data();
    
    populate(theInitialData);
  }
  
  @AfterClass 
  public static void globalTeardown() {
    truncate(theTable);
    teardown(theDB, true);
  }
  
  private static final Conversion<User> CONVERSION = 
      u -> new Object[] { 
        u.getId(), 
        u.getLogin(), 
        u.getName(), 
        u.getPassword(),
        u.getRole().toString(),
        u.getCreated()
      };
      
  static DataSet userSet(User... users) {
    return data(theTable, CONVERSION).rows(users);
  }
  
  @Before
  public void saveState() {
    // Set save point
    save(theDB);
  }
  
  @After
  public void restoreState() {
    // Restore state to save point
    restore(theDB);
  }
  
  static User existingUser() {
    return new User(0, "root", "Root user", "pass0", Role.ADMIN, FIXED_DATE);
  }
  
  static User nonExistingUser() {
    return new User(99, "john99", "John Doe 99", "doeit 99", Role.REGULAR, FIXED_DATE);
  }
  
  @Test
  public void testNonExistingUserInsertion() throws SQLException {
    User u = nonExistingUser();
    theDAO.insertUser(u);
    assertInserted("DB change", userSet(u));
  }
  
  @Test
  public void testExistingUserInsertion() {
    try {
      User u = existingUser();
      theDAO.insertUser(u);
      fail("Expected " + SQLException.class);
    }
    catch (SQLException e) {
      assertUnchanged("No DB changes", theTable);
    }
  }
  
  @Test
  public void testExistingUserDelete() throws SQLException {
    User u = existingUser();
    boolean deleted = theDAO.deleteUser(u);
    assertTrue("return value", deleted);
    assertDeleted("DB change", userSet(u));
  }
  
  
  @Test
  public void testNonExistingUserDelete() throws SQLException {
    User u = nonExistingUser();
    boolean deleted = theDAO.deleteUser(u);
    assertFalse("return value", deleted);
    assertUnchanged("No DB changes", theTable);
  }
  
  @Test 
  public void testDeleteAll() throws SQLException {
    int n = theDAO.deleteAllUsers();
    assertEquals("deleted users", n, theInitialData.size());
    assertEmpty("DB cleaned up", theTable);
  }
  
  @Test
  public void testExistingUserUpdate() throws SQLException {
    User u = existingUser();
    u.setPassword("new pass");
    u.setName("new name");
    boolean updated = theDAO.updateUser(u);
    assertTrue("return value", updated);
    assertDelta("DB change", userSet(existingUser()), userSet(u));
  }
  
  @Test
  public void testNonExistingUserUpdate() throws SQLException {
    User u = nonExistingUser();
    boolean updated = theDAO.updateUser(u);
    assertFalse("return value", updated);
    assertUnchanged("No DB changes", theTable);
  }
  
  @Test
  public void testGetAllUsers() throws SQLException {
    List<User> list = theDAO.getAllUsers();
    DataSet expected = theInitialData;
    DataSet actual = data(theTable, CONVERSION).rows(list);
    assertEquals("User list", expected, actual);
    assertUnchanged("No DB changes", theTable); 
  }
  
  @Test
  public void testGetUserById() throws SQLException {
    User expected = existingUser();
    User actual = theDAO.getUser(expected.getId());
    assertEquals("User", expected, actual);
    assertUnchanged("No DB changes", theTable); 
  }
  
  @Test
  public void testGetUserByLogin() throws SQLException {
    User expected = existingUser();
    User actual = theDAO.getUser(expected.getLogin());
    assertEquals("User", expected, actual);
    assertUnchanged("No DB changes", theTable); 
  }
  

  
}
