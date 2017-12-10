package de.hss.sae.sue.chat.client.communication;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.hss.sae.sue.chat.common.communication.Message;

/**
 * Created by Robin on 02.01.2017.
 */

class SendRunnable extends MessagingRunnable {
    private Message message;

    SendRunnable(Socket client, Handler stateHandler, Message message) {
        super(client, stateHandler);
        this.message = message;
    }

    @Override
    public void run() {
        ObjectOutputStream out;

        try {
            out = MessagingService.getInstance().getOutputStream();
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Error writing getting output stream", e);
            stateHandler.sendMessage(stateHandler.obtainMessage(MessagingService.STATE_ERROR, e));
            return;
        }

        try {
            out.writeObject(message);
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Error writing to output stream", e);
            stateHandler.sendMessage(stateHandler.obtainMessage(MessagingService.STATE_ERROR, e));
            return;
        }

        stateHandler.sendEmptyMessage(MessagingService.STATE_FINISH);
    }
}