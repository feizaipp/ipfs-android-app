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
                    msg.what = 1;
                    msg.obj = id;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
