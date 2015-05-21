package com.ruoyan.map500px.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.ruoyan.map500px.bean.PhotoInfo;
import com.ruoyan.map500px.data.LocalDataSource;
import com.ruoyan.map500px.data.RequestManager;
import com.ruoyan.map500px.utils.JsonUtils;
import com.ruoyan.map500px.utils.TaskUtils;

import at.markushi.ui.CircleButton;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageViewActivity extends BaseActivity {
    public static final String IMAGE_ID = "image_id";
    public static final String THUMBNAIL_URL = "thumbnail_url";
    private PhotoView photoView;
    private PhotoViewAttacher mAttacher;
    private ProgressBar mProgressBar;
    private CircleButton mButton;
    private PhotoInfo photoInfo;
    private String photoId;
    private String thumbnailUrl;
    private LocalDataSource dataSource;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        getActionBar().hide();

        if (!ImageLoader.getInstance().isInited())
            initImageLoader(getApplicationContext());

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

        mButton = (CircleButton)findViewById(R.id.image_info_button);
        dataSource = new LocalDataSource(ImageViewActivity.this);
        photoId = getIntent().getStringExtra(IMAGE_ID);
        thumbnailUrl = getIntent().getStringExtra(THUMBNAIL_URL);

        if (!photoId.equals(IMAGE_ID)) {
            if (dataSource.hasPhoto(photoId)) {
                photoInfo = dataSource.getPhoto(photoId);
                if (photoInfo != null) {
                    showPhoto(photoInfo.getFullSizeUrl());
                }

            }
            else
                requestFullImage();
        }
    }

    private void requestFullImage() {
        String requestImg = Api500px.HOST_BASIC + photoId + "?image_size=" + "4"
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
                        String author, camera="", lens="", focal="", iso="", shutter="", aperture="";
                        Object imageInfo = JsonUtils.getMappedData(response).get("photo");
                        imageUrl = (String)((LinkedTreeMap)imageInfo).get("image_url");
                        LinkedTreeMap userInfo = (LinkedTreeMap)((LinkedTreeMap)imageInfo).get
                                ("user");
                        author = userInfo.get("fullname").toString();

                        Object cameraObj = ((LinkedTreeMap)imageInfo).get("camera");
                        if (cameraObj != null) {
                            camera = cameraObj.toString();
                        }

                        Object lensObj = ((LinkedTreeMap)imageInfo).get("lens");
                        if (lensObj != null) {
                            lens = lensObj.toString();
                        }

                        Object focalObj = ((LinkedTreeMap)imageInfo).get("focal_length");
                        if (focalObj != null) {
                            focal = focalObj.toString();
                            if (!focal.equals(""))
                                focal += "mm";
                        }

                        Object isoObj = ((LinkedTreeMap)imageInfo).get("iso");
                        if (isoObj != null) {
                            iso = isoObj.toString();
                        }

                        Object shutterObj = ((LinkedTreeMap)imageInfo).get("shutter_speed");
                        if (shutterObj != null) {
                            shutter = shutterObj.toString();
                            if (!shutter.equals(""))
                                shutter += " s";
                        }

                        Object apertureObj = ((LinkedTreeMap)imageInfo).get("aperture");
                        if (apertureObj != null) {
                            aperture = apertureObj.toString();
                            if (!aperture.equals(""))
                                aperture = "f/"+aperture;
                        }

                        double latitude = (double)((LinkedTreeMap)imageInfo).get("latitude");

                        double longitude = (double)((LinkedTreeMap)imageInfo).get("longitude");

                        photoInfo = new PhotoInfo(author,camera,lens,focal,iso,shutter,aperture,
                                photoId,imageUrl,thumbnailUrl,latitude,longitude);

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
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc(true)
                .considerExifParams(true).build();
        ImageLoader.getInstance().displayImage(imageUrl, photoView, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
                mAttacher.update();
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImageInfoDialog();
                    }
                });
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
            }
        });
    }

    private void showImageInfoDialog() {
        final Dialog dialog = new Dialog(ImageViewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = View.inflate(ImageViewActivity.this, R.layout.fragment_dialog,
                null);
        dialog.setContentView(dialogView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView author = (TextView)dialogView.findViewById(R.id.author_name);
        TextView camera = (TextView)dialogView.findViewById(R.id.camera);
        TextView lens = (TextView)dialogView.findViewById(R.id.lens);
        TextView focal = (TextView)dialogView.findViewById(R.id.focal_length);
        TextView iso = (TextView)dialogView.findViewById(R.id.iso);
        TextView shutter = (TextView)dialogView.findViewById(R.id.shutter_speed);
        TextView aperture = (TextView)dialogView.findViewById(R.id.aperture);

        author.setText("author: "+photoInfo.getAuthor());
        camera.setText("camera: "+photoInfo.getCamera());
        lens.setText("lens: "+photoInfo.getLens());
        focal.setText("focal: "+photoInfo.getFocal());
        iso.setText("iso: "+photoInfo.getIso());
        shutter.setText("shutter: "+photoInfo.getShutter());
        aperture.setText("aperture: "+photoInfo.getAperture());

        Button favorButton= (Button)dialogView.findViewById(R.id.favorite_button);
        final boolean inFavorite = isPhotoInFavorites();
        if (inFavorite) {
            favorButton.setText("Remove from favorite");
            favorButton.setTextColor(getResources().getColor(R.color.lime));
        }

        else {
            favorButton.setText("Add to favorite");
            favorButton.setTextColor(getResources().getColor(R.color.pink));
        }
        favorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFavorites(inFavorite);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color
                .TRANSPARENT));
        dialog.show();
    }

    private void updateFavorites(boolean inFavorite) {
        if (inFavorite) {
            dataSource.removePhoto(photoId);
            Style style = new Style.Builder().setBackgroundColor(R.color.trans_lime)
                    .build();
            Crouton.makeText(ImageViewActivity.this,R.string.remove_from_favor,style).show();
        }
        else {
            dataSource.updateFavorites(photoInfo);
            Style style = new Style.Builder().setBackgroundColor(R.color.trans_pink).build();
            Crouton.makeText(ImageViewActivity.this,R.string.add_to_favor,style).show();
        }
    }

    private boolean isPhotoInFavorites() {
        return dataSource.hasPhoto(photoId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.rgb(0, 0, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
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
