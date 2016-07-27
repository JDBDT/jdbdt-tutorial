package org.jdbdt.tutorial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ 
  DerbyTest.class, 
  H2Test.class, 
  HSQLDBTest.class 
})
public class AllTests {

}
