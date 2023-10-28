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

    public void init() {
        int[] bits = new int[80];
        boolean USEALLBITS = false;
        int i;
        if (!USEALLBITS) {
            bits[0] = this.ReadBitname("pr/d>1/4", specialbits);
            bits[1] = this.ReadBitname("pr/d>1/2", specialbits);
            bits[2] = this.ReadBitname("pr/d>3/4", specialbits);
            bits[3] = this.ReadBitname("pr/d>7/8", specialbits);
            bits[4] = this.ReadBitname("pr/d>1", specialbits);
            bits[5] = this.ReadBitname("pr/d>9/8", specialbits);
            bits[6] = this.ReadBitname("pr/d>5/4", specialbits);
            bits[7] = this.ReadBitname("pr/d>3/2", specialbits);
            bits[8] = this.ReadBitname("pr/d>2", specialbits);
            bits[9] = this.ReadBitname("pr/d>4", specialbits);
            bits[10] = this.ReadBitname("p>p5", specialbits);
            bits[11] = this.ReadBitname("p>p20", specialbits);
            bits[12] = this.ReadBitname("p>p100", specialbits);
            bits[13] = this.ReadBitname("p>p500", specialbits);
            bits[14] = this.ReadBitname("on", specialbits);
            bits[15] = this.ReadBitname("off", specialbits);
        } else {
            condbits = 60;

            i = 0;
            while (i < condbits) {
                bits[i] = i++;
            }
        }

        for (i = 0; i < condbits; ++i) {
            bitlist[i] = bits[i];
            problist[i] = bitprob;
        }

        condwords = (condbits + 15) / 16;
        if (1.0D + bitcost * (double) (condbits - nnulls) <= 0.0D) {
            System.out.println("The bitcost is too negative.");
        }

        a_range = a_max - a_min;
        b_range = b_max - b_min;
        c_range = c_max - c_min;
        npool = (int) ((double) numfcasts * poolfrac + 0.5D);
        nnew = (int) ((double) numfcasts * newfrac + 0.5D);
        if (npool > npoolmax) {
            npoolmax = npool;
        }

        if (nnew > nnewmax) {
            nnewmax = nnew;
        }

        if (condwords > ncondmax) {
            ncondmax = condwords;
        }

    }

    public int[] getBitListPtr() {
        return bitlist;
    }

    public void copyBitList$Length(int[] x, int size) {
        if (size >= 0) {
            System.arraycopy(x, 0, bitlist, 0, size);
        }
    }

    public double[] getProbListPtr() {
        return problist;
    }

    public void copyProbList$Length(double[] p, int size) {
        if (size >= 0) {
            System.arraycopy(p, 0, problist, 0, size);
        }
    }

    public int ReadBitname(String variable, KeyTable[] table) {
        return World.bitNumberOf(variable);
    }

    public BFParams copy() {
        BFParams bfParams = new BFParams(true);
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
        specialbits[8] = new KeyTable((String) null, -6);
    }

    BFParams(boolean a) {
    }
}
