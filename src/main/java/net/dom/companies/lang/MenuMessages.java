package net.dom.companies.lang;

public enum MenuMessages {

    SECTION("", "&m                             "),
    SECTION_LONGER("", "&m                                        "),
    NOT_PURCHASED("", "X Neįsigytas"),
    PURCHASED("", "✔ Įsigytas");

    private String path;
    private String message;

    MenuMessages(String path, String msg) {
        this.path = path;
        this.message = msg;
    }



    public String getMessage() {
        return message;
    }
}
