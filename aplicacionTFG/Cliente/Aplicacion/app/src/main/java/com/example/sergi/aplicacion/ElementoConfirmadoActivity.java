package com.example.sergi.aplicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ElementoConfirmadoActivity extends AppCompatActivity {

    Retrofit cliente;
    ApiService apiService;
    MapView map;
    IMapController mapController;
    SharedPreferences sharedPreferences;
    EstadoSesion estadoSesion;
    int id;
    Button botonEliminar;

    Elemento elemento = new Elemento();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elemento_confirmado);

        //Recuperar datos sesión
        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);

        //Datos del usuario
        CredencialesLogin cr = new CredencialesLogin();
        cr = getProfile();
        String a = cr.getUsuario();
        id = cr.getId();

        map = findViewById(R.id.mapaConfirmado);
        botonEliminar = findViewById(R.id.button5);
        //Obtenemos el id del elemento que pasa la activity anterior
        Bundle bundle = getIntent().getExtras();
        int dato=bundle.getInt("id");

        //Obtenemos elemento seleccionado en la pantalla anterior y lo mostramos en el mapa
        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        apiService.obtenerElementoId(dato).enqueue(new Callback<Elemento>() {
            @Override
            public void onResponse(Call<Elemento> call, Response<Elemento> response) {
                if (response.isSuccessful()) {
                    elemento = response.body();
                    if(response.body() != null) {
                        String coo = elemento.getCoordenada();
                        String[] parts = coo.split(", ");
                        String latitud = parts[0];
                        String longitud = parts[1];

                        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
                        map.setBuiltInZoomControls(true);
                        map.setMultiTouchControls(true);

                        GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));

                        if(elemento.getAccesible() == 1) {
                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                            Marker startMarker = new Marker(map);
                            startMarker.setPosition(point);
                            startMarker.setIcon(icon);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            startMarker.setTitle(elemento.getNombre());
                            startMarker.setSubDescription(elemento.getDescripcion());
                            map.getOverlays().add(startMarker);
                        } else {
                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                            Marker startMarker = new Marker(map);
                            startMarker.setPosition(point);
                            startMarker.setIcon(icon);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            startMarker.setTitle(elemento.getNombre());
                            startMarker.setSubDescription(elemento.getDescripcion());
                            map.getOverlays().add(startMarker);
                        }

                        mapController = map.getController();
                        mapController.setZoom(15.0);
                        mapController.setCenter(point);


                    } else {
                        //No existe usuario
                        System.out.println("No existe elemento");
                    }
                }
            }

            @Override
            public void onFailure(Call<Elemento> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println(t.toString());
            }
        });

        //Botón para eliminar confirmación
        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService.eliminarConfirmacion(id, elemento.getIdElemento()).enqueue(new Callback<Elemento>() {
                    @Override
                    public void onResponse(Call<Elemento> call, Response<Elemento> response) {
                        if (response.isSuccessful()) {
                            elemento = response.body();
                            if(response.body() != null) {
                                Intent intentSug = new Intent(ElementoConfirmadoActivity.this, ContribucionActivity.class);
                                ElementoConfirmadoActivity.this.startActivity(intentSug);
                                finish();
                            } else {
                                //No existe usuario
                                System.out.println("No existe elemento");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Elemento> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println(t.toString());
                    }
                });

            }
        });

    }

    //Obtener datos usuario
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
