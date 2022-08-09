package net.dom.companies.licences;

import java.util.List;

public class ProfitPack extends Licence {

    public ProfitPack() {
        super(2, "Paramos gavėjo statusas", 60000.00, 3);
    }

    @Override
    public List<String> getDescription() {
        return List.of("Įgaunate teisę gauti įvairias subsidijas.");
    }
}
