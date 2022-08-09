package net.dom.companies.functions;

import net.dom.companies.Companies;

public class functionsHandler {

    Companies comp;
    list cList;

    companyManager cMng;
    employeeManager empMng;

    licencesManager licMng;

    public functionsHandler(Companies plugin) {
        this.comp = plugin;
        cList = new list(plugin);
        cMng = new companyManager(plugin);
        empMng = new employeeManager(plugin);
        licMng = new licencesManager(plugin);
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

    public licencesManager getLicMng() {
        return licMng;
    }
}
