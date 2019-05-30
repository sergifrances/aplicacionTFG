package com.example.sergi.aplicacion;

public class Sugerencia {

    private int id;
    private String sugerencia;

    public Sugerencia() {

    }

    public Sugerencia(int id, String sugerencia) {
        this.id = id;
        this.sugerencia = sugerencia;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }

    public String getSugerencia() {
        return sugerencia;
    }
}
