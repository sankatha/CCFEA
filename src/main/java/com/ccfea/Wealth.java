/**
 * Decompiled code from lib/sCCFEA-ASM_beta1.jar using https://the.bytecode.club/fernflower.jar
 */
package com.ccfea;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Wealth {

   LinkedList agentList;
   int NumAgents;


   public Object InitList(LinkedList list, int numAgents) {
      this.agentList = list;
      this.NumAgents = numAgents;
      return this;
   }

   public double averageWealth() {
      int average = 0;

      for(int i = 0; i < this.NumAgents; ++i) {
         BFagent agent = (BFagent)this.agentList.get(i);
         average = (int)((double)average + agent.getWealth());
      }

      return (double)(average / this.NumAgents);
   }

   public double maxWealth() throws IndexOutOfBoundsException {
      final List<Double> wealth = (List<Double>) this.agentList.stream().map(bfAgent -> ((BFagent) bfAgent).getWealth()).collect(Collectors.toList());
      Collections.reverse(wealth);
      return wealth.get(0);
   }

   public double minWealth() throws IndexOutOfBoundsException {
      final List<Double> wealth = (List<Double>) this.agentList.stream().map(bfAgent -> ((BFagent) bfAgent).getWealth()).sorted().collect(Collectors.toList());
      return wealth.get(0);
   }

   public double medianWealth() throws IndexOutOfBoundsException {
      final List<Double> wealth = (List<Double>) this.agentList.stream().map(bfAgent -> ((BFagent) bfAgent).getWealth()).sorted().collect(Collectors.toList());
      final int size = wealth.size();

      if (size % 2 == 0) {
         // If the list has an even number of elements, take the average of the middle two elements
         final int middleIndex1 = size / 2 - 1;
         final int middleIndex2 = size / 2;
         final double middleValue1 = wealth.get(middleIndex1);
         final double middleValue2 = wealth.get(middleIndex2);
         return (middleValue1 + middleValue2) / 2.0;
      } else {
         // If the list has an odd number of elements, return the middle element
         final int middleIndex = (size - 1) / 2;
         return wealth.get(middleIndex + 1);
      }
   }
}
