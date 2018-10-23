package com.jiuzhang.yeyuan.dribbbo.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadHelper {

    @IntDef({
            DownloadStatus.SUCCESS,
            DownloadStatus.FAILED,
            DownloadStatus.DOWNLOADING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadStatus {
        int SUCCESS = 1;
        int FAILED = -1;
        int DOWNLOADING = 0;
    }

    private DownloadManager downloadManager;
    private Map<Long, String> mDownloads = new ArrayMap<>();

    public DownloadHelper (Context context) {
        this.downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    }

    public static DownloadHelper instance;

    public static DownloadHelper getInstance (Context context) {
        if (instance == null) {
            synchronized (DownloadHelper.class) {
                instance = new DownloadHelper(context);
            }
        }
        return instance;
    }

    public long addDownloadRequest (String url, String fileName) {
        Uri uri = Uri.parse(url);
        long downloadReference;
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("Photo Download")
                .setDescription("Wendo photo download using DownloadManager")
                .setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory() + Wendo.DOWNLOAD_PATH, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .allowScanningByMediaScanner();
        downloadReference = downloadManager.enqueue(request);
        mDownloads.put(downloadReference, fileName);
        return downloadReference;
    }

    public void removeDownloadRequest (long downloadReference) {
        if (downloadManager == null) return;
        downloadManager.remove(downloadReference);
        mDownloads.remove(downloadReference);
    }

    public Cursor getDownloadCursor (long downloadReference) {
        DownloadManager.Query downloadQuery = new DownloadManager.Query();
        downloadQuery.setFilterById(downloadReference);
        Cursor cursor = downloadManager.query(downloadQuery);

        if (cursor == null) {
            return null;
        } else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            return cursor;
        } else {
            cursor.close();
            return null;
        }
    }

    public int checkDownloadStatus (@NonNull Cursor cursor) {
        // column for download status
        int columnStatusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnStatusIndex);

        switch (status) {
            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_PAUSED:
                return DownloadStatus.FAILED;
            case DownloadManager.STATUS_SUCCESSFUL:
                return DownloadStatus.SUCCESS;
            default:
                return DownloadStatus.DOWNLOADING;
        }
    }

    public String getFileName (long downloadReference) {
        return mDownloads.get(downloadReference);
    }

    public String getFilePath (long downloadReference) {
        return Environment.getExternalStorageDirectory() + Wendo.DOWNLOAD_PATH + getFileName(downloadReference);
    }
}
