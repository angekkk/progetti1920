package it.uniba.di.piu1920.healthapp.classes;


//check del 22/06
public class Sessione {

    int id;
    int tipo;
    String ema, pass;
    String link;


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


    public String getEma() {
        return ema;
    }


    public String getPass() {
        return pass;
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
