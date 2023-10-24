package com.ccfea;

public class Parameters {
    public ASMModelParams asmModelParams;
    public BFParams bfParams;
    int run;

    Parameters() {
        this.asmModelParams = new ASMModelParams();
        this.bfParams = new BFParams();
    }


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


/* Location:              M:\pc\downloads\sCCFEA-ASM_beta1.jar!\Parameters.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */