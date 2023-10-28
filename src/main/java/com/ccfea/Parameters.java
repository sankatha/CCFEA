/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
package com.ccfea;

public class Parameters {

   public ASMModelParams asmModelParams = new ASMModelParams();
   public BFParams bfParams = new BFParams();
   int run;


   public Object init() {
      this.bfParams.init();
      return this;
   }

   public ASMModelParams getModelParams() {
      return this.asmModelParams;
   }

   public BFParams getBFParams() {
      return this.bfParams;
   }

   public int getRunArg() {
      return this.run;
   }

   public Object sayHello() {
      System.out.println("Lo que hacemos en vida se refleja en la eternidad");
      return this;
   }
}
