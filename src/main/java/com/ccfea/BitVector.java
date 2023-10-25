package com.ccfea;

public class BitVector {
    int condwords;
    int condbits;
    int[] conditions;
    public static final int MAXCONDBITS = 80;
    public static int[] SHIFT = new int[80];
    public static int[] MASK = new int[80];
    public static int[] NMASK = new int[80];

    public int WORD(int bit) {
        int a = bit >> 4;
        return a;
    }


    public Object createEnd() {
        if (this.condwords == 0) {
            System.out.println("Must have condwords to create BFCast. 1");
        }
        this.conditions = new int[this.condwords];
        for (int i = 0; i < this.condwords; i++)
            this.conditions[i] = 0;
        return this;
    }

    public static void init() {
    }

    public void setCondwords(int x) {
        this.condwords = x;
    }

    public void setCondbits(int x) {
        this.condbits = x;
    }

    public void setConditions(int[] x) {
        for (int i = 0; i < this.condwords; i++) {
            this.conditions[i] = x[i];
        }
    }

    public int[] getConditions() {
        return this.conditions;
    }

    public void setConditionsWord$To(int i, int value) {
        this.conditions[i] = value;
    }

    public int getConditionsWord(int i) {
        return this.conditions[i];
    }

    public void setConditionsbit$To(int bit, int x) {
        this.conditions[WORD(bit)] = (this.conditions[WORD(bit)] & NMASK[bit] | x << SHIFT[bit]);
    }

    public void setConditionsbit$FromZeroTo(int bit, int x) {
        this.conditions[WORD(bit)] |= x << SHIFT[bit];
    }

    public int getConditionsbit(int bit) {
        int value = this.conditions[WORD(bit)] >> SHIFT[bit] & 0x3;
        return value;
    }

    public void setConditionsbitToThree(int bit) {
        this.conditions[WORD(bit)] |= MASK[bit];
    }

    public void maskConditionsbit(int bit) {
        this.conditions[WORD(bit)] &= NMASK[bit];
    }

    public void switchConditionsbit(int bit) {
        this.conditions[WORD(bit)] ^= MASK[bit];
    }

    public static void makebittables() {
        for (int bit = 0; bit < 80; bit++) {
            SHIFT[bit] = (bit % 16 * 2);
            MASK[bit] = (3 << SHIFT[bit]);
            MASK[bit] ^= 0xFFFFFFFF;
        }
    }
}