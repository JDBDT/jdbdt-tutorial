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
public abstract class UserDAOTest {
    
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
  
  // Global setup
  protected static 
  void globalSetup(String jdbcDriverClass, String databaseURL) throws Throwable {
    // Load JDBC driver class
    Class.forName(jdbcDriverClass);
    
    // Create database handle
    theDB = database(databaseURL);
    
    // Create DAO and in turn let it create USERS table 
    theDAO = new UserDAO(theDB.getConnection());
    theDAO.createTable();
    
    // Create table data source.
    theTable = table(theDB, "USERS")
              .columns("ID",
                       "LOGIN", 
                       "NAME", 
                       "PASSWORD",
                       "ROLE",
                       "CREATED" );
    
    // Define data set for populating the database
    theInitialData
      =  builder(theTable)
        .sequence("ID", 0)
        .sequence("PASSWORD", i -> "pass" + i)
        .value("LOGIN", "root")
        .nullValue("NAME")
        .value("CREATED", FIXED_DATE)
        .value("ROLE", ADMIN)
        .generate(1)
        .sequence("LOGIN", "alice", "bob", "charles")
        .sequence("NAME",  "Alice", "Bob", "Charles")
        .value("ROLE", REGULAR)
        .generate(3)
        .sequence("LOGIN", i -> "guest" + i, 1)
        .sequence("NAME",  i -> "Guest User " + i, 1)
        .value("ROLE", GUEST)
        .generate(2)
        .data();
    // dump(theInitialData, System.err);
    
    // Populate database using the built data set
    populate(theInitialData);
    
    // Set auto-commit off (to allow for save-points)
    theDB.getConnection().setAutoCommit(false);
  }
  
  @AfterClass 
  public static void globalTeardown() {
    truncate(theTable);
    teardown(theDB, true);
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
  
  private static final Conversion<User> CONVERSION = 
      u -> new Object[] { 
        u.getId(), 
        u.getLogin(), 
        u.getName(), 
        u.getPassword(),
        u.getRole().toString(),
        u.getCreated()
      };
      
  static DataSet toDataSet(User u) {
    return data(theTable, CONVERSION).row(u);
  }
  
  static User anExistingUser() {
    return new User(0, "root", null, "pass0", Role.ADMIN, FIXED_DATE);
  }
  
  static User nonExistingUser() {
    return new User(99, "john99", "John Doe 99", "doeit 99", Role.REGULAR, FIXED_DATE);
  }
  
  @Test
  public void testNonExistingUserInsertion() throws SQLException {
    User u = nonExistingUser();
    theDAO.insertUser(u);
    assertInserted("DB change", toDataSet(u));
  }
  
  @Test
  public void testNonExistingUserInsertionVariant() throws SQLException {
    User u = nonExistingUser();
    theDAO.insertUser(u);
    DataSet expected = DataSet.join(theInitialData, toDataSet(u));
    assertState("DB state", expected);
  }
  
  @Test
  public void testExistingUserInsertion() {
    try {
      User u = anExistingUser();
      theDAO.insertUser(u);
      fail("Expected " + SQLException.class);
    }
    catch (SQLException e) {
      assertUnchanged("No DB changes", theTable);
    }
  }
  
  @Test
  public void testExistingUserDelete() throws SQLException {
    User u = anExistingUser();
    boolean deleted = theDAO.deleteUser(u);
    assertDeleted("DB change", toDataSet(u));
    assertTrue("return value", deleted);
  }
  
  
  @Test
  public void testNonExistingUserDelete() throws SQLException {
    User u = nonExistingUser();
    boolean deleted = theDAO.deleteUser(u);    
    assertUnchanged("No DB changes", theTable);
    assertFalse("return value", deleted);
  }
  
  @Test 
  public void testDeleteAll() throws SQLException {
    int count = theDAO.deleteAllUsers();
    assertEmpty("DB cleaned up", theTable);
    assertEquals("return value", count, theInitialData.size());
  }
  
  @Test
  public void testExistingUserUpdate() throws SQLException {
    User u = anExistingUser();
    u.setPassword("new pass");
    u.setName("new name");
    boolean updated = theDAO.updateUser(u);
    assertDelta("DB change", toDataSet(anExistingUser()), toDataSet(u));
    assertTrue("return value", updated);
  }
  
  @Test
  public void testNonExistingUserUpdate() throws SQLException {
    User u = nonExistingUser();
    boolean updated = theDAO.updateUser(u);
    assertUnchanged("No DB changes", theTable);
    assertFalse("return value", updated);
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
  public void testGetUsersByRole() throws SQLException {
    List<User> list = theDAO.getUsers(Role.GUEST);
    DataSet expected = DataSet.last(theInitialData, 2);
    DataSet actual = data(theTable, CONVERSION).rows(list);
    assertEquals("Guest user list", expected, actual);
    assertUnchanged("No DB changes", theTable); 
  }
  
  @Test
  public void testGetUserById() throws SQLException {
    User expected = anExistingUser();
    User actual = theDAO.getUser(expected.getId());
    assertEquals("User", expected, actual);
    assertUnchanged("No DB changes", theTable); 
  }
  
  @Test
  public void testGetUserByLogin() throws SQLException {
    User expected = anExistingUser();
    User actual = theDAO.getUser(expected.getLogin());
    assertEquals("User", expected, actual);
    assertUnchanged("No DB changes", theTable); 
  }
 
}
