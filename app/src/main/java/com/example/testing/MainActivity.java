package com.example.testing;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    NEO6M gpsModule = new NEO6M("NEO6M", true);
    Trail trail = new Trail(gpsModule);
    private GoogleMap mMap;

    public Trail getTrail() {
        return trail;
    }

    //region UI Variables
    private TextView DHT11dataShow, deviceConected;
    private Button connectToDevice, startButton, goButton; // Adăugăm un buton pentru conexiunea la ESP
    private ViewGroup rootContainer;
    private Scene mapScene, menuScene;
    private ProgressBar loadingConnetion;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_SCAN
                    },
                    1);
        }
        //endregion

        //region UI elements initialize
        // buttonON = findViewById(R.id.button1);
        // buttonOFF = findViewById(R.id.button2);
        // buttonDeviceList = findViewById(R.id.button5);
        //DHT11dataShow = findViewById(R.id.textView3);
        connectToDevice = findViewById(R.id.button);
        startButton = findViewById(R.id.button2);
        startButton.setVisibility(View.INVISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);
        deviceConected = findViewById(R.id.textView4);
        rootContainer = findViewById(R.id.rootContainer);
        loadingConnetion = findViewById(R.id.progressBar);
        loadingConnetion.getIndeterminateDrawable().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingConnetion.setVisibility(View.INVISIBLE);
        //endregion

        ConnectionManager connectionManager = new ConnectionManager(MainActivity.this, loadingConnetion, deviceConected, startButton);

        connectToDevice.setOnClickListener(view -> {
            connectionManager.enableBluetooth(this);
            connectionManager.connectToESP();
        });

        startButton.setOnClickListener(view -> {
            Transition explode = new Fade();
            TransitionManager.go(menuScene, explode);
        });
    }
}
