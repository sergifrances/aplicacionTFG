package com.example.sergi.aplicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PerfilActivity extends AppCompatActivity {

    EditText etNombre, etUsuario, etCont, etEdad;
    Button actualizar, btnImagen;
    Retrofit cliente;
    ApiService apiService;
    int id;
    SharedPreferences sharedPreferences;
    EstadoSesion estadoSesion;
    ImageView iv;
    int PICK_IMAGE_REQUEST = 1;
    Usuario usuario = new Usuario();
    Bitmap bitmap;
    Boolean exist;
    RadioButton r1, r2;
    CheckBox cb1, cb2, cb3;
    List<Necesidad> necesidades;

    Boolean existeImagen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //Recuperar datos sesión
        sharedPreferences = getSharedPreferences("fichero", MODE_PRIVATE);
        //Volver pantalla anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etNombre = findViewById(R.id.editText);
        etUsuario = findViewById(R.id.editText2);
        etCont = findViewById(R.id.editText3);
        etEdad = findViewById(R.id.editText4);
        actualizar = findViewById(R.id.button);
        iv = findViewById(R.id.imageView);
        btnImagen = findViewById(R.id.btnImagen);
        r1 = findViewById(R.id.radioButton3);
        r2 = findViewById(R.id.radioButton4);
        cb1 = findViewById(R.id.checkBox4);
        cb2 = findViewById(R.id.checkBox5);
        cb3 = findViewById(R.id.checkBox6);

        //id del usuario de la sesion
        id = getProfile().getId();

        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        //Obtener todos los datos del usuario y mostrarlos
        apiService.usuarioId(id).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Log.i("Cliente", "Cliente Android");
                if (response.isSuccessful()) {
                    usuario = response.body();
                    etNombre.setText(usuario.getNombre());
                    etUsuario.setText(usuario.getUsuario());
                    etCont.setText(usuario.getContrasenya());
                    etEdad.setText(Integer.toString(usuario.getEdad()));
                    if(usuario.getFoto() != null) {
                        existeImagen = true;
                        decodeStringToImage(usuario.getFoto(), iv);
                    }

                    if(usuario.getMostrar() == 1) {
                        r1.setChecked(true);
                    } else {
                        if(usuario.getMostrar() == 0) {
                            r2.setChecked(true);
                        }
                    }
                    necesidades = usuario.getNecesidades();
                    for(int i = 0; i < necesidades.size(); i++) {
                        if(necesidades.get(i).getIdNecesidad() == 1) {
                            cb1.setChecked(true);
                        }
                        if(necesidades.get(i).getIdNecesidad() == 2) {
                            cb2.setChecked(true);
                        }
                        if(necesidades.get(i).getIdNecesidad() == 3) {
                            cb3.setChecked(true);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.i("Error", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Cambiar imagen
        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        //Actualizar con los nuevos datos
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String user = etUsuario.getText().toString();
                String contr = etCont.getText().toString();
                String edadS = etEdad.getText().toString();
                int edad = 0;
                if(!edadS.isEmpty()) {
                    edad = Integer.parseInt(etEdad.getText().toString());
                }
                String imagen = "";
                if(existeImagen == true) {
                    imagen = getStringImagen(bitmap);
                }

                if(!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(user) && !TextUtils.isEmpty(contr) && !TextUtils.isEmpty(etEdad.getText().toString())) {
                    Usuario usuario = new Usuario();
                    usuario.setNombre(nombre);
                    usuario.setUsuario(user);
                    usuario.setContrasenya(contr);
                    usuario.setEdad(edad);
                    usuario.setFoto(imagen);
                    if(r1.isChecked() == true) {
                        usuario.setMostrar(1);
                    } else {
                        if(r2.isChecked() == true) {
                            usuario.setMostrar(0);
                        }
                    }
                    necesidades = new ArrayList<Necesidad>();
                    if(cb1.isChecked() == true) {
                        Necesidad n = new Necesidad();
                        n.setIdNecesidad(1);
                        n.setNombre("movilidad");
                        necesidades.add(n);
                    }
                    if(cb2.isChecked() == true) {
                        Necesidad n = new Necesidad();
                        n.setIdNecesidad(2);
                        n.setNombre("visual");
                        necesidades.add(n);
                    }
                    if(cb3.isChecked() == true) {
                        Necesidad n = new Necesidad();
                        n.setIdNecesidad(3);
                        n.setNombre("auditiva");
                        necesidades.add(n);
                    }
                    usuario.setNecesidades(necesidades);

                    comprobarExisteUsuario(usuario);

                } else {
                    Toast.makeText(PerfilActivity.this,"Rellena los campos de nombre, usuario, contraseña y edad.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Comprobar si al actualizar el nombre de usuario existía, ya que no se puede repetir
    public void comprobarExisteUsuario(final Usuario usuario) {

        exist = false;

        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        //Buscar si ya existe usuario y si no actualizar usuario
        apiService.buscarUsuarioReg(usuario.getUsuario()).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if(response.isSuccessful()) {
                    if(response.body().size() != 1) {
                        Toast.makeText(PerfilActivity.this,"El usuario ya existe.", Toast.LENGTH_SHORT).show();
                    } else {
                        sendUpdate(usuario);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.i("Error de conexión API", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Decodificar string a imagen
    private void decodeStringToImage(String completeImageData, ImageView imageView) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        bitmap = BitmapFactory.decodeStream(stream);

        imageView.setImageBitmap(bitmap);
    }

    //Datos del usuario
    private CredencialesLogin getProfile() {
        CredencialesLogin login = new CredencialesLogin();
        String nombre = sharedPreferences.getString("usuario", "");
        login.setUsuario(nombre);
        String contrasenya = sharedPreferences.getString("contraseña", "");
        login.setContraseña(contrasenya);
        int id = sharedPreferences.getInt("id", 0);
        login.setId(id);
        return login;
    }

    public void sendUpdate(Usuario usuario) {

        int id2 = getProfile().getId();

        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        //Enviar nuevos datos del usuario
        apiService.updateUsuario(usuario, id2).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()) {
                    Log.i("Bien", "ad");
                    Intent main = new Intent(PerfilActivity.this, MainActivity.class);
                    startActivity(main);
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.i("Error de conexión API", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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

    //Codificar de imagen a string
    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //Elegimos la imagen de nuesto móvil
    private void showFileChooser() {
        existeImagen = true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Cómo obtener el mapa de bits de la Galería
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Configuración del mapa de bits en ImageView
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
