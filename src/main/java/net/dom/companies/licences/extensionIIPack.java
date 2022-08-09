package net.dom.companies.licences;

import java.util.List;

public class extensionIIPack extends Licence {

    public extensionIIPack() {
        super(8, "Antrasis plėtros etapas", 3500.00, 3);
    }

    public int getExtendCoefficient() {
        return 4;
    }

    public int getType() {
        return 2;
    }

    @Override
    public List<Integer> getRequirements() {
        return List.of(7);
    }

    @Override
    public List<String> getDescription() {
        return List.of("+4 darb", "+2 akcininkai", "1.4x neapmokestinamas pelnas");
    }

}
