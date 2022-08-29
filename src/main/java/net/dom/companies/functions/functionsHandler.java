package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.licences.LicHandler;

public class functionsHandler {

    static Companies comp;
    list cList;

    companyManager cMng;
    employeeManager empMng;
    LicHandler licHandler;

    public functionsHandler(Companies plugin) {
        comp = plugin;
        cList = new list(plugin);
        cMng = new companyManager(plugin);
        empMng = new employeeManager(plugin);
        licHandler = new LicHandler(plugin);
    }


    public list getCList() {
        return cList;
    }

    public companyManager getCompMng() {
        return cMng;
    }

    public employeeManager getEmpMng() {
        return empMng;
    }

    public LicHandler getLicHandler() {
        return licHandler;
    }
}
