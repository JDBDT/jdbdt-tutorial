package org.jdbdt.tutorial;

import org.junit.BeforeClass;

// For tests using HSQLDB - http://hsqldb.org
@SuppressWarnings("javadoc")
public class HSQLDBTest extends UserDAOTest {

  private static final String 
    JDBC_DRIVER_CLASS = "org.hsqldb.jdbcDriver";
  
  private static final String 
    DATABASE_URL = "jdbc:hsqldb:file:jdbdtTutHSQLDB;shutdown=true";
  
  @BeforeClass
  public static void globalSetup() throws Throwable {
    globalSetup(JDBC_DRIVER_CLASS, DATABASE_URL);
  }
}
