package com.teama.requestsubsystem;

import java.lang.reflect.MalformedParametersException;

public class InputValidation {

    public String phoneChecker(String inputtedPhone){

        if(inputtedPhone.length() != 10) throw new MalformedParametersException();
        if(! inputtedPhone.matches("[0-9]+")) throw new MalformedParametersException();
        return inputtedPhone;
    }


    public String emailChecker(String inputtedEmail) {

        CharSequence atEmail = "@";
        CharSequence dotEmail = ".";

        if(inputtedEmail.indexOf("@")==-1 || inputtedEmail.indexOf(".")==-1){
            throw new MalformedParametersException();
        }
        return inputtedEmail;
    }



}
