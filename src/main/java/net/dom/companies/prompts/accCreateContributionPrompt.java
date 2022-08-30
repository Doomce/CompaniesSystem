package net.dom.companies.prompts;

import net.dom.companies.Companies;
import net.dom.companies.economy.Eco;
import net.dom.companies.menus.compCreateMenu;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Map;

public class accCreateContributionPrompt extends StringPrompt {

    private Companies plugin = Companies.getInstance();
    private double minDeposit;

    public String getPromptText(ConversationContext Cc) {
        return "Irasyk norimo inaso dydi (Min - "+ Eco.INIT_CONTRIBUTION.cost()+"EUR): \n Noredami atsaukti, rasykite atsaukti arba palaukite.";
    }

    public Prompt acceptInput(ConversationContext Cc, String input) {
        Player p = (Player)Cc.getForWhom();
        try {
            double amount = Double.parseDouble(input);
            if (input.length()>6) {
                throw new NumberFormatException();
            }
            if (amount<=0) {
                throw new NumberFormatException();
            }
            if (minDeposit > amount) {
                p.sendRawMessage("Per mazas inasas");
                return new accCreateContributionPrompt();
            }
            Map<Object, Object> data = Cc.getAllSessionData();
            data.replace("init", amount);
            new compCreateMenu(p, data).open();
        }
        catch (NumberFormatException ignore) {
            p.sendRawMessage("Netinkama ivestis");
        }
        return Prompt.END_OF_CONVERSATION;
    }
}
