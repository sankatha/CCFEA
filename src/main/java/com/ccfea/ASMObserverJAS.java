/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
package com.ccfea;

import jas.engine.SimEngine;
import jas.engine.SimModel;
import jas.engine.gui.JAS;
import jas.graphics.plot.CollectionBarPlotter;
import jas.graphics.plot.TimeSeriesPlotter;
import jas.statistics.CrossSection.Double;
import jas.statistics.functions.MeanArrayFunction;

public class ASMObserverJAS extends SimModel {

   ASMModelJas asmModel;
   private TimeSeriesPlotter pricePlot;
   private TimeSeriesPlotter volumePlot;
   private TimeSeriesPlotter RetrainingAgentsPlot;
   private TimeSeriesPlotter averageWealthPlot;
   private CollectionBarPlotter agentPositionBarPlot;
   private CollectionBarPlotter agentWealthBarPlot;
   private Double agentsWealth;
   private Double agentsPosition;


   public static void main(String[] args) {
      SimEngine eng = new SimEngine();
      JAS jas = new JAS(eng);
      jas.setVisible(true);
      ASMModelJas m = new ASMModelJas();
      eng.addModel(m);
      m.setParameters();
      ASMObserverJAS o = new ASMObserverJAS();
      eng.addModel(o);
      o.setParameters();
   }

   public void setParameters() {
      this.asmModel = new ASMModelJas();
      if(this.asmModel == null) {
         throw new IllegalStateException("The model has not loaded yet.");
      }
   }

   public void buildModel() {
      this.agentsWealth = new Double(ASMModelJas.agentList, 0);
      this.agentsPosition = new Double(ASMModelJas.agentList, 1);
      this.pricePlot = new TimeSeriesPlotter("Price");
      this.pricePlot.addSeries("Marker Price", this.asmModel, 0);
      this.pricePlot.addSeries("Rational Expectation", this.asmModel, 1);
      this.pricePlot.addSeries("Risk Neutral", this.asmModel, 2);
      this.RetrainingAgentsPlot = new TimeSeriesPlotter("Retraining Agents");
      this.RetrainingAgentsPlot.addSeries("Number", this.asmModel, 4);
      this.averageWealthPlot = new TimeSeriesPlotter("Wealth");
      this.averageWealthPlot.addSeries("max", new jas.statistics.functions.MaxArrayFunction.Double(this.agentsWealth));
      this.averageWealthPlot.addSeries("min", new jas.statistics.functions.MinArrayFunction.Double(this.agentsWealth));
      this.averageWealthPlot.addSeries("mean", new MeanArrayFunction(this.agentsWealth));
      this.volumePlot = new TimeSeriesPlotter("Negotiation Volume");
      this.volumePlot.addSeries("Volume", this.asmModel, 3);
      this.agentPositionBarPlot = new CollectionBarPlotter("Agent\'s Position");
      this.agentPositionBarPlot.addSeries("Position", this.agentsPosition);
      this.agentWealthBarPlot = new CollectionBarPlotter("Agent\'s Wealth");
      this.agentWealthBarPlot.addSeries("Wealth", this.agentsWealth);
      this.pricePlot.setSize(360, 250);
      this.RetrainingAgentsPlot.setSize(360, 250);
      this.volumePlot.setSize(360, 250);
      this.averageWealthPlot.setSize(360, 250);
      this.agentPositionBarPlot.setSize(360, 250);
      this.agentWealthBarPlot.setSize(360, 250);
      this.addSimWindow(this.pricePlot);
      this.addSimWindow(this.RetrainingAgentsPlot);
      this.addSimWindow(this.volumePlot);
      this.addSimWindow(this.averageWealthPlot);
      this.addSimWindow(this.agentPositionBarPlot);
      this.addSimWindow(this.agentWealthBarPlot);
      this.eventList.scheduleSimple(0L, 1, this.pricePlot, 10003);
      this.eventList.scheduleSimple(0L, 1, this.RetrainingAgentsPlot, 10003);
      this.eventList.scheduleSimple(0L, 1, this.volumePlot, 10003);
      this.eventList.scheduleSimple(0L, 1, this.averageWealthPlot, 10003);
      this.eventList.scheduleSimple(0L, 1, this.agentPositionBarPlot, 10003);
      this.eventList.scheduleSimple(0L, 1, this.agentWealthBarPlot, 10003);
   }
}
