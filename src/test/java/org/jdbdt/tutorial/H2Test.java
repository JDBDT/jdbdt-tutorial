package org.jdbdt.tutorial;

import org.junit.BeforeClass;

// For tests using H2 - http://www.h2database.com
@SuppressWarnings("javadoc")
public class H2Test extends UserDAOTest {

  private static final String 
    JDBC_DRIVER_CLASS = "org.h2.Driver";
  
  private static final String 
    DATABASE_URL = "jdbc:h2:./db/h2/jdbdtTutorial";
  
  @BeforeClass
  public static void globalSetup() throws Throwable {
    globalSetup(JDBC_DRIVER_CLASS, DATABASE_URL);
  }
}
