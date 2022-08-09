package net.dom.companies.provisions;

public interface Provision {
    int getMaxWorkers();
    double maxProfit();
    int licCount();
    int maxInProgressContracts();
    double initContribution();
    int maxShareholders();
}
