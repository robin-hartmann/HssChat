package de.hss.sae.sue.chat.client.communication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import de.hss.sae.sue.chat.client.localstorage.Database;
import de.hss.sae.sue.chat.common.communication.Message;

/**
 * Created by Robin on 02.01.2017.
 */

class ListenRunnable extends MessagingRunnable {
    private Database db;

    ListenRunnable(Socket client, Handler stateHandler, Context context) {
        super(client, stateHandler);
        this.db = new Database(context);
    }

    @Override
    public void run() {
        MessagingService messagingService = MessagingService.getInstance();
        try {
            Message message;
            ObjectInputStream in;

            messagingService.setListenerRunning(true);

            try {
                in = messagingService.getInputStream();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Error getting input stream", e);
                stateHandler.sendMessage(stateHandler.obtainMessage(MessagingService.STATE_ERROR, e));
                return;
            }

            while (messagingService.isListenerRunning()) {
                try {
                    message = (Message) in.readObject();
                } catch (ClassNotFoundException | IOException e) {
                    Log.e(this.getClass().toString(), "Error reading from input stream", e);
                    stateHandler.sendMessage(stateHandler.obtainMessage(MessagingService.STATE_ERROR, e));
                    return;
                }

                db.addMessage(message);
                stateHandler.sendMessage(stateHandler.obtainMessage(MessagingService.STATE_UPDATE, message));
            }
        } finally {
            messagingService.setListenerRunning(false);
            stateHandler.sendEmptyMessage(MessagingService.STATE_FINISH);
        }
    }
}