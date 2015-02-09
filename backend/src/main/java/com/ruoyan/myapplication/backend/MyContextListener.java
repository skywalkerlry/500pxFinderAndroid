package com.ruoyan.myapplication.backend;

import com.google.appengine.api.ThreadManager;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bean.Api500px;
import utils.ConnectionUtils;
import utils.JsonUtils;

/**
 * Created by ruoyan on 2/8/15.
 */
public class MyContextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    fetchDataAndSaveToDB();
                    try {
                        Thread.sleep(Api500px.getUpateInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

    }

    private void fetchDataAndSaveToDB() {
        String feed = ConnectionUtils.getFeed();
        Map<String, Object> feedMap = JsonUtils.getMappedData(feed);
        List<Map<String, Object>> photoList = null;
        if (feedMap != null) {
            if (feedMap.containsKey("photos")) {
                photoList = JsonUtils.getListedData(feedMap.get
                        ("photos").toString());
            }
        }
        if (photoList != null) {
            for (Map<String, Object> map : photoList) {

            }
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
