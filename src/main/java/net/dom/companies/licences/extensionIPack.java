package net.dom.companies.licences;

import java.util.List;

public class extensionIPack extends Licence {

    public extensionIPack() {
        super(7, "Pirmasis plėtros etapas", 1600.00, 2);
    }

    public int getExtendCoefficient() {
        return 2;
    }

    public int getType() {
        return 1;
    }

    @Override
    public List<String> getDescription() {
        return List.of("+2 darb", "+1 akcininkas", "1.2x neapmokestinamas pelnas");
    }

}
