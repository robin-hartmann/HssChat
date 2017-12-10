package de.hss.sae.sue.chat.client.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.hss.sae.sue.chat.client.R;
import de.hss.sae.sue.chat.client.Utils;
import de.hss.sae.sue.chat.client.communication.MessagingService;
import de.hss.sae.sue.chat.client.localstorage.SettingsEditor;

public class EnterAddress extends AppCompatActivity {
    private EditText etEnterIp;
    private EditText etEnterPort;

    private SettingsEditor settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_enter_address);

        settings = new SettingsEditor(this);

        etEnterIp = (EditText) findViewById(R.id.etEnterIp);
        etEnterPort = (EditText) findViewById(R.id.etEnterPort);

        etEnterIp.setText(settings.getString(SettingsEditor.ID_IP));
        if (settings.getInt(SettingsEditor.ID_Port) != 0) {
            etEnterPort.setText(String.valueOf(settings.getInt(SettingsEditor.ID_Port)));
        }
    }

    public void onSendIp(View view) {
        String ip = etEnterIp.getText().toString();
        String stringPort = etEnterPort.getText().toString();

        if (ip.isEmpty() && !Utils.isValidPort(stringPort)) {
            Toast.makeText(this, R.string.toastInvalidIpAndPort, Toast.LENGTH_LONG).show();
            return;
        } else if (ip.isEmpty()) {
            Toast.makeText(this, R.string.toastInvalidIp, Toast.LENGTH_SHORT).show();
            return;
        } else if (!Utils.isValidPort(stringPort)) {
            Toast.makeText(this, R.string.toastInvalidPort, Toast.LENGTH_SHORT).show();
            return;
        }

        int port = Integer.parseInt(stringPort);

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.progressConnectTitle));
        progress.setMessage(getString(R.string.progressConnectMessage));
        progress.setCancelable(false);
        progress.show();

        ConnectStateHandler stateHandler = new ConnectStateHandler(this, ip, port, settings, progress);
        MessagingService.getInstance().connect(ip, port, stateHandler);
    }

    private static class ConnectStateHandler extends Handler {
        private EnterAddress instance;
        private String ip;
        private int port;
        private SettingsEditor settings;
        private ProgressDialog progress;

        ConnectStateHandler(EnterAddress instance, String ip, int port, SettingsEditor settings, ProgressDialog progress) {
            super(instance.getMainLooper());
            this.instance = instance;
            this.ip = ip;
            this.port = port;
            this.settings = settings;
            this.progress = progress;
        }

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();

            switch (msg.what) {
                case MessagingService.STATE_ERROR:
                    new AlertDialog.Builder(instance)
                            .setTitle(instance.getString(R.string.alertConnectErrorTitle))
                            .setMessage(instance.getString(R.string.alertConnectErrorMessage))
                            .setNeutralButton(instance.getString(R.string.dialogOk), null)
                            .show();
                    return;

                case MessagingService.STATE_FINISH:
                    settings.setString(SettingsEditor.ID_IP, ip);
                    settings.setInt(SettingsEditor.ID_Port, port);
                    instance.startActivity(new Intent(instance, ChatRoom.class));
            }
        }
    }
}