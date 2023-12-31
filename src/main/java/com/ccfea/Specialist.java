/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
package com.ccfea;

import java.util.LinkedList;

public class Specialist {

   final int SP_RE = 0;
   final int SP_SLOPE = 1;
   final int SP_ETA = 2;
   double maxprice;
   double minprice;
   double eta;
   double minexcess;
   double rea;
   double reb;
   double bidfrac;
   double offerfrac;
   int maxiterations;
   static double volume;
   double taupdecay;
   double taupnew;
   int sptype;


   public Object setMaxPrice(double maximumPrice) {
      this.maxprice = maximumPrice;
      return this;
   }

   public Object setMinPrice(double minimumPrice) {
      this.minprice = minimumPrice;
      return this;
   }

   public Object setTaup(double aTaup) {
      this.taupnew = 1.0D - Math.exp(-1.0D / aTaup);
      this.taupdecay = 1.0D - this.taupnew;
      return this;
   }

   public Object setSPtype(int i) {
      if(i != 0 && i != 1 && i != 2) {
         System.out.println("The specialist type chosen is invalid.  Only 0, 1, or 2 are acceptable.  The Specialist will be set to Slope (i.e., 1).");
         i = 1;
      }

      this.sptype = i;
      return this;
   }

   public Object setMaxIterations(int someIterations) {
      this.maxiterations = someIterations;
      return this;
   }

   public Object setMinExcess(double minimumExcess) {
      this.minexcess = minimumExcess;
      return this;
   }

   public Object setETA(double ETA) {
      this.eta = ETA;
      return this;
   }

   public Object setREA(double REA) {
      this.rea = REA;
      return this;
   }

   public Object setREB(double REB) {
      this.reb = REB;
      return this;
   }

   public double performTrading$Market(LinkedList agentList, World worldForSpec) {
      double slopetotal = 0.0D;
      double trialprice = 0.0D;
      double offertotal = 0.0D;
      double bidtotal = 0.0D;
      new LinkedList();
      volume = 0.0D;
      double dividend = worldForSpec.getDividend();
      int mcount = 0;

      for(boolean done = false; mcount < this.maxiterations && !done; ++mcount) {
         switch(this.sptype) {
         case 0:
            trialprice = this.rea * dividend + this.reb;
            done = true;
            break;
         case 1:
            if(mcount == 0) {
               trialprice = worldForSpec.getPrice();
            } else {
               double imbalance = bidtotal - offertotal;
               if(imbalance <= this.minexcess && imbalance >= -this.minexcess) {
                  done = true;
                  continue;
               }

               if(slopetotal != 0.0D) {
                  trialprice -= imbalance / slopetotal;
               } else {
                  trialprice *= 1.0D + this.eta * imbalance;
               }
            }
            break;
         case 2:
            if(mcount == 0) {
               trialprice = worldForSpec.getPrice();
            } else {
               trialprice = worldForSpec.getPrice() * (1.0D + this.eta * (bidtotal - offertotal));
               done = true;
            }
         }

         if(trialprice < this.minprice) {
            trialprice = this.minprice;
         }

         if(trialprice > this.maxprice) {
            trialprice = this.maxprice;
         }

         bidtotal = 0.0D;
         offertotal = 0.0D;
         slopetotal = 0.0D;
         LinkedList index = agentList;

         for(int i = 0; i < index.size(); ++i) {
            Agent agent = (Agent)index.get(i);
            double slope = 0.0D;
            double demand = agent.getDemandAndSlope$forPrice(slope, trialprice);
            slopetotal += slope;
            if(demand > 0.0D) {
               bidtotal += demand;
            } else if(demand < 0.0D) {
               offertotal -= demand;
            }
         }

         volume = bidtotal > offertotal?offertotal:bidtotal;
         this.bidfrac = bidtotal > 0.0D?volume / bidtotal:0.0D;
         this.offerfrac = offertotal > 0.0D?volume / offertotal:0.0D;
      }

      return trialprice;
   }

   public double getVolume() {
      return volume;
   }

   public Object completeTrades$Market(LinkedList agentList, World worldForSpec) {
      new LinkedList();
      double price = 0.0D;
      price = worldForSpec.getPrice();
      double profitperunit = worldForSpec.getProfitPerUnit();
      double bfp = this.bidfrac * price;
      double ofp = this.offerfrac * price;
      double tp = this.taupnew * profitperunit;
      LinkedList index = agentList;

      for(int i = 0; i < index.size(); ++i) {
         Agent agent = (Agent)index.get(i);
         agent.profit = this.taupdecay * agent.profit + tp * agent.position;
         if(agent.demand > 0.0D) {
            agent.position += agent.demand * this.bidfrac;
            agent.cash -= agent.demand * bfp;
         } else if(agent.demand < 0.0D) {
            agent.position += agent.demand * this.offerfrac;
            agent.cash -= agent.demand * ofp;
         }
      }

      return this;
   }
}
