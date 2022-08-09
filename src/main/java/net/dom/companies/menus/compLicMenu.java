package net.dom.companies.menus;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.functions.companyManager;
import net.dom.companies.licences.Licence;
import net.dom.companies.licences.buildingLic;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class compLicMenu {

    private Player player;

    private Gui gui;


    /**
     * @param var1 Player
     * @param var2 A companyId
     * @param var3 All licences;
     */
    public compLicMenu(Player var1, Long var2, List<Licence> var3) {
        this.player = var1;
        this.gui = Gui.gui()
                .type(GuiType.CHEST)
                .disableAllInteractions()
                .title(Component.text("Įmonės licencijos"))
                .rows(3)
                .create();

        for (Licence lic : var3) {
            gui.addItem(new GuiItem(lic.getItemStack(), (event -> {
                if (!event.isShiftClick()) return;
                companyManager.licAction(var1, var2, lic.getCode(), lic.isOwned());
            })));
        }

        gui.setItem(3, 9, new GuiItem(ItemBuilder.from(Material.CLOCK)
            .name(Component.text("<- Atgal").decorate(TextDecoration.BOLD).color(TextColor.fromHexString("#138D75")))
            .build(), (event -> companyManager.openCompanyPanel(player, var2))));
    }

    public void open() {
        gui.open(player);
    }


}
