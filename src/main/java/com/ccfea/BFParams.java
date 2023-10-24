package com.ccfea;

public class BFParams {
    public static int numfcasts = 100;
    public static int condwords;
    public static int condbits = 16;
    public static int mincount = 5;
    public static int gafrequency = 250;
    public static int firstgatime = 250;
    public static int longtime = 250;
    public static int individual = 0;
    public static double tauv = 75.0D;
    public static double lambda = 0.5D;
    public static double maxbid = 10.0D;
    public static double bitprob = 0.1D;
    public static double subrange = 0.5D;
    public static double a_min = 0.7D;
    public static double a_max = 1.2D;
    public static double b_min = 0.0D;
    public static double b_max = 0.0D;
    public static double c_min = -7.293691545D;
    public static double c_max = 21.70630846D;
    public static double a_range;
    public static double b_range;
    public static double c_range;
    public static double newfcastvar = 3.999769641D;
    public static double initvar = 3.999769641D;
    public static double bitcost = 0.01D;
    public static double maxdev = 100.0D;
    public static double poolfrac = 0.1D;
    public static double newfrac = 0.05D;
    public static double pcrossover = 0.3D;
    public static double plinear = 0.333D;
    public static double prandom = 0.333D;
    public static double pmutation = 0.01D;
    public static double plong = 0.05D;
    public static double pshort = 0.2D;
    public static double nhood = 0.05D;
    public static double genfrac = 0.1D;
    public static double gaprob = 1.0D;
    public static int npool;
    public static int nnew;
    public static int nnulls = 0;
    public static int[] bitlist = new int[condbits];
    public static double[] problist = new double[condbits];

    public static int npoolmax = -1;
    public static int nnewmax = -1;
    public static int ncondmax = -1;

    public static final int MAXCONDBITS = 80;

    public static final int ENDLIST = -2;

    public static final int ALL = -3;

    public static final int SETPROB = -4;
    public static final int BADINPUT = -5;
    public static final int NOTFOUND = -6;
    public static final int EQ = 0;
    public static final int NULLBIT = -1;
    public static KeyTable[] specialbits = new KeyTable[9];

    public int WORD(int bit) {
        int a = bit >> 4;
        return a;
    }

    public Object init() {
        int[] bits = new int[80];

        boolean USEALLBITS = false;

        if (!USEALLBITS) {
            bits[0] = ReadBitname("pr/d>1/4", specialbits);
            bits[1] = ReadBitname("pr/d>1/2", specialbits);
            bits[2] = ReadBitname("pr/d>3/4", specialbits);
            bits[3] = ReadBitname("pr/d>7/8", specialbits);
            bits[4] = ReadBitname("pr/d>1", specialbits);
            bits[5] = ReadBitname("pr/d>9/8", specialbits);
            bits[6] = ReadBitname("pr/d>5/4", specialbits);
            bits[7] = ReadBitname("pr/d>3/2", specialbits);
            bits[8] = ReadBitname("pr/d>2", specialbits);
            bits[9] = ReadBitname("pr/d>4", specialbits);
            bits[10] = ReadBitname("p>p5", specialbits);
            bits[11] = ReadBitname("p>p20", specialbits);
            bits[12] = ReadBitname("p>p100", specialbits);
            bits[13] = ReadBitname("p>p500", specialbits);
            bits[14] = ReadBitname("on", specialbits);
            bits[15] = ReadBitname("off", specialbits);
        } else {
            condbits = 60;

            for (int i = 0; i < condbits; i++) {
                bits[i] = i;
            }
        }
        for (int i = 0; i < condbits; i++) {
            bitlist[i] = bits[i];

            problist[i] = bitprob;
        }

        condwords = (condbits + 15) / 16;

        if (1.0D + bitcost * (condbits - nnulls) <= 0.0D) {
            System.out.println("The bitcost is too negative.");
        }

        a_range = a_max - a_min;
        b_range = b_max - b_min;
        c_range = c_max - c_min;

        npool = (int) (numfcasts * poolfrac + 0.5D);
        nnew = (int) (numfcasts * newfrac + 0.5D);


        if (npool > npoolmax) npoolmax = npool;
        if (nnew > nnewmax) nnewmax = nnew;
        if (condwords > ncondmax) {
            ncondmax = condwords;
        }
        return this;
    }

    public int[] getBitListPtr() {
        return bitlist;
    }

    public void copyBitList$Length(int[] x, int size) {
        for (int i = 0; i < size; i++) {
            bitlist[i] = x[i];
        }
    }

    public double[] getProbListPtr() {
        return problist;
    }

    public void copyProbList$Length(double[] p, int size) {
        for (int i = 0; i < size; i++) {
            problist[i] = p[i];
        }
    }

    public int ReadBitname(String variable, KeyTable[] table) {
        int n = World.bitNumberOf(variable);


        return n;
    }

    public BFParams copy() {
        BFParams bfParams = new BFParams(true);

        numfcasts = numfcasts;
        condwords = condwords;
        condbits = condbits;
        mincount = mincount;
        gafrequency = gafrequency;
        firstgatime = firstgatime;
        longtime = longtime;
        individual = individual;
        tauv = tauv;
        lambda = lambda;
        maxbid = maxbid;
        bitprob = bitprob;
        subrange = subrange;
        a_min = a_min;
        a_max = a_max;
        b_min = b_min;
        b_max = b_max;
        c_min = c_min;
        c_max = c_max;
        a_range = a_range;
        b_range = b_range;
        c_range = c_range;
        newfcastvar = newfcastvar;
        initvar = initvar;
        bitcost = bitcost;
        maxdev = maxdev;
        poolfrac = poolfrac;
        newfrac = newfrac;
        pcrossover = pcrossover;
        plinear = plinear;
        prandom = prandom;
        pmutation = pmutation;
        plong = plong;
        pshort = pshort;
        nhood = nhood;
        genfrac = genfrac;

        npool = npool;
        nnew = nnew;
        nnulls = nnulls;
        npoolmax = npoolmax;
        nnewmax = nnewmax;
        ncondmax = ncondmax;

        bfParams.copyBitList$Length(bitlist, condbits);
        bfParams.copyProbList$Length(problist, condbits);
        return bfParams;
    }

    BFParams() {
        specialbits[0] = new KeyTable("null", -1);
        specialbits[1] = new KeyTable("end", -2);
        specialbits[2] = new KeyTable(".", -2);
        specialbits[3] = new KeyTable("all", -3);
        specialbits[4] = new KeyTable("allbits", -3);
        specialbits[5] = new KeyTable("p", -4);
        specialbits[6] = new KeyTable("P", -4);
        specialbits[7] = new KeyTable("???", -5);
        specialbits[8] = new KeyTable(null, -6);
    }

    BFParams(boolean a) {
    }
}

/* Location:              M:\pc\downloads\sCCFEA-ASM_beta1.jar!\BFParams.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */