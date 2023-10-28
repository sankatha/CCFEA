package com.ccfea;

import jas.engine.SimEngine;
import jas.engine.gui.JAS;

public class StartASM {

    public static void main(String[] args) {
        final SimEngine eng = new SimEngine();
        final JAS jas = new JAS(eng);
        jas.setVisible(true);

        final ASMModelJas m = new ASMModelJas();
        eng.addModel(m);
        m.setParameters();

        if (!ASMModelParams.batch) {
            ASMObserverJAS o = new ASMObserverJAS();
            eng.addModel(o);
            o.setParameters();
        }
    }
}
