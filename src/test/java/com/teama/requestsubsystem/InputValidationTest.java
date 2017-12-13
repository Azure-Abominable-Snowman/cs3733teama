package com.teama.requestsubsystem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.MalformedParametersException;

import static org.junit.Assert.*;

public class InputValidationTest {

    private String phone1 = "1111111111";
    private String phone2 = "111111a111";

    private String email1 = "giveUsanA@wpi.edu";
    private String email2 = "letthisfail";


    private InputValidation phoneChecker = new InputValidation();
    private InputValidation emailChecker = new InputValidation();


    @Test
    public void phoneChecker1() throws Exception {
        assertEquals(phoneChecker.phoneChecker(phone1), "1111111111");

    }

    @Test(expected = MalformedParametersException.class)
    public void phoneChecker2() throws Exception {
        assertEquals(phoneChecker.phoneChecker(phone2), "111111a111");
        Assert.fail("phone2 = \"111111a111\"\") should throw \"converter.exceptions.MalformedParametersException\"");

    }


    @Test
    public void emailChecker1() throws Exception {
        assertEquals(emailChecker.emailChecker(email1), "giveUsanA@wpi.edu");
    }

    @Test(expected = MalformedParametersException.class)
    public void emailChecker2() throws Exception {
        assertEquals(emailChecker.emailChecker(email2), "letthisfail");
        Assert.fail("phone2 = \"letthisfail\"\") should throw \"converter.exceptions.MalformedParametersException\"");

    }

}