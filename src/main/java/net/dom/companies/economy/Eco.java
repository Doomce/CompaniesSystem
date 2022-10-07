package net.dom.companies.economy;

import java.util.List;

public enum Eco {

    INIT_CONTRIBUTION(12000.00),
    CREATION_TAX(1500.00),
    MIN_WAGE(300.00),
    NPD(600),
    PSD(0.25),
    GPM(0.15),

    ;




    final double sum;

    double inflation = 1.00;

    Eco(double cost) {
        this.sum = cost;
    }

    public double cost() {
        return sum*inflation;
    }

    public static Double[] calcWageTax(Double salary) {
        Double[] wageData = (Double[]) List.of(0.00,0.00,0.00).toArray();
        if (salary != null) {
            wageData[0] += salary;
            double npd = salary;
            if (salary >= MIN_WAGE.cost() && salary <= NPD.cost()*2) {
                npd -= MIN_WAGE.cost();
            } else {
                npd -= NPD.cost();
            }
            wageData[1] += npd * GPM.cost();
            wageData[2] += npd * PSD.cost();
        } else {
            wageData[1] += MIN_WAGE.cost() * GPM.cost();
            wageData[2] += MIN_WAGE.cost() * PSD.cost();
        }
        return wageData;
    }

}
