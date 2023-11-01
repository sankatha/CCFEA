package com.ccfea;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Wealth {
    private LinkedList<BFagent> agentList;

    public void InitList(LinkedList<BFagent> list) {
        this.agentList = list;
    }

    public double averageWealth() {
        int average = 0;

        for (BFagent agent : this.agentList) {
            average = (int) ((double) average + agent.getWealth());
        }

        return (double) average / this.agentList.size();
    }

    public double maxWealth() {
        final List<Double> wealth = this.agentList.stream().map(Agent::getWealth).collect(Collectors.toList());
        Collections.reverse(wealth);
        return wealth.get(0);
    }

    public double minWealth() {
        final List<Double> wealth = this.agentList.stream().map(Agent::getWealth).sorted().collect(Collectors.toList());
        return wealth.get(0);
    }

    public double medianWealth() {
        final List<Double> wealth = this.agentList.stream().map(Agent::getWealth).sorted().collect(Collectors.toList());
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
