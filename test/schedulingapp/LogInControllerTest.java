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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Brandon James <bcjames0357@gmail.com>
 */
public class LogInControllerTest {
    
    public LogInControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    } 

    /**
     * Test of validatePassword method, of class LogInController.
     */
    @Test
    public void testValidatePasswordPass() {
        System.out.println("validatePassword");
        byte[] hashPass = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        byte[] dbPass = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        LogInController instance = new LogInController();
        boolean expResult = true;
        boolean result = instance.validatePassword(hashPass, dbPass);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of validatePassword method, of class LogInController.
     */
    @Test
    public void testValidatePasswordPassFail() {
        System.out.println("validatePassword");
        byte[] hashPass = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        byte[] dbPass = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
            10, 11, 12, 13, 14, 15, 16, 17, 18, 1};
        LogInController instance = new LogInController();
        boolean expResult = false;
        boolean result = instance.validatePassword(hashPass, dbPass);
        assertEquals(expResult, result);
    }

    /**
     * Test of getEncryptedPassword method, of class LogInController.
     */
    @Test
    public void testGetEncryptedPassword() throws Exception {
        System.out.println("getEncryptedPassword");
        String password = "password";
        byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        int iterations = 1000 * 20;
        int derivedKeyLength = 256;
        byte[] expResult = {-113, 36, 65, 59, -97, -111, -53, 21, -4, -24, -15, 
            -65, -117, -23, -117, 38, 78, -6, 3, -88, 58, 118, 109, -84, -13, 
            -87, 67, 68, 31, 59, -114, -14, 2, 127, -78, 91, 42, 63, -60, -53, 
            -81, -97, 56, 105, 106, -84, 72, 13, -38, -14, 12, 3, -87, -23, -48, 
            58, -104, -64, -70, -126, -103, 112, -40, 52, -11, 113, 37, 64, 40, 
            48, 42, -53, 107, -108, 81, 26, -24, 80, -111, 110, 67, -42, -69, 
            -62, -86, 71, -29, 125, -44, -126, -28, 58, -45, -44, 81, 69, 27, 
            40, 26, -28, 31, -111, 82, -67, 1, -78, 6, 61, 123, -29, -75, 28, 
            126, -88, -79, -48, -3, 37, 108, 93, 9, -92, -97, 117, -95, 33, 14, 
            -59, -72, 81, 68, -29, -6, -90, 72, -75, -13, -67, 9, -59, 119, 83, 
            58, 104, -53, 82, 13, -113, -20, 87, 113, -49, -18, 65, -107, -63, 
            -73, -99, 112, -112, -36, 96, -118, 86, 27, -90, -88, 36, -109, 107, 
            -29, -67, -30, 24, 115, -21, -123, 98, 30, 107, -87, 71, 61, -6, 121, 
            -118, -8, 6, -4, -1, 20, 101, -105, -35, 66, 125, 68, 109, 27, 34, 13, 
            -101, -24, -96, 127, -31, 122, -62, -33, 102, 1, -6, 23, -54, -7, -66, 
            -108, 122, 26, -17, -100, 12, -87, 108, 31, -123, -44, 7, -83, -66, 
            -72, 97, -38, 42, 74, -127, -14, -95, 92, 8, -15, -99, -3, -7, 14, 
            -74, 71, 113, 84, -78, -101, 70, 116, 75, -70, -127}
;
        byte[] result = LogInController.getEncryptedPassword(password, salt, iterations, derivedKeyLength);
        assertArrayEquals(expResult, result);
    }
}
