package com.jiuzhang.yeyuan.dribbbo.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.ArrayMap;
import android.widget.Toast;

import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;

import java.util.Map;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadHelper {

    private DownloadManager downloadManager;
    private Map<Long, String> mDownloads = new ArrayMap<>();

    public DownloadHelper (Context context) {
        this.downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    }

    private static DownloadHelper instance;

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
                .setDescription("Android photo download using DownloadManager")
                .setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory() + Wendo.DOWNLOAD_PATH, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .allowScanningByMediaScanner();
        downloadReference = downloadManager.enqueue(request);
        mDownloads.put(downloadReference, fileName);
        return downloadReference;
    }

    private void removeDownloadRequest (long downloadReference) {
        if (downloadManager == null) return;
        downloadManager.remove(downloadReference);
        mDownloads.remove(downloadReference);
    }

    private Cursor getDownloadCursor (long downloadReference) {
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

    private void checkDownloadStatus (Cursor cursor, Context context) {
        // column for download status
        int columnStatusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnStatusIndex);

        // column for reason code if download failed or paused
        int columnReasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReasonIndex);

        String statusMessage = "";
        String reasonMessage = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusMessage = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonMessage = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonMessage = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonMessage = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonMessage = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonMessage = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonMessage = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonMessage = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonMessage = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonMessage = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusMessage = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonMessage = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonMessage = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonMessage = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonMessage = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusMessage = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusMessage = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusMessage = "STATUS_SUCCESSFUL";
                break;
        }

        Toast.makeText(context,
                "Download status: " + "\n" + statusMessage + "\n" + reasonMessage, Toast.LENGTH_LONG)
                .show();
    }

    public String getFileName (long downloadReference) {
        return mDownloads.get(downloadReference);
    }

    public String getFilePath (long downloadReference) {
        return Environment.getExternalStorageDirectory() + Wendo.DOWNLOAD_PATH + getFileName(downloadReference);
    }
}
