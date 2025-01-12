package com.example.testing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class ConnectionManager implements DataCallback {

    //region TAG and UUID
    private final String TAG = "ConnectionManager";
    private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //endregion

    //region Handler and BluetoothAdapter
    private Handler handler = new Handler(Looper.getMainLooper());
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();// UUID for SPP
    //endregion

    //region booleans
    private boolean isConnected = false;
    private boolean isListVisible = false;
    //endregion

    //region other
    private static ConnectionManager INSTANCE;
    private Context context;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private ProgressBar progressBar;
    private TextView textView;
    private Button startButton;
    //endregion

    public ConnectionManager(Context context, ProgressBar progressBar, TextView textView, Button startButton) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.startButton = startButton;
        checkIfBluetoothIsSupported();
    }

    public ConnectionManager() {
        checkIfBluetoothIsSupported();
    }

    public static ConnectionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionManager();
        }

        return INSTANCE;
    }

    public static ConnectionManager getInstance(Context context, ProgressBar progressBar, TextView textView, Button startButton) {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionManager(context, progressBar, textView, startButton);
        }

        return INSTANCE;
    }

    public void checkIfBluetoothIsSupported() {
        if (adapter == null) {
            Toast.makeText(context, "Bluetooth is not supported!!", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void enableBluetooth(Activity activity) {
        if (!adapter.isEnabled()) {
            Log.d(TAG, "onEnable: Enabling bluetooth");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Request to enable Bluetooth
            activity.startActivityForResult(intent, 1);

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        if (state == BluetoothAdapter.STATE_ON) {  //Check if Bluetooth is now enabled
                            Log.d(TAG, "Bluetooth activated");
                            context.unregisterReceiver(this); //Unregister receiver to prevent memory leaks
                            ((Activity) context).runOnUiThread(() -> //Update UI
                                    Toast.makeText(context, "Looking for device!", Toast.LENGTH_LONG).show()
                            );
                            connectToESP(context, progressBar, textView, startButton);
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED); //
            activity.registerReceiver(receiver, filter); //Register the receiver
        }
    }

    public void disconnect() {
        //Disconnect the Bluetooth socket
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
                isConnected = false;
                Log.d(TAG, "Disconnected");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing connection: " + e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    public void connectToESP(Context context, ProgressBar progressBar, TextView textView, Button button) {
        String targetDeviceName = "ESP32"; //Target device name
        final BluetoothDevice[] device = {null}; //Array to hold the found device

        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.startButton = button;

        if (adapter.isDiscovering()) {
            Log.d(TAG, "connectToESP: Starts descovering");
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery(); //Start Bluetooth discovery

        if (progressBar != null)
        {
            progressBar.setVisibility(View.VISIBLE);  //Show progress bar
        }

        if (textView != null) {
            textView.setText(""); //Reset text
        }

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //A device was found
                    BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (foundDevice != null && targetDeviceName.equals(foundDevice.getName())) {
                        //Check if the found device matches the target name
                        device[0] = foundDevice;
                        adapter.cancelDiscovery();
                        context.unregisterReceiver(this);

                        connectToDevice(device[0]); //Connect to the device

                        if (progressBar != null) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        if (textView != null) {
                            textView.setText("Device connected" + "\n" + ":)");
                        }

                        if (startButton != null) {
                            startButton.setVisibility(View.VISIBLE); // Arată butonul
                        }

                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Connected to " + foundDevice.getName(), Toast.LENGTH_SHORT).show()
                        );
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    if (device[0] == null) {
                        //No target device was found

                        if (progressBar != null) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        if (textView != null) {
                            textView.setText("Device not found" + "\n" + ":(");
                        }

                        if (startButton != null) {
                            startButton.setVisibility(View.INVISIBLE);
                        }

                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Device not found", Toast.LENGTH_SHORT).show()
                        );
                    }
                    context.unregisterReceiver(this);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filter); //Register receiver for discovery events
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {

        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            disconnect();
        }

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);  //Create a socket
            bluetoothSocket.connect();  //Connect to the device
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                inputStream = bluetoothSocket.getInputStream(); //Initialize InputStream
                isConnected = true;
                Log.d(TAG, "Connection established and inputStream initialized.");
            } else {
                Log.e(TAG, "Failed to initialize inputStream.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to device: " + e.getMessage());

            if (progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            if (textView != null) {
                textView.setText("Connection failed" + "\n" + ":(");
            }

            if (startButton != null) {
                startButton.setVisibility(View.INVISIBLE);
            }

            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Error closing socket: " + closeException.getMessage());
            }
        }
    }

    private volatile boolean shouldStop = false;

    public void readData(DataCallback callback) {
        // Read data from the InputStream on a separate thread

        new Thread(() -> {
            StringBuilder dataBuilder = new StringBuilder(); //To "store" data
            try {
                byte[] buffer = new byte[1024];  //Buffer to store incoming data
                int bytes;
                while ((bytes = inputStream.read(buffer)) != -1) { //Read from InputStream
                    if (shouldStop) {
                        break; //Stop reading if flag is set
                    }

                    String data = new String(buffer, 0, bytes); //Convert bytes to String
                    dataBuilder.append(data); //Append to StringBuilder
                    handler.post(() -> {
                        if (callback != null) {
                            callback.onDataReceived(data); //Send data to callback
                        }
                    });
                }

                if (!shouldStop && callback != null) {
                    handler.post(() -> callback.onDataReceived(dataBuilder.toString()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in readData(): " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void stopReading() {
        shouldStop = true;
        disconnect();
    }

    public void startReading() {
        shouldStop = false;
    }

    @Override
    public void onDataReceived(String data) {

    }

    public boolean isConnected() {
        return isConnected;
    }
}