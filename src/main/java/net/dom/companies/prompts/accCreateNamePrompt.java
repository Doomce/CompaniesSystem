package net.dom.companies.prompts;

import net.dom.companies.Companies;
import net.dom.companies.menus.compCreateMenu;
import org.apache.commons.lang.WordUtils;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Map;

public class accCreateNamePrompt extends StringPrompt {

        private Companies plugin = Companies.getInstance();

        public String getPromptText(ConversationContext Cc) {
            return "Irasyk norima imones pavadinima:";
        }

    public Prompt acceptInput(ConversationContext Cc, String input) {
        Player p = (Player)Cc.getForWhom();
        input = WordUtils.capitalizeFully(input);
        if (input.length()>20) {
            p.sendRawMessage("Netinkama ivestis");
            return Prompt.END_OF_CONVERSATION;
        }
        //TODO: CHECK IF NOT EXISTS

        Map<Object, Object> data = Cc.getAllSessionData();
        data.replace("name", input);
        new compCreateMenu(p, data).open();
        return Prompt.END_OF_CONVERSATION;
    }
}
