package com.ccfea;

import jas.engine.Sim;
import jas.statistics.IDoubleSource;
import org._3pq.jgrapht.Graph;

import java.util.LinkedList;

public class BFagent extends Agent implements IDoubleSource {

    public static final int WEALTH = 0;
    public static final int POSITION = 1;
    public int currentTime;
    public int lastgatime;
    public double avspecificity;
    public double forecast;
    public double lforecast;
    public double global_mean;
    public double realDeviation;
    public double variance;
    public double pdcoeff;
    public double offset;
    public double divisor;
    public int gacount;
    public boolean retrainig = false;
    public BFParams privateParams;
    public LinkedList fcastList = new LinkedList();
    public LinkedList activeList = new LinkedList();
    public LinkedList oldActiveList = new LinkedList();
    public World worldForAgent = new World();
    public ASMModelParams modelParams = new ASMModelParams();
    public final double WEIGHTED = 0.0D;
    public static BFParams params;
    static double minstrength;


    BFagent(Graph g) {
        super(g);
    }

    public final double drand() {
        return Sim.getRnd().getDblFromTo(0.0D, 1.0D);
    }

    public final double urand() {
        return Sim.getRnd().getDblFromTo(-1.0D, 1.0D);
    }

    public int irand(int x) {
        return Sim.getRnd().getIntFromTo(0, x - 1);
    }

    public static void setBFParameterObject(BFParams x) {
        params = x;
    }

    public static void init() {
        BFCast.init();
    }

    public Object initForecasts() {
        int sumspecificity = 0;
        this.privateParams = params.copy();
        int numfcasts = BFParams.numfcasts;
        this.avspecificity = 0.0D;
        this.gacount = 0;
        this.variance = BFParams.initvar;
        this.getPriceFromWorld();
        this.getDividendFromWorld();
        this.global_mean = this.price + this.dividend;
        this.forecast = this.lforecast = this.global_mean;
        this.fcastList.add(0, this.createNewForecast());

        int i;
        BFCast aForecast;
        for (i = 1; i < numfcasts; ++i) {
            aForecast = this.createNewForecast();
            this.setConditionsRandomly(aForecast);
            this.fcastList.add(i, aForecast);
        }

        for (i = 1; i < numfcasts; ++i) {
            aForecast = (BFCast) this.fcastList.get(i);
            sumspecificity += aForecast.getSpecificity();
        }

        this.avspecificity = (double) sumspecificity / (double) numfcasts;
        return this;
    }

    public Object initForecasts2() {
        int sumspecificity = 0;
        this.privateParams = new BFParams();
        this.privateParams.init();
        int numfcasts = BFParams.numfcasts;
        this.avspecificity = 0.0D;
        this.gacount = 0;
        this.variance = BFParams.initvar;
        this.getPriceFromWorld();
        this.getDividendFromWorld();
        this.global_mean = this.price + this.dividend;
        this.forecast = this.lforecast = this.global_mean;
        this.fcastList.add(0, this.createNewForecast());

        int i;
        BFCast aForecast;
        for (i = 1; i < numfcasts; ++i) {
            aForecast = this.createNewForecast();
            this.setConditionsRandomly(aForecast);
            this.fcastList.add(i, aForecast);
        }

        for (i = 1; i < numfcasts; ++i) {
            aForecast = (BFCast) this.fcastList.get(i);
            sumspecificity += aForecast.getSpecificity();
        }

        this.avspecificity = (double) sumspecificity / (double) numfcasts;
        return this;
    }

    public BFCast createNewForecast() {
        double abase = BFParams.a_min + 0.5D * (1.0D - BFParams.subrange) * BFParams.a_range;
        double bbase = BFParams.b_min + 0.5D * (1.0D - BFParams.subrange) * BFParams.b_range;
        double cbase = BFParams.c_min + 0.5D * (1.0D - BFParams.subrange) * BFParams.c_range;
        double asubrange = BFParams.subrange * BFParams.a_range;
        double bsubrange = BFParams.subrange * BFParams.b_range;
        double csubrange = BFParams.subrange * BFParams.c_range;
        BFCast aForecast = new BFCast();
        aForecast.setCondwords(BFParams.condwords);
        aForecast.setCondbits(BFParams.condbits);
        aForecast.setNNulls(BFParams.nnulls);
        aForecast.setBitcost(BFParams.bitcost);
        aForecast.createEnd();
        aForecast.setForecast(0.0D);
        aForecast.setLforecast(this.global_mean);
        aForecast.setVariance(BFParams.newfcastvar);
        aForecast.setStrength(0.0D);
        aForecast.setAval(abase + this.drand() * asubrange);
        aForecast.setBval(bbase + this.drand() * bsubrange);
        aForecast.setCval(cbase + this.drand() * csubrange);
        return aForecast;
    }

    public void setConditionsRandomly(BFCast fcastObject) {
        double[] problist = new double[BFParams.condbits];
        int[] bitlist = new int[BFParams.condbits];
        System.arraycopy(this.privateParams.getBitListPtr(), 0, bitlist, 0, BFParams.condbits);
        System.arraycopy(this.privateParams.getProbListPtr(), 0, problist, 0, BFParams.condbits);

        for (int bit = 0; bit < BFParams.condbits; ++bit) {
            if (bitlist[bit] < 0) {
                fcastObject.setConditionsbit$FromZeroTo(bit, 3);
            } else if (this.drand() < problist[bit]) {
                fcastObject.setConditionsbit$FromZeroTo(bit, this.irand(2) + 1);
                fcastObject.incrSpecificity();
                fcastObject.updateSpecfactor();
            }
        }

    }

    public Object prepareForTrading2() {
        double forecastvar = 0.0D;
        BitVector myworld = new BitVector();
        myworld.setCondwords(BFParams.condwords);
        myworld.setCondbits(BFParams.condbits);
        myworld.createEnd();
        this.currentTime = (int) Sim.getAbsoluteTime();
        if (ASMModelParams.RedQueen && this.getWealth() < this.worldForAgent.getAverageWealth() && this.currentTime >= BFParams.firstgatime) {
            this.performGA();
            this.retrainig = true;
        } else if (!ASMModelParams.RedQueen && this.currentTime >= BFParams.firstgatime && this.currentTime % BFParams.gafrequency == 0 && this.drand() < BFParams.gaprob) {
            this.performGA();
            this.retrainig = true;
        } else {
            this.retrainig = false;
        }

        this.lforecast = this.forecast;
        myworld = this.collectWorldData2();
        this.updateActiveList(myworld);
        double maxstrength = -1.0E50D;
        BFCast bestForecast = null;
        int nactive = 0;
        int mincount = BFParams.mincount;

        int i;
        BFCast aForecast;
        for (i = 0; i < this.activeList.size(); ++i) {
            aForecast = (BFCast) this.activeList.get(i);
            aForecast.setLastactive(this.currentTime);
            if (aForecast.incrCount() >= mincount) {
                double strength = aForecast.getStrength();
                ++nactive;
                if (strength > maxstrength) {
                    maxstrength = strength;
                    bestForecast = aForecast;
                }
            }
        }

        if (nactive != 0) {
            this.pdcoeff = bestForecast.getAval();
            this.offset = bestForecast.getBval() * this.dividend + bestForecast.getCval();
            if (BFParams.individual != 0) {
                forecastvar = this.variance;
            } else {
                forecastvar = bestForecast.getVariance();
            }
        } else {
            double countsum = 0.0D;
            this.pdcoeff = 0.0D;
            this.offset = 0.0D;
            mincount = BFParams.mincount;

            for (i = 0; i < this.fcastList.size(); ++i) {
                aForecast = (BFCast) this.fcastList.get(i);
                if (aForecast.getCnt() >= mincount) {
                    double weight;
                    countsum += weight = aForecast.getStrength();
                    this.offset += (aForecast.getBval() * this.dividend + aForecast.getCval()) * weight;
                    this.pdcoeff += aForecast.getAval() * weight;
                }

                if (countsum > 0.0D) {
                    this.offset /= countsum;
                    this.pdcoeff /= countsum;
                } else {
                    this.offset = this.global_mean;
                }

                forecastvar = this.variance;
            }
        }

        this.divisor = BFParams.lambda * forecastvar;
        return this;
    }

    public BitVector collectWorldData2() {
        int nworldbits = Agent.worldForAgent.getNumWorldBits();
        int[] bitlist = new int[BFParams.condbits];
        int[] myRealWorld = new int[nworldbits];
        BitVector world = new BitVector();
        world.setCondwords(BFParams.condwords);
        world.setCondbits(BFParams.condbits);
        world.createEnd();
        bitlist = this.privateParams.getBitListPtr();
        Agent.worldForAgent.getRealWorld(myRealWorld);

        for (int i = 0; i < BFParams.condbits; ++i) {
            int n;
            if ((n = bitlist[i]) >= 0) {
                world.setConditionsbit$To(i, myRealWorld[n]);
            }
        }

        return world;
    }

    public boolean changeIntToBoolean(int a) {
        return a != 0;
    }

    public void updateActiveList(BitVector worldvalues) {
        this.copyList$To(this.activeList, this.oldActiveList);
        this.activeList.clear();

        BFCast aForecast;
        int i;
        for (i = 0; i < this.oldActiveList.size(); ++i) {
            aForecast = (BFCast) this.oldActiveList.get(i);
            aForecast.setLforecast(aForecast.getForecast());
        }

        label112:
        switch (BFParams.condwords) {
            case 1:
                i = 0;

                while (true) {
                    if (i >= this.fcastList.size()) {
                        break label112;
                    }

                    aForecast = (BFCast) this.fcastList.get(i);
                    if (!this.changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0))) {
                        this.activeList.add(aForecast);
                    }

                    ++i;
                }
            case 2:
                i = 0;

                while (true) {
                    if (i >= this.fcastList.size()) {
                        break label112;
                    }

                    aForecast = (BFCast) this.fcastList.get(i);
                    if (!this.changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0)) && !this.changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1))) {
                        this.activeList.add(aForecast);
                    }

                    ++i;
                }
            case 3:
                i = 0;

                while (true) {
                    if (i >= this.fcastList.size()) {
                        break label112;
                    }

                    aForecast = (BFCast) this.fcastList.get(i);
                    if (!this.changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0)) && !this.changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1)) && !this.changeIntToBoolean(aForecast.getConditionsWord(2) & worldvalues.getConditionsWord(2))) {
                        this.activeList.add(aForecast);
                    }

                    ++i;
                }
            case 4:
                i = 0;

                while (true) {
                    if (i >= this.fcastList.size()) {
                        break label112;
                    }

                    aForecast = (BFCast) this.fcastList.get(i);
                    if (!this.changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0)) && !this.changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1)) && !this.changeIntToBoolean(aForecast.getConditionsWord(2) & worldvalues.getConditionsWord(2)) && !this.changeIntToBoolean(aForecast.getConditionsWord(3) & worldvalues.getConditionsWord(3))) {
                        this.activeList.add(aForecast);
                    }

                    ++i;
                }
            case 5:
                for (i = 0; i < this.fcastList.size(); ++i) {
                    aForecast = (BFCast) this.fcastList.get(i);
                    if (!this.changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0)) && !this.changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1)) && !this.changeIntToBoolean(aForecast.getConditionsWord(2) & worldvalues.getConditionsWord(2)) && !this.changeIntToBoolean(aForecast.getConditionsWord(3) & worldvalues.getConditionsWord(3)) && !this.changeIntToBoolean(aForecast.getConditionsWord(4) & worldvalues.getConditionsWord(4))) {
                        this.activeList.add(aForecast);
                    }
                }
        }
    }

    public Object getInputValues() {
        return this;
    }

    public Object feedForward() {
        return this;
    }

    public double getDemandAndSlope$forPrice(double slope, double trialprice) {
        this.forecast = (trialprice + this.dividend) * this.pdcoeff + this.offset;
        if (this.forecast >= 0.0D) {
            this.demand = -((trialprice * this.intratep1 - this.forecast) / this.divisor + this.position);
            slope = (this.pdcoeff - this.intratep1) / this.divisor;
        } else {
            this.forecast = 0.0D;
            this.demand = -(trialprice * this.intratep1 / this.divisor + this.position);
            slope = -this.intratep1 / this.divisor;
        }

        if (this.demand > BFParams.maxbid) {
            this.demand = BFParams.maxbid;
            slope = 0.0D;
        } else if (this.demand < -BFParams.maxbid) {
            this.demand = -BFParams.maxbid;
            slope = 0.0D;
        }

        super.constrainDemand(slope, trialprice);
        return this.demand;
    }

    public double getRealForecast() {
        return this.forecast;
    }

    public Object updatePerformance2() {
        double tauv = BFParams.tauv;
        double a = 1.0D / tauv;
        double b = 1.0D - a;
        double av = 0.01D;
        double bv = 1.0D - av;
        if (tauv == 100000.0D) {
            a = 0.0D;
            b = 1.0D;
            av = 0.0D;
            bv = 1.0D;
        }

        double maxdev = BFParams.maxdev;
        this.getPriceFromWorld();
        double ftarget = this.price + this.dividend;
        double deviation;
        this.realDeviation = deviation = ftarget - this.lforecast;
        if (Math.abs(deviation) > maxdev) {
            deviation = maxdev;
        }

        this.global_mean = b * this.global_mean + a * ftarget;
        this.currentTime = (int) Sim.getAbsoluteTime();
        if (this.currentTime < 1) {
            this.variance = BFParams.initvar;
        } else {
            this.variance = bv * this.variance + av * deviation * deviation;
        }

        BFCast aForecast;
        int i;
        for (i = 0; i < this.activeList.size(); ++i) {
            aForecast = (BFCast) this.activeList.get(i);
            aForecast.updateForecastPrice$Dividend(this.price, this.dividend);
        }

        if (this.currentTime > 0) {
            for (i = 0; i < this.oldActiveList.size(); ++i) {
                aForecast = (BFCast) this.oldActiveList.get(i);
                double lastForecast = aForecast.getLforecast();
                deviation = (ftarget - lastForecast) * (ftarget - lastForecast);
                if (deviation > maxdev) {
                    deviation = maxdev;
                }

                if ((double) aForecast.getCnt() > tauv) {
                    aForecast.setVariance(b * aForecast.getVariance() + a * deviation);
                } else {
                    double c = 1.0D / (1.0D + (double) aForecast.getCnt());
                    aForecast.setVariance((1.0D - c) * aForecast.getVariance() + c * deviation);
                }

                aForecast.setStrength(BFParams.maxdev - aForecast.getVariance() + aForecast.getSpecfactor());
            }
        }

        return this;
    }

    public double getDeviation() {
        return Math.abs(this.realDeviation);
    }

    public double getError() {
        return this.divisor / BFParams.lambda;
    }

    public Object updateWeights() {
        return this;
    }

    public int nbits() {
        return BFParams.condbits;
    }

    public int nrules() {
        return BFParams.numfcasts;
    }

    public int lastgatime() {
        return this.lastgatime;
    }

    public Object performGA() {
        double madv = 0.0D;
        double meanv = 0.0D;
        LinkedList newList = new LinkedList();
        int[] bitlist = new int[BFParams.condbits];
        LinkedList rejectList = new LinkedList();
        ++this.gacount;
        this.currentTime = (int) Sim.getAbsoluteTime();
        this.lastgatime = this.currentTime;
        bitlist = BFParams.bitlist;
        this.MakePool$From(rejectList, this.fcastList);
        double sumc = 0.0D;
        double avc = 0.0D;
        double avb = 0.0D;
        double ava = 0.0D;
        double avstrength = 0.0D;
        minstrength = 1.0E20D;

        int f;
        double varvalue;
        for (f = 0; f < BFParams.numfcasts; ++f) {
            BFCast specificity = (BFCast) this.fcastList.get(f);
            varvalue = 0.0D;
            varvalue = specificity.getVariance();
            meanv += varvalue;
            if (specificity.getCnt() > 0) {
                if (varvalue != 0.0D) {
                    avstrength += ((BFCast) this.fcastList.get(f)).getStrength();
                    sumc += 1.0D / varvalue;
                    ava += specificity.getAval() / varvalue;
                    avb += specificity.getBval() / varvalue;
                    avc += specificity.getCval() / varvalue;
                }

                double temp;
                if ((temp = specificity.getStrength()) < minstrength) {
                    minstrength = temp;
                }
            }
        }

        meanv /= (double) BFParams.numfcasts;

        for (f = 0; f < BFParams.numfcasts; ++f) {
            madv += Math.abs(((BFCast) this.fcastList.get(f)).getVariance()) - meanv;
        }

        madv /= (double) BFParams.numfcasts;
        ((BFCast) this.fcastList.get(0)).setAval(ava / sumc);
        ((BFCast) this.fcastList.get(0)).setBval(avb / sumc);
        ((BFCast) this.fcastList.get(0)).setCval(avc / sumc);
        avstrength /= (double) BFParams.numfcasts;

        BFCast parent1;
        for (int new2 = 0; new2 < BFParams.nnew; ++new2) {
            boolean var30 = false;
            double altvarvalue = 9.99999999E8D;
            BFCast aNewForecast = this.createNewForecast();
            aNewForecast.updateSpecfactor();
            aNewForecast.setStrength(avstrength);
            aNewForecast.setLastactive(this.currentTime);
            varvalue = BFParams.maxdev - avstrength + aNewForecast.getSpecfactor();
            aNewForecast.setVariance(varvalue);
            altvarvalue = ((BFCast) this.fcastList.get(0)).getVariance() - madv;
            if (varvalue < altvarvalue) {
                aNewForecast.setVariance(altvarvalue);
                aNewForecast.setStrength(BFParams.maxdev - altvarvalue + aNewForecast.getSpecfactor());
            }

            aNewForecast.setLastactive(this.currentTime);
            newList.add(aNewForecast);

            do {
                parent1 = this.Tournament(this.fcastList);
            } while (parent1 == null);

            if (this.drand() < BFParams.pcrossover) {
                BFCast parent2;
                do {
                    do {
                        parent2 = this.Tournament(this.fcastList);
                    } while (parent2 == parent1);
                } while (parent2 == null);

                this.Crossover$Parent1$Parent2(aNewForecast, parent1, parent2);
                if (aNewForecast == null) {
                    System.out.println("got nil back from crossover");
                }

                var30 = true;
            } else {
                this.CopyRule$From(aNewForecast, parent1);
                if (aNewForecast == null) {
                    System.out.println("got nil back from CopyRule");
                }

                this.Mutate$Status(aNewForecast, var30);
            }
        }

        this.TransferFcastsFrom$To$Replace(newList, this.fcastList, rejectList);
        this.Generalize$AvgStrength(this.fcastList, avstrength);
        int var31 = 0;

        for (f = 0; f < BFParams.numfcasts; ++f) {
            parent1 = (BFCast) this.fcastList.get(0);
            var31 += parent1.getSpecificity();
        }

        this.avspecificity = (double) var31 / (double) BFParams.numfcasts;
        newList.clear();
        return this;
    }

    public BFCast CopyRule$From(BFCast to, BFCast from) {
        to.setForecast(from.getForecast());
        to.setLforecast(from.getLforecast());
        to.setVariance(from.getVariance());
        to.setStrength(from.getStrength());
        to.setAval(from.getAval());
        to.setBval(from.getBval());
        to.setCval(from.getCval());
        to.setSpecfactor(from.getSpecfactor());
        to.setLastactive(from.getLastactive());
        to.setSpecificity(from.getSpecificity());
        to.setConditions(from.getConditions());
        to.setCnt(from.getCnt());
        if (from.getCnt() == 0) {
            to.setStrength(minstrength);
        }

        return to;
    }

    public void MakePool$From(LinkedList rejects, LinkedList list) {
        int j = 0;
        int top = -1;

        int i;
        BFCast aForecast;
        BFCast aReject;
        for (i = 1; i < BFParams.npool; ++i) {
            aForecast = (BFCast) list.get(i);

            for (j = top; j >= 0 && (aReject = (BFCast) rejects.get(j)) != null && aForecast.getStrength() < aReject.getStrength(); --j) {
                rejects.add(j + 1, aReject);
            }

            rejects.add(j + 1, aForecast);
            ++top;
        }

        while (i < BFParams.numfcasts) {
            aForecast = (BFCast) list.get(i);
            if (aForecast.getStrength() < ((BFCast) rejects.get(top)).getStrength()) {
                for (j = top - 1; j >= 0 && (aReject = (BFCast) rejects.get(j)) != null && aForecast.getStrength() < aReject.getStrength(); --j) {
                    rejects.add(j + 1, aReject);
                }
            }

            rejects.add(j + 1, aForecast);
            ++i;
        }

    }

    public BFCast Tournament(LinkedList list) {
        int numfcasts = list.size();
        BFCast candidate1 = (BFCast) list.get(this.irand(numfcasts));

        BFCast candidate2;
        do {
            candidate2 = (BFCast) list.get(this.irand(numfcasts));
        } while (candidate2 == candidate1);

        return candidate1.getStrength() > candidate2.getStrength() ? candidate1 : candidate2;
    }

    public boolean Mutate$Status(BFCast new2, boolean changed) {
        boolean bitchanged = false;
        int[] bitlist = new int[BFParams.condbits];
        bitlist = this.privateParams.getBitListPtr();
        if (BFParams.pmutation > 0.0D) {
            for (int bit = 0; bit < BFParams.condbits; ++bit) {
                if (bitlist[bit] >= 0 && this.drand() < BFParams.pmutation) {
                    if (new2.getConditionsbit(bit) > 0) {
                        if (this.irand(3) > 0) {
                            new2.maskConditionsbit(bit);
                            new2.decrSpecificity();
                        } else {
                            new2.switchConditionsbit(bit);
                        }

                        changed = true;
                        bitchanged = true;
                    } else if (this.irand(3) > 0) {
                        new2.setConditionsbit$FromZeroTo(bit, this.irand(2) + 1);
                        new2.incrSpecificity();
                        changed = true;
                        bitchanged = true;
                    }
                }
            }
        }

        double choice = this.drand();
        double temp;
        if (choice < BFParams.plong) {
            new2.setAval(BFParams.a_min + BFParams.a_range * this.drand());
            changed = true;
        } else if (choice < BFParams.plong + BFParams.pshort) {
            temp = new2.getAval() + BFParams.a_range * BFParams.nhood * this.urand();
            new2.setAval(temp > BFParams.a_max ? BFParams.a_max : (temp < BFParams.a_min ? BFParams.a_min : temp));
            changed = true;
        }

        choice = this.drand();
        if (choice < BFParams.plong) {
            new2.setBval(BFParams.b_min + BFParams.b_range * this.drand());
            changed = true;
        } else if (choice < BFParams.plong + BFParams.pshort) {
            temp = new2.getBval() + BFParams.b_range * BFParams.nhood * this.urand();
            new2.setBval(temp > BFParams.b_max ? BFParams.b_max : (temp < BFParams.b_min ? BFParams.b_min : temp));
            changed = true;
        }

        choice = this.drand();
        if (choice < BFParams.plong) {
            new2.setCval(BFParams.c_min + BFParams.c_range * this.drand());
            changed = true;
        } else if (choice < BFParams.plong + BFParams.pshort) {
            temp = new2.getCval() + BFParams.c_range * BFParams.nhood * this.urand();
            new2.setCval(temp > BFParams.c_max ? BFParams.c_max : (temp < BFParams.c_min ? BFParams.c_min : temp));
            changed = true;
        }

        new2.setCnt(0);
        if (changed) {
            new2.updateSpecfactor();
        }

        return changed;
    }

    public BFCast Crossover$Parent1$Parent2(BFCast newForecast, BFCast parent1, BFCast parent2) {
        newForecast.setSpecificity(0);

        for (int word = 0; word < BFParams.condwords; ++word) {
            newForecast.setConditionsWord$To(word, 0);
        }

        int bit;
        for (bit = 0; bit < BFParams.condbits; ++bit) {
            int newcond;
            if (this.irand(2) == 0) {
                newcond = parent1.getConditionsbit(bit);
                newForecast.setConditionsbit$FromZeroTo(bit, newcond);
                if (newcond > 0) {
                    newForecast.incrSpecificity();
                }
            } else {
                newcond = parent2.getConditionsbit(bit);
                newForecast.setConditionsbit$FromZeroTo(bit, newcond);
                if (newcond > 0) {
                    newForecast.incrSpecificity();
                }
            }
        }

        double choice = this.drand();
        if (choice < BFParams.plinear) {
            double weight1 = parent1.getStrength() / (parent1.getStrength() + parent2.getStrength());
            double weight2 = 1.0D - weight1;
            newForecast.setAval(weight1 * parent1.getAval() + weight2 * parent2.getAval());
            newForecast.setBval(weight1 * parent1.getBval() + weight2 * parent2.getBval());
            newForecast.setCval(weight1 * parent1.getCval() + weight2 * parent2.getCval());
        } else if (choice < BFParams.plinear + BFParams.prandom) {
            if (this.irand(2) != 0) {
                newForecast.setAval(parent1.getAval());
            } else {
                newForecast.setAval(parent2.getAval());
            }

            if (this.irand(2) != 0) {
                newForecast.setBval(parent1.getBval());
            } else {
                newForecast.setBval(parent2.getBval());
            }

            if (this.irand(2) != 0) {
                newForecast.setCval(parent1.getCval());
            } else {
                newForecast.setCval(parent2.getCval());
            }
        } else if (this.irand(2) != 0) {
            newForecast.setAval(parent1.getAval());
            newForecast.setBval(parent1.getBval());
            newForecast.setCval(parent1.getCval());
        } else {
            newForecast.setAval(parent2.getAval());
            newForecast.setBval(parent2.getBval());
            newForecast.setCval(parent2.getCval());
        }

        int specificity = 0;
        newForecast.setCnt(0);
        newForecast.updateSpecfactor();
        newForecast.setStrength(0.5D * (parent1.getStrength() + parent2.getStrength()));
        BitVector var14 = newForecast.getConditionsObject();

        for (bit = 0; bit < BFParams.condbits; ++bit) {
            if (var14.getConditionsbit(bit) != 0) {
                ++specificity;
            }
        }

        return newForecast;
    }

    public void TransferFcastsFrom$To$Replace(LinkedList newList, LinkedList forecastList, LinkedList rejects) {
        for (int i = 0; i < newList.size(); ++i) {
            BFCast aForecast = (BFCast) newList.get(i);
            BFCast toDieForecast = this.GetMort$Rejects(aForecast, rejects);
            this.CopyRule$From(toDieForecast, aForecast);
        }

    }

    public BFCast GetMort$Rejects(BFCast new2, LinkedList rejects) {
        int[] cond1 = new int[BFParams.condwords];
        int[] cond2 = new int[BFParams.condwords];
        int[] newcond = new int[BFParams.condwords];
        boolean bitmax = false;
        int numrejects = BFParams.npool;

        int r1;
        do {
            r1 = this.irand(numrejects);
        } while (rejects.get(r1) == null);

        int r2;
        do {
            do {
                r2 = this.irand(numrejects);
            } while (r1 == r2);
        } while (rejects.get(r2) == null);

        cond1 = ((BFCast) rejects.get(r1)).getConditions();
        cond2 = ((BFCast) rejects.get(r2)).getConditions();
        newcond = new2.getConditions();
        int different1 = 0;
        int different2 = 0;
        int var17 = 16;

        for (int word = 0; word < BFParams.condwords; ++word) {
            int temp1 = cond1[word] ^ newcond[word];
            int temp2 = cond2[word] ^ newcond[word];
            if (word == BFParams.condwords - 1) {
                var17 = (BFParams.condbits - 1 & 15) + 1;
            }

            for (int bit = 0; bit < var17; ++bit) {
                if ((temp1 & 3) != 0) {
                    ++different1;
                }

                if ((temp2 & 3) != 0) {
                    ++different2;
                }

                temp1 >>= 2;
                temp2 >>= 2;
            }
        }

        BFCast aReject;
        if (different1 < different2) {
            aReject = (BFCast) rejects.get(r1);
            rejects.add(r1, (Object) null);
        } else {
            aReject = (BFCast) rejects.get(r2);
            rejects.add(r2, (Object) null);
        }

        return aReject;
    }

    public void Generalize$AvgStrength(LinkedList list, double avgstrength) {
        int[] bitlist = new int[BFParams.condbits];
        bitlist = this.privateParams.getBitListPtr();
        this.currentTime = (int) Sim.getAbsoluteTime();

        for (int f = 0; f < BFParams.numfcasts; ++f) {
            BFCast aForecast = (BFCast) list.get(f);
            if (this.currentTime - aForecast.getLastactive() > BFParams.longtime) {
                boolean changed = false;
                int j = (int) Math.ceil((double) aForecast.getSpecificity() * BFParams.genfrac);

                while (j > 0) {
                    int bit = this.irand(BFParams.condbits);
                    if (bitlist[bit] >= 0 && aForecast.getConditionsbit(bit) > 0) {
                        aForecast.maskConditionsbit(bit);
                        aForecast.decrSpecificity();
                        changed = true;
                        --j;
                    }
                }

                if (changed) {
                    aForecast.setCnt(0);
                    aForecast.setLastactive(this.currentTime);
                    aForecast.updateSpecfactor();
                    double varvalue = BFParams.maxdev - avgstrength + aForecast.getSpecfactor();
                    if (varvalue > 0.0D) {
                        aForecast.setVariance(varvalue);
                    }

                    aForecast.setStrength(avgstrength);
                }
            }
        }

    }

    public void copyList$To(LinkedList list, LinkedList outputList) {
        outputList.clear();

        for (int i = 0; i < list.size(); ++i) {
            outputList.add(i, list.get(i));
        }

    }

    public double getDoubleValue(int valueId) {
        switch (valueId) {
            case 0:
                return this.getWealth();
            case 1:
                return this.position;
            default:
                throw new UnsupportedOperationException("Bad argument");
        }
    }
}
