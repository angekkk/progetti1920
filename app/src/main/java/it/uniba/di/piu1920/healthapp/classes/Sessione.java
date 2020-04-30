package it.uniba.di.piu1920.healthapp.classes;


public class Sessione {

    int id;int tipo; String ema,pass,lang;
    String link;

    public Sessione(int id, int tipo, String ema, String pass) {
        this.id = id;
        this.tipo = tipo;
        this.ema = ema;
        this.pass = pass;
        this.link = "";
    }

    public Sessione(int id, int tipo, String ema, String pass, String link) {
        this.id = id;
        this.tipo = tipo;
        this.ema = ema;
        this.pass = pass;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getEma() {
        return ema;
    }

    public void setEma(String ema) {
        this.ema = ema;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
