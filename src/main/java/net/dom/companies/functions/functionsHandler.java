package net.dom.companies.functions;

import net.dom.companies.Companies;
import net.dom.companies.licences.LicHandler;

public class functionsHandler {

    static Companies plugin;
    list cList;

    public companyManager compMng;
    employeeManager empMng;
    LicHandler licHandler;

    public functionsHandler(Companies pl) {
        plugin = pl;
        cList = new list(pl);
        compMng = new companyManager(pl);
        empMng = new employeeManager(pl);
        licHandler = new LicHandler(pl);
    }


    public list getCList() {
        return cList;
    }

    public employeeManager getEmpMng() {
        return empMng;
    }

    public LicHandler getLicHandler() {
        return licHandler;
    }
}
