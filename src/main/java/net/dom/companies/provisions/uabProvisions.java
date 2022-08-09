package net.dom.companies.provisions;

public class uabProvisions implements Provision {

    @Override
    public int getMaxWorkers() {
        return 8;
    }

    @Override
    public double maxProfit() {
        return 350000;
    }

    @Override
    public int licCount() {
        return 6;
    }

    @Override
    public int maxInProgressContracts() {
        return 6;
    }

    @Override
    public double initContribution() {
        return 12000;
    }

    @Override
    public int maxShareholders() {
        return 3;
    }
}
