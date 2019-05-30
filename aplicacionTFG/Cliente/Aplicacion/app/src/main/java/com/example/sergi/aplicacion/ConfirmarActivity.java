package com.example.sergi.aplicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmarActivity extends AppCompatActivity {

    Retrofit cliente;
    ApiService apiService;

    ArrayList<Elemento> listaElementos = new ArrayList<>();

    SharedPreferences sharedPreferences;
    EstadoSesion estadoSesion;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar);

        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);

        //Obtener datos usuario
        CredencialesLogin cr = new CredencialesLogin();
        cr = getProfile();
        String a = cr.getUsuario();
        id = cr.getId();

        //Mostrar flecha para volver pantalla anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Comunicación con API, para que nos devuelva los elementos para confirmar (aún no tiene 3 confirmaciones y que este usuario no ha confirmado)
        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        apiService.elementosPorConfirmar(id).enqueue(new Callback<List<Elemento>>() {
            @Override
            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                if (response.isSuccessful()) {
                    listaElementos = new ArrayList<>(response.body());
                    if(response.body().size() != 0) {
                        mostrarLista();
                    } else {
                        //No existe usuario
                        System.out.println("No existe usuario");
                        Toast.makeText(getApplicationContext(), "No hay elementos para confirmar.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    //Llamamos al adaptador para mostrar la lista
    //Cuando pulsamos en un elemento de la lista, nos lleve a otra pantalla donde muestra ese elemento en el mapa y la opción de confirmar
    public void mostrarLista() {
        AdaptadorElementos adaptador = new AdaptadorElementos(this);
        ListView lv1 = findViewById(R.id.listConfirmar);
        lv1.setAdapter(adaptador);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Elemento ele = listaElementos.get(position);
                Intent intentSug = new Intent(ConfirmarActivity.this, ElementoAConfirmar.class);
                intentSug.putExtra("id", ele.getIdElemento());
                ConfirmarActivity.this.startActivity(intentSug);
                finish();
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

    //Clase adpaptador para mostrar la lista
    class AdaptadorElementos extends ArrayAdapter<Elemento> {

        AppCompatActivity appCompatActivity;

        AdaptadorElementos(AppCompatActivity context) {
            super(context, R.layout.elemento, listaElementos);
            appCompatActivity = context;
        }
        //Los 2 datos que se van a mostrar de cada elemento: tipo y lugar
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.elemento, null);

            TextView textView1 = item.findViewById(R.id.textView10);
            textView1.setText(listaElementos.get(position).getTipo());

            TextView textView2 = item.findViewById(R.id.textView11);
            textView2.setText(listaElementos.get(position).getLugar());

            return(item);
        }

    }


}

