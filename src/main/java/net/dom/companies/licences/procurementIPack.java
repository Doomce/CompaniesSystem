package net.dom.companies.licences;

import java.util.List;

public class procurementIPack extends Licence {

    public procurementIPack() {
        super(4, "Dalyvavimas konkursuose", 13000.00, 1);
    }


    @Override
    public List<String> getDescription() {
        return List.of("Viešieji pirkimai");
    }
}
