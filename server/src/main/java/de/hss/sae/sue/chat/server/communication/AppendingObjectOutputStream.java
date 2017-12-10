package de.hss.sae.sue.chat.server.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Robin on 07.01.2017.
 */

// enables instantiating multiple ObjectOutputStreams
// on the same OutputStream without causing the stream
// to get corrupted because of multiple headers written to it
// http://stackoverflow.com/questions/1194656/appending-to-an-objectoutputstream/1195078#1195078
class AppendingObjectOutputStream extends ObjectOutputStream {
    private static final ArrayList<Socket> initializedSockets = new ArrayList<>();
    private final Socket socket;

    private AppendingObjectOutputStream(Socket socket) throws IOException {
        super(socket.getOutputStream());
        this.socket = socket;
    }

    static ObjectOutputStream getInstance(Socket socket) throws IOException {
        if (initializedSockets.contains(socket)) return new AppendingObjectOutputStream(socket);
        ObjectOutputStream instance = new ObjectOutputStream(socket.getOutputStream());
        initializedSockets.add(socket);
        return instance;
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        reset();
    }

    @Override
    public void close() throws IOException {
        initializedSockets.remove(socket);
        super.close();
    }
}
