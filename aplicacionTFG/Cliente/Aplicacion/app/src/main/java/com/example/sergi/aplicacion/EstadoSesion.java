package com.example.sergi.aplicacion;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class EstadoSesion {

    //SharedPreferences sharedPreferences;

    public EstadoSesion() {

    }

    //Guardar datos usuario
    public void saveProfile(Usuario usuar, SharedPreferences sharedPreferences) {

        String usuario = usuar.getUsuario();
        int id = usuar.getId();
        String contraseña = usuar.getContrasenya();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("contraseña", contraseña);
        editor.putInt("id", id);

        editor.commit();

    }

    //Devolver datos usuario
    public CredencialesLogin getProfile(SharedPreferences sharedPreferences) {
        CredencialesLogin login = new CredencialesLogin();
        String nombre = sharedPreferences.getString("usuario", "");
        login.setUsuario(nombre);
        String contrasenya = sharedPreferences.getString("contraseña", "");
        login.setContraseña(contrasenya);
        int id = sharedPreferences.getInt("id", 0);
        login.setId(id);
        return login;
    }

    //Limpiar datos usuario
    public void clearProfile(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("usuario");
        editor.remove("contraseña");
        editor.commit();
    }
}
