package com.example.sergi.aplicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SugerenciasActivity extends AppCompatActivity {

    EditText sugerencia;
    Button boton;

    Retrofit cliente;
    ApiService apiService;

    int id;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);
        //Recuperar datos sesión
        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);

        //Volver pantalla anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sugerencia = findViewById(R.id.editText5);
        boton = findViewById(R.id.button4);

        //Datos del usuario
        CredencialesLogin cr = new CredencialesLogin();
        cr = getProfile();
        String a = cr.getUsuario();
        id = cr.getId();

        //Botón para enviar sugerencia
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprueba que se ha introducido texto
                if(TextUtils.isEmpty(sugerencia.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Introduzca sugerencia.", Toast.LENGTH_SHORT).show();
                } else {
                    cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
                    apiService = cliente.create(ApiService.class);

                    Sugerencia sug = new Sugerencia();
                    sug.setSugerencia(sugerencia.toString());
                    //Envía sugerencia a la bd
                    apiService.crearSugerencia(sug).enqueue(new Callback<Sugerencia>() {
                        @Override
                        public void onResponse(Call<Sugerencia> call, Response<Sugerencia> response) {
                            if(response.isSuccessful()) {
                                Log.i("Bien", "ad");
                                Intent main = new Intent(SugerenciasActivity.this, MainActivity.class);
                                startActivity(main);
                            }
                        }
                        @Override
                        public void onFailure(Call<Sugerencia> call, Throwable t) {
                            Log.i("Error de conexión API", t.getMessage());
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //Devuelve datos usuario
    private CredencialesLogin getProfile() {
        System.out.println("Hola aaa");
        CredencialesLogin login = new CredencialesLogin();
        String nombre = sharedPreferences.getString("usuario", "");
        login.setUsuario(nombre);
        String contrasenya = sharedPreferences.getString("contraseña", "");
        login.setContraseña(contrasenya);
        int id = sharedPreferences.getInt("id", 0);
        login.setId(id);
        return login;
    }
}
