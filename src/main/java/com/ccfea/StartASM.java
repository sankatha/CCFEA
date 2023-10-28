/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
package com.ccfea;

import jas.engine.SimEngine;
import jas.engine.gui.JAS;

public class StartASM {

   public static void main(String[] args) {
      new ASMModelParams();
      SimEngine eng = new SimEngine();
      JAS jas = new JAS(eng);
      jas.setVisible(true);
      ASMModelJas m = new ASMModelJas();
      eng.addModel(m);
      m.setParameters();
      if(!ASMModelParams.batch) {
         ASMObserverJAS o = new ASMObserverJAS();
         eng.addModel(o);
         o.setParameters();
      }

   }
}
