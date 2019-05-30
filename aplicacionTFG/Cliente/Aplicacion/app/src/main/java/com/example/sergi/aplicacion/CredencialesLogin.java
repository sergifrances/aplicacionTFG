package com.example.sergi.aplicacion;

public class CredencialesLogin {

    public CredencialesLogin() {
    }

    private String usuario;

    private String contraseña;

    private int id;

    public void setUsuario(String usuario) { this.usuario =  usuario;}

    public void setContraseña(String contraseña) { this.contraseña = contraseña;}

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
