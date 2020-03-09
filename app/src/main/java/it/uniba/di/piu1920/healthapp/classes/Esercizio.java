package it.uniba.di.piu1920.healthapp.classes;

import java.io.Serializable;

public class Esercizio implements Serializable {

    String nome,nomecategoria,link,esecuzione;
    int id,tipo;

    public Esercizio(String nome, String nomecategoria, String link, String esecuzione, int id, int tipo) {
        this.nome = nome;
        this.nomecategoria = nomecategoria;
        this.link = link;
        this.esecuzione = esecuzione;
        this.id = id;
        this.tipo = tipo;
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomecategoria() {
        return nomecategoria;
    }

    public void setNomecategoria(String nomecategoria) {
        this.nomecategoria = nomecategoria;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEsecuzione() {
        return esecuzione;
    }

    public void setEsecuzione(String esecuzione) {
        this.esecuzione = esecuzione;
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
