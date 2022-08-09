package net.dom.companies.provisions;

public class ivProvisions implements Provision {

    @Override
    public int getMaxWorkers() {
        return 1;
    }

    @Override
    public double maxProfit() {
        return 50000;
    }

    @Override
    public int licCount() {
        return 2;
    }

    @Override
    public int maxInProgressContracts() {
        return 2;
    }

    @Override
    public double initContribution() {
        return 0;
    }

    @Override
    public int maxShareholders() {
        return 0;
    }
}
