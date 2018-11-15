package com.belstu.gulko.passwordHelper.model;

public class Base {
    private String login;
    private String site;
    private String siteLogin;
    private String sitePass;

    public Base() {
    }

    public Base(String login, String site, String siteLogin, String sitePass) {
        this.login = login;
        this.site = site;
        this.siteLogin = siteLogin;
        this.sitePass = sitePass;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSiteLogin() {
        return siteLogin;
    }

    public void setSiteLogin(String siteLogin) {
        this.siteLogin = siteLogin;
    }

    public String getSitePass() {
        return sitePass;
    }

    public void setSitePass(String sitePass) {
        this.sitePass = sitePass;
    }
}
