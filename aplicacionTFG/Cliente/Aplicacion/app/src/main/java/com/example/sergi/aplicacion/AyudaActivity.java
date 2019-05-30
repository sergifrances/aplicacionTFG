package com.example.sergi.aplicacion;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AyudaActivity extends AppCompatActivity {

    TextView enlaceIntro, enlaceUsu, enlaceEle, enlaceRuta;
    String direccion;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        enlaceIntro = findViewById(R.id.textViewIntro);
        enlaceUsu = findViewById(R.id.textView14);
        enlaceEle = findViewById(R.id.textViewElem);
        enlaceRuta = findViewById(R.id.textViewRuta);

        //Enlace al html con la infirmación de introducción
        enlaceIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AyudaActivity.this,"Abriendo...", Toast.LENGTH_SHORT).show();
                direccion = "http://localhost/aplicacionLaravelTFG/public/ayuda.html";
                uri = Uri.parse(direccion);
                Intent a = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(a);
            }
        });

        //Enlace al html con la infirmación de usuario
        enlaceUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AyudaActivity.this,"Abriendo...", Toast.LENGTH_SHORT).show();
                direccion = "http://localhost/aplicacionLaravelTFG/public/usuario.html";
                uri = Uri.parse(direccion);
                Intent a = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(a);
            }
        });

        //Enlace al html con la infirmación de elementos
        enlaceEle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AyudaActivity.this,"Abriendo...", Toast.LENGTH_SHORT).show();
                direccion = "http://localhost/aplicacionLaravelTFG/public/elemento.html";
                uri = Uri.parse(direccion);
                Intent a = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(a);
            }
        });

        //Enlace al html con la infirmación de ruta
        enlaceRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AyudaActivity.this,"Abriendo...", Toast.LENGTH_SHORT).show();
                direccion = "http://localhost/aplicacionLaravelTFG/public/ruta.html";
                uri = Uri.parse(direccion);
                Intent a = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(a);
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
