package com.ipfstest.z11.ipfs1.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.ipfstest.z11.ipfs1.common.DaemonStatus;
import com.ipfstest.z11.ipfs1.common.Status;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class CmdIntentService extends IntentService {

    private static final String EXTRA_FILEPATH = "EXTRA_FILEPATH";
    public static final String EXTRA_EXEC = "EXTRA_EXEC";
    private static final String TAG = "CmdIntentService";

    Thread thread;
    EXEC_TYPE exec_type;
    String absPath;
    int status;

    Handler handler = new Handler();

    enum EXEC_TYPE {
        daemon, shutdown, restart
    }

    public CmdIntentService() {
        super("CmdIntentService");
        Log.d(TAG, "CmdIntentService");
    }

    public static void startActionDaemon(Context context) {
        Log.d(TAG, "startActionDaemon");
        Intent intent = new Intent(context, CmdIntentService.class);
        intent.putExtra(EXTRA_EXEC, EXEC_TYPE.daemon);
        context.startService(intent);
    }

    public static void startActionShutdown(Context context) {
        Intent intent = new Intent(context, CmdIntentService.class);
        intent.putExtra(EXTRA_EXEC, EXEC_TYPE.shutdown);
        context.startService(intent);
    }

    public static void startActionRestart(Context context) {
        Intent intent = new Intent(context, CmdIntentService.class);
        intent.putExtra(EXTRA_EXEC, EXEC_TYPE.restart);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            try {
                exec_type = (EXEC_TYPE) intent.getSerializableExtra(EXTRA_EXEC);
                switch (exec_type) {
                    case daemon:
                        Log.d(TAG, "onHandleIntent daemon");
                        daemon();
                        break;
                    case shutdown:
                        shutdown();
                        break;
                    case restart:
                        restart();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void daemon() throws IOException {
        boolean stat = false;
        IpfsExec.getInstance(getBaseContext()).init();
        EventBus.getDefault().post(new DaemonStatus(Status.starting));
        stat = IpfsExec.getInstance(getBaseContext()).daemon();
        if (stat) {
            EventBus.getDefault().post(new DaemonStatus(Status.started));
        }
    }

    private void shutdown() throws IOException {
        EventBus.getDefault().post(new DaemonStatus(Status.stopping));
        IpfsExec.getInstance(getBaseContext()).shutDown();
        EventBus.getDefault().post(new DaemonStatus(Status.stopped));
    }

    private void restart() throws IOException {
        boolean stat = false;
        EventBus.getDefault().post(new DaemonStatus(Status.stopping));
        stat = IpfsExec.getInstance(getBaseContext()).shutDown();
        if (stat) {
            EventBus.getDefault().post(new DaemonStatus(Status.stopped));
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        stat = false;
        EventBus.getDefault().post(new DaemonStatus(Status.starting));
        stat = IpfsExec.getInstance(getBaseContext()).daemon();
        if (stat) {
            EventBus.getDefault().post(new DaemonStatus(Status.started));
        }
    }
}
