package it.uniba.di.piu1920.healthapp.classes;

public class Cliente {

    int id;
    String nome,cognome,email,qr;

    public Cliente(int id, String nome, String cognome, String email,String qr) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.qr = qr;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
