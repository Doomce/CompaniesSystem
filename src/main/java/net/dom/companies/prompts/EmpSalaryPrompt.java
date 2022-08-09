package net.dom.companies.prompts;

import net.dom.companies.Companies;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EmpSalaryPrompt extends NumericPrompt {

    @Nullable
    @Override
    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull Number input) {
        Companies comp = (Companies) context.getPlugin();
        UUID uid = (UUID) context.getSessionData("empUid");
        long compId = (Long) context.getSessionData("compId");

        comp.getEmpMng().changeWage(context.getForWhom(), uid, compId, input.doubleValue(), false);

        return Prompt.END_OF_CONVERSATION;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return "Įvesk darbuotojui skiriamą algą. Minimali dienos alga: 300e. \n" +
                "Jei darbuotojas nedirba, įvesk 0.";
    }
}
