package net.dom.companies.objects;

import java.util.List;

public enum duty {

    OWNER (List.of(true, true, true, true, true, true, true, true, true, true)),
    CO_OWNER (List.of(true, true, false, true, false, true, false, true, false, false)),
    MANAGER (List.of(false, false, false, true, false, true, false, false, false, false)),
    WORKER (List.of(false, false, false, true, false, true, false, false, false, false));

    private final List<Boolean> permissions;

    duty(List<Boolean> perms) {
        permissions = perms;
    }

    /**
    PERMISSIONS:

    0 inviteWorker;
    1 kickWorker;
    2 changeWage;
    3 empMenu;
    4 useAssist;
    5 licMenu;
    6 sellLic;
    7 buyLic;
    8 promote;
    9 demote;



     **/
    public boolean getPermission(int num) {
        return permissions.get(num);
    }
}
