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
    Date today;
    String timeString;
    public int currentTime;
    String outputFile;
    String paramFileName;
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter salida;
    FileWriter fw2;
    BufferedWriter bw2;
    PrintWriter salida2;
    public LinkedList agentList;


    Output() {
        this.today = new Date(this.runTime);
        this.timeString = this.today.toString();
        this.agentList = new LinkedList<>();
    }

    public void createEnd() {
        this.asmModelParams = new ASMModelParams();
        this.dataFileExists = false;
        this.timeString = this.timeString.replace(':', '_');
        this.timeString = this.timeString.replace(' ', '_');
        if (!ASMModelParams.batch) {
            this.paramFileName = "guiSettings";
        } else {
            this.paramFileName = "batchSettings";
        }

        this.paramFileName = this.paramFileName.concat(this.timeString);
        this.paramFileName = this.paramFileName.concat(".scm");
    }

    public void setSpecialist(Specialist theSpec) {
        this.outputSpecialist = theSpec;
    }

    public void setWorld(World theWorld) {
        this.outputWorld = theWorld;
    }

    public void writeParams$BFAgent$Time(ASMModelParams modelParam, BFParams bfParms, long t) {
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
        } catch (IOException var6) {
            System.err.println("Exception writing Parameters");
        }
    }

    public void prepareOutputFile() {
        if (!this.dataFileExists) {
            this.outputFile = "output.data";
            this.outputFile = this.outputFile.concat(this.timeString);

            try {
                this.fw2 = new FileWriter(this.outputFile);
                this.bw2 = new BufferedWriter(this.fw2);
                this.salida2 = new PrintWriter(this.bw2);
                this.salida2.println("currentTime\t\t price\t dividend\t volume\t\t agent\'s wealth \n\n");
            } catch (IOException var2) {
                System.err.println("Exception writing data");
            }

            this.dataFileExists = true;
        }
    }

    public void writeData(LinkedList list) {
        long t = ((int) Sim.getAbsoluteTime());

        try {
            this.salida2.print(t);
            this.salida2.print("\t\t");
            this.salida2.print((float) this.outputWorld.getPrice());
            this.salida2.print("\t");
            this.salida2.print((float) this.outputWorld.getDividend());
            this.salida2.print("\t");
            this.salida2.print("\t");
            this.agentList = list;

            for (int i = 0; i < ASMModelParams.numBFagents; ++i) {
                BFagent agent = (BFagent) this.agentList.get(i);
                this.salida2.print("\t");
                this.salida2.print((float) agent.getWealth());
            }

            this.salida2.print("\n");
        } catch (Exception var9) {
            System.err.println("Exception dataOutputFile.writeChars: " + var9.getMessage());
        }
    }

    public void drop() {
        if (this.salida2 != null) {
            this.salida2.close();
        }
    }
}
