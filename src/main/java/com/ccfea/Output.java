package com.ccfea;

import jas.engine.Sim;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;

public class Output {
    private boolean dataFileExists;
    World outputWorld;
    Specialist outputSpecialist;
    ASMModelParams asmModelParams;
    long runTime = System.currentTimeMillis();

    Date today = new Date(this.runTime);

    String timeString = new String(this.today.toString());


    public int currentTime;


    String outputFile;


    String paramFileName;


    FileWriter fw;

    BufferedWriter bw;

    PrintWriter salida;

    FileWriter fw2;

    BufferedWriter bw2;

    PrintWriter salida2;

    public LinkedList agentList = new LinkedList();


    public Object createEnd() {
        this.asmModelParams = new ASMModelParams();

        this.dataFileExists = false;

        this.timeString = this.timeString.replace(':', '_');
        this.timeString = this.timeString.replace(' ', '_');

        if (!ASMModelParams.batch) {
            this.paramFileName = new String("guiSettings");
        } else {
            this.paramFileName = new String("batchSettings");
        }

        this.paramFileName = this.paramFileName.concat(this.timeString);
        this.paramFileName = this.paramFileName.concat(".scm");

        return this;
    }

    public Object setSpecialist(Specialist theSpec) {
        this.outputSpecialist = theSpec;
        return this;
    }

    public Object setWorld(World theWorld) {
        this.outputWorld = theWorld;
        return this;
    }

    public Object writeParams$BFAgent$Time(ASMModelParams modelParam, BFParams bfParms, long t) {
        try {
            this.fw = new FileWriter(this.paramFileName);
            this.bw = new BufferedWriter(this.fw);
            this.salida = new PrintWriter(this.bw);

            this.salida.println("Parameters at " + t);

            this.salida.println("\nModel Parameters\n");
            this.salida.println("\tnumBFagents = " + ASMModelParams.numBFagents);
            this.salida.println("\tinitholding = " + ASMModelParams.initholding);
            this.salida.println("\tinitialcash = " + ASMModelParams.initialcash);
            this.salida.println("\tminholding = " + ASMModelParams.minholding);
            this.salida.println("\tmincash = " + ASMModelParams.mincash);
            this.salida.println("\tintrate = " + ASMModelParams.intrate);

            this.salida.println("\n\tDividend parameters\n");
            this.salida.println("\tbaseline = " + ASMModelParams.baseline);
            this.salida.println("\tmindividend = " + ASMModelParams.mindividend);
            this.salida.println("\tmaxdividend = " + ASMModelParams.maxdividend);
            this.salida.println("\tamplitude = " + ASMModelParams.amplitude);
            this.salida.println("\tperiod = " + ASMModelParams.period);
            this.salida.println("\texponentialMAs = " + ASMModelParams.exponentialMAs);
            this.salida.println("\n\tSpecialist parameters\n");
            this.salida.println("\tmaxprice = " + ASMModelParams.maxprice);
            this.salida.println("\tminprice = " + ASMModelParams.minprice);
            this.salida.println("\ttaup = " + ASMModelParams.taup);
            this.salida.println("\tsptype = " + ASMModelParams.sptype);
            this.salida.println("\tmaxiterations = " + ASMModelParams.maxiterations);
            this.salida.println("\tminexcess = " + ASMModelParams.minexcess);
            this.salida.println("\teta = " + ASMModelParams.eta);
            this.salida.println("\tetamax = " + ASMModelParams.etamax);
            this.salida.println("\tetamin = " + ASMModelParams.etamin);
            this.salida.println("\trea = " + ASMModelParams.rea);
            this.salida.println("\treb = " + ASMModelParams.reb);
            this.salida.println("\trandomSeed= " + ASMModelParams.randomSeed);

            this.salida.println("\n\tAgent parameters\n");

            this.salida.println("\ttauv = " + ASMModelParams.tauv);
            this.salida.println("\tlambda = " + ASMModelParams.lambda);
            this.salida.println("\tmaxbid = " + ASMModelParams.maxbid);
            this.salida.println("\tinitvar = " + ASMModelParams.initvar);
            this.salida.println("\tmaxdev = " + ASMModelParams.maxdev);
            this.salida.println("\tsetOutputForData = " + ASMModelParams.setOutputForData);

            this.salida.println("\n\nBF Agents Parameters\n");
            this.salida.println("\tnumfcasts = " + BFParams.numfcasts);
            this.salida.println("\tcondwords =" + BFParams.condwords);
            this.salida.println("\tcondbits = " + BFParams.condbits);
            this.salida.println("\tmincount = " + BFParams.mincount);
            this.salida.println("\tgafrequency = " + BFParams.gafrequency);
            this.salida.println("\tfirstgatime = " + BFParams.firstgatime);
            this.salida.println("\tlongtime = " + BFParams.longtime);
            this.salida.println("\tindividual = " + BFParams.individual);
            this.salida.println("\ttauv = " + BFParams.tauv);
            this.salida.println("\tlambda = " + BFParams.lambda);
            this.salida.println("\tmaxbid = " + BFParams.maxbid);
            this.salida.println("\tbitprob = " + BFParams.bitprob);
            this.salida.println("\tsubrange = " + BFParams.subrange);
            this.salida.println("\ta_min = " + BFParams.a_min);
            this.salida.println("\ta_max = " + BFParams.a_max);
            this.salida.println("\tb_min = " + BFParams.b_min);
            this.salida.println("\tb_max = " + BFParams.b_max);
            this.salida.println("\tc_min = " + BFParams.c_min);
            this.salida.println("\tc_max = " + BFParams.c_max);
            this.salida.println("\ta_range = " + BFParams.a_range);
            this.salida.println("\tb_range = " + BFParams.b_range);
            this.salida.println("\tc_range = " + BFParams.c_range);
            this.salida.println("\tnewfcastvar = " + BFParams.newfcastvar);
            this.salida.println("\tinitvar = " + BFParams.initvar);
            this.salida.println("\tbitcost = " + BFParams.bitcost);
            this.salida.println("\tmaxdev = " + BFParams.maxdev);
            this.salida.println("\tpoolfrac = " + BFParams.poolfrac);
            this.salida.println("\tnewfrac = " + BFParams.newfrac);
            this.salida.println("\tpcrossover = " + BFParams.pcrossover);
            this.salida.println("\tplinear = " + BFParams.plinear);
            this.salida.println("\tprandom = " + BFParams.prandom);
            this.salida.println("\tpmutation = " + BFParams.pmutation);
            this.salida.println("\tplong = " + BFParams.plong);
            this.salida.println("\tpshort = " + BFParams.pshort);
            this.salida.println("\tnhood = " + BFParams.nhood);
            this.salida.println("\tgenfrac = " + BFParams.genfrac);
            this.salida.println("\tgaprob = " + BFParams.gaprob);
            this.salida.println("\tnpool = " + BFParams.npool);
            this.salida.println("\tnnew = " + BFParams.nnew);
            this.salida.println("\tnnulls = " + BFParams.nnulls);
            this.salida.close();
        } catch (IOException e) {
            System.err.println("Exception writing Parameters");
        }
        return this;
    }

    public Object prepareOutputFile() {
        if (this.dataFileExists) {
            return this;
        }

        this.outputFile = new String("output.data");
        this.outputFile = this.outputFile.concat(this.timeString);
        try {
            this.fw2 = new FileWriter(this.outputFile);
            this.bw2 = new BufferedWriter(this.fw2);
            this.salida2 = new PrintWriter(this.bw2);
            this.salida2.println("currentTime\t\t price\t dividend\t volume\t\t agent's wealth \n\n");
        } catch (IOException e) {
            System.err.println("Exception writing data");
        }

        this.dataFileExists = true;
        return this;
    }

    public Object writeData(LinkedList list) {
        long t = (int) Sim.getAbsoluteTime();
        String worldName = new String("world");
        String specName = new String("specialist");

        try {
            this.salida2.print(t);
            this.salida2.print("\t\t");
            this.salida2.print((float) this.outputWorld.getPrice());
            this.salida2.print("\t");
            this.salida2.print((float) this.outputWorld.getDividend());
            this.salida2.print("\t");
            this.salida2.print((float) this.outputSpecialist.getVolume());
            this.salida2.print("\t");

            ASMModelParams asmModelParam = new ASMModelParams();
            this.agentList = list;

            for (int i = 0; i < ASMModelParams.numBFagents; i++) {

                BFagent agent = (BFagent) this.agentList.get(i);
                this.salida2.print("\t");
                this.salida2.print((float) agent.getWealth());
            }

            this.salida2.print("\n");
        } catch (Exception e) {
            System.err.println("Exception dataOutputFile.writeChars: " + e.getMessage());
        }

        return this;
    }

    public void drop() {
        if (this.salida2 != null) {
            this.salida2.close();
        }
    }
}


/* Location:              M:\pc\downloads\sCCFEA-ASM_beta1.jar!\Output.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */