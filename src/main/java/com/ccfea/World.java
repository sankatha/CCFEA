package com.ccfea;

import jas.engine.Sim;

import java.util.LinkedList;

public class World {

    public static final int UPDOWNLOOKBACK = 5;
    public static final int NMAS = 4;
    public static final int MAXHISTORY = 500;
    public static int NWORLDBITS;
    public static final int NULLBIT = -1;
    public static final int PUPDOWNBITNUM = 42;
    public static final double[] ratios = new double[]{0.25D, 0.5D, 0.75D, 0.875D, 1.0D, 1.125D, 1.25D, 1.5D, 2.0D, 4.0D};
    public static final int NRATIOS = ratios.length;
    public static final int EQ = 0;
    public double intrate;
    public double dividendscale;
    public int[] pupdown = new int[5];
    public int[] dupdown = new int[5];
    public int history_top;
    public int updown_top;
    public static double price;
    public double oldprice;
    public double dividend;
    public static double AverageWealth;
    public double olddividend;
    public double saveddividend;
    public double savedprice;
    public static double riskNeutral;
    public static double rationalExpectations;
    public double rea;
    public double reb;
    public double profitperunit;
    public double returnratio;
    public int[] malength = new int[4];
    public int nworldbits;
    int[] realworld;
    public boolean exponentialMAs;
    public MovingAverage[] priceMA = new MovingAverage[4];
    public MovingAverage[] divMA = new MovingAverage[4];
    public MovingAverage[] oldpriceMA = new MovingAverage[4];
    public MovingAverage[] olddivMA = new MovingAverage[4];
    public static LinkedList<BitName> bitnameList = new LinkedList<>();
    private double[] divhistory;
    private double[] pricehistory;


    public void createBitnameList() {
        bitnameList.add(new BitName("on", "dummy bit -- always on"));
        bitnameList.add(new BitName("off", "dummy bit -- always off"));
        bitnameList.add(new BitName("random", "random on or off"));
        bitnameList.add(new BitName("dup", "dividend went up this period"));
        bitnameList.add(new BitName("dup1", "dividend went up one period ago"));
        bitnameList.add(new BitName("dup2", "dividend went up two periods ago"));
        bitnameList.add(new BitName("dup3", "dividend went up three periods ago"));
        bitnameList.add(new BitName("dup4", "dividend went up four periods ago"));
        bitnameList.add(new BitName("d5up", "5-period MA of dividend went up"));
        bitnameList.add(new BitName("d20up", "20-period MA of dividend went up"));
        bitnameList.add(new BitName("d100up", "100-period MA of dividend went up"));
        bitnameList.add(new BitName("d500up", "500-period MA of dividend went up"));
        bitnameList.add(new BitName("d>d5", "dividend > 5-period MA"));
        bitnameList.add(new BitName("d>d20", "dividend > 20-period MA"));
        bitnameList.add(new BitName("d>d100", "dividend > 100-period MA"));
        bitnameList.add(new BitName("d>d500", "dividend > 500-period MA"));
        bitnameList.add(new BitName("d5>d20", "dividend: 5-period MA > 20-period MA"));
        bitnameList.add(new BitName("d5>d100", "dividend: 5-period MA > 100-period MA"));
        bitnameList.add(new BitName("d5>d500", "dividend: 5-period MA > 500-period MA"));
        bitnameList.add(new BitName("d20>d100", "dividend: 20-period MA > 100-period MA"));
        bitnameList.add(new BitName("d20>d500", "dividend: 20-period MA > 500-period MA"));
        bitnameList.add(new BitName("d100>d500", "dividend: 100-period MA > 500-period MA"));
        bitnameList.add(new BitName("d/md>1/4", "dividend/mean dividend > 1/4"));
        bitnameList.add(new BitName("d/md>1/2", "dividend/mean dividend > 1/2"));
        bitnameList.add(new BitName("d/md>3/4", "dividend/mean dividend > 3/4"));
        bitnameList.add(new BitName("d/md>7/8", "dividend/mean dividend > 7/8"));
        bitnameList.add(new BitName("d/md>1", "dividend/mean dividend > 1  "));
        bitnameList.add(new BitName("d/md>9/8", "dividend/mean dividend > 9/8"));
        bitnameList.add(new BitName("d/md>5/4", "dividend/mean dividend > 5/4"));
        bitnameList.add(new BitName("d/md>3/2", "dividend/mean dividend > 3/2"));
        bitnameList.add(new BitName("d/md>2", "dividend/mean dividend > 2"));
        bitnameList.add(new BitName("d/md>4", "dividend/mean dividend > 4"));
        bitnameList.add(new BitName("pr/d>1/4", "price*interest/dividend > 1/4"));
        bitnameList.add(new BitName("pr/d>1/2", "price*interest/dividend > 1/2"));
        bitnameList.add(new BitName("pr/d>3/4", "price*interest/dividend > 3/4"));
        bitnameList.add(new BitName("pr/d>7/8", "price*interest/dividend > 7/8"));
        bitnameList.add(new BitName("pr/d>1", "price*interest/dividend > 1"));
        bitnameList.add(new BitName("pr/d>9/8", "price*interest/dividend > 9/8"));
        bitnameList.add(new BitName("pr/d>5/4", "price*interest/dividend > 5/4"));
        bitnameList.add(new BitName("pr/d>3/2", "price*interest/dividend > 3/2"));
        bitnameList.add(new BitName("pr/d>2", "price*interest/dividend > 2"));
        bitnameList.add(new BitName("pr/d>4", "price*interest/dividend > 4"));
        bitnameList.add(new BitName("pup", "price went up this period"));
        bitnameList.add(new BitName("pup1", "price went up one period ago"));
        bitnameList.add(new BitName("pup2", "price went up two periods ago"));
        bitnameList.add(new BitName("pup3", "price went up three periods ago"));
        bitnameList.add(new BitName("pup4", "price went up four periods ago"));
        bitnameList.add(new BitName("p5up", "5-period MA of price went up"));
        bitnameList.add(new BitName("p20up", "20-period MA of price went up"));
        bitnameList.add(new BitName("p100up", "100-period MA of price went up"));
        bitnameList.add(new BitName("p500up", "500-period MA of price went up"));
        bitnameList.add(new BitName("p>p5", "price > 5-period MA"));
        bitnameList.add(new BitName("p>p20", "price > 20-period MA"));
        bitnameList.add(new BitName("p>p100", "price > 100-period MA"));
        bitnameList.add(new BitName("p>p500", "price > 500-period MA"));
        bitnameList.add(new BitName("p5>p20", "price: 5-period MA > 20-period MA"));
        bitnameList.add(new BitName("p5>p100", "price: 5-period MA > 100-period MA"));
        bitnameList.add(new BitName("p5>p500", "price: 5-period MA > 500-period MA"));
        bitnameList.add(new BitName("p20>p100", "price: 20-period MA > 100-period MA"));
        bitnameList.add(new BitName("p20>p500", "price: 20-period MA > 500-period MA"));
        bitnameList.add(new BitName("p100>p500", "price: 100-period MA > 500-period MA"));
        NWORLDBITS = bitnameList.size();
    }

    public int irand(int x) {
        return Sim.getRnd().getIntFromTo(0, x - 1);
    }

    public double GETMA(MovingAverage[] x, int j) {
        return this.exponentialMAs ? x[j].getEWMA() : x[j].getMA();
    }

    public int ChangeBooleanToInt(boolean a) {
        return a ? 1 : 0;
    }

    public static String descriptionOfBit(int n) {
        return n == -1 ? "(Unused bit for spacing)" : (n >= 0 && n < NWORLDBITS ? bitnameList.get(n).description : "(Invalid world bit)");
    }

    public static String nameOfBit(int n) {
        return n == -1 ? "null" : (n >= 0 && n < NWORLDBITS ? bitnameList.get(n).name : "");
    }

    public static int bitNumberOf(String name) {
        int n;
        n = 0;
        while (n < NWORLDBITS && name.compareTo(bitnameList.get(n).name) != 0) {
            ;
            ++n;
        }

        if (n >= NWORLDBITS) {
            n = -1;
        }

        return n;
    }

    public void setintrate(double rate) {
        this.intrate = rate;
    }

    public void setExponentialMAs(boolean aBool) {
        this.exponentialMAs = aBool;
    }

    public int getNumWorldBits() {
        return this.nworldbits;
    }

    public void initWithBaseline(double baseline) {
        if (nameOfBit(42).compareTo("pup") != 0) {
            System.out.println("PUPDOWNBITNUM is incorrect");
        }

        this.dividendscale = baseline;
        double initprice = baseline / this.intrate;
        double initdividend = baseline;
        this.saveddividend = this.dividend = baseline;
        this.setDividend(baseline);
        price = initprice;
        this.savedprice = initprice;
        this.setPrice(initprice);
        this.returnratio = this.intrate;
        this.profitperunit = 0.0D;
        this.nworldbits = NWORLDBITS;
        this.malength[0] = 5;
        this.malength[1] = 20;
        this.malength[2] = 100;
        this.malength[3] = 500;
        this.history_top = 0;
        this.updown_top = 0;
        this.divhistory = new double[500];
        this.pricehistory = new double[500];
        this.realworld = new int[NWORLDBITS];

        int i;
        for (i = 0; i < 5; ++i) {
            this.pupdown[i] = 0;
            this.dupdown[i] = 0;
        }

        for (i = 0; i < 500; ++i) {
            this.pricehistory[i] = initprice;
            this.divhistory[i] = initdividend;
        }

        for (i = 0; i < 4; ++i) {
            MovingAverage a = new MovingAverage();
            this.priceMA[i] = a;
            this.priceMA[i].initWidth$Value(this.malength[i], initprice);
            MovingAverage b = new MovingAverage();
            this.divMA[i] = b;
            this.divMA[i].initWidth$Value(this.malength[i], initdividend);
            MovingAverage c = new MovingAverage();
            this.oldpriceMA[i] = c;
            this.oldpriceMA[i].initWidth$Value(this.malength[i], initprice);
            MovingAverage d = new MovingAverage();
            this.olddivMA[i] = d;
            this.olddivMA[i].initWidth$Value(this.malength[i], initdividend);
        }

        this.makebitvector();
    }

    public void setPrice(double p) {
        if (price != this.savedprice) {
            System.out.println("Price was changed illegally");
        }

        this.oldprice = price;
        price = p;
        this.profitperunit = price - this.oldprice + this.dividend;
        if (this.oldprice <= 0.0D) {
            this.returnratio = this.profitperunit * 1000.0D;
        } else {
            this.returnratio = this.profitperunit / this.oldprice;
        }

        this.savedprice = price;
    }

    public double getPrice() {
        return price;
    }

    public double getProfitPerUnit() {
        return this.profitperunit;
    }

    public void setDividend(double d) {
        if (this.dividend != this.saveddividend) {
            System.out.println("Dividend was changed illegally.");
        }

        this.olddividend = this.dividend;
        this.dividend = d;
        this.saveddividend = this.dividend;
        riskNeutral = this.dividend / this.intrate;
        rationalExpectations = this.rea * this.dividend + this.reb;
    }

    public double getDividend() {
        return this.dividend;
    }

    public void setAverageWealth(double avr) {
        AverageWealth = avr;
    }

    public double getAverageWealth() {
        return AverageWealth;
    }

    public double getRiskNeutral() {
        return riskNeutral;
    }

    public double getRationalExpectations() {
        return rationalExpectations;
    }

    public void updateWorld() {
        this.updown_top = (this.updown_top + 1) % 5;
        this.pupdown[this.updown_top] = this.ChangeBooleanToInt(price > this.oldprice);
        this.dupdown[this.updown_top] = this.ChangeBooleanToInt(this.dividend > this.olddividend);
        this.history_top = this.history_top + 1 + 500;

        for (int i = 0; i < 4; ++i) {
            int rago = (this.history_top - this.malength[i]) % 500;
            this.priceMA[i].addValue(price);
            this.divMA[i].addValue(this.dividend);
            this.oldpriceMA[i].addValue(this.pricehistory[rago]);
            this.olddivMA[i].addValue(this.divhistory[rago]);
        }

        this.history_top %= 500;
        this.pricehistory[this.history_top] = price;
        this.divhistory[this.history_top] = this.dividend;
        this.makebitvector();
    }

    private void makebitvector() {
        byte i = 0;
        int var7 = i + 1;
        this.realworld[i] = 1;
        this.realworld[var7++] = 0;
        this.realworld[var7++] = this.irand(2);
        int temp = this.updown_top + 5;

        int j;
        for (j = 0; j < 5; --temp) {
            this.realworld[var7++] = this.dupdown[temp % 5];
            ++j;
        }

        for (j = 0; j < 4; ++j) {
            this.realworld[var7++] = this.ChangeBooleanToInt(this.GETMA(this.divMA, j) > this.GETMA(this.olddivMA, j));
        }

        for (j = 0; j < 4; ++j) {
            this.realworld[var7++] = this.ChangeBooleanToInt(this.dividend > this.GETMA(this.divMA, j));
        }

        int k;
        for (j = 0; j < 3; ++j) {
            for (k = j + 1; k < 4; ++k) {
                this.realworld[var7++] = this.ChangeBooleanToInt(this.GETMA(this.divMA, j) > this.GETMA(this.divMA, k));
            }
        }

        double multiple = this.dividend / this.dividendscale;

        for (j = 0; j < NRATIOS; ++j) {
            this.realworld[var7++] = this.ChangeBooleanToInt(multiple > ratios[j]);
        }

        multiple = price * this.intrate / this.olddividend;

        for (j = 0; j < NRATIOS; ++j) {
            this.realworld[var7++] = this.ChangeBooleanToInt(multiple > ratios[j]);
        }

        temp = this.updown_top + 5;

        for (j = 0; j < 5; --temp) {
            this.realworld[var7++] = this.pupdown[temp % 5];
            ++j;
        }

        for (j = 0; j < 4; ++j) {
            this.realworld[var7++] = this.ChangeBooleanToInt(this.GETMA(this.priceMA, j) > this.GETMA(this.oldpriceMA, j));
        }

        for (j = 0; j < 4; ++j) {
            this.realworld[var7++] = this.ChangeBooleanToInt(price > this.GETMA(this.priceMA, j));
        }

        for (j = 0; j < 3; ++j) {
            for (k = j + 1; k < 4; ++k) {
                this.realworld[var7++] = this.ChangeBooleanToInt(this.GETMA(this.priceMA, j) > this.GETMA(this.priceMA, k));
            }
        }

        if (var7 != NWORLDBITS) {
            System.out.println("Bits calculated != bits defined.");
        }

        for (var7 = 0; var7 < NWORLDBITS; ++var7) {
            this.realworld[var7] = 2 - this.realworld[var7];
        }
    }

    public void getRealWorld(int[] anArray) {
        System.arraycopy(this.realworld, 0, anArray, 0, NWORLDBITS);
    }

    public int pricetrend(int n) {
        if (n > 5) {
            System.out.println("argument " + n + " to pricetrend() exceeds " + 5);
        }

        int i = 0;

        int trend;
        for (trend = 0; i < n; ++i) {
            trend |= this.realworld[i + 42];
        }

        return trend == 1 ? 1 : (trend == 2 ? -1 : 0);
    }

    public void setRea$Reb(double rea1, double reb1) {
        this.rea = rea1;
        this.reb = reb1;
    }
}
