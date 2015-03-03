package com.ruoyan.map500px.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.internal.LinkedTreeMap;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.api.Api500px;
import com.ruoyan.map500px.data.RequestManager;
import com.ruoyan.map500px.utils.JsonUtils;
import com.ruoyan.map500px.utils.TaskUtils;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends BaseActivity {
    public static final String IMAGE_ID = "image_id";
    private PhotoView photoView;
    private PhotoViewAttacher mAttacher;
    private ProgressBar mProgressBar;

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

        mProgressBar = (ProgressBar)findViewById(R.id.google_progress);
        mProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this).build());

        String imageId = getIntent().getStringExtra(IMAGE_ID);

        if (!imageId.equals(IMAGE_ID)) {
            requestFullImage(imageId);
        }
    }

    private void requestFullImage(String imageId) {
        String requestImg = Api500px.HOST_BASIC + imageId + "?image_size=" + "4"
                + "&consumer_key=" + Api500px
                .getConsumerKey();

        StringRequest stringRequest = new StringRequest(requestImg,
                infoListener(),
                errorListener());

        RequestManager.getInstance().addToRequestQueue(stringRequest);
    }

    private Response.Listener<String> infoListener() {
        return new Response.Listener<String>() {
            String imageUrl;
            @Override
            public void onResponse(final String response) {
                TaskUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... inputs) {
                        getImageUrl();
                        return null;
                    }

                    private void getImageUrl() {
                        Object imageInfo = JsonUtils.getMappedData(response).get("photo");
                        imageUrl = (String)((LinkedTreeMap)imageInfo).get("image_url");
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        showPhoto(imageUrl);
                    }
                });
            }
        };
    }

    private void showPhoto(String imageUrl) {
//        Log.i("imageUrl",imageUrl);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc(true)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(imageUrl, photoView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
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

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ToastUtils.showLong(error.getMessage());
            }
        };
    }
}
