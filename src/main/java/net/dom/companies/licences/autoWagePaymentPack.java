package net.dom.companies.licences;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class autoWagePaymentPack extends Licence {

    public autoWagePaymentPack() {
        super(1, "Automatinis algų pervedimas", 12000.00, 2);
    }

    @Override
    public List<String> getDescription() {
        return List.of("Automatinis algų pervedimas darbuotojams");
    }

    public ItemStack generateItem() {



        return null;
    }
}
