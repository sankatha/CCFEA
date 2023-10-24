package com.ccfea;

import jas.engine.Sim;

public class Dividend {
    double baseline;
    double amplitude;
    double period;
    double mindividend;
    double maxdividend;
    double deviation;
    double rho;
    double gauss;
    double dvdnd;

    public Object initNormal() {
        return this;
    }

    public Object setBaseline(double theBaseline) {
        this.baseline = theBaseline;
        return this;
    }

    public Object setmindividend(double minimumDividend) {
        this.mindividend = minimumDividend;
        return this;
    }

    public Object setmaxdividend(double maximumDividend) {
        this.maxdividend = maximumDividend;
        return this;
    }

    public double setAmplitude(double theAmplitude) {
        this.amplitude = theAmplitude;
        if (this.amplitude < 0.0D)
            this.amplitude = 0.0D;
        if (this.amplitude > 1.0D)
            this.amplitude = 1.0D;
        this.amplitude = (1.0E-4D * (int) (10000.0D * this.amplitude));
        return this.amplitude;
    }

    public double setPeriod(double thePeriod) {
        this.period = thePeriod;
        if (this.period < 2.0D)
            this.period = 2.0D;
        return this.period;
    }

    public Object setDerivedParams() {
        this.deviation = (this.baseline * this.amplitude);

        this.rho = Math.exp(-1.0D / this.period);
        this.rho = (1.0E-4D * (int) (10000.0D * this.rho));
        this.gauss = (this.deviation * Math.sqrt(1.0D - this.rho * this.rho));


        this.dvdnd = (this.baseline + this.gauss * Sim.getRnd().getDblFromTo(0.0D, 1.0D));
        return this;
    }

    public double dividend() {
        this.dvdnd = (this.baseline + this.rho * (this.dvdnd - this.baseline) + this.gauss * Sim.getRnd().getDblFromTo(0.0D, 1.0D));
        if (this.dvdnd < this.mindividend)
            this.dvdnd = this.mindividend;
        if (this.dvdnd > this.maxdividend) {
            this.dvdnd = this.maxdividend;
        }

        return this.dvdnd;
    }
}


/* Location:              M:\pc\downloads\sCCFEA-ASM_beta1.jar!\Dividend.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */