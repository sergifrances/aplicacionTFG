package com.example.sergi.aplicacion;

import java.util.List;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String usuario;
    private String contraseña;
    private int edad;
    private int mostrar;
    private String foto;
    private List<Necesidad> necesidades;

    public Usuario() {
    }

    public Usuario (int idUsuario, String nombre, String usuario, String contraseña, int edad, int mostrar, String foto, List<Necesidad> necesidades) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.edad = edad;
        this.mostrar = mostrar;
        this.foto = foto;
        this.necesidades = necesidades;
    }

    public int getId() {
        return idUsuario;
    }

    public void setId(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenya() {
        return contraseña;
    }

    public void setContrasenya(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFoto() {
        return foto;
    }

    public void setMostrar(int mostrar) {
        this.mostrar = mostrar;
    }

    public int getMostrar() {
        return mostrar;
    }

    public void setNecesidades(List<Necesidad> necesidades) {
        this.necesidades = necesidades;
    }

    public List<Necesidad> getNecesidades() {
        return necesidades;
    }
}
