package com.ipfstest.z11.ipfs1;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Handler;

import io.ipfs.api.IPFS;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

public class IPFSHttpAPI {
    private static final String TAG = "IPFSHttpAPI";
    public static final int HTTP_API_BASE = 0;
    public static final int HTTP_API_GET_PEERS_ID = HTTP_API_BASE + 1;
    public static final int HTTP_API_GET_PINS = HTTP_API_BASE + 2;

    private Handler handler;

    public IPFSHttpAPI(Handler handler) {
        this.handler = handler;
    }

    public void getPeerID() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    Map id = ipfs.id();
                    Log.d(TAG, id.toString());
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_GET_PEERS_ID;
                    msg.obj = id;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getPins() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    Map pins = ipfs.pin.ls();
                    Log.d(TAG, pins.toString());
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_GET_PINS;
                    msg.obj = pins;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
