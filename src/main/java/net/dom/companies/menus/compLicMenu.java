package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.dom.companies.functions.companyManager;
import net.dom.companies.licences.LicHandler;
import net.dom.companies.licences.Licence;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class compLicMenu {

    private Player player;

    private Gui gui;


    /**
     * @param var1 Player
     * @param var2 A companyId
     * @param var3 All licences;
     */
    public compLicMenu(Player var1, Long var2, List<Licence.Properties> var3) {
        this.player = var1;
        this.gui = Gui.gui()
                .type(GuiType.CHEST)
                .disableAllInteractions()
                .title(Component.text("Įmonės licencijos"))
                .rows(3)
                .create();

        var3.forEach((licProperty) -> {

            gui.addItem(new GuiItem(licProperty.lic.getItemStack(licProperty.isOwned), (event) -> {
                if (!event.isShiftClick()) return;
                companyManager.licAction(var1, var2, licProperty);
            }));

        });



        gui.setItem(3, 9, new GuiItem(ItemBuilder.from(Material.CLOCK)
            .name(Component.text("<- Atgal").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
            .build(), (event -> companyManager.openCompanyPanel(player, var2))));
    }

    public void open() {
        gui.open(player);
    }


}
