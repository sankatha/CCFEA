package com.ccfea;

import jas.engine.Sim;
import jas.engine.SimModel;
import jas.events.SimGroupEvent;
import jas.graph.GraphViewer;
import jas.graph.layout.RegularCircleLayout;
import jas.statistics.IDoubleSource;
import org._3pq.jgrapht.Graph;
import org._3pq.jgrapht.graph.DirectedWeightedMultigraph;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class ASMModelJas extends SimModel implements IDoubleSource {
    public static final int MARKET_PRICE = 0;
    public static final int RATIONAL_EXPECTATIONS = 1;
    public static final int RISK_NEUTRAL = 2;
    public static final int VOLUME = 3;
    public static final int RETRAINING_AGENTS = 4;
    int modelTime;
    public static LinkedList agentList = new LinkedList();

    public Specialist specialist = new Specialist();

    public Dividend dividendProcess;

    public World world = new World();


    public Output output;

    public BFParams bfParams = new BFParams();

    public ASMModelParams asmModelParams = new ASMModelParams();

    public AverageWealth averageWealth;

    public BufferedWriter out;

    public Graph graph;

    private GraphViewer graphViewer;


    public void setParameters() {
        Sim.openProbe(this.asmModelParams, "Model Parameters");
        Sim.openProbe(this.bfParams, "BF Parameters");
    }


    public Object setParamsModel$BF(ASMModelParams modelParams, BFParams bfp) {
        this.bfParams = bfp;
        this.asmModelParams = modelParams;
        return this;
    }


    public Object setOutputObject(Output obj) {
        this.output = obj;
        return this;
    }


    public int getNumBFagents() {
        return ASMModelParams.numBFagents;
    }


    public double getInitialCash() {
        return ASMModelParams.initialcash;
    }


    public LinkedList getAgentList() {
        return agentList;
    }


    public World getWorld() {
        if (this.world == null) System.out.println("Empty world!");
        return this.world;
    }


    public Specialist getSpecialist() {
        return this.specialist;
    }


    public Output getOutput() {
        return this.output;
    }


    public int getModelTime() {
        return this.modelTime;
    }


    public Object setBatchRandomSeed(int newSeed) {
        ASMModelParams.randomSeed = newSeed;
        return this;
    }


    public void buildModel() {
        this.modelTime = 0;

        this.asmModelParams = new ASMModelParams();

        Sim.getRnd().setSeed(ASMModelParams.randomSeed);

        this.dividendProcess = new Dividend();
        this.dividendProcess.initNormal();
        this.dividendProcess.setBaseline(ASMModelParams.baseline);
        this.dividendProcess.setmindividend(ASMModelParams.mindividend);
        this.dividendProcess.setmaxdividend(ASMModelParams.maxdividend);
        this.dividendProcess.setAmplitude(ASMModelParams.amplitude);
        this.dividendProcess.setPeriod(ASMModelParams.period);
        this.dividendProcess.setDerivedParams();

        this.world.createBitnameList();
        this.world.setintrate(ASMModelParams.intrate);

        if (ASMModelParams.exponentialMAs == 1) {
            this.world.setExponentialMAs(true);
        }
        else {
            this.world.setExponentialMAs(false);
        }

        this.world.initWithBaseline(ASMModelParams.baseline);
        this.world.setRea$Reb(ASMModelParams.rea, ASMModelParams.reb);

        this.specialist.setMaxPrice(ASMModelParams.maxprice);
        this.specialist.setMinPrice(ASMModelParams.minprice);
        this.specialist.setTaup(ASMModelParams.taup);
        this.specialist.setSPtype(ASMModelParams.sptype);
        this.specialist.setMaxIterations(ASMModelParams.maxiterations);
        this.specialist.setMinExcess(ASMModelParams.minexcess);
        this.specialist.setETA(ASMModelParams.eta);
        this.specialist.setREA(ASMModelParams.rea);
        this.specialist.setREB(ASMModelParams.reb);

        this.output = new Output();

        this.output.setWorld(this.world);
        this.output.setSpecialist(this.specialist);

        BFagent.init();
        BFagent.setBFParameterObject(this.bfParams);
        BFagent.setWorld(this.world);

        this.graph = new DirectedWeightedMultigraph();

        for (int i = 0; i < ASMModelParams.numBFagents; i++) {
            BFagent agent = new BFagent(this.graph);
            agent.setID(i);
            agent.setLabel(i + "");
            agent.setColor(Color.BLUE);
            agent.setintrate(ASMModelParams.intrate);
            agent.setminHolding$minCash(ASMModelParams.minholding, ASMModelParams.mincash);
            agent.setInitialCash(ASMModelParams.initialcash);
            agent.setInitialHoldings();
            agent.setPosition(ASMModelParams.initholding);
            agent.initForecasts2();
            agentList.add(agent);
        }

        this.averageWealth = new AverageWealth();
        this.averageWealth.InitList(agentList, ASMModelParams.numBFagents);

        for (int c = 0; c < 502; c++) {
            doWarmupStep();
        }

        new RegularCircleLayout(this.graph, new Dimension(500, 500)).init();
        this.graphViewer = new GraphViewer();
        this.graphViewer.setMinMaxWeightValue(0.0D, 10.0D);
        this.graphViewer.setDrawNodeLabel(true);
        this.graphViewer.setGraph(this.graph, new Dimension(500, 500));

        addSimWindow(this.graphViewer);
        buildAction();
    }

    public Object writeParams() {
        if ((this.asmModelParams != null) && (this.bfParams != null))
            this.output.writeParams$BFAgent$Time(this.asmModelParams, this.bfParams, this.modelTime);
        return this;
    }

    public void buildAction() {
        SimGroupEvent periodActions = this.eventList.scheduleGroup(0L, 1);

        this.eventList.scheduleSimple(0L, 1, this, "periodStepDividend");
        periodActions.addCollectionEvent(agentList, BFagent.class, "creditEarningsAndPayTaxes");
        this.eventList.scheduleSimple(0L, 1, this.world, "updateWorld");
        periodActions.addCollectionEvent(agentList, BFagent.class, "prepareForTrading2");
        this.eventList.scheduleSimple(0L, 1, this, "periodStepPrice");
        this.eventList.scheduleSimple(0L, 1, this, "completeTrades$Market");
        periodActions.addCollectionEvent(agentList, BFagent.class, "updatePerformance2");

        if (ASMModelParams.batch) {
            this.eventList.scheduleSimple(ASMModelParams.numOfIterations - 10000, 1, this, "printResultsPrice");
            this.eventList.scheduleSimple(ASMModelParams.numOfIterations - 1000, 1, this, "printResultsAgentWealth");
        }

        this.eventList.scheduleSimple(0L, 1, this, "agentColor");
        this.eventList.scheduleSimple(0L, 1, this.graphViewer, 10003);
        this.eventList.scheduleSystem(ASMModelParams.numOfIterations, 10000);
    }

    public Object doWarmupStep() {
        double div = this.dividendProcess.dividend();
        this.world.setDividend(div);
        this.world.updateWorld();
        this.world.setPrice(div / ASMModelParams.intrate);
        return this;
    }

    public Object periodStepDividend() {
        this.modelTime += 1;
        this.world.setDividend(this.dividendProcess.dividend());
        this.world.setAverageWealth(this.averageWealth.averageWealth());
        return this;
    }

    public Object periodStepPrice() {
        this.world.setPrice(this.specialist.performTrading$Market(agentList, this.world));
        return this;
    }

    public Object completeTrades$Market() {
        this.specialist.completeTrades$Market(agentList, this.world);
        return this;
    }

    public void printPrice() {
        System.out.println(this.world.getPrice() + "  " + this.world.getRationalExpectations() +
                "  " + this.world.getRiskNeutral() + "  " + this.specialist.getVolume());
    }

    public void printBatch() {
        System.out.println("Step... " + Sim.getAbsoluteTime());
    }

    public double getDoubleValue(int valueId) {
        switch (valueId) {
            case 0:
                return this.world.getPrice();
            case 1:
                return this.world.getRationalExpectations();
            case 2:
                return this.world.getRiskNeutral();
            case 3:
                return this.specialist.getVolume();
            case 4:
                return numRetrainingAgents();
        }
        throw new UnsupportedOperationException("Bad argument");
    }

    public void printResultsPrice() {
        try {
            this.out = new BufferedWriter(new FileWriter("Price.txt", true));
            this.out.write("\n" + Sim.getAbsoluteTime() + "\t");
            this.out.write(this.world.getPrice() + "\t");
            this.out.write(this.world.getRationalExpectations() + "\t");
            this.out.write(this.world.getRiskNeutral() + "\t");
            this.out.close();
        } catch (IOException localIOException) {
        }
    }

    public void printResultsAgentWealth() {
        try {
            this.out = new BufferedWriter(new FileWriter("AgentWealth.txt", true));
            this.out.write("\n" + Sim.getAbsoluteTime() + "\t");

            for (int i = 0; i < ASMModelParams.numBFagents; i++) {
                BFagent agent = (BFagent) agentList.get(i);
                this.out.write(agent.getWealth() + "\t");
            }

            this.out.close();
        } catch (IOException localIOException) {
        }
    }

    public void agentColor() {
        for (int i = 0; i < ASMModelParams.numBFagents; i++) {
            BFagent agent = (BFagent) agentList.get(i);
            if (agent.retrainig) agent.setColor(Color.RED);
            else {
                agent.setColor(Color.BLUE);
            }
        }
    }

    public int numRetrainingAgents() {
        int count = 0;

        for (int i = 0; i < ASMModelParams.numBFagents; i++) {
            BFagent agent = (BFagent) agentList.get(i);
            if (agent.retrainig) {
                count++;
            }
        }
        return count;
    }
}