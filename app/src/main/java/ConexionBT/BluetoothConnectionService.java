package ConexionBT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.dani.smartblood.RegistrarAnalisis;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothConnectionService extends AppCompatActivity {
    private static final String TAG = "BluetoothConnectionServ";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String appName = "MYAPP";

    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private BluetoothDevice device;
    private UUID deviceUUID;
    private final BluetoothAdapter adapter;

    private boolean closeConnection = false;
    private String glucosa = "";

    Context context;


    public BluetoothConnectionService(Context ctx) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        context = ctx;
        glucosa = "";
        start();
    }

    public void close() {
        this.closeConnection = true;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "BluetoohConnectionService.start");
        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket socket = null;
            // Create a new listening server socket
            try {
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
                socket = adapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            serverSocket = socket;
        }

        public void run() {
            Log.d(TAG, "AcceptThread.run: AcceptThread Running.");
            BluetoothSocket socket = null;
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "AcceptThread.run: RFCOM server socket start.....");
                socket = serverSocket.accept();
                Log.d(TAG, "AcceptThread.run: RFCOM server socket accepted connection.");
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread.run: IOException: " + e.getMessage());
            }
            if (socket != null) {
                acceptConnection(socket, device);
            }
            Log.i(TAG, "AcceptThread.run: finished.");
        }

        public void cancel() {
            Log.d(TAG, "AcceptThread: cancel");
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread.cancel: closing ServerSocket failed. " + e.getMessage());
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            BluetoothConnectionService.this.device = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN connectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_UUID_INSECURE);
                tmp = device.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            socket = tmp;

            // Always cancel discovery because it will slow down a connection
            adapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: ConnectThread acceptConnection.");
                socket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    socket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "connectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE);
            }
            acceptConnection(socket, device);
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of socket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    private void acceptConnection(BluetoothSocket socket, BluetoothDevice device) {
        Log.d(TAG, "acceptConnection: Starting.");
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }


    /**
     * AcceptThread starts and sits waiting for a connection.
     * Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/

    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startClient: Started.");
        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }

    /**
     * Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
     * receiving incoming data through input/output streams respectively.
     **/

    private class ConnectedThread extends Thread {
        private BluetoothSocket socket;
        private InputStream inStream;

        public ConnectedThread(BluetoothSocket sock) {
            Log.d(TAG, "ConnectedThread: Starting.");
            glucosa = "";
            socket = sock;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                Log.e(TAG, "close connection: " + closeConnection);
                if (closeConnection) {
                    cancel();
                }
                // Read from the InputStream
                try {
                    bytes = inStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                    glucosa = glucosa + incomingMessage;
                    Log.d(TAG, "Glucosa: " + glucosa);
                    if (Integer.valueOf(glucosa) > 50) {
                        try {
                            Intent intent = new Intent(context, RegistrarAnalisis.class);
                            intent.putExtra("MedidaSangre", glucosa);
                            context.startActivity(intent);
                            glucosa = "";
                        } catch (Exception e) {
                            Log.e(TAG, "Error en startActivity Registrar activity. Error: " + e);
                        }
                    }
                    //stuck here
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            Log.d(TAG, "ConnectedThread.cancel: shutdown connection");
            if (inStream != null) {
                try {
                    inStream.close();
                    Log.d(TAG, "ConnectedThread.cancel: input stream closed");

                } catch (Exception e) {
                    Log.e(TAG, "ConnectedThread.cancel: Error al cerrar input stream: " + e.getMessage());
                }
                inStream = null;
                Log.d(TAG, "ConnectedThread.cancel: input stream is now null");
            }

            if (socket != null) {
                try {
                    socket.close();
                    Log.d(TAG, "ConnectedThread.cancel: socket closed");

                } catch (IOException e) {
                    Log.e(TAG, "ConnectedThread.cancel: Error closing socket: " + e.getMessage());
                }
                socket = null;
            }
        }
    }
}