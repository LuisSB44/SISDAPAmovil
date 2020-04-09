package com.example.sisdapamovil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    EditText usu, pas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usu = (EditText) findViewById(R.id.usu);
        pas = (EditText) findViewById(R.id.pas);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Thread tr= new Thread(){
            @Override
            public void run() {
                final String resultado = enviarDatosGET(usu.getText().toString(), pas.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = obtenerDatosJSON(resultado);
                        if(r>0){
                            Intent i= new Intent(getApplicationContext(), registroNotas.class);
                            i.putExtra("cod", usu.getText().toString());
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(), "Usuario o Contraseña incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        tr.start();
    }

    public String enviarDatosGET(String usu, String pas){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;

        try{
            url= new URL("http://192.168.100.135:80/ServiciosWeb/source%20files/valida.php?usu="+usu+"&pas="+pas);
            HttpURLConnection conection=(HttpURLConnection) url.openConnection();
            respuesta= conection.getResponseCode();

            resul= new StringBuilder();

            if(respuesta==HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                while((linea= reader.readLine()) !=null){
                    resul.append(linea);
                }
            }
        }catch(Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

        return resul.toString();
    }

    public int obtenerDatosJSON(String response){
        int res=0;
            try{
                JSONArray json= new JSONArray(response);
                if(json.length()>0){
                    res=1;
                }
            }catch(Exception e){
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        return res;
    }


}
