package de.hss.sae.sue.chat.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import de.hss.sae.sue.chat.client.localstorage.SettingsEditor;
import de.hss.sae.sue.chat.client.R;

public class EnterName extends AppCompatActivity {
    private EditText etEnterName;
    private SettingsEditor settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);

        settings = new SettingsEditor(this);
        etEnterName = (EditText) findViewById(R.id.etEnterName);

        etEnterName.setText(settings.getString(SettingsEditor.ID_Nickname));
    }

    public void onSendName(View view) {
        String stringName = etEnterName.getText().toString();

        if (stringName.isEmpty()) {
            Toast.makeText(this, R.string.toastInvalidNickname, Toast.LENGTH_LONG).show();
            return;
        }

        settings.setString(SettingsEditor.ID_Nickname, etEnterName.getText().toString());
        startActivity(new Intent(this, EnterAddress.class));
    }
}
