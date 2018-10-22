package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Environment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListAdapter;
import com.jiuzhang.yeyuan.dribbbo.utils.DateUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.DownloadHelper;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;
import com.jiuzhang.yeyuan.dribbbo.utils.Utils;
import com.jiuzhang.yeyuan.dribbbo.wendo.Wendo;

import java.util.Map;


public class ShotDetailActivity extends BaseActivity {

    Shot shot;
    long downloadReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        shot = ModelUtils.toObject(intent.getStringExtra(ShotDetailFragment.KEY_SHOT),
                                                         new TypeToken<Shot>(){});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shot_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_to_album:
                if (Utils.isStoragePermissionGranted(this) && shot != null) {
                    Toast.makeText(this, getString(R.string.download_started), Toast.LENGTH_LONG).show();
                    downloadImage(shot.getDownloadUrl());
                }
                return true;
            case R.id.info:
                Dialog dialog = new Dialog(this, R.style.Dialog);
                dialog.setContentView(R.layout.shot_info);
                dialog.setTitle("Info");

                TextView createAt = dialog.findViewById(R.id.create_at);
                TextView dimension = dialog.findViewById(R.id.image_dimension);
                TextView make = dialog.findViewById(R.id.make);
                TextView model = dialog.findViewById(R.id.model);
                TextView exposureTime = dialog.findViewById(R.id.exposure_time);
                TextView aperture = dialog.findViewById(R.id.aperture);
                TextView focalLength = dialog.findViewById(R.id.focal_length);
                TextView iso = dialog.findViewById(R.id.iso);

                String dateString = shot.created_at == null ? "---" :
                        "Create At: " + DateUtils.formatDateString(shot.created_at);
                createAt.setText(dateString);

                String dimensionText = shot.width == 0 && shot.height == 0 ? "---"
                                   : "Dimension: " + shot.width + " x " + shot.height;
                dimension.setText(dimensionText);

                if (shot.exif != null) {
                    String makeText = shot.exif.make == null ? "---" : "Make: " + shot.exif.make;
                    make.setText(makeText);

                    String modelText = shot.exif.model == null ? "---" : "Model: " + shot.exif.model;
                    model.setText(modelText);

                    String exposureTimeText = shot.exif.exposure_time == null ? "---"
                                              : "Exposure time: " + shot.exif.exposure_time;
                    exposureTime.setText(exposureTimeText);

                    String apertureText = shot.exif.aperture == null ? "---"
                                          : "Aperture: " + shot.exif.aperture;
                    aperture.setText(apertureText);

                    String focalLengthText = shot.exif.focal_length == null ? "---"
                                             : "Focal length: " + shot.exif.focal_length;
                    focalLength.setText(focalLengthText);

                    String isoText = shot.exif.iso == 0 ? "---" : "ISO: " + shot.exif.iso;
                    iso.setText(isoText);
                    dialog.show();
                }
                return true;
            case R.id.url:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shot.getHtmlUrl()));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void downloadImage (String url) {
        String fileName = shot.id + Wendo.DOWNLOAD_PHOTO_FORMAT;
        DownloadHelper helper = DownloadHelper.getInstance(this);
        downloadReference = helper.addDownloadRequest(url, fileName);
    }

    @Override
    protected Fragment newFragment() {
        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        ShotDetailFragment fragment = ShotDetailFragment.newInstance(extra);

        return fragment;
    }

    @Override
    protected String getActivityTitle() {

        return super.getActivityTitle();
    }



//    private String saveImage (Bitmap resource) {
//
//        boolean isWritable = isExternalStorageWritable();
//
//        if (isWritable) {
//            String savedImagePath = null;
//            String imageFileName = "JPEG_" + shot.id + ".jpg";
//            File storageDir = new File(Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "Wendo");
//            boolean success = false;
//
//            // file not exist, create one
//            if (!storageDir.exists()) {
//                success = storageDir.mkdirs();
//
//            }
//
//            if (success) {
//                File imageFile = new File(storageDir, imageFileName);
//                savedImagePath = imageFile.getAbsolutePath();
//                try {
//                    OutputStream fOut = new FileOutputStream(imageFile);
////                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
//                    resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//                    fOut.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                // Add the image to the system gallery
//                galleryAddPic(savedImagePath);
//            }
//            return savedImagePath;
//        }
//        return null;
//    }
}
