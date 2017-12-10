package de.hss.sae.sue.chat.server.communication;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.hss.sae.sue.chat.common.communication.Message;

/**
 * Created by robin.hartmann on 14.11.2016.
 */
class ListenRunnable implements Runnable {
    private final CommunicationService service;
    private final Socket client;

    ListenRunnable(CommunicationService service, Socket client) {
        this.service = service;
        this.client = client;
    }

    @Override
    public void run() {
        service.getConnectedClients().add(client);
        ObjectInputStream in = null;

        try {
            try {
                in = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Error getting client input stream.", e);
                return;
            }

            Message message;

            while (service.isRunning()) {
                try {
                    message = (Message) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    Log.e(this.getClass().toString(), "Error reading data from input stream.", e);
                    return;
                }

                broadcastMessage(message);
            }
        } finally {
            service.getConnectedClients().remove(client);

            try {
                if (in != null) in.close();
                client.close();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Error releasing resources.", e);
            }
        }
    }

    private void broadcastMessage(Message message) {
        ObjectOutputStream out;

        for (Socket c : service.getConnectedClients()) {
            try {
                out = AppendingObjectOutputStream.getInstance(c);
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Error getting client output stream.", e);
                continue;
            }

            try {
                out.writeObject(message);
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Error writing to output stream.", e);
                continue;
            }

            try {
                out.reset();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Error resetting output stream.", e);
                continue;
            }
        }
    }
}
