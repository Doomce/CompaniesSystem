package net.dom.companies.licences;

import java.util.Arrays;
import java.util.List;

public class procurementIIPack extends Licence {

    public procurementIIPack() {
        super(5, "Dalyvavimas valdžios konkursuose", 25000.00, 2);
    }

    @Override
    public List<Integer> getRequirements() {
        return List.of(4);
    }

    @Override
    public List<String> getDescription() {
        return List.of("Viešieji pirkimai.");
    }

}
