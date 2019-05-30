package com.example.sergi.aplicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button login;
    TextView registro, usu, cont;

    SharedPreferences sharedPreferences;
    EstadoSesion estadoSesion;

    Retrofit cliente;
    ApiService apiService;
    ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    Boolean encontrado = false;
    Usuario usr = new Usuario();
    CredencialesLogin credencial = new CredencialesLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usu = findViewById(R.id.log_usu);
        cont = findViewById(R.id.log_cont);
        //Recuperar datos sesión
        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);

        login = findViewById(R.id.btn_log);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Comprobamos que haya introducido usuario y contraseña
                if(!TextUtils.isEmpty(usu.getText().toString()) && !TextUtils.isEmpty(cont.getText().toString())) {
                    cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
                    apiService = cliente.create(ApiService.class);
                    //Busca si existe usuario
                    apiService.buscarUsuario(usu.getText().toString(), cont.getText().toString()).enqueue(new Callback<List<Usuario>>() {
                        @Override
                        public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                            if (response.isSuccessful()) {
                                listaUsuarios = new ArrayList<>(response.body());
                                //Si existe entra en la app con ese usuario, si no te indica que no existe usuario
                                if(response.body().size() != 0) {
                                    saveProfile(listaUsuarios.get(0));
                                    Intent intentLog = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intentLog);
                                } else {
                                    //No existe usuario
                                    System.out.println("No existe usuario");
                                    Toast.makeText(LoginActivity.this,"No existe el usuario.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Usuario>> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(t.getMessage());
                        }
                    });

                } else {
                    Toast.makeText(LoginActivity.this,"Introduzca usuario y contraseña.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registro = findViewById(R.id.btn_reg);
        //LLeva a la pantalla para el registro
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReg = new Intent(LoginActivity.this, RegistroActivity.class);
                LoginActivity.this.startActivity(intentReg);
            }
        });

    }

    //Guardar los datos del usuario en el estado de la app
    private void saveProfile(Usuario usuar) {
        String usuario = usuar.getUsuario();
        int id = usuar.getId();
        String contraseña = usuar.getContrasenya();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("contraseña", contraseña);
        editor.putInt("id", id);

        editor.commit();

    }
}
