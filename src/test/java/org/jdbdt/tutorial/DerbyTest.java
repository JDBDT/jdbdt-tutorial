package org.jdbdt.tutorial;

import org.junit.BeforeClass;

// For tests using Apache Derby - http://db.apache.org/derby/
@SuppressWarnings("javadoc")
public class DerbyTest extends UserDAOTest {

  private static final String 
    JDBC_DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";
  
  private static final String 
    DATABASE_URL = "jdbc:derby:./db/derby/jdbdtTutorial;create=true";
  
  @BeforeClass
  public static void globalSetup() throws Throwable {
    globalSetup(JDBC_DRIVER_CLASS, DATABASE_URL);
  }
}
