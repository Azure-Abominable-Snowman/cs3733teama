package controllers;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MortgageTest {
    @Test
    public void calculateMortgage() {
        assertEquals(new Mortgage(1, 2, 3).calculateMortgage(), 6.0);
    }

}