package com.example.mytest.adapteritems;

public class itemdata_frends {
    public String mail, name;
    public boolean incheck;

    public itemdata_frends(String mail, String name) {
        this.mail = mail;
        this.name = name;
        this.incheck = false;
    }

    public itemdata_frends(String mail, String name, boolean incheck) {
        this.mail = mail;
        this.name = name;
        this.incheck = incheck;
    }

    public void setIncheck(boolean incheck) {
        this.incheck = incheck;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }
}
