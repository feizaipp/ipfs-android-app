package com.ipfstest.z11.ipfs1.activity;

import androidx.annotation.IntDef;
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
import android.widget.TextView;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.adapter.MainAdapter;
import com.ipfstest.z11.ipfs1.api.IPFSHttpAPI;
import com.ipfstest.z11.ipfs1.common.DaemonStatus;
import com.ipfstest.z11.ipfs1.common.ExecLog;
import com.ipfstest.z11.ipfs1.common.MyDividerItemDecoration;
import com.ipfstest.z11.ipfs1.common.Status;
import com.ipfstest.z11.ipfs1.service.CmdIntentService;
import com.ipfstest.z11.ipfs1.utils.ProcessUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Menu mMenu;
    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView tv_status;
    private TextView tv_info;

    private void initData() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new MainAdapter(null);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private Map<String, String> getData(Map id) {

        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject object = new JSONObject(id);
            JSONArray array = new JSONArray(object.getString("Addresses"));
            String addr = "";
            for (int i = 0; i < array.length(); i++) {
                addr += array.get(i) + "\n";
            }
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IPFSHttpAPI.HTTP_API_GET_PEERS_ID:
                    Map id = (Map) msg.obj;
                    try {
                        mAdapter.updateData(getData(id));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case IPFSHttpAPI.HTTP_API_GET_SWARM_PEERS_COUNT:
                    int count = (int)msg.obj;
                    tv_info.setText("Discovered " + count);
                    tv_info.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mHttpApi.getSwarmPeersCount();
            handler.sendEmptyMessageDelayed(1, 3000);
            return true;
        }
    });

    IPFSHttpAPI mHttpApi = new IPFSHttpAPI(mHandler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_status = findViewById(R.id.status);
        tv_info = findViewById(R.id.info);

        initData();
        initView();

        Log.d(TAG, "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.daemon_status).setTitle("IPFS运行中");
        mMenu.findItem(R.id.daemon_start).setVisible(false);
        mMenu.findItem(R.id.daemon_stop).setVisible(true);
        mMenu.findItem(R.id.daemon_restart).setVisible(true);
        mMenu.findItem(R.id.files).setVisible(true);
        mMenu.findItem(R.id.pin).setVisible(true);
        mMenu.findItem(R.id.peers).setVisible(true);
        mMenu.findItem(R.id.config).setVisible(true);
        if (!ProcessUtils.daemonStarted(MainActivity.this)) {
            CmdIntentService.startActionDaemon(MainActivity.this);
        }
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
                handler.removeCallbacksAndMessages(null);
                CmdIntentService.startActionShutdown(MainActivity.this);
                break;
            case R.id.daemon_restart:
                handler.removeCallbacksAndMessages(null);
                CmdIntentService.startActionRestart(MainActivity.this);
                break;
            case R.id.files:
                startActivity(FilesActivity.class);
                break;
            case R.id.pin:
                startActivity(PinActivity.class);
                break;
            case R.id.peers:
                startActivity(PeersActivity.class);
                break;
            case R.id.config:
                startActivity((ConfigActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void daemonStarted() {
        mHttpApi.getPeerID();
        handler.sendEmptyMessageDelayed(1, 3000);
        tv_status.setText("Connected to IPFS");
        tv_status.setVisibility(View.VISIBLE);
        tv_info.setVisibility(View.INVISIBLE);
    }

    private void daemonStoped() {
        tv_status.setText("Stopped IPFS Daemon...");
        tv_status.setVisibility(View.VISIBLE);
        tv_info.setVisibility(View.INVISIBLE);
        mAdapter.updateData(null);
    }

    private void daemonStarting() {
        tv_status.setText("Connecting to IPFS...");
        tv_status.setVisibility(View.VISIBLE);
        tv_info.setVisibility(View.INVISIBLE);
        mAdapter.updateData(null);
    }

    private void daemonStopping() {
        tv_status.setText("Stopping IPFS Daemon...");
        tv_status.setVisibility(View.VISIBLE);
        tv_info.setVisibility(View.INVISIBLE);
        mAdapter.updateData(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(DaemonStatus mDS) {
        Status ds = mDS.getDaemonStatus();
        switch (ds) {
            case started:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS运行中");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(true);
                mMenu.findItem(R.id.daemon_restart).setVisible(true);
                mMenu.findItem(R.id.files).setVisible(true);
                mMenu.findItem(R.id.pin).setVisible(true);
                mMenu.findItem(R.id.peers).setVisible(true);
                mMenu.findItem(R.id.config).setVisible(true);
                daemonStarted();
                break;
            case stopped:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS没有运行");
                mMenu.findItem(R.id.daemon_start).setVisible(true);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                mMenu.findItem(R.id.files).setVisible(false);
                mMenu.findItem(R.id.pin).setVisible(false);
                mMenu.findItem(R.id.peers).setVisible(false);
                mMenu.findItem(R.id.config).setVisible(false);
                daemonStoped();
                break;
            case starting:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS is starting");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                mMenu.findItem(R.id.files).setVisible(false);
                mMenu.findItem(R.id.pin).setVisible(false);
                mMenu.findItem(R.id.peers).setVisible(false);
                mMenu.findItem(R.id.config).setVisible(false);
                daemonStarting();
                break;
            case stopping:
                mMenu.findItem(R.id.daemon_status).setTitle("IPFS is stopping");
                mMenu.findItem(R.id.daemon_start).setVisible(false);
                mMenu.findItem(R.id.daemon_stop).setVisible(false);
                mMenu.findItem(R.id.daemon_restart).setVisible(false);
                mMenu.findItem(R.id.files).setVisible(false);
                mMenu.findItem(R.id.pin).setVisible(false);
                mMenu.findItem(R.id.peers).setVisible(false);
                mMenu.findItem(R.id.config).setVisible(false);
                daemonStopping();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEvent(ExecLog event) {
        //Log.d(TAG, event.log);
        if (event.log.contains("shutdown")) {
            CmdIntentService.startActionDaemon(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (ProcessUtils.daemonStarted(MainActivity.this)) {
            daemonStarted();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ProcessUtils.daemonStarted(MainActivity.this)) {
            handler.removeCallbacksAndMessages(null);
            CmdIntentService.startActionShutdown(this);
        }
    }
}
