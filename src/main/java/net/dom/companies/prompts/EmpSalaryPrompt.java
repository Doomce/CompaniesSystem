package net.dom.companies.prompts;

import net.dom.companies.Companies;
import net.dom.companies.economy.Eco;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
        String empName = Objects.requireNonNullElse(
                        Bukkit.getPlayer((UUID) context.getSessionData("empUid")).getName(),
                        "darbuotojui");
        return "Įvesk "+empName+" skiriamą algą. Minimali dienos alga: "+ Eco.MIN_WAGE.cost() +"e. \n" +
                "Jei darbuotojas nedirba, įvesk 0.";
    }
}
