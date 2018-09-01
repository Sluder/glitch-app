package com.zsluder.pandaai;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Messenger extends Thread {

    private BluetoothSocket bluetooth_socket;
    private InputStream input_stream;
    private OutputStream output_stream;
    private boolean connection_open = true;

    /**
     * Constructor
     *
     * @param bluetooth_socket : Socket to connected device
     */
    Messenger(BluetoothSocket bluetooth_socket)
    {
        this.bluetooth_socket = bluetooth_socket;

        try {
            this.input_stream = bluetooth_socket.getInputStream();
            this.output_stream = bluetooth_socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loop for listening thread
     */
    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (this.connection_open) {
            try {
                bytes = this.input_stream.read(buffer);
                String message = new String(buffer, 0, bytes);

                Log.e("error", message);

            } catch(IOException ex) {
                Log.e("error", "Error while reading from bluetooth socket");
            }
        }
    }

    /**
     * Send data to output stream
     *
     * @param bytes : Data to send to PI
     */
    public void write(byte[] bytes)
    {
        try {
            output_stream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close Bluetooth connection
     */
    public void close()
    {
        try {
            this.bluetooth_socket.close();
            this.connection_open = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
