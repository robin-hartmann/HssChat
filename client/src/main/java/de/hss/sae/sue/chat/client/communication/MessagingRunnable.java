package de.hss.sae.sue.chat.client.communication;

import android.os.Handler;

import java.net.Socket;

/**
 * Created by Robin on 05.01.2017.
 */

abstract class MessagingRunnable implements Runnable {
    Socket client;
    Handler stateHandler;

    MessagingRunnable(Socket client, Handler stateHandler) {
        this.client = client;
        this.stateHandler = stateHandler;
    }
}
