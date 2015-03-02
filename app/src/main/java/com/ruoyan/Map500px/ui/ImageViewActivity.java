package com.ruoyan.map500px.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.ruoyan.map500px.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends BaseActivity {
    public static final String IMAGE_URL = "image_url";
//    public static final String IMAGE_ORDER = "image_order";
    private PhotoView photoView;
    private PhotoViewAttacher mAttacher;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        getActionBar().hide();

        photoView = (PhotoView)findViewById(R.id.photoView);
        mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });

        String imageUrl = getIntent().getStringExtra(IMAGE_URL);
//        int imageOrder = getIntent().getIntExtra(IMAGE_ORDER,-1);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc(true)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(imageUrl, photoView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mAttacher.update();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideStatusBar();
    }

    public void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAttacher != null) {
            mAttacher.cleanup();
        }
    }
}
