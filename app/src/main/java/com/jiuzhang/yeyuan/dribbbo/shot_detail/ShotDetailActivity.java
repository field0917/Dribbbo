package com.jiuzhang.yeyuan.dribbbo.shot_detail;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.reflect.TypeToken;
import com.jiuzhang.yeyuan.dribbbo.R;
import com.jiuzhang.yeyuan.dribbbo.base.BaseActivity;
import com.jiuzhang.yeyuan.dribbbo.base.CustomTarget;
import com.jiuzhang.yeyuan.dribbbo.base.WendoTask;
import com.jiuzhang.yeyuan.dribbbo.model.Shot;
import com.jiuzhang.yeyuan.dribbbo.shot_list.ShotListAdapter;
import com.jiuzhang.yeyuan.dribbbo.utils.ModelUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotDetailActivity extends BaseActivity {

    Shot shot;

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
//                CustomTarget target = new CustomTarget(shot, this.findViewById(android.R.id.content)) {
//                    @Override
//                    public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
//                        saveImage((Drawable) resource);
//                    }
//                };
//
//                Glide.with(this.getApplicationContext()) // Glide will stop the request when ShotDetailActivity stopped, so pass the application context
//                        .load(shot.getDownloadUrl())
//                        .into(target);
                try {
                    Bitmap imageBitmap = new DownloadImageTask().execute(shot.getDownloadUrl()).get();
                    saveImage(imageBitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return true;
            case R.id.info:
                Dialog dialog = new Dialog(this, R.style.Dialog);
                dialog.setContentView(R.layout.shot_info);
                dialog.setTitle("Info");

                TextView width = dialog.findViewById(R.id.image_width);
                TextView height = dialog.findViewById(R.id.image_height);
                TextView make = dialog.findViewById(R.id.make);
                TextView model = dialog.findViewById(R.id.model);
                TextView exposureTime = dialog.findViewById(R.id.exposure_time);
                TextView aperture = dialog.findViewById(R.id.aperture);
                TextView focalLength = dialog.findViewById(R.id.focal_length);
                TextView iso = dialog.findViewById(R.id.iso);

                width.setText("Width: " + shot.width);
                height.setText("Height: " + shot.height);
                if (shot.exif != null) {
                    make.setText("Make: " + shot.exif.make);
                    model.setText("Model: " + shot.exif.model);
                    exposureTime.setText("Exposure time: " + shot.exif.exposure_time);
                    aperture.setText("Aperture: " + shot.exif.aperture);
                    focalLength.setText("Focal length: " + shot.exif.focal_length);
                    iso.setText("ISO: " + shot.exif.iso);
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String saveImage (Bitmap resource) {

        boolean isWritable = isExternalStorageWritable();

        if (isWritable) {

            String savedImagePath = null;
            String imageFileName = "JPEG_" + shot.id + ".jpg";
            File storageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "Wendo");
            boolean success = true;

            // file not exist, create one
            if (!storageDir.exists()) {
                success = storageDir.mkdirs();
            }

            if (success) {
                File imageFile = new File(storageDir, imageFileName);
                savedImagePath = imageFile.getAbsolutePath();
                try {
                    OutputStream fOut = new FileOutputStream(imageFile);
//                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Add the image to the system gallery
                galleryAddPic(savedImagePath);
            }
            return savedImagePath;
        }
        return null;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private class DownloadImageTask extends WendoTask<String, Void, Bitmap> {

        @Override
        public Bitmap doOnNewThread(String... urls) throws Exception {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            return imageBitmap;
        }

        @Override
        public void onSuccess(Bitmap bitmap) {
            Snackbar.make(findViewById(android.R.id.content), "Saved!", Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onFailed(Exception e) {
            Snackbar.make(findViewById(android.R.id.content), "Download Failed!", Snackbar.LENGTH_LONG).show();
        }
    }
}
