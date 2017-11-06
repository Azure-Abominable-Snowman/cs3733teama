package controllers;

public class Mortgage {
    private int amt, years;
    private double interest;
    public Mortgage(int amt, double interest, int years) {
        this.amt = amt;
        this.interest = interest;
        this.years = years;
    }

    public double calculateMortgage() {
        return amt+years+interest;
    }
}
