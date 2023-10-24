package com.ccfea;

import java.util.LinkedList;

public class AverageWealth {
    LinkedList agentList;
    int NumAgents;

    public Object InitList(LinkedList list, int numAgents) {
        this.agentList = list;
        this.NumAgents = numAgents;

        return this;
    }


    public double averageWealth() {
        int average = 0;

        for (int i = 0; i < this.NumAgents; i++) {
            BFagent agent = (BFagent) this.agentList.get(i);
            average = (int) (average + agent.getWealth());
        }

        return average / this.NumAgents;
    }
}

/* Location:              M:\pc\downloads\sCCFEA-ASM_beta1.jar!\AverageWealth.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */