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

    public final double WEIGHTED = 0.0D;

    public static BFParams params;

    static double minstrength;

    public static void setBFParameterObject(BFParams x) {
        params = x;
    }

    public static void init() {
    }

    public Object initForecasts() {
        int sumspecificity = 0;

        this.privateParams = params.copy();

        int numfcasts = BFParams.numfcasts;

        this.avspecificity = 0.0D;
        this.gacount = 0;

        this.variance = BFParams.initvar;
        getPriceFromWorld();
        getDividendFromWorld();
        this.global_mean = (this.price + this.dividend);
        this.forecast = (this.lforecast = this.global_mean);

        this.fcastList.add(0, createNewForecast());


        for (int i = 1; i < numfcasts; i++) {
            BFCast aForecast = createNewForecast();
            setConditionsRandomly(aForecast);
            this.fcastList.add(i, aForecast);
        }


        int i;
        for (i = 1; i < numfcasts; i++) {
            BFCast aForecast = (BFCast) this.fcastList.get(i);
            sumspecificity += aForecast.getSpecificity();
        }

        this.avspecificity = (sumspecificity / numfcasts);
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
        getPriceFromWorld();
        getDividendFromWorld();
        this.global_mean = (this.price + this.dividend);
        this.forecast = (this.lforecast = this.global_mean);

        this.fcastList.add(0, createNewForecast());


        for (int i = 1; i < numfcasts; i++) {
            BFCast aForecast = createNewForecast();
            setConditionsRandomly(aForecast);
            this.fcastList.add(i, aForecast);
        }


        for (int i = 1; i < numfcasts; i++) {
            BFCast aForecast = (BFCast) this.fcastList.get(i);
            sumspecificity += aForecast.getSpecificity();
        }

        this.avspecificity = (sumspecificity / numfcasts);
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


        aForecast.setAval(abase + drand() * asubrange);
        aForecast.setBval(bbase + drand() * bsubrange);
        aForecast.setCval(cbase + drand() * csubrange);

        return aForecast;
    }

    public Object setConditionsRandomly(BFCast fcastObject) {
        double[] problist = new double[BFParams.condbits];
        int[] bitlist = new int[BFParams.condbits];

        System.arraycopy(this.privateParams.getBitListPtr(), 0, bitlist, 0, BFParams.condbits);
        System.arraycopy(this.privateParams.getProbListPtr(), 0, problist, 0, BFParams.condbits);

        for (int bit = 0; bit < BFParams.condbits; bit++) {
            if (bitlist[bit] < 0) {
                fcastObject.setConditionsbit$FromZeroTo(bit, 3);
            } else if (drand() < problist[bit]) {
                fcastObject.setConditionsbit$FromZeroTo(bit, irand(2) + 1);

                fcastObject.incrSpecificity();
                fcastObject.updateSpecfactor();
            }
        }
        return this;
    }

    public Object prepareForTrading2() {
        double forecastvar = 0.0D;


        BitVector myworld = new BitVector();
        myworld.setCondwords(BFParams.condwords);
        myworld.setCondbits(BFParams.condbits);
        myworld.createEnd();


        this.currentTime = ((int) Sim.getAbsoluteTime());


        if ((ASMModelParams.RedQueen) && (getWealth() < this.worldForAgent.getAverageWealth()) && (this.currentTime >= BFParams.firstgatime)) {
            performGA();
            this.retrainig = true;
        } else if ((!ASMModelParams.RedQueen) && (this.currentTime >= BFParams.firstgatime) && (this.currentTime % BFParams.gafrequency == 0) && (drand() < BFParams.gaprob)) {
            performGA();
            this.retrainig = true;
        } else {
            this.retrainig = false;
        }


        this.lforecast = this.forecast;

        myworld = collectWorldData2();

        updateActiveList(myworld);


        double maxstrength = -1.0E50D;
        BFCast bestForecast = null;
        int nactive = 0;
        int mincount = BFParams.mincount;


        for (int i = 0; i < this.activeList.size(); i++) {
            BFCast aForecast = (BFCast) this.activeList.get(i);
            aForecast.setLastactive(this.currentTime);
            if (aForecast.incrCount() >= mincount) {
                double strength = aForecast.getStrength();
                nactive++;
                if (strength > maxstrength) {
                    maxstrength = strength;
                    bestForecast = aForecast;
                }
            }
        }


        if (nactive != 0) {
            this.pdcoeff = bestForecast.getAval();
            this.offset = (bestForecast.getBval() * this.dividend + bestForecast.getCval());
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

            for (int i = 0; i < this.fcastList.size(); i++) {
                BFCast aForecast = (BFCast) this.fcastList.get(i);
                if (aForecast.getCnt() >= mincount) {
                    double weight;
                    countsum += (weight = aForecast.getStrength());
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


        this.divisor = (BFParams.lambda * forecastvar);

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

        for (int i = 0; i < BFParams.condbits; i++) {
            int n;
            if ((n = bitlist[i]) >= 0) {
                world.setConditionsbit$To(i, myRealWorld[n]);
            }
        }

        return world;
    }

    public boolean changeIntToBoolean(int a) {
        if (a != 0)
            return true;
        return false;
    }

    public Object updateActiveList(BitVector worldvalues) {
        copyList$To(this.activeList, this.oldActiveList);

        this.activeList.clear();

        for (int i = 0; i < this.oldActiveList.size(); i++) {
            BFCast aForecast = (BFCast) this.oldActiveList.get(i);
            aForecast.setLforecast(aForecast.getForecast());
        }

        switch (BFParams.condwords) {
            case 1:
                for (int i = 0; i < this.fcastList.size(); i++) {
                    BFCast aForecast = (BFCast) this.fcastList.get(i);
                    if (!changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0))) {


                        this.activeList.add(aForecast);
                    }
                }
                break;

            case 2:
                for (int i = 0; i < this.fcastList.size(); i++) {
                    BFCast aForecast = (BFCast) this.fcastList.get(i);
                    if (!changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0))) {


                        if (!changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1))) {


                            this.activeList.add(aForecast);
                        }
                    }
                }
                break;

            case 3:
                for (int i = 0; i < this.fcastList.size(); i++) {
                    BFCast aForecast = (BFCast) this.fcastList.get(i);
                    if (!changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0))) {

                        if (!changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1))) {
                            if (!changeIntToBoolean(aForecast.getConditionsWord(2) & worldvalues.getConditionsWord(2))) {

                                this.activeList.add(aForecast);
                            }
                        }
                    }
                }
                break;

            case 4:
                for (int i = 0; i < this.fcastList.size(); i++) {
                    BFCast aForecast = (BFCast) this.fcastList.get(i);
                    if (!changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0))) {

                        if (!changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1))) {
                            if (!changeIntToBoolean(aForecast.getConditionsWord(2) & worldvalues.getConditionsWord(2))) {
                                if (!changeIntToBoolean(aForecast.getConditionsWord(3) & worldvalues.getConditionsWord(3))) {
                                    this.activeList.add(aForecast);
                                }
                            }
                        }
                    }
                }
                break;

            case 5:
                for (int i = 0; i < this.fcastList.size(); i++) {
                    BFCast aForecast = (BFCast) this.fcastList.get(i);
                    if (!changeIntToBoolean(aForecast.getConditionsWord(0) & worldvalues.getConditionsWord(0))) {

                        if (!changeIntToBoolean(aForecast.getConditionsWord(1) & worldvalues.getConditionsWord(1))) {
                            if (!changeIntToBoolean(aForecast.getConditionsWord(2) & worldvalues.getConditionsWord(2))) {
                                if (!changeIntToBoolean(aForecast.getConditionsWord(3) & worldvalues.getConditionsWord(3))) {
                                    if (!changeIntToBoolean(aForecast.getConditionsWord(4) & worldvalues.getConditionsWord(4))) {
                                        this.activeList.add(aForecast);
                                    }
                                }
                            }
                        }
                    }
                }
        }
        if (80 > 80) {
            System.out.println("error Too many condition bits (MAXCONDBITS)");
        }

        return this;
    }

    public Object getInputValues() {
        return this;
    }

    public Object feedForward() {
        return this;
    }

    public double getDemandAndSlope$forPrice(double slope, double trialprice) {
        this.forecast = ((trialprice + this.dividend) * this.pdcoeff + this.offset);

        if (this.forecast >= 0.0D) {
            this.demand = (-((trialprice * this.intratep1 - this.forecast) / this.divisor + this.position));
            slope = (this.pdcoeff - this.intratep1) / this.divisor;
        } else {
            this.forecast = 0.0D;
            this.demand = (-(trialprice * this.intratep1 / this.divisor + this.position));
            slope = -this.intratep1 / this.divisor;
        }

        if (this.demand > BFParams.maxbid) {
            this.demand = BFParams.maxbid;
            slope = 0.0D;
        } else if (this.demand < -BFParams.maxbid) {
            this.demand = (-BFParams.maxbid);
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

        getPriceFromWorld();
        double ftarget = this.price + this.dividend;

        double deviation;

        this.realDeviation = (deviation = ftarget - this.lforecast);
        if (Math.abs(deviation) > maxdev) deviation = maxdev;
        this.global_mean = (b * this.global_mean + a * ftarget);

        this.currentTime = ((int) Sim.getAbsoluteTime());
        if (this.currentTime < 1) {
            this.variance = BFParams.initvar;
        } else {
            this.variance = (bv * this.variance + av * deviation * deviation);
        }

        for (int i = 0; i < this.activeList.size(); i++) {
            BFCast aForecast = (BFCast) this.activeList.get(i);
            aForecast.updateForecastPrice$Dividend(this.price, this.dividend);
        }

        if (this.currentTime > 0) {
            for (int i = 0; i < this.oldActiveList.size(); i++) {
                BFCast aForecast = (BFCast) this.oldActiveList.get(i);
                double lastForecast = aForecast.getLforecast();
                deviation = (ftarget - lastForecast) * (ftarget - lastForecast);

                if (deviation > maxdev) deviation = maxdev;
                if (aForecast.getCnt() > tauv) {
                    aForecast.setVariance(b * aForecast.getVariance() + a * deviation);
                } else {
                    double c = 1.0D / (1.0D + aForecast.getCnt());
                    aForecast.setVariance((1.0D - c) * aForecast.getVariance() +
                            c * deviation);
                }

                aForecast.setStrength(BFParams.maxdev -
                        aForecast.getVariance() +
                        aForecast.getSpecfactor());
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

        this.gacount += 1;
        this.currentTime = ((int) Sim.getAbsoluteTime());
        this.lastgatime = this.currentTime;

        bitlist = BFParams.bitlist;

        MakePool$From(rejectList, this.fcastList);
        double sumc;
        double avc;
        double avb;
        double ava;
        double avstrength = ava = avb = avc = sumc = 0.0D;
        minstrength = 1.0E20D;

        for (int f = 0; f < BFParams.numfcasts; f++) {
            BFCast aForecast = (BFCast) this.fcastList.get(f);
            double varvalue = 0.0D;

            varvalue = aForecast.getVariance();
            meanv += varvalue;
            if (aForecast.getCnt() > 0) {
                if (varvalue != 0.0D) {
                    avstrength += ((BFCast) this.fcastList.get(f)).getStrength();
                    sumc += 1.0D / varvalue;
                    ava += aForecast.getAval() / varvalue;
                    avb += aForecast.getBval() / varvalue;
                    avc += aForecast.getCval() / varvalue;
                }
                double temp;
                if ((temp = aForecast.getStrength()) < minstrength) {
                    minstrength = temp;
                }
            }
        }
        meanv /= BFParams.numfcasts;

        for (int f = 0; f < BFParams.numfcasts; f++) {
            madv += Math.abs(((BFCast) this.fcastList.get(f)).getVariance()) - meanv;
        }

        madv /= BFParams.numfcasts;

        ((BFCast) this.fcastList.get(0)).setAval(ava / sumc);
        ((BFCast) this.fcastList.get(0)).setBval(avb / sumc);
        ((BFCast) this.fcastList.get(0)).setCval(avc / sumc);

        avstrength /= BFParams.numfcasts;

        for (int new2 = 0; new2 < BFParams.nnew; new2++) {

            boolean changed = false;
            double altvarvalue = 9.99999999E8D;

            BFCast aNewForecast = createNewForecast();
            aNewForecast.updateSpecfactor();
            aNewForecast.setStrength(avstrength);


            aNewForecast.setLastactive(this.currentTime);
            double varvalue = BFParams.maxdev - avstrength + aNewForecast.getSpecfactor();
            aNewForecast.setVariance(varvalue);
            altvarvalue = ((BFCast) this.fcastList.get(0)).getVariance() - madv;
            if (varvalue < altvarvalue) {
                aNewForecast.setVariance(altvarvalue);
                aNewForecast.setStrength(BFParams.maxdev - altvarvalue + aNewForecast.getSpecfactor());
            }
            aNewForecast.setLastactive(this.currentTime);

            newList.add(aNewForecast);

            BFCast parent1;
            do {
                parent1 = Tournament(this.fcastList);
            }
            while (parent1 == null);

            if (drand() < BFParams.pcrossover) {
                BFCast parent2;
                do {
                    parent2 = Tournament(this.fcastList);
                }
                while ((parent2 == parent1) || (parent2 == null));

                Crossover$Parent1$Parent2(aNewForecast, parent1, parent2);
                if (aNewForecast == null) System.out.println("got nil back from crossover");
                changed = true;
            } else {
                CopyRule$From(aNewForecast, parent1);
                if (aNewForecast == null) System.out.println("got nil back from CopyRule");
                changed = Mutate$Status(aNewForecast, changed);
            }
        }

        TransferFcastsFrom$To$Replace(newList, this.fcastList, rejectList);
        Generalize$AvgStrength(this.fcastList, avstrength);

        int specificity = 0;

        for (int f = 0; f < BFParams.numfcasts; f++) {
            BFCast parent1 = (BFCast) this.fcastList.get(0);
            specificity += parent1.getSpecificity();
        }
        this.avspecificity = (specificity / BFParams.numfcasts);
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
        if (from.getCnt() == 0)
            to.setStrength(minstrength);
        return to;
    }


    public void MakePool$From(LinkedList rejects, LinkedList list) {
        int j = 0;
        int top = -1;

        for (int i = 1; i < BFParams.npool; i++) {
            BFCast aForecast = (BFCast) list.get(i);
            BFCast aReject;
            for (j = top; (j >= 0) && ((aReject = (BFCast) rejects.get(j)) != null) && (aForecast.getStrength() < aReject.getStrength()); j--) {
                // BFCast aReject;
                rejects.add(j + 1, aReject);
            }
            rejects.add(j + 1, aForecast);
            top++;
        }
        for (int i = 0; i < BFParams.numfcasts; i++) {
            BFCast aForecast = (BFCast) list.get(i);
            if (aForecast.getStrength() < ((BFCast) rejects.get(top)).getStrength()) {
                BFCast aReject;
                for (j = top - 1; (j >= 0) && ((aReject = (BFCast) rejects.get(j)) != null) && (aForecast.getStrength() < aReject.getStrength()); j--) {
                    //BFCast aReject;
                    rejects.add(j + 1, aReject);
                }
            }
            rejects.add(j + 1, aForecast);
        }
    }

    public BFCast Tournament(LinkedList list) {
        int numfcasts = list.size();
        BFCast candidate1 = (BFCast) list.get(irand(numfcasts));
        BFCast candidate2;
        do {
            candidate2 = (BFCast) list.get(irand(numfcasts));
        } while (candidate2 == candidate1);

        if (candidate1.getStrength() > candidate2.getStrength()) {
            return candidate1;
        }
        return candidate2;
    }

    public boolean Mutate$Status(BFCast new2, boolean changed) {
        boolean bitchanged = false;
        int[] bitlist = new int[BFParams.condbits];

        bitlist = this.privateParams.getBitListPtr();

        bitchanged = changed;
        if (BFParams.pmutation > 0.0D) {
            for (int bit = 0; bit < BFParams.condbits; bit++) {
                if ((bitlist[bit] >= 0) &&
                        (drand() < BFParams.pmutation)) {


                    if (new2.getConditionsbit(bit) > 0) {
                        if (irand(3) > 0) {


                            new2.maskConditionsbit(bit);
                            new2.decrSpecificity();
                        } else {
                            new2.switchConditionsbit(bit);
                        }
                        bitchanged = changed = true;
                    } else if (irand(3) > 0) {


                        new2.setConditionsbit$FromZeroTo(bit, irand(2) + 1);
                        new2.incrSpecificity();
                        bitchanged = changed = true;
                    }
                }
            }
        }

        double choice = drand();
        if (choice < BFParams.plong) {
            new2.setAval(BFParams.a_min + BFParams.a_range * drand());
            changed = true;
        } else if (choice < BFParams.plong + BFParams.pshort) {
            double temp = new2.getAval() + BFParams.a_range * BFParams.nhood * urand();
            new2.setAval(
                    temp < BFParams.a_min ? BFParams.a_min : temp > BFParams.a_max ? BFParams.a_max : temp);
            changed = true;
        }

        choice = drand();
        if (choice < BFParams.plong) {
            new2.setBval(BFParams.b_min + BFParams.b_range * drand());
            changed = true;
        } else if (choice < BFParams.plong + BFParams.pshort) {
            double temp = new2.getBval() + BFParams.b_range * BFParams.nhood * urand();
            new2.setBval(
                    temp < BFParams.b_min ? BFParams.b_min : temp > BFParams.b_max ? BFParams.b_max : temp);
            changed = true;
        }


        choice = drand();
        if (choice < BFParams.plong) {
            new2.setCval(BFParams.c_min + BFParams.c_range * drand());
            changed = true;
        } else if (choice < BFParams.plong + BFParams.pshort) {
            double temp = new2.getCval() + BFParams.c_range * BFParams.nhood * urand();
            new2.setCval(temp < BFParams.c_min ? BFParams.c_min : temp > BFParams.c_max ? BFParams.c_max : temp);
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

        for (int word = 0; word < BFParams.condwords; word++) {
            newForecast.setConditionsWord$To(word, 0);
        }
        for (int bit = 0; bit < BFParams.condbits; bit++) {
            if (irand(2) == 0) {
                int value = parent1.getConditionsbit(bit);
                newForecast.setConditionsbit$FromZeroTo(bit, value);
                if (value > 0) newForecast.incrSpecificity();
            } else {
                int value = parent2.getConditionsbit(bit);
                newForecast.setConditionsbit$FromZeroTo(bit, value);
                if (value > 0) {
                    newForecast.incrSpecificity();
                }
            }
        }

        double choice = drand();

        if (choice < BFParams.plinear) {

            double weight1 = parent1.getStrength() / (parent1.getStrength() +
                    parent2.getStrength());
            double weight2 = 1.0D - weight1;
            newForecast.setAval(weight1 * parent1.getAval() + weight2 * parent2.getAval());
            newForecast.setBval(weight1 * parent1.getBval() + weight2 * parent2.getBval());
            newForecast.setCval(weight1 * parent1.getCval() + weight2 * parent2.getCval());
        } else if (choice < BFParams.plinear + BFParams.prandom) {

            if (irand(2) != 0)
                newForecast.setAval(parent1.getAval());
            else newForecast.setAval(parent2.getAval());
            if (irand(2) != 0)
                newForecast.setBval(parent1.getBval());
            else newForecast.setBval(parent2.getBval());
            if (irand(2) != 0)
                newForecast.setCval(parent1.getCval());
            else {
                newForecast.setCval(parent2.getCval());
            }
        } else if (irand(2) != 0) {
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
        BitVector newcond = newForecast.getConditionsObject();

        for (int bit = 0; bit < BFParams.condbits; bit++) {

            if (newcond.getConditionsbit(bit) != 0) {
                specificity++;
            }
        }

        return newForecast;
    }

    public void TransferFcastsFrom$To$Replace(LinkedList newList, LinkedList forecastList, LinkedList rejects) {
        for (int i = 0; i < newList.size(); i++) {
            BFCast aForecast = (BFCast) newList.get(i);


            BFCast toDieForecast = GetMort$Rejects(aForecast, rejects);
            toDieForecast = CopyRule$From(toDieForecast, aForecast);
        }
    }

    public BFCast GetMort$Rejects(BFCast new2, LinkedList rejects) {
        int[] cond1 = new int[BFParams.condwords];
        int[] cond2 = new int[BFParams.condwords];
        int[] newcond = new int[BFParams.condwords];

        int bitmax = 0;
        int numrejects = BFParams.npool;

        int r1;
        do {
            r1 = irand(numrejects);
        }
        while (rejects.get(r1) == null);

        int r2;
        do {
            r2 = irand(numrejects);
        }
        while ((r1 == r2) || (rejects.get(r2) == null));

        cond1 = ((BFCast) rejects.get(r1)).getConditions();
        cond2 = ((BFCast) rejects.get(r2)).getConditions();
        newcond = new2.getConditions();

        int different1 = 0;
        int different2 = 0;
        bitmax = 16;

        for (int word = 0; word < BFParams.condwords; word++) {
            int temp1 = cond1[word] ^ newcond[word];
            int temp2 = cond2[word] ^ newcond[word];
            if (word == BFParams.condwords - 1)
                bitmax = (BFParams.condbits - 1 & 0xF) + 1;
            for (int bit = 0; bit < bitmax; bit++) {
                if ((temp1 & 0x3) != 0)
                    different1++;
                if ((temp2 & 0x3) != 0) {
                    different2++;
                }
                temp1 >>= 2;
                temp2 >>= 2;
            }
        }

        BFCast aReject;

        if (different1 < different2) {
            aReject = (BFCast) rejects.get(r1);
            rejects.add(r1, null);

        } else {
            aReject = (BFCast) rejects.get(r2);
            rejects.add(r2, null);
        }
        return aReject;
    }

    public void Generalize$AvgStrength(LinkedList list, double avgstrength) {
        int[] bitlist = new int[BFParams.condbits];
        bitlist = this.privateParams.getBitListPtr();

        this.currentTime = ((int) Sim.getAbsoluteTime());

        for (int f = 0; f < BFParams.numfcasts; f++) {

            BFCast aForecast = (BFCast) list.get(f);
            if (this.currentTime - aForecast.getLastactive() > BFParams.longtime) {
                boolean changed = false;
                int j = (int) Math.ceil(aForecast.getSpecificity() * BFParams.genfrac);
                while (j > 0) {
                    int bit = irand(BFParams.condbits);
                    if (bitlist[bit] >= 0) {
                        if (aForecast.getConditionsbit(bit) > 0) {

                            aForecast.maskConditionsbit(bit);
                            aForecast.decrSpecificity();
                            changed = true;
                            j--;
                        }
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

    public Object copyList$To(LinkedList list, LinkedList outputList) {
        outputList.clear();
        for (int i = 0; i < list.size(); i++) {
            outputList.add(i, list.get(i));
        }
        return this;
    }

    public double getDoubleValue(int valueId) {
        switch (valueId) {
            case 0:
                return getWealth();
            case 1:
                return this.position;
        }
        throw new UnsupportedOperationException("Bad argument");
    }
}

/* Location:              M:\pc\downloads\sCCFEA-ASM_beta1.jar!\BFagent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */