package com.ipfstest.z11.ipfs1.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

public class DownloadFiles {
    private long mTaskId;
    private Context mContext;
    private DownloadManager downloadManager;

    public DownloadFiles(Context mContext) {
        this.mContext = mContext;
    }

    public void download(String downloadUrl, String path, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(path, fileName);
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mTaskId = downloadManager.enqueue(request);
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(mContext, "下载暂停!", Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(mContext, "下载延迟!", Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(mContext, "正在下载!", Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(mContext, "下载完成!", Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "下载失败!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
