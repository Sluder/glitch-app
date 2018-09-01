package com.zsluder.pandaai;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private BluetoothAdapter bluetooth;
    private Messenger messenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openConnection();
        setupListeners();

        // Search for raspberry PI pivot
        for (BluetoothDevice paired_device : bluetooth.getBondedDevices()) {
            if (paired_device.getAddress().equals("B8:27:EB:1D:03:4B")) {
                try {
                    BluetoothSocket bluetooth_socket = (BluetoothSocket) paired_device
                        .getClass()
                        .getMethod("createRfcommSocket", new Class[] {int.class})
                        .invoke(paired_device , 1);

                    bluetooth_socket.connect();

                    messenger = new Messenger(bluetooth_socket);
                    messenger.start();
                    messenger.write("Hello".getBytes());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupListeners()
    {
        Button listen_btn = findViewById(R.id.listen_btn);

        listen_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, 1);

                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Open connection to PI
     */
    private void openConnection()
    {
        try {
            bluetooth = BluetoothAdapter.getDefaultAdapter();

            if (!bluetooth.isEnabled()){
                bluetooth.enable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Opened connection", Toast.LENGTH_SHORT).show();
    }

    /**
     * Close connection to PI
     */
    private void closeConnection()
    {
        messenger.close();

        if (bluetooth.isEnabled()){
            bluetooth.disable();
        }
    }

    /**
     * Listener for STT
     *
     * @param request_code : Integer request code originally supplied
     * @param result_code : Action result code
     * @param data : Data returned from action
     */
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        super.onActivityResult(request_code, result_code, data);

        switch (request_code) {
            case 1: {
                if (result_code == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    Log.e("error", text.get(0));
                }
            }
        }
    }
}
