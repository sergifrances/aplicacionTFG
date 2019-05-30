package com.example.sergi.aplicacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int id;
    protected static int START_INDEX=-2, DEST_INDEX=-1;
    SharedPreferences sharedPreferences;
    EstadoSesion estadoSesion;
    TextView tv1;
    EditText et1;
    Retrofit cliente;
    ApiService apiService;
    Integer i;
    int val;
    Usuario usuario = new Usuario();
    String nav_nombre;
    String nav_usuario;
    Bitmap nav_foto;
    Drawable nav_foto2;
    ArrayList<Elemento> listaElementos = new ArrayList<>();
    ArrayList<Elemento> elementosRecuadroRuta = new ArrayList<>();
    ArrayList<Elemento> elementosEnRuta = new ArrayList<>();
    MapView map;
    IMapController mapController;

    String coordenada;

    Button rutaConInicio, transporte, transporteActual;
    Context context;
    Marker startMarker;
    Marker finalMarker;
    GeoPoint startPoint, endPoint;
    Boolean destino = false;
    Boolean enRuta = false;
    LinearLayout layout_primero, layout_segundo, layout_cancelar, layout_informacion, layout_itinerario;
    ImageView imagenDificultad;
    TextView cantidadElementos, dificultadRuta, cantidad2;
    ImageButton cancelarInformacion, rutaActual, calcular, biciActual, bici, cancelar, itinerario, calcular_ruta_escritura, calcular_ruta_escritura_bici;
    Boolean buscarSalida = false;
    Boolean buscarLLegada = false;

    MyLocationNewOverlay miPosicion;

    Polyline roadOverlay;

    int mostrar;
    int numElementos = 0;

    List<GeoPoint> puntosRuta;

    protected static Road road;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);

        layout_primero = findViewById(R.id.layout_primero);
        layout_segundo = findViewById(R.id.layout_segundo);
        layout_cancelar = findViewById(R.id.layout_cancelar);
        layout_informacion = findViewById(R.id.layout_informacion);
        layout_itinerario = findViewById(R.id.layout_itinerario);
        cancelarInformacion = findViewById(R.id.cancelar_inf);
        rutaActual = findViewById(R.id.posActual);
        rutaConInicio = findViewById(R.id.posSalida);
        calcular = findViewById(R.id.calcular);
        biciActual = findViewById(R.id.biciActual);
        bici = findViewById(R.id.bici);
        cancelar = findViewById(R.id.cancelar_ruta);
        imagenDificultad = findViewById(R.id.dificultadRuta);
        cantidadElementos = findViewById(R.id.cantidad);
        cantidad2 = findViewById(R.id.cantidad2);
        dificultadRuta = findViewById(R.id.nivel);
        itinerario = findViewById(R.id.itinerario);
        calcular_ruta_escritura = findViewById(R.id.calcular_ruta_escrito);
        calcular_ruta_escritura_bici = findViewById(R.id.calcular_ruta_escrito_bici);

        layout_primero.setVisibility(View.GONE);
        layout_segundo.setVisibility(View.GONE);
        layout_cancelar.setVisibility(View.GONE);
        layout_informacion.setVisibility(View.GONE);
        layout_itinerario.setVisibility(View.GONE);
        //rutaActual.setVisibility(View.GONE);
        //rutaConInicio.setVisibility(View.GONE);
        //calcular.setVisibility(View.GONE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                Intent intentNuevoEle = new Intent(MainActivity.this, NuevoElemento.class);
                MainActivity.this.startActivity(intentNuevoEle);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Obtener usuario
        CredencialesLogin cr = new CredencialesLogin();
        cr = getProfile();
        id = cr.getId();

        //Preparar comunicación cliente con API Rest
        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);

        //Mostrar elementos al usuario
        //Mostrar imágen, usuario y nombre en el desplegable
        apiService.usuarioId(id).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    usuario = response.body();
                    nav_nombre = usuario.getNombre();
                    nav_usuario = usuario.getUsuario();
                    if(usuario.getFoto() != null) {
                        nav_foto = decodeStringToImage(usuario.getFoto());
                    } else {
                        nav_foto2 = getResources().getDrawable(R.drawable.imagenusuario);
                    }

                    mostrar = usuario.getMostrar();

                    //Si mostrar es true, muestra todos los elementos, si no solo los de la necesidad del usuario
                    if(mostrar == 1) {
                        //Mostrar todos los elementos confirmados
                        apiService.elementosConfirmados().enqueue(new Callback<List<Elemento>>() {
                            @Override
                            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                                if (response.isSuccessful()) {
                                    listaElementos = new ArrayList<>(response.body());
                                    for(int i=0; i < listaElementos.size(); i++) {
                                        String coo = listaElementos.get(i).getCoordenada();
                                        String[] parts = coo.split(", ");
                                        String latitud = parts[0];
                                        String longitud = parts[1];
                                        //Si es elemento accesible marcador verde, si no rojo
                                        if(listaElementos.get(i).getAccesible() == 1) {
                                            //Drawable icon = getResources().getDrawable(R.mipmap.marker_departure);
                                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                            GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                                            startMarker.setTitle(listaElementos.get(i).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(i).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        } else {
                                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                            GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                                            startMarker.setTitle(listaElementos.get(i).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(i).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", t.getMessage());
                            }
                        });
                    } else {
                        apiService.elementosNecesidadUsuario(id).enqueue(new Callback<List<Elemento>>() {
                            @Override
                            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                                Log.i("Cliente", "Cliente Android");
                                if (response.isSuccessful()) {
                                    listaElementos = new ArrayList<>(response.body());
                                    for(int i=0; i < listaElementos.size(); i++) {
                                        String coo = listaElementos.get(i).getCoordenada();
                                        String[] parts = coo.split(", ");
                                        String latitud = parts[0];
                                        String longitud = parts[1];

                                        if(listaElementos.get(i).getAccesible() == 1) {
                                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                            GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(i).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(i).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        } else {
                                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                            GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(i).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(i).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", t.getMessage());
                            }
                        });
                    }


                    View headerView = navigationView.getHeaderView(0);
                    TextView navName = (TextView) headerView.findViewById(R.id.tvNombre);
                    TextView navUser = (TextView) headerView.findViewById(R.id.tvUsuario);
                    ImageView navFoto = (ImageView) headerView.findViewById(R.id.imageView);
                    System.out.println(nav_nombre);
                    navName.setText(nav_nombre);
                    navUser.setText(nav_usuario);
                    //navFoto.setImageBitmap(nav_foto);
                    if(usuario.getFoto() != null) {
                        navFoto.setImageBitmap(nav_foto);
                    } else {
                        navFoto.setImageDrawable(nav_foto2);
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error", t.getMessage());

            }
        });

        //Mostrar mapa y sus opciones
        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        //Mi localización
        miPosicion = new MyLocationNewOverlay(map);
        miPosicion.enableMyLocation();
        miPosicion.enableFollowLocation();
        IMyLocationProvider s= miPosicion.getMyLocationProvider();
        miPosicion.getMyLocation();
        map.getOverlays().add(miPosicion);

        mapController = map.getController();
        mapController.setZoom(15.0);

        //Buscar lugares introducidos por teclado en el buscador
        final EditText salida = findViewById(R.id.etOrigen);
        Button origenBoton = (Button)findViewById(R.id.butOrigen);
        origenBoton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                miPosicion.disableFollowLocation();
                handleSearchButton(START_INDEX, R.id.etOrigen);

            }
        });

        final EditText llegada = findViewById(R.id.etDestino);
        Button destinoBoton = (Button)findViewById(R.id.butDestino);
        destinoBoton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                miPosicion.disableFollowLocation();
                handleSearchButton(DEST_INDEX, R.id.etDestino);
            }
        });
        //Expandir y contraer buscador
        View expander = findViewById(R.id.expandir);
        expander.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                View searchPanel = findViewById(R.id.layout_introducirdatos);
                if (searchPanel.getVisibility() == View.VISIBLE){
                    searchPanel.setVisibility(View.GONE);
                } else {
                    searchPanel.setVisibility(View.VISIBLE);
                }
            }
        });

        context = this;

        //Botón calcular ruta desde posición actual
        rutaActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calcular ruta, con RoadManager
                //Añadimos las opciones que queramos para calcular la ruta
                RoadManager roadManager = new MapQuestRoadManager("zxeoh6HZ8UFl8mtW1YFm9nEQa67XtOTz");
                roadManager.addRequestOption("unit=k");
                roadManager.addRequestOption("routeType=pedestrian");
                roadManager.addRequestOption("locale=es_ES");

                //Creamos arraylist con punto inicial y final, y calculamos ruta
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(miPosicion.getMyLocation());
                waypoints.add(endPoint);

                road = roadManager.getRoad(waypoints);
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
                map.invalidate();

                //Calculamos elementos de accesibilidad presentes en el cuadro entre punto inicio y fin
                Double longitudInicio = miPosicion.getMyLocation().getLongitude();
                Double latitudInicio = miPosicion.getMyLocation().getLatitude();
                Double longitudFinal = endPoint.getLongitude();
                Double latitudFinal = endPoint.getLatitude();
                Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                if(longitudInicio < longitudFinal) {
                    longitudMayor = longitudFinal;
                    longitudMenor = longitudInicio;
                } else {
                    longitudMayor = longitudInicio;
                    longitudMenor = longitudFinal;
                }

                if(latitudInicio < latitudFinal) {
                    latitudMayor = latitudFinal;
                    latitudMenor = latitudInicio;
                } else {
                    latitudMayor = latitudInicio;
                    latitudMenor = latitudFinal;
                }
                //Guardamos los puntos al azar de la ruta que nos devuelve al calcular la ruta
                puntosRuta = road.mRouteHigh;

                apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                    @Override
                    public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                        if(response.isSuccessful()) {
                            elementosRecuadroRuta = new ArrayList<>(response.body());
                            //Calculamos la mayor distancia entre puntos consecutivos de los puntos guardados de la ruta
                            Double mayor=0.0;
                            for(int i=0; i<(puntosRuta.size()-1); i++) {
                                Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                if(dist > mayor) {
                                    mayor=dist;
                                }
                            }
                            mayor = mayor/2;
                            //Comprobamos la distancia entre los elementos del recuadro y los puntos de la ruta
                            for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                for(int y=0; y<puntosRuta.size(); y++) {
                                    Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                    //Si la distancia es menor que la mayor distancia entre los puntos consecutivos, ese elemento estará en la ruta
                                    if(dist2<=mayor) {
                                        elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                        break;
                                    }
                                }
                            }

                            map.getOverlays().clear();
                            map.getOverlays().add(roadOverlay);
                            map.getOverlays().add(miPosicion);
                            //Añadimos el marker de salida (en este caso la posición actual) al mapa, con la ruta
                            Drawable nodeIcon1 = getResources().getDrawable(R.mipmap.marker_departure);
                            GeoPoint point2 = new GeoPoint(miPosicion.getMyLocation());
                            startMarker = new Marker(map);
                            startMarker.setPosition(point2);
                            startMarker.setIcon(nodeIcon1);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            map.getOverlays().add(startMarker);

                            map.getOverlays().add(finalMarker);
                            //map.getOverlays().rem
                            map.invalidate();

                            //Recorremos los elementos que están en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                            for(int z=0; z<elementosEnRuta.size();z++) {
                                for(int w=0; w<listaElementos.size();w++) {
                                    if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                        //Mostar
                                        map.getOverlays().clear();

                                        if(listaElementos.get(w).getAccesible() == 1) {
                                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        } else {
                                            numElementos++;
                                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        }

                                    }
                                }
                            }

                            //Mostramos las indicaciones de la ruta, que nos devuelve MapQuest API, al calcular ruta
                            Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                            for (int i=0; i<road.mNodes.size(); i++){
                                RoadNode node = road.mNodes.get(i);
                                Marker nodeMarker = new Marker(map);
                                nodeMarker.setPosition(node.mLocation);
                                nodeMarker.setIcon(nodeIcon);
                                nodeMarker.setTitle("Step "+i);
                                nodeMarker.setSnippet(node.mInstructions);
                                nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                if(node.mManeuverType == 1) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 3) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 4) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 5) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 6) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 7) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 8) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                    nodeMarker.setImage(icon);
                                }
                                map.getOverlays().add(nodeMarker);
                            }

                            //Mostramos al usuario la información de accesibilidad de esa ruta, según la cantidad de elementos presentes en la ruta
                            if(numElementos < 3) {
                            //if(elementosEnRuta.size() < 3) {
                                imagenDificultad.setColorFilter(Color.GREEN);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Accesible");
                            } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                imagenDificultad.setColorFilter(Color.YELLOW);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Intermedio");
                            } else {
                                imagenDificultad.setColorFilter(Color.RED);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("No accesible");
                            }
                            layout_cancelar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Elemento>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("Error", t.getMessage());
                    }
                });

                layout_primero.setVisibility(View.GONE);
                layout_informacion.setVisibility(View.VISIBLE);
                layout_itinerario.setVisibility(View.VISIBLE);

                enRuta = true;

            }
        });



        //Ruta desde punto actual en bici
        biciActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calcular ruta
                //RoadManager roadManager = new OSRMRoadManager(context);
                RoadManager roadManager = new MapQuestRoadManager("zxeoh6HZ8UFl8mtW1YFm9nEQa67XtOTz");

                roadManager.addRequestOption("unit=k");
                roadManager.addRequestOption("routeType=bicycle");
                roadManager.addRequestOption("locale=es_ES");

                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(miPosicion.getMyLocation());
                waypoints.add(endPoint);

                road = roadManager.getRoad(waypoints);
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
                map.invalidate();

                Double longitudInicio = miPosicion.getMyLocation().getLongitude();
                Double latitudInicio = miPosicion.getMyLocation().getLatitude();
                Double longitudFinal = endPoint.getLongitude();
                Double latitudFinal = endPoint.getLatitude();
                Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                if(longitudInicio < longitudFinal) {
                    longitudMayor = longitudFinal;
                    longitudMenor = longitudInicio;
                } else {
                    longitudMayor = longitudInicio;
                    longitudMenor = longitudFinal;
                }

                if(latitudInicio < latitudFinal) {
                    latitudMayor = latitudFinal;
                    latitudMenor = latitudInicio;
                } else {
                    latitudMayor = latitudInicio;
                    latitudMenor = latitudFinal;
                }

                puntosRuta = road.mRouteHigh;

                apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                    @Override
                    public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                        if(response.isSuccessful()) {
                            elementosRecuadroRuta = new ArrayList<>(response.body());

                            Double mayor=0.0;
                            for(int i=0; i<(puntosRuta.size()-1); i++) {
                                Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                if(dist > mayor) {
                                    mayor=dist;
                                }
                            }
                            mayor = mayor/2;

                            for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                for(int y=0; y<puntosRuta.size(); y++) {
                                    Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                    if(dist2<=mayor) {
                                        elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                        break;
                                    }
                                }
                            }

                            map.getOverlays().clear();
                            map.getOverlays().add(roadOverlay);

                            map.getOverlays().add(miPosicion);

                            Drawable nodeIcon1 = getResources().getDrawable(R.mipmap.marker_departure);
                            GeoPoint point2 = new GeoPoint(miPosicion.getMyLocation());
                            startMarker = new Marker(map);
                            startMarker.setPosition(point2);
                            startMarker.setIcon(nodeIcon1);
                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            map.getOverlays().add(startMarker);

                            map.getOverlays().add(finalMarker);
                            //map.getOverlays().rem
                            map.invalidate();

                            //Recorremos los elementos que estan en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                            for(int z=0; z<elementosEnRuta.size();z++) {
                                for(int w=0; w<listaElementos.size();w++) {
                                    if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                        //Mostrar
                                        map.getOverlays().clear();

                                        if(listaElementos.get(w).getAccesible() == 1) {
                                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        } else {
                                            numElementos++;
                                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        }

                                    }
                                }
                            }

                            Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                            for (int i=0; i<road.mNodes.size(); i++){
                                RoadNode node = road.mNodes.get(i);
                                Marker nodeMarker = new Marker(map);
                                nodeMarker.setPosition(node.mLocation);
                                nodeMarker.setIcon(nodeIcon);
                                nodeMarker.setTitle("Step "+i);
                                nodeMarker.setSnippet(node.mInstructions);
                                nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                if(node.mManeuverType == 1) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 3) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 4) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 5) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 6) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 7) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 8) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                    nodeMarker.setImage(icon);
                                }
                                map.getOverlays().add(nodeMarker);
                            }

                            if(numElementos < 3) {
                            //if(elementosEnRuta.size() < 3) {
                                imagenDificultad.setColorFilter(Color.GREEN);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Accesible");
                            } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                imagenDificultad.setColorFilter(Color.YELLOW);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Intermedio");
                            } else {
                                imagenDificultad.setColorFilter(Color.RED);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("No accesible");
                            }
                            layout_cancelar.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Elemento>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("Error", t.getMessage());
                    }
                });


                layout_primero.setVisibility(View.GONE);
                layout_informacion.setVisibility(View.VISIBLE);
                layout_itinerario.setVisibility(View.VISIBLE);
                layout_cancelar.setVisibility(View.VISIBLE);
                enRuta = true;

            }
        });

        //Botón introducir inicio
        rutaConInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Poner inicio y calcular ruta
                destino = true;
            }
        });

        bici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoadManager roadManager = new MapQuestRoadManager("zxeoh6HZ8UFl8mtW1YFm9nEQa67XtOTz");

                roadManager.addRequestOption("unit=k");
                roadManager.addRequestOption("routeType=bicycle");
                roadManager.addRequestOption("locale=es_ES");

                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(startPoint);
                waypoints.add(endPoint);

                road = roadManager.getRoad(waypoints);
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
                map.invalidate();

                Double longitudInicio = startPoint.getLongitude();
                Double latitudInicio = startPoint.getLatitude();
                Double longitudFinal = endPoint.getLongitude();
                Double latitudFinal = endPoint.getLatitude();
                Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                if(longitudInicio < longitudFinal) {
                    longitudMayor = longitudFinal;
                    longitudMenor = longitudInicio;
                } else {
                    longitudMayor = longitudInicio;
                    longitudMenor = longitudFinal;
                }

                if(latitudInicio < latitudFinal) {
                    latitudMayor = latitudFinal;
                    latitudMenor = latitudInicio;
                } else {
                    latitudMayor = latitudInicio;
                    latitudMenor = latitudFinal;
                }

                puntosRuta = road.mRouteHigh;

                apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                    @Override
                    public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                        if(response.isSuccessful()) {
                            elementosRecuadroRuta = new ArrayList<>(response.body());
                            System.out.println("tamaño array: " + elementosRecuadroRuta.size());

                            Double mayor=0.0;
                            for(int i=0; i<(puntosRuta.size()-1); i++) {
                                Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                if(dist > mayor) {
                                    mayor=dist;
                                }
                            }
                            mayor = mayor/2;

                            for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                for(int y=0; y<puntosRuta.size(); y++) {
                                    Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                    if(dist2<=mayor) {
                                        elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                        break;
                                    }
                                }
                            }

                            map.getOverlays().clear();
                            map.getOverlays().add(roadOverlay);
                            map.getOverlays().add(startMarker);
                            map.getOverlays().add(finalMarker);

                            map.getOverlays().add(miPosicion);
                            //map.getOverlays().rem
                            map.invalidate();

                            System.out.println("elementos en la ruta: " + elementosEnRuta.size());
                            //Recorremos los elementos que están en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                            for(int z=0; z<elementosEnRuta.size();z++) {
                                for(int w=0; w<listaElementos.size();w++) {
                                    if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                        //Mostrar
                                        if(listaElementos.get(w).getAccesible() == 1) {
                                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        } else {
                                            numElementos++;
                                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        }

                                        map.invalidate();

                                    }
                                }
                            }

                            Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                            for (int i=0; i<road.mNodes.size(); i++){
                                RoadNode node = road.mNodes.get(i);
                                Marker nodeMarker = new Marker(map);
                                nodeMarker.setPosition(node.mLocation);
                                nodeMarker.setIcon(nodeIcon);
                                nodeMarker.setTitle("Step "+i);
                                nodeMarker.setSnippet(node.mInstructions);
                                nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                if(node.mManeuverType == 1) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 3) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 4) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 5) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 6) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 7) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 8) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                    nodeMarker.setImage(icon);
                                }
                                map.getOverlays().add(nodeMarker);
                            }

                            if(numElementos < 3) {
                            //if(elementosEnRuta.size() < 3) {
                                imagenDificultad.setColorFilter(Color.GREEN);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Accesible");
                            } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                imagenDificultad.setColorFilter(Color.YELLOW);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Intermedio");
                            } else {
                                imagenDificultad.setColorFilter(Color.RED);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("No accesible");
                            }
                            layout_cancelar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Elemento>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("Error", t.getMessage());
                    }
                });

                layout_segundo.setVisibility(View.GONE);
                layout_cancelar.setVisibility(View.VISIBLE);
                layout_informacion.setVisibility(View.VISIBLE);
                layout_itinerario.setVisibility(View.VISIBLE);
                enRuta = true;
            }
        });

        //Botón calcular ruta con inicio
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RoadManager roadManager = new OSRMRoadManager(context);
                RoadManager roadManager = new SergiMapQuestRoadManager("zxeoh6HZ8UFl8mtW1YFm9nEQa67XtOTz");

                roadManager.addRequestOption("unit=k");
                roadManager.addRequestOption("routeType=pedestrian");
                roadManager.addRequestOption("locale=es_ES");

                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(startPoint);
                waypoints.add(endPoint);

                road = roadManager.getRoad(waypoints);
                roadOverlay = RoadManager.buildRoadOverlay(road);
                map.getOverlays().add(roadOverlay);
                map.invalidate();

                Double longitudInicio = startPoint.getLongitude();
                Double latitudInicio = startPoint.getLatitude();
                Double longitudFinal = endPoint.getLongitude();
                Double latitudFinal = endPoint.getLatitude();
                Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                if(longitudInicio < longitudFinal) {
                    longitudMayor = longitudFinal;
                    longitudMenor = longitudInicio;
                } else {
                    longitudMayor = longitudInicio;
                    longitudMenor = longitudFinal;
                }

                if(latitudInicio < latitudFinal) {
                    latitudMayor = latitudFinal;
                    latitudMenor = latitudInicio;
                } else {
                    latitudMayor = latitudInicio;
                    latitudMenor = latitudFinal;
                }

                //Guardamos en una variable los puntos de esa ruta que están en la propiedad mRouteHigh de RoadManager
                puntosRuta = road.mRouteHigh;
                //Calculamos los elementos dentro del cuadrado entre el punto de inicio y fin
                apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                    @Override
                    public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                        if(response.isSuccessful()) {
                            elementosRecuadroRuta = new ArrayList<>(response.body());
                            //Calculamos la mayor distancia entre los puntos consecutivos de la ruta
                            Double mayor=0.0;
                            for(int i=0; i<(puntosRuta.size()-1); i++) {
                                Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                if(dist > mayor) {
                                    mayor=dist;
                                }
                            }
                            mayor = mayor/2;
                            //Calculamos la distancia entre cada elemento del recuadro y los puntos de la ruta
                            for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                for(int y=0; y<puntosRuta.size(); y++) {
                                    Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                    //Si la distancia es menor que la mayor distancia entre 2 puntos consecutivos lo añadimos como elemento de la ruta
                                    if(dist2<=mayor) {
                                        //Añadir marker que este confirmado y del tipo del usuario
                                        //Hacer una lista de elementos que estén en la ruta, sin restricciones
                                        elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                        break;
                                    }
                                }
                            }

                            map.getOverlays().clear();
                            map.getOverlays().add(roadOverlay);
                            map.getOverlays().add(startMarker);
                            map.getOverlays().add(finalMarker);
                            map.getOverlays().add(miPosicion);
                            map.invalidate();

                            //Recorremos los elementos que están en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                            for(int z=0; z<elementosEnRuta.size();z++) {
                                for(int w=0; w<listaElementos.size();w++) {
                                    if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                        //Mostrar
                                        if(listaElementos.get(w).getAccesible() == 1) {
                                            Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        } else {
                                            numElementos++;
                                            Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                            GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                            Marker startMarker = new Marker(map);
                                            startMarker.setPosition(point);
                                            startMarker.setIcon(icon);
                                            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                            startMarker.setTitle(listaElementos.get(w).getNombre());
                                            startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                            map.getOverlays().add(startMarker);
                                        }
                                        map.invalidate();
                                    }
                                }
                            }

                            Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                            for (int i=0; i<road.mNodes.size(); i++){
                                RoadNode node = road.mNodes.get(i);
                                Marker nodeMarker = new Marker(map);
                                nodeMarker.setPosition(node.mLocation);
                                nodeMarker.setIcon(nodeIcon);
                                nodeMarker.setTitle("Step "+i);
                                nodeMarker.setSnippet(node.mInstructions);
                                nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                if(node.mManeuverType == 1) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 3) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 4) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 5) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 6) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 7) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 8) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                    nodeMarker.setImage(icon);
                                } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                    Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                    nodeMarker.setImage(icon);
                                }
                                map.getOverlays().add(nodeMarker);
                            }

                            System.out.println("Número elementos no accesibles: " + numElementos);

                            if(numElementos < 3) {
                            //if(elementosEnRuta.size() < 3) {
                                imagenDificultad.setColorFilter(Color.GREEN);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Accesible");
                            } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                imagenDificultad.setColorFilter(Color.YELLOW);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("Intermedio");
                            } else {
                                imagenDificultad.setColorFilter(Color.RED);
                                cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                cantidad2.setText(numElementos + " no accesibles");
                                dificultadRuta.setText("No accesible");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Elemento>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("Error", t.getMessage());
                    }
                });

                layout_segundo.setVisibility(View.GONE);
                layout_cancelar.setVisibility(View.VISIBLE);

                layout_informacion.setVisibility(View.VISIBLE);
                layout_itinerario.setVisibility(View.VISIBLE);
                enRuta = true;
            }
        });



        //Botón calcular ruta desde punto actual introducidos por teclado
        calcular_ruta_escritura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RoadManager roadManager = new OSRMRoadManager(context);
                RoadManager roadManager = new SergiMapQuestRoadManager("zxeoh6HZ8UFl8mtW1YFm9nEQa67XtOTz");

                roadManager.addRequestOption("unit=k");
                roadManager.addRequestOption("routeType=pedestrian");
                roadManager.addRequestOption("locale=es_ES");

                //Si salida y llegada están vacías
                if(TextUtils.isEmpty(salida.getText().toString()) && TextUtils.isEmpty(llegada.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Introduzca salida y llegada.", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(salida.getText().toString()) && !TextUtils.isEmpty(llegada.getText().toString())) {
                    //Si la salida esta vacía y la llegada no, la salida por defecto es el punto actual gps
                    //Salida desde ubicación actual
                    //Primero el usuario tiene que haberle dado a buscar la llegada
                    if(buscarLLegada == true) {
                        Toast.makeText(getApplicationContext(), "Salida desde punto actual.", Toast.LENGTH_SHORT).show();

                        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                        waypoints.add(miPosicion.getMyLocation());
                        waypoints.add(endPoint);
                        System.out.println(miPosicion.getMyLocation());
                        System.out.println(endPoint);

                        road = roadManager.getRoad(waypoints);
                        roadOverlay = RoadManager.buildRoadOverlay(road);
                        map.getOverlays().add(roadOverlay);
                        map.invalidate();

                        Double longitudInicio = miPosicion.getMyLocation().getLongitude();
                        Double latitudInicio = miPosicion.getMyLocation().getLatitude();
                        Double longitudFinal = endPoint.getLongitude();
                        Double latitudFinal = endPoint.getLatitude();
                        Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                        if(longitudInicio < longitudFinal) {
                            longitudMayor = longitudFinal;
                            longitudMenor = longitudInicio;
                        } else {
                            longitudMayor = longitudInicio;
                            longitudMenor = longitudFinal;
                        }

                        if(latitudInicio < latitudFinal) {
                            latitudMayor = latitudFinal;
                            latitudMenor = latitudInicio;
                        } else {
                            latitudMayor = latitudInicio;
                            latitudMenor = latitudFinal;
                        }

                        puntosRuta = road.mRouteHigh;

                        apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                            @Override
                            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                                if(response.isSuccessful()) {
                                    elementosRecuadroRuta = new ArrayList<>(response.body());

                                    Double mayor=0.0;
                                    for(int i=0; i<(puntosRuta.size()-1); i++) {
                                        Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                        if(dist > mayor) {
                                            mayor=dist;
                                        }
                                    }
                                    mayor = mayor/2;

                                    for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                        for(int y=0; y<puntosRuta.size(); y++) {
                                            Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                            if(dist2<=mayor) {
                                                //Añadir marker que este confirmado y del tipo del usuario
                                                //Hacer una lista de elementos que estén en la ruta, sin restricciones
                                                elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                                break;
                                            }
                                        }
                                    }

                                    map.getOverlays().clear();
                                    map.getOverlays().add(roadOverlay);

                                    map.getOverlays().add(miPosicion);

                                    Drawable nodeIcon1 = getResources().getDrawable(R.mipmap.marker_departure);
                                    GeoPoint point2 = new GeoPoint(miPosicion.getMyLocation());
                                    startMarker = new Marker(map);
                                    startMarker.setPosition(point2);
                                    startMarker.setIcon(nodeIcon1);
                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(startMarker);

                                    map.getOverlays().add(finalMarker);
                                    //map.getOverlays().rem
                                    map.invalidate();

                                    //Recorremos los elementos que están en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                                    for(int z=0; z<elementosEnRuta.size();z++) {
                                        for(int w=0; w<listaElementos.size();w++) {
                                            if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                                //Mostrar
                                                map.getOverlays().clear();

                                                if(listaElementos.get(w).getAccesible() == 1) {
                                                    Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                } else {
                                                    numElementos++;
                                                    Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                }

                                            }
                                        }
                                    }

                                    Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                                    for (int i=0; i<road.mNodes.size(); i++){
                                        RoadNode node = road.mNodes.get(i);
                                        Marker nodeMarker = new Marker(map);
                                        nodeMarker.setPosition(node.mLocation);
                                        nodeMarker.setIcon(nodeIcon);
                                        nodeMarker.setTitle("Step "+i);
                                        nodeMarker.setSnippet(node.mInstructions);
                                        nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                        if(node.mManeuverType == 1) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 3) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 4) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 5) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 6) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 7) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 8) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                            nodeMarker.setImage(icon);
                                        }
                                        map.getOverlays().add(nodeMarker);
                                    }

                                    if(numElementos < 3) {
                                    //if(elementosEnRuta.size() < 3) {
                                        imagenDificultad.setColorFilter(Color.GREEN);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Accesible");
                                    } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                        imagenDificultad.setColorFilter(Color.YELLOW);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Intermedio");
                                    } else {
                                        imagenDificultad.setColorFilter(Color.RED);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("No accesible");
                                    }
                                    layout_cancelar.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", t.getMessage());
                            }
                        });

                        layout_primero.setVisibility(View.GONE);
                        layout_informacion.setVisibility(View.VISIBLE);
                        layout_itinerario.setVisibility(View.VISIBLE);

                        enRuta = true;

                    } else {
                        Toast.makeText(getApplicationContext(), "Busque llegada.", Toast.LENGTH_SHORT).show();
                    }


                } else if(!TextUtils.isEmpty(salida.getText().toString()) && TextUtils.isEmpty(llegada.getText().toString())) {
                    //Esta vacía la llegada
                    Toast.makeText(getApplicationContext(), "Introduzca llegada.", Toast.LENGTH_SHORT).show();
                } else {
                    //Tanto salida como llegada rellenados y además se ha dado a busca en ambas
                    if(buscarLLegada == true && buscarSalida == true) {
                        //Ruta con salida y llegada
                        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                        waypoints.add(startPoint);
                        waypoints.add(endPoint);

                        road = roadManager.getRoad(waypoints);
                        roadOverlay = RoadManager.buildRoadOverlay(road);
                        map.getOverlays().add(roadOverlay);
                        map.invalidate();

                        Double longitudInicio = startPoint.getLongitude();
                        Double latitudInicio = startPoint.getLatitude();
                        Double longitudFinal = endPoint.getLongitude();
                        Double latitudFinal = endPoint.getLatitude();
                        Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                        if(longitudInicio < longitudFinal) {
                            longitudMayor = longitudFinal;
                            longitudMenor = longitudInicio;
                        } else {
                            longitudMayor = longitudInicio;
                            longitudMenor = longitudFinal;
                        }

                        if(latitudInicio < latitudFinal) {
                            latitudMayor = latitudFinal;
                            latitudMenor = latitudInicio;
                        } else {
                            latitudMayor = latitudInicio;
                            latitudMenor = latitudFinal;
                        }

                        puntosRuta = road.mRouteHigh;

                        apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                            @Override
                            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                                if(response.isSuccessful()) {
                                    elementosRecuadroRuta = new ArrayList<>(response.body());
                                    System.out.println("tamaño array: " + elementosRecuadroRuta.size());

                                    Double mayor=0.0;
                                    for(int i=0; i<(puntosRuta.size()-1); i++) {
                                        Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                        if(dist > mayor) {
                                            mayor=dist;
                                        }
                                    }
                                    mayor = mayor/2;

                                    for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                        for(int y=0; y<puntosRuta.size(); y++) {
                                            Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                            if(dist2<=mayor) {
                                                elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                                break;
                                            }
                                        }
                                    }

                                    map.getOverlays().clear();
                                    map.getOverlays().add(roadOverlay);
                                    map.getOverlays().add(startMarker);
                                    map.getOverlays().add(finalMarker);

                                    map.getOverlays().add(miPosicion);
                                    //map.getOverlays().rem
                                    map.invalidate();

                                    System.out.println("elementos en la ruta: " + elementosEnRuta.size());
                                    //Recorremos los elementos que están en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                                    for(int z=0; z<elementosEnRuta.size();z++) {
                                        for(int w=0; w<listaElementos.size();w++) {
                                            if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                                //Mostrar
                                                if(listaElementos.get(w).getAccesible() == 1) {
                                                    Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                } else {
                                                    numElementos++;
                                                    Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                }

                                                map.invalidate();
                                            }
                                        }
                                    }

                                    Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                                    for (int i=0; i<road.mNodes.size(); i++){
                                        RoadNode node = road.mNodes.get(i);
                                        Marker nodeMarker = new Marker(map);
                                        nodeMarker.setPosition(node.mLocation);
                                        nodeMarker.setIcon(nodeIcon);
                                        nodeMarker.setTitle("Step "+i);
                                        nodeMarker.setSnippet(node.mInstructions);
                                        nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                        if(node.mManeuverType == 1) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 3) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 4) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 5) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 6) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 7) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 8) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                            nodeMarker.setImage(icon);
                                        }
                                        map.getOverlays().add(nodeMarker);
                                    }

                                    if(numElementos < 3) {
                                    //if(elementosEnRuta.size() < 3) {
                                        imagenDificultad.setColorFilter(Color.GREEN);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Accesible");
                                    } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                        imagenDificultad.setColorFilter(Color.YELLOW);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Intermedio");
                                    } else {
                                        imagenDificultad.setColorFilter(Color.RED);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("No accesible");
                                    }
                                }



                            }

                            @Override
                            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", t.getMessage());
                            }
                        });

                        layout_segundo.setVisibility(View.GONE);
                        layout_cancelar.setVisibility(View.VISIBLE);

                        layout_informacion.setVisibility(View.VISIBLE);
                        layout_itinerario.setVisibility(View.VISIBLE);
                        enRuta = true;

                    } else {
                        Toast.makeText(getApplicationContext(), "Busque salida y llegada.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Botón calcular ruta desde posición actual introducidos por teclado
        calcular_ruta_escritura_bici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RoadManager roadManager = new OSRMRoadManager(context);
                RoadManager roadManager = new SergiMapQuestRoadManager("zxeoh6HZ8UFl8mtW1YFm9nEQa67XtOTz");

                roadManager.addRequestOption("unit=k");
                roadManager.addRequestOption("routeType=bicycle");
                roadManager.addRequestOption("locale=es_ES");

                if(TextUtils.isEmpty(salida.getText().toString()) && TextUtils.isEmpty(llegada.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Introduzca salida y llegada.", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(salida.getText().toString()) && !TextUtils.isEmpty(llegada.getText().toString())) {
                    //Salida desde ubicación actual
                    Toast.makeText(getApplicationContext(), "Salida desde punto actual.", Toast.LENGTH_SHORT).show();

                    if(buscarLLegada == true) {
                        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                        waypoints.add(miPosicion.getMyLocation());
                        waypoints.add(endPoint);

                        road = roadManager.getRoad(waypoints);
                        roadOverlay = RoadManager.buildRoadOverlay(road);
                        map.getOverlays().add(roadOverlay);
                        map.invalidate();

                        Double longitudInicio = miPosicion.getMyLocation().getLongitude();
                        Double latitudInicio = miPosicion.getMyLocation().getLatitude();
                        Double longitudFinal = endPoint.getLongitude();
                        Double latitudFinal = endPoint.getLatitude();
                        Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                        if(longitudInicio < longitudFinal) {
                            longitudMayor = longitudFinal;
                            longitudMenor = longitudInicio;
                        } else {
                            longitudMayor = longitudInicio;
                            longitudMenor = longitudFinal;
                        }

                        if(latitudInicio < latitudFinal) {
                            latitudMayor = latitudFinal;
                            latitudMenor = latitudInicio;
                        } else {
                            latitudMayor = latitudInicio;
                            latitudMenor = latitudFinal;
                        }

                        puntosRuta = road.mRouteHigh;

                        apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                            @Override
                            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                                if(response.isSuccessful()) {
                                    elementosRecuadroRuta = new ArrayList<>(response.body());

                                    Double mayor=0.0;
                                    for(int i=0; i<(puntosRuta.size()-1); i++) {
                                        Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                        if(dist > mayor) {
                                            mayor=dist;
                                        }
                                    }
                                    mayor = mayor/2;

                                    for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                        for(int y=0; y<puntosRuta.size(); y++) {
                                            Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                            if(dist2<=mayor) {
                                                //Añadir marker que este confirmado y del tipo del usuario
                                                //Hacer una lista de elementos que esten en la ruta, sin restricciones
                                                elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                                break;
                                            }
                                        }
                                    }

                                    map.getOverlays().clear();
                                    map.getOverlays().add(roadOverlay);

                                    map.getOverlays().add(miPosicion);

                                    Drawable nodeIcon1 = getResources().getDrawable(R.mipmap.marker_departure);
                                    GeoPoint point2 = new GeoPoint(miPosicion.getMyLocation());
                                    startMarker = new Marker(map);
                                    startMarker.setPosition(point2);
                                    startMarker.setIcon(nodeIcon1);
                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                    map.getOverlays().add(startMarker);

                                    map.getOverlays().add(finalMarker);
                                    //map.getOverlays().rem
                                    map.invalidate();

                                    //Recorremos los elementos que están en la ruta y miramos si estan confirmados o son de la accesibilidad del usuario
                                    for(int z=0; z<elementosEnRuta.size();z++) {
                                        for(int w=0; w<listaElementos.size();w++) {
                                            if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                                //Mostrar
                                                map.getOverlays().clear();

                                                if(listaElementos.get(w).getAccesible() == 1) {
                                                    Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                } else {
                                                    numElementos++;
                                                    Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                }

                                                /*
                                                GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                Marker startMarker = new Marker(map);
                                                startMarker.setPosition(point);
                                                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                startMarker.setTitle(listaElementos.get(w).getNombre());
                                                startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                map.getOverlays().add(startMarker);
                                                */
                                            }
                                        }
                                    }

                                    Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                                    for (int i=0; i<road.mNodes.size(); i++){
                                        RoadNode node = road.mNodes.get(i);
                                        Marker nodeMarker = new Marker(map);
                                        nodeMarker.setPosition(node.mLocation);
                                        nodeMarker.setIcon(nodeIcon);
                                        nodeMarker.setTitle("Step "+i);
                                        nodeMarker.setSnippet(node.mInstructions);
                                        nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                        if(node.mManeuverType == 1) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 3) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 4) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 5) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 6) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 7) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 8) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                            nodeMarker.setImage(icon);
                                        }
                                        map.getOverlays().add(nodeMarker);
                                    }

                                    if(numElementos < 3) {
                                    //if(elementosEnRuta.size() < 3) {
                                        imagenDificultad.setColorFilter(Color.GREEN);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Accesible");
                                    } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                        imagenDificultad.setColorFilter(Color.YELLOW);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Intermedio");
                                    } else {
                                        imagenDificultad.setColorFilter(Color.RED);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("No accesible");
                                    }
                                    layout_cancelar.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", t.getMessage());
                            }
                        });


                        layout_primero.setVisibility(View.GONE);
                        layout_informacion.setVisibility(View.VISIBLE);
                        layout_itinerario.setVisibility(View.VISIBLE);

                        enRuta = true;
                        //Cancelar ruta
                    } else {
                        Toast.makeText(getApplicationContext(), "Busque llegada.", Toast.LENGTH_SHORT).show();
                    }

                } else if(!TextUtils.isEmpty(salida.getText().toString()) && TextUtils.isEmpty(llegada.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Introduzca llegada.", Toast.LENGTH_SHORT).show();
                } else {
                    //Ruta con salida y llegada

                    if(buscarLLegada == true && buscarSalida == true) {
                        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                        waypoints.add(startPoint);
                        waypoints.add(endPoint);

                        road = roadManager.getRoad(waypoints);
                        roadOverlay = RoadManager.buildRoadOverlay(road);
                        map.getOverlays().add(roadOverlay);
                        map.invalidate();

                        Double longitudInicio = startPoint.getLongitude();
                        Double latitudInicio = startPoint.getLatitude();
                        Double longitudFinal = endPoint.getLongitude();
                        Double latitudFinal = endPoint.getLatitude();
                        Double longitudMenor, longitudMayor, latitudMenor, latitudMayor;

                        if(longitudInicio < longitudFinal) {
                            longitudMayor = longitudFinal;
                            longitudMenor = longitudInicio;
                        } else {
                            longitudMayor = longitudInicio;
                            longitudMenor = longitudFinal;
                        }

                        if(latitudInicio < latitudFinal) {
                            latitudMayor = latitudFinal;
                            latitudMenor = latitudInicio;
                        } else {
                            latitudMayor = latitudInicio;
                            latitudMenor = latitudFinal;
                        }

                        puntosRuta = road.mRouteHigh;

                        apiService.elementosEntreInicioYFin(longitudMenor, latitudMenor, longitudMayor, latitudMayor).enqueue(new Callback<List<Elemento>>() {
                            @Override
                            public void onResponse(Call<List<Elemento>> call, Response<List<Elemento>> response) {
                                if(response.isSuccessful()) {
                                    elementosRecuadroRuta = new ArrayList<>(response.body());
                                    System.out.println("tamaño array: " + elementosRecuadroRuta.size());

                                    Double mayor=0.0;
                                    for(int i=0; i<(puntosRuta.size()-1); i++) {
                                        Double dist = getDistanceFromLatLonInKm(puntosRuta.get(i).getLatitude(),puntosRuta.get(i).getLongitude(),puntosRuta.get(i+1).getLatitude(),puntosRuta.get(i+1).getLongitude());
                                        if(dist > mayor) {
                                            mayor=dist;
                                        }
                                    }
                                    mayor = mayor/2;

                                    for(int x=0; x<elementosRecuadroRuta.size();x++) {
                                        for(int y=0; y<puntosRuta.size(); y++) {
                                            Double dist2 = getDistanceFromLatLonInKm(elementosRecuadroRuta.get(x).getLatitud(),elementosRecuadroRuta.get(x).getLongitud(),puntosRuta.get(y).getLatitude(),puntosRuta.get(y).getLongitude());
                                            if(dist2<=mayor) {
                                                //Añadir marker que este confirmado y del tipo del usuario
                                                //Hacer una lista de elementos que esten en la ruta, sin restricciones
                                                elementosEnRuta.add(elementosRecuadroRuta.get(x));
                                                break;
                                            }
                                        }
                                    }

                                    map.getOverlays().clear();
                                    map.getOverlays().add(roadOverlay);
                                    map.getOverlays().add(startMarker);
                                    map.getOverlays().add(finalMarker);

                                    map.getOverlays().add(miPosicion);
                                    //map.getOverlays().rem
                                    map.invalidate();

                                    System.out.println("elementos en la ruta: " + elementosEnRuta.size());
                                    //Recorremos los elementos que están en la ruta y miramos si están confirmados o son de la accesibilidad del usuario
                                    for(int z=0; z<elementosEnRuta.size();z++) {
                                        for(int w=0; w<listaElementos.size();w++) {
                                            if(elementosEnRuta.get(z).getIdElemento() == listaElementos.get(w).getIdElemento()) {
                                                //Mostrar
                                                if(listaElementos.get(w).getAccesible() == 1) {
                                                    Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                } else {
                                                    numElementos++;
                                                    Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                                                    GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                    Marker startMarker = new Marker(map);
                                                    startMarker.setPosition(point);
                                                    startMarker.setIcon(icon);
                                                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                    startMarker.setTitle(listaElementos.get(w).getNombre());
                                                    startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                    map.getOverlays().add(startMarker);
                                                }
                                                /*
                                                GeoPoint point = new GeoPoint(listaElementos.get(w).getLatitud(), listaElementos.get(w).getLongitud());
                                                Marker startMarker = new Marker(map);
                                                startMarker.setPosition(point);
                                                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                                startMarker.setTitle(listaElementos.get(w).getNombre());
                                                startMarker.setSubDescription(listaElementos.get(w).getDescripcion());
                                                map.getOverlays().add(startMarker);*/
                                                map.invalidate();
                                            }
                                        }
                                    }

                                    Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_node);
                                    for (int i=0; i<road.mNodes.size(); i++){
                                        RoadNode node = road.mNodes.get(i);
                                        Marker nodeMarker = new Marker(map);
                                        nodeMarker.setPosition(node.mLocation);
                                        nodeMarker.setIcon(nodeIcon);
                                        nodeMarker.setTitle("Step "+i);
                                        nodeMarker.setSnippet(node.mInstructions);
                                        nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                                        if(node.mManeuverType == 1) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_continue);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 3) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 4) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 5) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_left);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 6) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_slight_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 7) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_turn_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 8) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 0 || node.mManeuverType == 2) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_sharp_right);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 12 || node.mManeuverType == 13 || node.mManeuverType == 14) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_u_turn);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 27 || node.mManeuverType == 28 || node.mManeuverType == 29 || node.mManeuverType == 30 || node.mManeuverType == 31 || node.mManeuverType == 32 || node.mManeuverType == 33 || node.mManeuverType == 34) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_roundabout);
                                            nodeMarker.setImage(icon);
                                        } else if(node.mManeuverType == 24 || node.mManeuverType == 25 || node.mManeuverType == 26) {
                                            Drawable icon = getResources().getDrawable(R.mipmap.ic_arrived);
                                            nodeMarker.setImage(icon);
                                        }
                                        map.getOverlays().add(nodeMarker);
                                    }

                                    if(numElementos < 3) {
                                    //if(elementosEnRuta.size() < 3) {
                                        imagenDificultad.setColorFilter(Color.GREEN);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Accesible");
                                    } else if (numElementos == 3)/*(elementosEnRuta.size() == 3)*/ {
                                        imagenDificultad.setColorFilter(Color.YELLOW);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("Intermedio");
                                    } else {
                                        imagenDificultad.setColorFilter(Color.RED);
                                        cantidadElementos.setText(elementosEnRuta.size() + " Elementos");
                                        cantidad2.setText(numElementos + " no accesibles");
                                        dificultadRuta.setText("No accesible");
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<List<Elemento>> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Error", t.getMessage());
                            }
                        });

                        layout_segundo.setVisibility(View.GONE);
                        layout_cancelar.setVisibility(View.VISIBLE);

                        layout_informacion.setVisibility(View.VISIBLE);
                        layout_itinerario.setVisibility(View.VISIBLE);
                        enRuta = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Busque salida y llegada.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Desaparece recuadro con información
        cancelarInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_informacion.setVisibility(View.GONE);
            }
        });

        //Cancelar la ruta que estabamos empezando a calcular
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.getOverlays().clear();
                map.invalidate();
                //Volver a mostrar listaElementos
                buscarSalida = false;
                buscarLLegada = false;

                //map.getOverlays().clear();
                System.out.println(listaElementos.size());

                for(int i=0; i < listaElementos.size(); i++) {
                    String coo = listaElementos.get(i).getCoordenada();
                    String[] parts = coo.split(", ");
                    String latitud = parts[0];
                    String longitud = parts[1];

                    if(listaElementos.get(i).getAccesible() == 1) {
                        Drawable icon = getResources().getDrawable(R.drawable.markeraccesible);
                        GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));
                        Marker startMarker = new Marker(map);
                        startMarker.setPosition(point);
                        startMarker.setIcon(icon);
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        startMarker.setTitle(listaElementos.get(i).getNombre());
                        startMarker.setSubDescription(listaElementos.get(i).getDescripcion());
                        map.getOverlays().add(startMarker);
                    } else {
                        Drawable icon = getResources().getDrawable(R.drawable.markernoaccesible);
                        GeoPoint point = new GeoPoint(Double.valueOf(latitud), Double.valueOf(longitud));
                        Marker startMarker = new Marker(map);
                        startMarker.setPosition(point);
                        startMarker.setIcon(icon);
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        startMarker.setTitle(listaElementos.get(i).getNombre());
                        startMarker.setSubDescription(listaElementos.get(i).getDescripcion());
                        map.getOverlays().add(startMarker);
                    }
                }

                final MyLocationNewOverlay miPosicion = new MyLocationNewOverlay(map);
                miPosicion.enableMyLocation();
                //miPosicion.enableFollowLocation();
                IMyLocationProvider s= miPosicion.getMyLocationProvider();
                miPosicion.getMyLocation();
                map.getOverlays().add(miPosicion);

                layout_informacion.setVisibility(View.GONE);
                layout_itinerario.setVisibility(View.GONE);

                //layout_segundo.setVisibility(View.GONE);
                layout_cancelar.setVisibility(View.GONE);
                destino = false;
                enRuta = false;
                //elementosEnRuta.clear();
                elementosEnRuta.removeAll(elementosEnRuta);
                numElementos = 0;


                MapEventsReceiver mReceive = new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p) {
                        if(enRuta == false) {
                            layout_primero.setVisibility(View.GONE);
                            layout_segundo.setVisibility(View.GONE);

                            map.getOverlays().remove(startMarker);
                            map.getOverlays().remove(finalMarker);
                            destino = false;
                        }
                        return false;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p) {
                        if(enRuta == false) {
                            if(destino == false) {
                                endPoint = p;
                                map.getOverlays().remove(finalMarker);
                                Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_destination);
                                finalMarker = new Marker(map);
                                finalMarker.setIcon(nodeIcon);
                                finalMarker.setPosition(p);
                                finalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(finalMarker);

                                layout_primero.setVisibility(View.VISIBLE);

                            }
                            else {
                                startPoint = p;
                                map.getOverlays().remove(startMarker);
                                Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_departure);
                                startMarker = new Marker(map);
                                startMarker.setIcon(nodeIcon);
                                startMarker.setPosition(p);
                                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(startMarker);

                                layout_primero.setVisibility(View.GONE);
                                layout_segundo.setVisibility(View.VISIBLE);
                            }
                        }

                        return false;
                    }
                };

                MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
                map.getOverlays().add(OverlayEvents);

            }
        });

        //Mostrar el itinerario
        itinerario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itin = new Intent(MainActivity.this, ItinerarioLista.class);
                startActivity(itin);

            }
        });

        //Controlar cuando se da un 'singleTap' o 'longPress'
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(enRuta == false) {
                    layout_primero.setVisibility(View.GONE);
                    layout_segundo.setVisibility(View.GONE);

                    map.getOverlays().remove(startMarker);
                    map.getOverlays().remove(finalMarker);

                    destino = false;
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                //La primera vez para añadir destino, la segunda vez si se mantiene el destino para marcar la salida (si no se esta en ruta)
                if(enRuta == false) {
                    if(destino == false) {
                        endPoint = p;
                        map.getOverlays().remove(finalMarker);
                        Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_destination);
                        finalMarker = new Marker(map);
                        finalMarker.setIcon(nodeIcon);
                        finalMarker.setPosition(p);
                        finalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                        map.getOverlays().add(finalMarker);

                        layout_primero.setVisibility(View.VISIBLE);

                    }
                    else {
                        startPoint = p;
                        map.getOverlays().remove(startMarker);
                        Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_departure);
                        startMarker = new Marker(map);
                        startMarker.setIcon(nodeIcon);
                        startMarker.setPosition(p);
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(startMarker);

                        layout_primero.setVisibility(View.GONE);
                        layout_segundo.setVisibility(View.VISIBLE);

                    }
                }

                return false;
            }
        };

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        //val = this.usarioId(cr.getUsuario());
    }

    //Calcular distancias entre coordenadas
    private Double getDistanceFromLatLonInKm(Double lat1, Double lon1, Double lat2, Double lon2) {
        int R = 6371;
        Double dLat = (lat2-lat1) * (Math.PI/180);
        Double dLon = (lon2-lon1) * (Math.PI/180);
        Double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(lat1 * (Math.PI/180)) * Math.cos(lat2 * (Math.PI/180)) *
                Math.sin(dLon/2) * Math.sin(dLon/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c;
        return d;
    }

    //Obtener datos usuario
    private CredencialesLogin getProfile() {
        System.out.println("Hola");
        CredencialesLogin login = new CredencialesLogin();
        String nombre = sharedPreferences.getString("usuario", "");
        login.setUsuario(nombre);
        String contrasenya = sharedPreferences.getString("contraseña", "");
        login.setContraseña(contrasenya);
        int id = sharedPreferences.getInt("id", 0);
        login.setId(id);
        return login;
    }

    //Eliminar el usuario
    private void clearProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("usuario");
        editor.remove("contraseña");
        editor.commit();
    }

    //Decodificar imagen, string -> imagen
    private Bitmap decodeStringToImage(String completeImageData) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        return bitmap;
    }

    private int usarioId(String usuario) {

        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);

        apiService.devolverIdUsuario(usuario).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    i = response.body();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error de conexión API", t.getMessage());
            }
        });

        return i;

    }

    //Buscar un lugar por su nombre, al pulsar boton de 'Buscar'
    private class GeocodingTask extends AsyncTask<Object, Void, List<Address>> {
        int mIndex;
        protected List<Address> doInBackground(Object... params) {
            String locationAddress = (String)params[0];
            mIndex = (Integer)params[1];
            GeocoderNominatim geocoder = new GeocoderNominatim("asdsa");
            geocoder.setOptions(true); //ask for enclosing polygon (if any)
            //GeocoderGraphHopper geocoder = new GeocoderGraphHopper(Locale.getDefault(), graphHopperApiKey);
            try {
                BoundingBox viewbox = map.getBoundingBox();
                List<Address> foundAdresses = geocoder.getFromLocationName(locationAddress, 1,
                        viewbox.getLatSouth(), viewbox.getLonEast(),
                        viewbox.getLatNorth(), viewbox.getLonWest(), false);
                return foundAdresses;
            } catch (Exception e) {
                return null;
            }
        }
        protected void onPostExecute(List<Address> foundAdresses) {
            if (foundAdresses == null) {
                Toast.makeText(getApplicationContext(), "Geocoding error", Toast.LENGTH_SHORT).show();
            } else if (foundAdresses.size() == 0) { //if no address found, display an error
                Toast.makeText(getApplicationContext(), "Dirección no encontrada.", Toast.LENGTH_SHORT).show();
            } else {

                Address address = foundAdresses.get(0); //get first address
                String addressDisplayName = address.getExtras().getString("display_name");

                if (mIndex == START_INDEX){
                    buscarSalida = true;
                    startPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                    map.getOverlays().remove(startMarker);
                    Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_departure);
                    startMarker = new Marker(map);
                    startMarker.setIcon(nodeIcon);
                    startMarker.setPosition(startPoint);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(startMarker);

                    mapController.setCenter(startPoint);
                    mapController.setZoom(19.0);

                } else if (mIndex == DEST_INDEX){
                    buscarLLegada = true;
                    endPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                    map.getOverlays().remove(finalMarker);
                    Drawable nodeIcon = getResources().getDrawable(R.mipmap.marker_destination);
                    finalMarker = new Marker(map);
                    finalMarker.setIcon(nodeIcon);
                    finalMarker.setPosition(endPoint);
                    finalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(finalMarker);

                    mapController.setCenter(endPoint);
                    mapController.setZoom(19.0);

                }

            }
        }
    }

    public void handleSearchButton(int index, int editResId){
        EditText locationEdit = (EditText)findViewById(editResId);
        //Hide the soft keyboard:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(locationEdit.getWindowToken(), 0);

        String locationAddress = locationEdit.getText().toString();

        if (locationAddress.equals("")){
            //removePoint(index);
            map.invalidate();
            return;
        }

        Toast.makeText(this, "Searching:\n"+locationAddress, Toast.LENGTH_LONG).show();
        //AutoCompleteOnPreferences.storePreference(this, locationAddress, SHARED_PREFS_APPKEY, PREF_LOCATIONS_KEY);
        new GeocodingTask().execute(locationAddress, index);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.cerrarSesion) {
            //estadoSesion.clearProfile(sharedPreferences);
            clearProfile();
            Intent intentCer = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(intentCer);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Navegación desde la pantalla de inicio a las otras pantallas
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent intentPer = new Intent(MainActivity.this, PerfilActivity.class);
            MainActivity.this.startActivity(intentPer);
        } else if (id == R.id.nav_confirmar) {
            Intent intentCon = new Intent(MainActivity.this, ConfirmarActivity.class);
            MainActivity.this.startActivity(intentCon);
        } else if (id == R.id.nav_contribucion) {
            Intent intentCont = new Intent(MainActivity.this, ContribucionActivity.class);
            MainActivity.this.startActivity(intentCont);
        } else if (id == R.id.nav_sugerencia) {
            Intent intentSug = new Intent(MainActivity.this, SugerenciasActivity.class);
            MainActivity.this.startActivity(intentSug);
        } /*else if (id == R.id.nav_ajustes) {
            Intent intentAju = new Intent(MainActivity.this, AjustesActivity.class);
            MainActivity.this.startActivity(intentAju);
        } */else if (id == R.id.nav_ayuda) {
            Intent intentAyu = new Intent(MainActivity.this, AyudaActivity.class);
            MainActivity.this.startActivity(intentAyu);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
