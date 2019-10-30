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
import com.ipfstest.z11.ipfs1.utils.Constants;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class SetPrivNetActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_reset;
    Button btn_set;
    EditText et_pn;
    private static final String TAG = "SetPrivNetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_priv_net);

        btn_reset = findViewById(R.id.btn_reset);
        btn_set = findViewById(R.id.btn_set);
        et_pn = findViewById(R.id.et_pn);

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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset:
                mHttpApi.getConfig();
                break;
            case R.id.btn_set:
                mHttpApi.setConfig();
                break;
        }
    }

    private void parseConfig(Map config) {
        try {
            JSONObject object = new JSONObject(config);
            String bs = object.getString("Bootstrap");
            et_pn.setText(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setConfig(Map config) {
        try {
            JSONObject object = new JSONObject(config);
            String bs = et_pn.getText().toString();
            object.put("Bootstrap", bs);
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
