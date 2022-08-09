package net.dom.companies.menus;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.StorageGui;
import net.dom.companies.functions.companyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class compVaultMenu_OPTIONAL {

    //TODO GALBŪT TIESIOG SUSIETI SU CHEST'O korrdinatėm?????

    protected StorageGui gui;
    protected Player player;

    HashMap<Integer, Integer> pickedItems;

    public compVaultMenu_OPTIONAL(Player p, List<ItemStack> items, Long compId) {

        gui = Gui.storage()
                .title(Component.text("Seifas").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
                .rows(4)
                .create();

        gui.addItem(items);

        gui.setCloseGuiAction((event -> {
            companyManager.openCompanyPanel(player, compId);
        }));

    }

}
