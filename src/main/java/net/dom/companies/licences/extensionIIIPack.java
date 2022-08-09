package net.dom.companies.licences;

import java.util.List;

public class extensionIIIPack extends Licence {

    public extensionIIIPack() {
        super(9, "Trečiasis plėtros etapas", 9000.00, 4);
    }

    public int getExtendCoefficient() {
        return 6;
    }

    public int getType() {
        return 3;
    }

    @Override
    public List<Integer> getRequirements() {
        return List.of(7, 8);
    }

    @Override
    public List<String> getDescription() {
        return List.of("+6 darb", "+3 akcininkai", "1.6x neapmokestinamas pelnas");
    }

}
