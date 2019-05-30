package com.example.sergi.aplicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NuevoElemento extends AppCompatActivity /*implements AdapterView.OnItemSelectedListener*/ {

    Spinner spinnerEle;
    MapView map;
    IMapController mapController;
    EditText etNombre, etDescripccion, etTipo, etLugar;
    String coordenada;
    Double longitud, latitud;
    CheckBox cbMovilidad, cbVisual, cbAuditiva, cbaccesible;
    Button boton;

    Retrofit cliente;
    ApiService apiService;
    ArrayList<Necesidad> listaNecesidades = new ArrayList<>();
    Elemento elemento = new Elemento();
    String tipoElemento;

    SharedPreferences sharedPreferences;
    EstadoSesion estadoSesion;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_elemento);
        //Recuperar datos sesión
        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);

        //spinnerAcc = findViewById(R.id.accesibilidadSpinner);
        spinnerEle = findViewById(R.id.spinner);
        map = findViewById(R.id.mapViewNuevo);
        etNombre = findViewById(R.id.editText6);
        etDescripccion = findViewById(R.id.editText7);
        //etTipo = findViewById(R.id.editText8);
        cbMovilidad = findViewById(R.id.checkBoxMov);
        cbVisual = findViewById(R.id.checkBoxVis);
        cbAuditiva = findViewById(R.id.checkBoxAud);
        cbaccesible = findViewById(R.id.cbaccesible);
        boton = findViewById(R.id.button2);
        etLugar = findViewById(R.id.editText9);

        //Mostrar mapa, centrado en la posición actual gps
        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        final MyLocationNewOverlay miPosicion = new MyLocationNewOverlay(map);
        miPosicion.enableMyLocation();
        miPosicion.enableFollowLocation();
        IMyLocationProvider s= miPosicion.getMyLocationProvider();
        miPosicion.getMyLocation();
        map.getOverlays().add(miPosicion);

        mapController = map.getController();
        mapController.setZoom(15.0);

        //Cuando pulse sobre mapa añadir marcador, indicando la posición del nuevo elemento
        final Marker startMarker = new Marker(map);
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                coordenada = p.getLatitude() + ", " + p.getLongitude();
                longitud = p.getLongitude();
                latitud = p.getLatitude();

                Drawable icon = getResources().getDrawable(R.drawable.markernuevo);
                GeoPoint point = new GeoPoint(Double.valueOf(p.getLatitude()), Double.valueOf(p.getLongitude()));
                map.getOverlays().remove(startMarker);
                startMarker.setPosition(point);
                startMarker.setIcon(icon);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(startMarker);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        //final HorizontalScrollView hsv = new HorizontalScrollView();

        //Permitir el scroll después de pulsar mapa
        final ScrollView hsv = findViewById(R.id.hsv);
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    /*
                    case MotionEvent.ACTION_MOVE:
                        hsv.requestDisallowInterceptTouchEvent(true);
                        break;
                        */
                    case MotionEvent.ACTION_DOWN:
                        hsv.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        hsv.requestDisallowInterceptTouchEvent(false);
                        break;
                    /*case MotionEvent.ACTION_CANCEL:
                        hsv.requestDisallowInterceptTouchEvent(false);
                        break;
                        */
                }
                return map.onTouchEvent(event);
            }
        });

        //
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_accesibilidad, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEle.setAdapter(adapter);

        //Spinner desplegable para elegir tipo elemento
        tipoElemento = spinnerEle.getSelectedItem().toString();
        System.out.println(tipoElemento);

        //Datos del usuario
        CredencialesLogin cr = new CredencialesLogin();
        cr = getProfile();
        String a = cr.getUsuario();
        id = cr.getId();
        //id=1;
        System.out.println(id);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
                apiService = cliente.create(ApiService.class);
                //Comprobar que necesidades están marcadas
                if(!TextUtils.isEmpty(etNombre.getText().toString()) && !TextUtils.isEmpty(etDescripccion.getText().toString()) && !TextUtils.isEmpty(etLugar.getText().toString()) && coordenada!=null) {
                    if(cbMovilidad.isChecked()==true) {
                        Necesidad nec = new Necesidad(1, "movilidad");
                        listaNecesidades.add(nec);
                    }
                    if(cbVisual.isChecked()==true) {
                        Necesidad nec = new Necesidad(2, "visual");
                        listaNecesidades.add(nec);
                    }
                    if(cbAuditiva.isChecked()==true) {
                        Necesidad nec = new Necesidad(3, "auditiva");
                        listaNecesidades.add(nec);
                    }
                    elemento.setNombre(etNombre.getText().toString());
                    elemento.setDescripcion(etDescripccion.getText().toString());
                    //System.out.println(elemento.getCoordenada());
                    elemento.setCoordenada(coordenada);
                    elemento.setLongitud(longitud);
                    elemento.setLatitud(latitud);
                    //elemento.setTipo(etTipo.getText().toString());
                    elemento.setTipo(tipoElemento);
                    elemento.setNecesidades(listaNecesidades);
                    elemento.setIdUsuario(id);
                    elemento.setLugar(etLugar.getText().toString());
                    //Si el elemnto es accesible
                    if(cbaccesible.isChecked()==true) {
                        elemento.setAccesible(1);
                    } else {
                        elemento.setAccesible(0);
                    }
                    //Crear el nuevo elemento
                    apiService.crearElemento(elemento).enqueue(new Callback<Elemento>() {
                        @Override
                        public void onResponse(Call<Elemento> call, Response<Elemento> response) {
                            if(response.isSuccessful()) {
                                Log.i("Bien", "ad");
                                Intent main = new Intent(NuevoElemento.this, MainActivity.class);
                                startActivity(main);
                            }
                        }
                        @Override
                        public void onFailure(Call<Elemento> call, Throwable t) {
                            Log.i("Error de conexión API", t.getMessage());
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Rellena todos los campos y elige un punto del mapa.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //Devolver datos usuario
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

    /*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //String text = parent.getItemAtPosition(position).toString();
        int[] elementos = {R.array.movilidad, R.array.visual, R.array.auditivo};

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, elementos[position], android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnerEle.setAdapter(adapter2);

        String accesibilidad = spinnerAcc.getSelectedItem().toString();
        //String elemento = spinnerEle.getSelectedItem().toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    */
}
