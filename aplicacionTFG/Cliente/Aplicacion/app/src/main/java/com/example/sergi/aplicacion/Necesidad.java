package com.example.sergi.aplicacion;

public class Necesidad {

    private int idNecesidad;
    private String nombre;

    public Necesidad() {

    }

    public Necesidad(int idNecesidad, String nombre) {
        this.idNecesidad = idNecesidad;
        this.nombre = nombre;
    }

    public void setIdNecesidad(int idNecesidad) {
        this.idNecesidad = idNecesidad;
    }

    public int getIdNecesidad() {
        return idNecesidad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
