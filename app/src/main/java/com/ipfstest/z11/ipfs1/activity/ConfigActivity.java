package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.service.CmdIntentService;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

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
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addr_setting:
                startActivity(SetaddrActivity.class);
                break;
            case R.id.priv_net_setting:
                startActivity(SetPrivNetActivity.class);
                break;
            case R.id.restart:
                CmdIntentService.startActionRestart(this);
                break;
        }
    }

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
