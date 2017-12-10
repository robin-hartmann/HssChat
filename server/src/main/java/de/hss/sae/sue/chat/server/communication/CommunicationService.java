package de.hss.sae.sue.chat.server.communication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import de.hss.sae.sue.chat.server.R;
import de.hss.sae.sue.chat.server.Utils;
import de.hss.sae.sue.chat.server.activities.MainActivity;

/**
 * Created by robin.hartmann on 19.10.2016.
 */
public class CommunicationService extends IntentService {
    public static final String EXTRA_ID_ADDRESS = "ADDRESS";
    public static final String BROADCAST_ID = "communication-service";

    private static final String SERVICE_NAME = "HSS Chat Server";
    private static final int NOTIFICATION_ID = 1;
    private static final int PORT = 16567;

    private final ArrayList<Socket> connectedClients = new ArrayList<>();

    private boolean isRunning = false;
    private Intent startIntent;
    private NotificationManager notificationManager;
    private ServerSocket server;
    private BroadcastReceiver receiver;
    private LocalBroadcastManager broadcastManager;

    public CommunicationService() {
        super(SERVICE_NAME);
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        receiver = createBroadcastReceiver();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(receiver, new IntentFilter(MainActivity.BROADCAST_ID));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startIntent = intent;
        runListener(PORT);
    }

    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(receiver);
        stopListener();
        hideNotification();
        broadcastAddress();
        WakefulBroadcastReceiver.completeWakefulIntent(startIntent);
        super.onDestroy();
    }

    private void runListener(int port) {
        isRunning = true;

        try {
            server = new ServerSocket(port, 50, Utils.getIPAddress(true));
            showNotification(CommunicationService.this);
            broadcastAddress();

            while (isRunning) {
                Socket client = server.accept();
                new Thread(new ListenRunnable(this, client)).start();
            }
        } catch (SocketException e) {
            // The server was stopped intentionally; ignore
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Server error.", e);
        }
    }

    private void stopListener() {
        try {
            isRunning = false;

            if (server != null) {
                server.close();
                server = null;
            }

            for (Socket s : getConnectedClients()) {
                s.close();
            }
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "Error closing server socket.", e);
        }
    }

    private void showNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(SERVICE_NAME)
                .setContentText(getString(R.string.twServerStatusRunning, getAddress()))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.logo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_SECRET);
        }

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            //noinspection deprecation
            notification = builder.getNotification();
        }

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcastAddress();
            }
        };
    }

    private void broadcastAddress() {
        Intent broadcast = new Intent(BROADCAST_ID);
        String extra = null;

        if (isRunning()) {
            extra = getAddress();
        }

        broadcast.putExtra(EXTRA_ID_ADDRESS, extra);
        broadcastManager.sendBroadcast(broadcast);
    }

    private String getAddress() {
        if (server != null) {
            return Utils.toString(server.getInetAddress()) + ":" + server.getLocalPort();
        } else {
            return null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public ArrayList<Socket> getConnectedClients() {
        return connectedClients;
    }
}
