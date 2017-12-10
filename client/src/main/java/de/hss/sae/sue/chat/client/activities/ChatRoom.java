package de.hss.sae.sue.chat.client.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.List;

import de.hss.sae.sue.chat.client.R;
import de.hss.sae.sue.chat.client.communication.MessagingService;
import de.hss.sae.sue.chat.client.localstorage.Database;
import de.hss.sae.sue.chat.client.localstorage.SettingsEditor;
import de.hss.sae.sue.chat.common.communication.Message;

public class ChatRoom extends AppCompatActivity {
    private SettingsEditor settings;
    private Database db;

    private EditText etEnterMsg;
    private LinearLayout messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        SQLiteDatabase.loadLibs(this);

        etEnterMsg = (EditText) findViewById(R.id.etEnterMsg);
        messageView = (LinearLayout) findViewById(R.id.messageView);
        messageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                scrollToBottom();
            }
        });

        settings = new SettingsEditor(this);
        db = new Database(this);
        displayOldMessages();
        MessagingService.getInstance().startListener(this, new ListenStateHandler(this));
    }

    @Override
    protected void onDestroy() {
        MessagingService messagingService = MessagingService.getInstance();
        messagingService.disconnect();
        super.onDestroy();
    }

    public void clearChat(View view) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alertClearChatTitle))
                .setMessage(getString(R.string.alertClearChatMessage))
                .setPositiveButton(getString(R.string.dialogYes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        messageView.removeAllViews();
                        db.deleteAllMessages();
                    }
                })
                .setNegativeButton(getString(R.string.dialogNo), null).show();
    }

    public void sendMessage(View view) {
        if (!etEnterMsg.getText().toString().isEmpty()) {
            Message message = Message.obtainMessage(settings.getString(SettingsEditor.ID_Nickname), etEnterMsg.getText().toString());
            MessagingService.getInstance().send(message, new SendStateHandler(this));
            etEnterMsg.setText("");
        }
    }

    public void displayMessage(Message message) {
        TextView messageView = new TextView(this);
        messageView.setText(message.getSender() + ":\n" + message.getMessage());
        messageView.setGravity(Gravity.START);
        messageView.setTextColor(Color.BLACK);
        messageView.setTextSize(22);
        messageView.setPadding(0, 0, 0, 20);
        this.messageView.addView(messageView);
        scrollToBottom();
    }

    private void scrollToBottom() {
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));
        assert scrollview != null;
        scrollview.post(new Runnable() {
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void displayOldMessages() {
        List<Message> messageList = db.getAllMessages();

        for (Message m : messageList) {
            displayMessage(m);
        }
    }

    private static class SendStateHandler extends Handler {
        private ChatRoom instance;

        SendStateHandler(ChatRoom instance) {
            super(instance.getMainLooper());
            this.instance = instance;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessagingService.STATE_ERROR:
                    new AlertDialog.Builder(instance)
                            .setTitle(instance.getString(R.string.alertSendErrorTitle))
                            .setMessage(instance.getString(R.string.alertSendErrorMessage))
                            .setNeutralButton(instance.getString(R.string.dialogOk), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    instance.finish();
                                }
                            })
                            .show();
                    return;
            }
        }
    }

    private static class ListenStateHandler extends Handler {
        ChatRoom instance;

        ListenStateHandler(ChatRoom instance) {
            super(instance.getMainLooper());
            this.instance = instance;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MessagingService.STATE_ERROR:

                    // This prevents the dialog from creating an error
                    // when the listener is closed because of the activity closing
                    if (instance.isFinishing()) return;

                    new AlertDialog.Builder(instance)
                            .setTitle(instance.getString(R.string.alertListenErrorTitle))
                            .setMessage(instance.getString(R.string.alertListenErrorMessage))
                            .setNeutralButton(instance.getString(R.string.dialogOk), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    instance.finish();
                                }
                            })
                            .show();
                    return;

                case MessagingService.STATE_UPDATE:
                    instance.displayMessage(instance.db.getLastMessage());
            }
        }
    }
}