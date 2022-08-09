package net.dom.companies.prompts;

import net.dom.companies.Companies;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PlayerNamePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmpInvitationPrompt extends PlayerNamePrompt {
    private Companies plugin;

    public EmpInvitationPrompt(@NotNull Plugin plugin) {
        super(plugin);
        this.plugin = (Companies) plugin;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return "Ivesk zaidejo nick.";
    }

    @Nullable
    @Override
    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Player input) {
        long compId = (Long) context.getSessionData("compId");
        plugin.getEmpMng().invite((Player) context.getForWhom(), input, compId);
        return Prompt.END_OF_CONVERSATION;
    }

}
