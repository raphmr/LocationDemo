package tech.gregori.locationdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String TAG = "MainActivity";
    private ArrayList<Contato> contatosList = new ArrayList<>();

    /**
     * Ativa a obtenção da localização atual, ao receber a permissão de utilizar o GPS.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, locationListener);
            }

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtém um objeto de LocationManager, pelo sistema.
        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Cria um LocationListener, que vai obter a localização do usuário
        locationListener = new LocationListener() {
            // Executado toda vez que a localização é alterada
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Se o dispositivo roda SDK < 23
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
        } else {
            // se SDK > =23, é necessário fazer a solicitação de uso do GPS, ao usuário.
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 10, locationListener);
            }
        }

        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.loadAll();
        Log.e(TAG, "onCreate: " + cursor.getCount());

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Contato contato = new Contato(cursor.getString(
                        cursor.getColumnIndex(CreateDatabase.NOME)),
                        cursor.getString(cursor.getColumnIndex(CreateDatabase.EMAIL)),
                        cursor.getDouble(cursor.getColumnIndex(CreateDatabase.LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(CreateDatabase.LONGITUDE))
                );

                this.contatosList.add(contato);
                cursor.moveToNext();
            }
        } else {
            ContatoDownloader contatoDownloader = new ContatoDownloader();

            try {

                String contatoJsonURL = "http://www.mocky.io/v2/5cdb4544300000640068cc7b";
                String contatoString = contatoDownloader.execute(contatoJsonURL).get();
                ContatoParser contatoParser = new ContatoParser();

                contatoParser.parse(contatoString);

                this.contatosList = contatoParser.getContatos();

                for (Contato c: this.contatosList) {
                    dbHelper.insert(c.getNome(), c.getEmail(), c.getLatitude(), c.getLongitude());
                }

            } catch (Exception e) {
                Log.e(TAG, "downloadJson: Não foi possível baixar os contatos" + e.getMessage());
            }

        }

        Button btnMaps = findViewById(R.id.btnMaps);

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putParcelableArrayListExtra("contatos", contatosList);
                startActivity(intent);

                //dbHelper.delete();
            }
        });

    }

    private class ContatoDownloader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String jsonString = downloadJson(strings[0]);

            if (jsonString == null) {
                Log.e(TAG, "doInBackground: Erro baixando Pokemon");
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

    }

    private String downloadJson(String urlString) {
        StringBuilder jsonStringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            int charsLidos;
            char[] inputBuffer = new char[5];
            while (true) {
                charsLidos = reader.read(inputBuffer);
                if (charsLidos < 0) {
                    break;
                }
                if (charsLidos > 0) {
                    jsonStringBuilder.append(String.copyValueOf(inputBuffer, 0, charsLidos));
                }
            }

            reader.close();
            return jsonStringBuilder.toString();

        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadRSS: URL é inválida" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadRSS: Ocorreu um erro de I/O ao baixar os dados" + e.getMessage());
        }

        return null;

    }
}
