package com.ccfea;

import jas.graph.RelationalAgent;
import org._3pq.jgrapht.Graph;

public class Agent extends RelationalAgent {

    public double demand;
    public double profit;
    public double wealth;
    public double position;
    public double cash;
    public double initialcash;
    public double minholding;
    public double mincash;
    public double intrate;
    public double intratep1;
    public double price;
    public double dividend;
    public int myID;
    public static World worldForAgent;


    Agent(Graph g) {
        super(g);
    }

    public static void setWorld(World aWorld) {
        worldForAgent = aWorld;
    }

    public void setID(int iD) {
        this.myID = iD;
    }

    public void setPosition(double aDouble) {
        this.position = aDouble;
    }

    public void setintrate(double rate) {
        this.intrate = rate;
        this.intratep1 = this.intrate + 1.0D;
    }

    public void setminHolding$minCash(double holding, double minimumcash) {
        this.minholding = holding;
        this.mincash = minimumcash;
    }

    public void setInitialCash(double initcash) {
        this.initialcash = initcash;
    }

    public void setInitialHoldings() {
        this.profit = 0.0D;
        this.wealth = 0.0D;
        this.cash = this.initialcash;
        this.position = 0.0D;
    }

    public void getPriceFromWorld() {
        this.price = worldForAgent.getPrice();
    }

    public void getDividendFromWorld() {
        this.dividend = worldForAgent.getDividend();
    }

    public Object creditEarningsAndPayTaxes() {
        this.getPriceFromWorld();
        this.getDividendFromWorld();
        this.cash -= (this.price * this.intrate - this.dividend) * this.position;
        if (this.cash < this.mincash) {
            this.cash = this.mincash;
        }

        this.wealth = this.cash + this.price * this.position;
        return this;
    }

    public void constrainDemand(double slope, double trialprice) {
        if (this.demand > 0.0D) {
            if (this.demand * trialprice > this.cash - this.mincash) {
                if (this.cash - this.mincash > 0.0D) {
                    this.demand = (this.cash - this.mincash) / trialprice;
                    slope = -this.demand / trialprice;
                } else {
                    this.demand = 0.0D;
                    slope = 0.0D;
                }
            }
        } else if (this.demand < 0.0D && this.demand + this.position < this.minholding) {
            this.demand = this.minholding - this.position;
            slope = 0.0D;
        }

    }

    public double getAgentPosition() {
        return this.position;
    }

    public double getWealth() {
        return this.wealth;
    }

    public double getCash() {
        return this.cash;
    }

    public Object prepareForTrading() {
        return this;
    }

    public double getDemandAndSlope$forPrice(double slope, double p) {
        return 0.0D;
    }

    public Object updatePerformance() {
        return this;
    }
}
