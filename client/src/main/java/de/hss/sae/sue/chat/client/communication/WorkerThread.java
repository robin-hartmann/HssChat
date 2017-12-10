package de.hss.sae.sue.chat.client.communication;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Robin on 02.01.2017.
 */

class WorkerThread extends HandlerThread {
    private Runnable task;

    WorkerThread(String name, Runnable task) {
        super(name);
        this.task = task;
    }

    @Override
    protected void onLooperPrepared() {
        // getLooper has to be called
        // after the looper is prepared
        // or else it returns null
        new Handler(getLooper()).post(task);
    }
}
