package de.hss.sae.sue.chat.client.communication;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Robin on 02.01.2017.
 */

class ConnectRunnable extends MessagingRunnable {
    private String ip;
    private int port;

    ConnectRunnable(Socket client, Handler stateHandler, String ip, int port) {
        super(client, stateHandler);
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        MessagingService messagingService = MessagingService.getInstance();
        InetSocketAddress endpoint = new InetSocketAddress(ip, port);

        try {
            client.connect(endpoint, MessagingService.CONNECT_TIMEOUT);
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Error establishing connection", e);
            messagingService.disconnect();
            stateHandler.sendMessage(stateHandler.obtainMessage(MessagingService.STATE_ERROR, e));
            return;
        }

        stateHandler.sendEmptyMessage(MessagingService.STATE_FINISH);
    }
}
