package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.service.CmdIntentService;

import cn.wandersnail.fileselector.FileSelector;

public class ConfigActivity extends AppCompatActivity {

    Button btn_addr;
    Button btn_pn;
    Button btn_restart;
    private static final String TAG = "ConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        btn_addr = findViewById(R.id.addr_setting);
        btn_pn = findViewById(R.id.priv_net_setting);
        btn_restart = findViewById(R.id.restart);
        btn_addr.setOnClickListener(listener);
        btn_pn.setOnClickListener(listener);
        btn_restart.setOnClickListener(listener);
    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.addr_setting:
                    Log.d(TAG, "addr setting");
                    startActivity(SetAddrActivity.class);
                    break;
                case R.id.priv_net_setting:
                    startActivity(SetPrivNetActivity.class);
                    break;
                case R.id.restart:
                    CmdIntentService.startActionRestart(ConfigActivity.this);
                    break;
            }
        }
    };

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
