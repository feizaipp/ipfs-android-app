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
import com.ipfstest.z11.ipfs1.utils.JsonUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
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

        getConfig();
    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.btn_reset:
                    getConfig();
                    break;
                case R.id.btn_set:
                    setConfig();
                    break;
            }
        }
    };

    private void getConfig() {
        try {
            String configPath = Constants.Dir.getConfigPath(this);
            File file = new File(configPath);
            long len = file.length();
            byte[] bytes = new byte[(int)len];
            InputStream input = new FileInputStream(file);
            input.read(bytes, 0, (int)len);
            input.close();
            String config = new String(bytes);
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

    private void setConfig() {
        try {
            String configPath = Constants.Dir.getConfigPath(this);
            File file = new File(configPath);
            long len = file.length();
            byte[] bytes = new byte[(int)len];
            InputStream input = new FileInputStream(file);
            input.read(bytes, 0, (int)len);
            input.close();
            String config = new String(bytes);
            JSONObject object = new JSONObject(config);
            JSONObject addr = object.getJSONObject("Addresses");
            String api = et_api.getText().toString();
            String gw = et_gw.getText().toString();
            addr.put("API", api);
            addr.put("Gateway", gw);
            BufferedWriter write = new BufferedWriter(new FileWriter(file));
            write.write(JsonUtil.JsonFormat(object.toString()));
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
