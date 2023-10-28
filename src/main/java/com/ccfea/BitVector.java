/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
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
      if(this.condwords == 0) {
         System.out.println("Must have condwords to create BFCast. 1");
      }

      this.conditions = new int[this.condwords];

      for(int i = 0; i < this.condwords; ++i) {
         this.conditions[i] = 0;
      }

      return this;
   }

   public static void init() {
      makebittables();
   }

   public void setCondwords(int x) {
      this.condwords = x;
   }

   public void setCondbits(int x) {
      this.condbits = x;
   }

   public void setConditions(int[] x) {
      for(int i = 0; i < this.condwords; ++i) {
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
      this.conditions[this.WORD(bit)] = this.conditions[this.WORD(bit)] & NMASK[bit] | x << SHIFT[bit];
   }

   public void setConditionsbit$FromZeroTo(int bit, int x) {
      int[] var10000 = this.conditions;
      int var10001 = this.WORD(bit);
      var10000[var10001] |= x << SHIFT[bit];
   }

   public int getConditionsbit(int bit) {
      int value = this.conditions[this.WORD(bit)] >> SHIFT[bit] & 3;
      return value;
   }

   public void setConditionsbitToThree(int bit) {
      int[] var10000 = this.conditions;
      int var10001 = this.WORD(bit);
      var10000[var10001] |= MASK[bit];
   }

   public void maskConditionsbit(int bit) {
      int[] var10000 = this.conditions;
      int var10001 = this.WORD(bit);
      var10000[var10001] &= NMASK[bit];
   }

   public void switchConditionsbit(int bit) {
      int[] var10000 = this.conditions;
      int var10001 = this.WORD(bit);
      var10000[var10001] ^= MASK[bit];
   }

   public static void makebittables() {
      for(int bit = 0; bit < 80; ++bit) {
         SHIFT[bit] = bit % 16 * 2;
         MASK[bit] = 3 << SHIFT[bit];
         NMASK[bit] = ~MASK[bit];
      }

   }
}
