package net.dom.companies.provisions;

public enum businessForms {

    IV,
    UAB;

    public Provision getClassByName(){
        switch (this) {
            case UAB: {
                return new uabProvisions();
            }
            case IV: {
                return new ivProvisions();
            }
        }
        return null;
    }
}
