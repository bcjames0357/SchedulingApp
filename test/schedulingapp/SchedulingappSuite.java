/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import schedulingapp.resources.ResourcesSuite;

/**
 *
 * @author Brandon James <bcjames0357@gmail.com>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ResourcesSuite.class, LogInControllerTest.class, SchedulingAppTest.class})
public class SchedulingappSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
