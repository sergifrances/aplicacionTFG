package com.example.sergi.aplicacion;

import java.util.List;

public class Elemento {

    private int idElemento;
    private String nombre;
    private String descripcion;
    private String coordenada;
    private Double longitud;
    private Double latitud;
    private String lugar;
    private int Usuario_idUsuario;
    private String tipo;
    private int accesible;
    private List<Necesidad> necesidades;

    public Elemento() {

    }

    public Elemento (int idElemento, String nombre, String descripcion, String coordenada, Double longitud, Double latitud, String lugar, int idUsuario, String tipo, int accesible, List<Necesidad> necesidades) {
        this.idElemento = idElemento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.coordenada = coordenada;
        this.longitud = longitud;
        this.latitud = latitud;
        this.lugar = lugar;
        this.Usuario_idUsuario = idUsuario;
        this.tipo = tipo;
        this.accesible = accesible;
        this.necesidades = necesidades;
    }

    public int getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(int idElemento) {
        this.idElemento = idElemento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(String coordenada) {
        this.coordenada = coordenada;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public int getIdUsuario() {
        return Usuario_idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.Usuario_idUsuario = idUsuario;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setAccesible(int accesible) { this.accesible = accesible; }

    public int getAccesible() {
        return accesible;
    }

    public void setNecesidades(List<Necesidad> necesidades) {
        this.necesidades = necesidades;
    }

    public List<Necesidad> getNecesidades() {
        return necesidades;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLatitud() {
        return latitud;
    }
}
