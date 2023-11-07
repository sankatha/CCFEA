/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
package com.ccfea;

public class MovingAverage {

   int width;
   int numInputs;
   double[] maInputs;
   int arrayPosition;
   double sumOfInputs;
   double uncorrectedSum;
   double expWMA;
   double aweight;
   double bweight;


   public Object initWidth(int w) {
      this.width = w;
      this.maInputs = new double[w];

      for(int i = 0; i < w; ++i) {
         this.maInputs[i] = 0.0D;
      }

      this.numInputs = 0;
      this.sumOfInputs = 0.0D;
      this.arrayPosition = 0;
      this.uncorrectedSum = 0.0D;
      this.bweight = 1.0D - Math.exp(-1.0D / (double)w);
      this.aweight = 1.0D - this.bweight;
      return this;
   }

   public Object initWidth$Value(int w, double val) {
      this.width = w;
      this.maInputs = new double[w];

      for(int i = 0; i < w; ++i) {
         this.maInputs[i] = val;
      }

      this.numInputs = w;
      this.sumOfInputs = (double)w * val;
      this.arrayPosition = 0;
      this.uncorrectedSum = (double)w * val;
      this.bweight = 1.0D - Math.exp(-1.0D / (double)w);
      this.aweight = 1.0D - this.bweight;
      this.expWMA = val;
      return this;
   }

   public int getNumInputs() {
      return this.numInputs;
   }

   public double getMA() {
      if(this.numInputs == 0) {
         return 0.0D;
      } else {
         double movingAverage;
         if(this.numInputs < this.width) {
            movingAverage = this.sumOfInputs / (double)this.numInputs;
         } else {
            movingAverage = this.sumOfInputs / (double)this.width;
         }

         return movingAverage;
      }
   }

   public double getAverage() {
      return this.numInputs == 0?0.0D:this.uncorrectedSum / (double)this.numInputs;
   }

   public double getEWMA() {
      return this.expWMA;
   }

   public void addValue(double x) {
      this.arrayPosition = (this.width + this.numInputs) % this.width;
      if(this.numInputs < this.width) {
         this.sumOfInputs += x;
         this.maInputs[this.arrayPosition] = x;
      } else {
         this.sumOfInputs = this.sumOfInputs - this.maInputs[this.arrayPosition] + x;
         this.maInputs[this.arrayPosition] = x;
      }

      ++this.numInputs;
      this.uncorrectedSum += x;
      this.expWMA = this.aweight * this.expWMA + this.bweight * x;
   }
}
