package com.ruoyan.map500px;

import android.app.Application;
import android.content.Context;

/**
 * Created by ruoyan on 2/13/15.
 */
public class App extends Application{
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
  //      initImageLoader(getApplicationContext());
    }

    public static Context getContext() {
        return sContext;
    }

   /* // 初始化ImageLoader
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)).discCacheSize(10 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }*/
}
