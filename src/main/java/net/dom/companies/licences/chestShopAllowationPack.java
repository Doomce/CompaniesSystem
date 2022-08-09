package net.dom.companies.licences;

import org.bukkit.Material;

import java.util.List;

public class chestShopAllowationPack extends Licence {

    public chestShopAllowationPack() {
        super(3, "ChestShop Integracijos naudojimas", 7000.00, 1, Material.CHEST);
    }

    @Override
    public List<String> getDescription() {
        return List.of("Leidžiama sukurti įmonės ChestShop'us");
    }

}
