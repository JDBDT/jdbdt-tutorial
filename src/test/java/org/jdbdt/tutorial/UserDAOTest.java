package org.jdbdt.tutorial;

import static org.jdbdt.JDBDT.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jdbdt.DB;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class UserDAOTest {
  
  // We'll make use of H2 ... try another database
  // engine / JDBC driver if you like
  private static final String 
    JDBC_DRIVER_CLASS = "org.h2.Driver";
  
  // H2 database URL 
  private static final String 
    DATABASE_URL = "jdbc:h2:./jdbdtTutorialDB";
  
  // JDBDT handle for the database 
  static DB theDB; 
  
  // DAO (the SUT)
  static UserDAO theSUT;
  
  @BeforeClass
  public static void globalSetup() throws Throwable {
    // Setup 
    Class.forName(JDBC_DRIVER_CLASS);
    theDB = database(DATABASE_URL);
    theSUT = new UserDAO(theDB.getConnection());
  }
  
  @AfterClass 
  public static void globalTeardown() {
    teardown(theDB, true);
  }
  
  @Test 
  public void doNothing() { }
}
