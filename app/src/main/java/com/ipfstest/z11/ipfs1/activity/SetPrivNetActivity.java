package com.ipfstest.z11.ipfs1.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.ipfstest.z11.ipfs1.utils.JsonUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import cn.wandersnail.fileselector.FileSelector;
import cn.wandersnail.fileselector.OnFileSelectListener;

public class SetPrivNetActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_reset;
    Button btn_set;
    Button btn_add_key;
    Button btn_del_key;
    EditText et_pn;

    FileSelector selector;

    private static final String TAG = "SetPrivNetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_priv_net);

        btn_reset = findViewById(R.id.btn_reset);
        btn_set = findViewById(R.id.btn_set);
        btn_add_key = findViewById(R.id.btn_add_key);
        btn_del_key = findViewById(R.id.btn_del_key);
        et_pn = findViewById(R.id.et_pn);

        mHttpApi.getConfig();

        selector = new FileSelector().setScreenOrientation(false).showHiddenFiles(true);
        selector.setTitle("文件选择器");
        selector.setOnFileSelectListener(listener);
    }

    OnFileSelectListener listener = new OnFileSelectListener() {
        @Override
        public void onFileSelect(int requestCode, List<String> paths) {
            Log.d(TAG, "requestCode: " + requestCode);
            switch (requestCode) {
                case 2:
                    try {
                        File file = new File(paths.get(0));
                        String name = file.getName();
                        FileInputStream input = new FileInputStream(file);
                        Log.d(TAG, name);
                        FileOutputStream fileOutputStream = new FileOutputStream(Constants.Dir.getKeyPath(SetPrivNetActivity.this));
                        byte[] bytes = new byte[1024];
                        int size = 0;
                        while ((size = input.read(bytes)) != -1) {
                            fileOutputStream.write(bytes, 0, size);
                        }
                        input.close();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selector.onActivityResult(requestCode, resultCode, data);
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
            case R.id.btn_add_key:
                selector.setMultiSelectionEnabled(true);
                selector.setSelectionMode(FileSelector.FILES_ONLY);
                selector.select(SetPrivNetActivity.this, 2);
                break;
            case R.id.btn_del_key:
                File file = new File(Constants.Dir.getKeyPath(SetPrivNetActivity.this));
                file.delete();
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

            File file = new File(Constants.Dir.getConfigPath(this));
            BufferedWriter write = new BufferedWriter(new FileWriter(file));
            write.write(JsonUtil.JsonFormat(object.toString()));
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
