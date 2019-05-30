package com.example.sergi.aplicacion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvNombre, tvUsuario, tvCont, tvEdad;
    Button registro;
    Button btnsubir;
    ImageView image;


    Retrofit cliente;
    ApiService apiService;

    Bitmap bitmap;

    Boolean existe;
    Boolean exist;
    Boolean existeImagen = false;
    int PICK_IMAGE_REQUEST = 1;

    List<Necesidad> necesidades = new ArrayList<Necesidad>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Volver pantalla anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvNombre = findViewById(R.id.reg_nom);
        tvUsuario = findViewById(R.id.reg_usu);
        tvCont = findViewById(R.id.reg_cont);
        tvEdad = findViewById(R.id.reg_eda);
        image = findViewById(R.id.imageView3);
        btnsubir = findViewById(R.id.btnBuscar);
        registro = findViewById(R.id.btn_gua);

        //Botón subir imagen
        btnsubir.setOnClickListener(this);
        //Botón de registrar
        registro.setOnClickListener(this);

    }

    //Convertir imgen en string
    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    /*
    private void getImagenString(String foto) {

        String imageDataBytes = foto.substring(foto.indexOf(",")+1);
        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        imageView.setImageBitmap(bitmap);
    }*/

    private void uploadImage(){

        String nombre = tvNombre.getText().toString();
        String user = tvUsuario.getText().toString();
        String contr = tvCont.getText().toString();
        String compEdad = tvEdad.getText().toString();

        //Comprobamos campos obligatorios estén rellenados y comprobos si existe usuario
        if(!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(user) && !TextUtils.isEmpty(contr) && !TextUtils.isEmpty(compEdad)) {

            int edad = Integer.parseInt(tvEdad.getText().toString());

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setUsuario(user);
            usuario.setContrasenya(contr);
            usuario.setEdad(edad);

            String imagen = "";
            if(existeImagen == true) {
                imagen = getStringImagen(bitmap);

            }
            usuario.setFoto(imagen);
            usuario.setMostrar(1);
            usuario.setNecesidades(necesidades);
            comprobarExisteUsuario(usuario);

        } else {
            Toast.makeText(RegistroActivity.this,"Rellena todos los campos.", Toast.LENGTH_SHORT).show();
        }

    }

    //Seleccionar imagen móvil
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
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == btnsubir){
            showFileChooser();
        }

        if(v == registro){
            uploadImage();
        }
    }

    //Comprobamos si el usuario ya existe, si no se envía para guardar en bd
    public void comprobarExisteUsuario(final Usuario usuario) {

        exist = false;

        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        //Busca usuario bd
        apiService.buscarUsuarioReg(usuario.getUsuario()).enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if(response.isSuccessful()) {
                    if(response.body().size() != 0) {
                        Toast.makeText(RegistroActivity.this,"El usuario ya existe.", Toast.LENGTH_SHORT).show();
                    } else {
                        sendPost(usuario);
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

    //Enviar nuevo usuario
    public void sendPost(Usuario usuario) {
        //La clase Retrofit genera implementacion de la interfaz ApiService y además indicamos que vamos a usar Gson para la
        // deserializacion
        cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = cliente.create(ApiService.class);
        //Llamámos método del apiservice, para realizar petición a la API
        apiService.crearUsuario(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                //Si la respuesta es satisfactoria(el usuario creado)
                if(response.isSuccessful()) {
                    Intent login = new Intent(RegistroActivity.this, LoginActivity.class);
                    startActivity(login);
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.i("Error de conexión API 2", t.getMessage());
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


}
