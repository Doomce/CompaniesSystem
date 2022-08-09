package net.dom.companies.licences;

import org.bukkit.Material;

import java.util.List;

public class supportPack extends Licence {

    public supportPack() {
        super(6, "Padidintas pelnas", 30000.00, 1, Material.GOLD_INGOT);
    }

    @Override
    public List<String> getDescription() {
        return List.of("Daugiau pelno, be mokesčių!");
    }
}
