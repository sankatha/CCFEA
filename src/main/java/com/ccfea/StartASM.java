package com.ccfea;

import jas.engine.Sim;
import jas.engine.SimEngine;
import jas.engine.gui.JAS;

public class StartASM {
    public static void main(String[] args) {
        ASMModelParams asmModelParams = new ASMModelParams();


        System.out.println(Sim.getStartDirectory());

        SimEngine eng = new SimEngine();
        JAS jas = new JAS(eng);
        jas.setVisible(true);

        ASMModelJas m = new ASMModelJas();
        eng.addModel(m);
        m.setParameters();

        if (!ASMModelParams.batch) {
            ASMObserverJAS o = new ASMObserverJAS();
            eng.addModel(o);
            o.setParameters();
        }
        jas.getController().buildModel();
        jas.getController().startModel();
    }
}