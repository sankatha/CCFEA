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

    public void initNormal() {
    }

    public void setBaseline(double theBaseline) {
        this.baseline = theBaseline;
    }

    public void setmindividend(double minimumDividend) {
        this.mindividend = minimumDividend;
    }

    public void setmaxdividend(double maximumDividend) {
        this.maxdividend = maximumDividend;
    }

    public void setAmplitude(double theAmplitude) {
        this.amplitude = theAmplitude;
        if (this.amplitude < 0.0D) {
            this.amplitude = 0.0D;
        }

        if (this.amplitude > 1.0D) {
            this.amplitude = 1.0D;
        }

        this.amplitude = 1.0E-4D * (double) ((int) (10000.0D * this.amplitude));
    }

    public void setPeriod(double thePeriod) {
        this.period = thePeriod;
        if (this.period < 2.0D) {
            this.period = 2.0D;
        }

    }

    public void setDerivedParams() {
        this.deviation = this.baseline * this.amplitude;
        this.rho = Math.exp(-1.0D / this.period);
        this.rho = 1.0E-4D * (double) ((int) (10000.0D * this.rho));
        this.gauss = this.deviation * Math.sqrt(1.0D - this.rho * this.rho);
        this.dvdnd = this.baseline + this.gauss * Sim.getRnd().getDblFromTo(0.0D, 1.0D);
    }

    public double dividend() {
        this.dvdnd = this.baseline + this.rho * (this.dvdnd - this.baseline) + this.gauss * Sim.getRnd().getDblFromTo(0.0D, 1.0D);
        if (this.dvdnd < this.mindividend) {
            this.dvdnd = this.mindividend;
        }

        if (this.dvdnd > this.maxdividend) {
            this.dvdnd = this.maxdividend;
        }

        return this.dvdnd;
    }
}
