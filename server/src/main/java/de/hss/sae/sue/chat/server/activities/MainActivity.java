package de.hss.sae.sue.chat.server.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hss.sae.sue.chat.server.Utils;
import de.hss.sae.sue.chat.server.communication.CommunicationService;
import de.hss.sae.sue.chat.server.R;

public class MainActivity extends AppCompatActivity {
    public static final String BROADCAST_ID = "main-activity";

    private TextView twServerStatus;
    private Button btToggleServer;

    private String serverAddress = null;
    private BroadcastReceiver serverAddressReceiver;
    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twServerStatus = (TextView) findViewById(R.id.twServerStatus);
        btToggleServer = (Button) findViewById(R.id.btToggleServer);

        serverAddressReceiver = createServerAddressReceiver();
        broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.registerReceiver(serverAddressReceiver, new IntentFilter(CommunicationService.BROADCAST_ID));

        if (isServiceRunning()) {
            broadcastManager.sendBroadcast(new Intent(BROADCAST_ID));
        } else {
            refreshServerStatus();
        }

        btToggleServer.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        broadcastManager.unregisterReceiver(serverAddressReceiver);
        super.onDestroy();
    }

    private void refreshServerStatus() {
        if (isServiceRunning() && isServerOnline()) {
            twServerStatus.setText(getString(R.string.twServerStatusRunning, serverAddress));
            btToggleServer.setText(getString(R.string.btToggleServerStop));
            btToggleServer.setOnClickListener(new OnStopServer());
        } else {
            twServerStatus.setText(getString(R.string.twServerStatusStopped));
            btToggleServer.setText(getString(R.string.btToggleServerStart));
            btToggleServer.setOnClickListener(new OnStartServer());
        }
    }

    private boolean isServiceRunning() {
        return Utils.isServiceRunning(this, CommunicationService.class);
    }

    private boolean isServerOnline() {
        return serverAddress != null;
    }

    private BroadcastReceiver createServerAddressReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                serverAddress = intent.getExtras().getString(CommunicationService.EXTRA_ID_ADDRESS);
                refreshServerStatus();
            }
        };
    }

    private class OnStartServer implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // start service with wake lock to keep phone from turning off cpu
            WakefulBroadcastReceiver.startWakefulService(MainActivity.this, new Intent(MainActivity.this, CommunicationService.class));
        }
    }

    private class OnStopServer implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            stopService(new Intent(MainActivity.this, CommunicationService.class));
        }
    }
}
