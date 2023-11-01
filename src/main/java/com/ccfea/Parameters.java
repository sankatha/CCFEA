package com.ccfea;

public class Parameters {

    public ASMModelParams asmModelParams = new ASMModelParams();
    public BFParams bfParams = new BFParams();
    int run;


    public void init() {
        this.bfParams.init();
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

    public void sayHello() {
        System.out.println("Lo que hacemos en vida se refleja en la eternidad");
    }
}
