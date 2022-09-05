package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.database.CompaniesEmployees;
import net.dom.companies.database.Company;
import net.dom.companies.economy.Eco;
import net.dom.companies.lang.Language;
import net.dom.companies.objects.duty;
import org.bukkit.entity.Player;

public class compManagementAssist {

    /**
     * SOLUTIONS: code - (object) action
     * 00 - none;
     * 01 - (CompaniesEmployees) salary change prompt;
     * 02 - (Company) set tag;
     */


    private static class Pair {
        int code;
        Object value;

        Pair(int code, Object value) {
            this.code = code;
            this.value = value;
        }
    }

    private final String guiMessage;
    private final Pair solution;

    public void action(Player p) {
        Companies plugin = functionsHandler.plugin;
        switch (solution.code) {
            case 0: {
                break;
            }
            case 1: {
                CompaniesEmployees emp = (CompaniesEmployees) solution.value;
                plugin.getFH().getEmpMng().wagePrompt(p, emp);
                break;
            }

        }
    }

    public String getMessage() {
        return guiMessage;
    }

    public compManagementAssist(Company comp, CompaniesEmployees player) {

        if (!player.getDuties().getPermission(4)) {
            guiMessage = Language.get("menus.compMngAssist.states.worker");
            solution = new Pair(0, null);
            return;
        }

        if (comp.getTag() == null || comp.getTag().isEmpty()) {
            guiMessage = Language.get("menus.compMngAssist.states.noTag");
            solution = new Pair(2, comp);
            return;
        }

        //TODO tikrinti banko balansa.
        //TODO Mokesciai
        //TODO Algos
        for (CompaniesEmployees employee : comp.getCE()) {
            if ((employee.getSalary() == null || player.getSalary() < Eco.MIN_WAGE.cost()) && !employee.getDuties().equals(duty.OWNER)) {
                guiMessage = Language.get("menus.compMngAssist.states.check_salary");
                solution = new Pair(1, employee);
                return;
            }
        }

        guiMessage = Language.get("menus.compMngAssist.states.ok");
        solution = new Pair(0, null);
    }

}
