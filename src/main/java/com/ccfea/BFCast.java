package com.ccfea;

public class BFCast {
    double forecast;
    double lforecast;
    double variance;
    double strength;
    double a;
    double b;
    double c;
    double specfactor;
    double bitcost;
    BitVector conditions;
    int lastactive;
    int specificity;
    int count;
    int condwords;
    int condbits;
    int nnulls;


    public void createEnd() {
        if (this.condwords == 0 || this.condbits == 0) {
            System.out.println("Must have condwords to create BFCast.0");
        }

        this.forecast = 0.0D;
        this.count = 0;
        this.lastactive = 1;
        this.specificity = 0;
        this.variance = 9.99999999E8D;
        this.conditions = new BitVector();
        this.conditions.setCondwords(this.condwords);
        this.conditions.setCondbits(this.condbits);
        this.conditions.createEnd();
    }

    public static void init() {
        BitVector.init();
    }

    public void setCondwords(int x) {
        this.condwords = x;
    }

    public void setCondbits(int x) {
        this.condbits = x;
    }

    public void setNNulls(int x) {
        this.nnulls = this.nnulls;
    }

    public void setBitcost(double x) {
        this.bitcost = x;
    }

    public void setConditions(int[] x) {
        this.conditions.setConditions(x);
    }

    public int[] getConditions() {
        return this.conditions.getConditions();
    }

    public BitVector getConditionsObject() {
        return this.conditions;
    }

    public void setConditionsWord$To(int i, int value) {
        this.conditions.setConditionsWord$To(i, value);
    }

    public int getConditionsWord(int x) {
        return this.conditions.getConditionsWord(x);
    }

    public void setConditionsbit$To(int bit, int x) {
        this.conditions.setConditionsbit$To(bit, x);
    }

    public void setConditionsbit$FromZeroTo(int bit, int x) {
        this.conditions.setConditionsbit$FromZeroTo(bit, x);
    }

    public int getConditionsbit(int bit) {
        return this.conditions.getConditionsbit(bit);
    }

    public void maskConditionsbit(int bit) {
        this.conditions.maskConditionsbit(bit);
    }

    public void switchConditionsbit(int bit) {
        this.conditions.switchConditionsbit(bit);
    }

    public void setAval(double x) {
        this.a = x;
    }

    public void setBval(double x) {
        this.b = x;
    }

    public void setCval(double x) {
        this.c = x;
    }

    public double getAval() {
        return this.a;
    }

    public double getBval() {
        return this.b;
    }

    public double getCval() {
        return this.c;
    }

    public void updateSpecfactor() {
        this.specfactor = (double) (this.condbits - this.nnulls - this.specificity) * this.bitcost;
    }

    public void setSpecfactor(double x) {
        this.specfactor = x;
    }

    public double getSpecfactor() {
        return this.specfactor;
    }

    public void incrSpecificity() {
        ++this.specificity;
    }

    public void decrSpecificity() {
        --this.specificity;
    }

    public void setSpecificity(int x) {
        this.specificity = x;
    }

    public int getSpecificity() {
        return this.specificity;
    }

    public void setVariance(double x) {
        this.variance = x;
    }

    public double getVariance() {
        return this.variance;
    }

    public void setLastactive(int x) {
        this.lastactive = x;
    }

    public int getLastactive() {
        return this.lastactive;
    }

    public int getCnt() {
        return this.count;
    }

    public void setCnt(int x) {
        this.count = x;
    }

    public int incrCount() {
        return ++this.count;
    }

    public double getStrength() {
        return this.strength;
    }

    public void setStrength(double x) {
        this.strength = x;
    }

    public void setLforecast(double x) {
        this.lforecast = x;
    }

    public double getLforecast() {
        return this.lforecast;
    }

    public void setForecast(double x) {
        this.forecast = x;
    }

    public double getForecast() {
        return this.forecast;
    }

    public void updateForecastPrice$Dividend(double price, double dividend) {
        this.lforecast = this.forecast;
        this.forecast = this.a * (price + dividend) + this.b * dividend + this.c;
    }

    public void copyEverythingFrom(BFCast from) {
        this.forecast = from.getForecast();
        this.lforecast = from.getLforecast();
        this.variance = from.getVariance();
        this.strength = from.getStrength();
        this.a = from.getAval();
        this.b = from.getBval();
        this.c = from.getCval();
        this.specfactor = from.getSpecfactor();
        this.lastactive = from.getLastactive();
        this.specificity = from.getSpecificity();
        this.count = from.getCnt();
        this.setConditions(from.getConditions());
    }

    public Object printcond(int word) {
        return this;
    }
}
