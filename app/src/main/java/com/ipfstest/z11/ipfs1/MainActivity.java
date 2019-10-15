package com.ipfstest.z11.ipfs1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;
import io.ipfs.multiaddr.MultiAddress;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button test;
    Context mContext;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (Button)findViewById(R.id.btntest);

        test.setOnClickListener(listener);
        //CmdIntentService.startActionDaemon(this);
        mContext = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        menu.findItem(R.id.daemon_status).setTitle("IPFS运行中");
        mMenu.findItem(R.id.daemon_start).setVisible(false);
        mMenu.findItem(R.id.daemon_stop).setVisible(true);
        mMenu.findItem(R.id.daemon_restart).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Menu menu;

        int id = item.getItemId();
        switch (id) {
            case R.id.daemon_status:
                break;
            case R.id.daemon_start:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS运行中");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(true);
                mMenu.findItem(R.id.daemon_restart).setVisible(true);
                break;
            case R.id.daemon_stop:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS没有运行");
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                break;
            case R.id.daemon_restart:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS is starting");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    Button.OnClickListener listener = new Button.OnClickListener(){

        public void onClick(View v){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    IPFS ipfs = new IPFS(new MultiAddress("/ip4/127.0.0.1/tcp/5001"));
                    try {
                        List<Multihash> list = ipfs.refs.local();
                        Iterator<Multihash> iter = list.iterator();
                        while (iter.hasNext()) {
                            Multihash hash = iter.next();
                            Log.d(TAG, hash.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    };
}
