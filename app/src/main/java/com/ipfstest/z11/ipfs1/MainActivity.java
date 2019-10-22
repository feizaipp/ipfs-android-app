package com.ipfstest.z11.ipfs1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Menu mMenu;
    boolean daemon_started = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MainAdapter(getData());
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private Map<String, String> getData() {
        String json = "{\n" +
                "\t\"ID\": \"QmXzYkFL93SVgteZJr2TtXNozaariQvPX6sEZe1Hb4EjAE\",\n" +
                "\t\"PublicKey\": \"CAASpgIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDJ+f2hI30g0fto2vF8+W32WM8ehFxjqRm/oJXosqf7VOI8we1/0R/gO1MlcdWiUDCk0BJfZSc5tTS2thLevaMhZTLNfHyS5KALMd65gBmAKQa+kZqtHvQpmIk6Er+ZrpcG4HQvXR4sAF0LScFL6dbWWEtPpG4vATS9XCj9+gXb69EOgxqm71q28a/2YUOtTd9kk9ANHVGv3vlT8jATOyA5ZL4ZaKxrLXjkAw1yS+rMTK1i11uc8VeSjWHBZPMEtrEU/yXRhC/idJba6IoQk8JS6sTM6cD2V+9Obm4Y5u6wfPg0rezVhF9DKbmeDR0ZkJKz0AEkq2JT/xglkd5RieErAgMBAAE=\",\n" +
                "\t\"Addresses\": [\n" +
                "\t\t\"/ip4/127.0.0.1/tcp/4001/ipfs/QmXzYkFL93SVgteZJr2TtXNozaariQvPX6sEZe1Hb4EjAE\",\n" +
                "\t\t\"/ip4/192.168.3.89/tcp/4001/ipfs/QmXzYkFL93SVgteZJr2TtXNozaariQvPX6sEZe1Hb4EjAE\",\n" +
                "\t\t\"/ip4/192.168.122.1/tcp/4001/ipfs/QmXzYkFL93SVgteZJr2TtXNozaariQvPX6sEZe1Hb4EjAE\",\n" +
                "\t\t\"/ip6/::1/tcp/4001/ipfs/QmXzYkFL93SVgteZJr2TtXNozaariQvPX6sEZe1Hb4EjAE\"\n" +
                "\t],\n" +
                "\t\"AgentVersion\": \"go-ipfs/0.4.22/\",\n" +
                "\t\"ProtocolVersion\": \"ipfs/0.1.0\"\n" +
                "}";


        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = new JSONArray(object.getString("Addresses"));
            Log.d(TAG, array.toString());
            String addr = "";
            for (int i=0; i<array.length(); i++) {
                addr += array.get(i) + "\n";
            }
            Log.d(TAG, addr);
            map.put("ID", object.getString("ID"));
            map.put("PublicKey", object.getString("PublicKey"));
            map.put("Addresses", addr);
            map.put("AgentVersion", object.getString("AgentVersion"));
            map.put("ProtocolVersion", object.getString("ProtocolVersion"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case IPFSHttpAPI.HTTP_API_GET_PEERS_ID:
                    Map id = (Map) msg.obj;
                    Log.d(TAG, id.toString());
                    try {
                        JSONObject object = new JSONObject(id);
                        String name = object.getString("ID");
                        Log.d(TAG, name);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case IPFSHttpAPI.HTTP_API_GET_PINS:

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!daemon_started) {
            CmdIntentService.startActionDaemon(MainActivity.this);
        }
        initData();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mMenu = menu;
        menu.findItem(R.id.daemon_status).setTitle("IPFS运行中");
        mMenu.findItem(R.id.daemon_start).setVisible(false);
        mMenu.findItem(R.id.daemon_stop).setVisible(true);
        mMenu.findItem(R.id.daemon_restart).setVisible(true);
        mMenu.findItem(R.id.files).setVisible(true);
        return true;
    }

    public void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Menu menu;

        int id = item.getItemId();
        switch (id) {
            case R.id.daemon_start:
                CmdIntentService.startActionDaemon(MainActivity.this);
                break;
            case R.id.daemon_stop:
                CmdIntentService.startActionShutdown(MainActivity.this);
                break;
            case R.id.daemon_restart:
                CmdIntentService.startActionRestart(MainActivity.this);
                break;
            case R.id.files:
                startActivity(FilesActivity.class);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(DaemonStatus mDS) {
        SERVICE_STATUS ds = mDS.getDaemonStatus();
        switch (ds) {
            case started:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS运行中");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(true);
                mMenu.findItem(R.id.daemon_restart).setVisible(true);
                mMenu.findItem(R.id.files).setVisible(true);
                daemon_started = true;
                //IPFSHttpAPI api = new IPFSHttpAPI(mHandler);
                //api.getPeerID();
                break;
            case stopped:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS没有运行");
                mMenu.findItem(R.id.daemon_start).setVisible(true);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                mMenu.findItem(R.id.files).setVisible(false);
                daemon_started = false;
                break;
            case starting:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS is starting");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                mMenu.findItem(R.id.files).setVisible(false);
                daemon_started = false;
                break;
            case stopping:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS is stopping");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                mMenu.findItem(R.id.files).setVisible(false);
                daemon_started = false;
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ExecLog event) {
        if (event.log.contains("shutdown")) {
            CmdIntentService.startActionDaemon(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
