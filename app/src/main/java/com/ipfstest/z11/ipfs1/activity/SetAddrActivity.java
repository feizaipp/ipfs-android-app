package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ipfstest.z11.ipfs1.R;
import com.ipfstest.z11.ipfs1.api.IPFSHttpAPI;
import com.ipfstest.z11.ipfs1.service.CmdIntentService;
import com.ipfstest.z11.ipfs1.utils.Constants;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class SetAddrActivity extends AppCompatActivity {

    Button btn_reset;
    Button btn_set;
    EditText et_api;
    EditText et_gw;
    private static final String TAG = "SetAddrActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_addr);

        btn_reset = findViewById(R.id.btn_reset);
        btn_set = findViewById(R.id.btn_set);
        btn_reset.setOnClickListener(listener);
        btn_set.setOnClickListener(listener);

        et_api = findViewById(R.id.et_api);
        et_gw = findViewById(R.id.et_gw);

        mHttpApi.getConfig();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IPFSHttpAPI.HTTP_API_GET_CONFIG:
                    Map config = (Map)msg.obj;
                    parseConfig(config);
                    break;
                case IPFSHttpAPI.HTTP_API_SET_CONFIG:
                    Map cfg = (Map)msg.obj;
                    setConfig(cfg);
                    break;
            }
        }
    };

    IPFSHttpAPI mHttpApi = new IPFSHttpAPI(mHandler);

    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.btn_reset:
                    mHttpApi.getConfig();
                    break;
                case R.id.btn_set:
                    mHttpApi.setConfig();
                    break;
            }
        }
    };

    private void parseConfig(Map config) {
        try {
            JSONObject object = new JSONObject(config);
            JSONObject addr = object.getJSONObject("Addresses");
            String api = addr.getString("API");
            String gw = addr.getString("Gateway");
            et_api.setText(api);
            et_gw.setText(gw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setConfig(Map config) {
        try {
            JSONObject object = new JSONObject(config);
            JSONObject addr = object.getJSONObject("Addresses");
            String api = et_api.getText().toString();
            String gw = et_gw.getText().toString();
            addr.put("API", api);
            addr.put("Gateway", gw);
            object.put("Addresses", addr);
            Log.d(TAG, object.toString());

            File file = new File(Constants.Dir.getConfigPath(this));
            BufferedWriter write = new BufferedWriter(new FileWriter(file));
            write.write(object.toString());
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
