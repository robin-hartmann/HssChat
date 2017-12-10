package de.hss.sae.sue.chat.client_pc;

import de.hss.sae.sue.chat.common.communication.Message;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PcClient {
    private ObjectInputStream in = null;

    public static void main(String args[]) {
        new PcClient();
    }

    public PcClient() {
        try {
            System.out.print("Connecting to server...\n");
            Socket socket = new Socket("192.168.178.26", 16567);

            System.out.print("Getting streams...\n");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Scanner consoleReader = new Scanner(System.in);

            String name = "Client" + socket.getLocalPort();
            String text;

            System.out.print("Starting listener...\n");
            new Thread(new ReceiveRunnable(socket)).start();

            System.out.print("Ready!\n");
            do {
                text = consoleReader.nextLine();
                out.writeObject(Message.obtainMessage(name, text));
            } while (!text.equalsIgnoreCase("quit"));

            in.close();
            consoleReader.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObjectInputStream getInputStream(Socket socket) throws IOException {
        if (in == null) {
            in = new ObjectInputStream(socket.getInputStream());
        }
        return in;
    }

    private class ReceiveRunnable implements Runnable {
        private Socket socket;

        ReceiveRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Message message;
                while(true) {
                    message = (Message) getInputStream(socket).readObject();

                    if (message.getSender() == null) System.out.println(message.getMessage());
                    else System.out.println(message.getSender() + ": " + message.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
