package de.hss.sae.sue.chat.client.communication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.hss.sae.sue.chat.common.communication.Message;

/**
 * Created by Robin on 27.12.2016.
 */

public class MessagingService {
    public static final int STATE_FINISH = 0;
    public static final int STATE_UPDATE = 1;
    public static final int STATE_ERROR = 2;

    static final int CONNECT_TIMEOUT = 5000;

    private static MessagingService instance = null;

    private Socket client = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private boolean listenerRunning = false;

    private MessagingService() {
    }

    public static MessagingService getInstance() {
        if (instance == null) instance = new MessagingService();
        return instance;
    }

    public void connect(String ip, int port, Handler stateHandler) {
        if (isConnected())
            throw new IllegalStateException("The MessagingService is already connected.");
        client = new Socket();
        new WorkerThread("Connect Worker.", new ConnectRunnable(client, stateHandler, ip, port)).start();
    }

    public void disconnect() {
        if (!isConnected())
            throw new IllegalStateException("The MessagingService is already disconnected.");

        try {
            client.close();
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Error releasing resources.", e);
        }

        client = null;
        in = null;
        out = null;
    }

    public void send(Message message, Handler stateHandler) {
        if (!isConnected())
            throw new IllegalStateException("The MessagingService needs to be connect first, in order to send a message.");
        new WorkerThread("Send Worker", new SendRunnable(client, stateHandler, message)).start();
    }

    public void startListener(Context context, Handler stateHandler) {
        if (!isConnected())
            throw new IllegalStateException("The MessagingService needs to be connect first, in order to start listening for messages.");
        if (isListenerRunning()) throw new IllegalStateException("The listener is already running.");
        new WorkerThread("Listen Worker", new ListenRunnable(client, stateHandler, context)).start();
    }

    boolean isListenerRunning() {
        return listenerRunning;
    }

    void setListenerRunning(boolean listenerRunning) {
        this.listenerRunning = listenerRunning;
    }

    ObjectInputStream getInputStream() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("The MessagingService needs to be connect first, in order to get the input stream.");
        if (in == null)
            in = new ObjectInputStream(client.getInputStream());
        return in;
    }

    ObjectOutputStream getOutputStream() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("The MessagingService needs to be connect first, in order to get the output stream.");
        if (out == null)
            out = new ObjectOutputStream(client.getOutputStream());
        return out;
    }

    private boolean isConnected() {
        return client != null;
    }
}
