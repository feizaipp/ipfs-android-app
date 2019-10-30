package com.ipfstest.z11.ipfs1.api;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Handler;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.api.Peer;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

public class IPFSHttpAPI {
    private static final String TAG = "IPFSHttpAPI";
    public static final int HTTP_API_BASE = 0;
    public static final int HTTP_API_GET_PEERS_ID = HTTP_API_BASE + 1;
    public static final int HTTP_API_GET_PINS = HTTP_API_BASE + 2;
    public static final int HTTP_API_GET_SWARM_PEERS = HTTP_API_BASE + 3;
    public static final int HTTP_API_GET_SWARM_PEERS_COUNT = HTTP_API_BASE + 4;
    public static final int HTTP_API_GET_REPO_STAT = HTTP_API_BASE + 5;
    public static final int HTTP_API_ADD_FILE = HTTP_API_BASE + 6;
    public static final int HTTP_API_ADD_DIR = HTTP_API_BASE + 7;
    public static final int HTTP_API_GET_CONFIG = HTTP_API_BASE + 8;
    public static final int HTTP_API_SET_CONFIG = HTTP_API_BASE + 9;

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
                    Map<Multihash, Object> pins = ipfs.pin.ls(IPFS.PinType.recursive);
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

    public void getSwarmPeers() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    List<Peer> peers = ipfs.swarm.peers();
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_GET_SWARM_PEERS;
                    msg.obj = peers;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getSwarmPeersCount() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    List<Peer> peers = ipfs.swarm.peers();
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_GET_SWARM_PEERS_COUNT;
                    msg.obj = peers.size();
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void add_files(String path, boolean pin, int type) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(path));
                    List<MerkleNode> addResult = ipfs.add(file, false, false, false);
                    Message msg = Message.obtain();
                    if (type == 0) {
                        msg.what = HTTP_API_ADD_FILE;
                    } else {
                        msg.what = HTTP_API_ADD_DIR;
                    }
                    msg.obj = addResult;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getRepoStat() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    Map stat = ipfs.repo.stat();
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_GET_REPO_STAT;
                    msg.obj = stat;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void getConfig() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    Map config = ipfs.config.show();
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_GET_CONFIG;
                    msg.obj = config;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void setConfig() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                try {
                    Map config = ipfs.config.show();
                    Message msg = Message.obtain();
                    msg.what = HTTP_API_SET_CONFIG;
                    msg.obj = config;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
