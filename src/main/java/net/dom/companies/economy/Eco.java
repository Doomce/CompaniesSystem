package net.dom.companies.economy;

public enum Eco {

    INIT_CONTRIBUTION(12000.00),
    CREATION_TAX(1500.00),
    MIN_WAGE(300.00),

    ;




    final double sum;

    double inflation = 1.00;

    Eco(double cost) {
        this.sum = cost;
    }

    public double cost() {
        return sum*inflation;
    }

}
