package com.ruoyan.myapplication.backend;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.internal.LinkedTreeMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bean.Api500px;
import dao.PhotoUpdater;
import utils.ConnectionUtils;
import utils.JdbcUtils;
import utils.JsonUtils;

/**
 * Created by ruoyan on 2/8/15.
 */
public class MyContextListener implements ServletContextListener{

    private String dbUrl;
    private int dateCounter;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        dateCounter = 0;

        try {
            initializeDBConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        dateCounter++;
                        fetchDataAndSaveToDB();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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

    private void initializeDBConnection() throws ClassNotFoundException {
        dbUrl = null;
        if (SystemProperty.environment.value() ==
                SystemProperty.Environment.Value.Production) {
            Class.forName("com.mysql.jdbc.GoogleDriver");
            dbUrl =
                    "jdbc:google:mysql://northern-carver-846:my500pxstorage/Data500px?user=root";
        } else {
            // Connecting from an external network.
            Class.forName("com.mysql.jdbc.Driver");
            dbUrl = "jdbc:mysql://173.194.235.129:3306/Data500px?user=root&password=lryeagle";
        }

    }

    private void fetchDataAndSaveToDB() throws SQLException {
        Connection conn = DriverManager.getConnection(dbUrl);
        JdbcUtils jdbcUtils = new JdbcUtils(conn);
        PhotoUpdater updater = new PhotoUpdater(jdbcUtils, dateCounter);
        List<List<Object>> paramList = new ArrayList<List<Object>>();
        String feed = ConnectionUtils.getFeed();

        List<Object> photoList = parseJsonToPhotoList(feed);

        if (photoList != null) {
            for (int i=0; i<photoList.size(); i++) {
                List<Object> params = getListParams(photoList, i);
                if (params != null) {
                    paramList.add(params);
                }
            }
            if (paramList.size() > 0)
                updater.updatePhotoDB(paramList);
            jdbcUtils.releaseConn();
        }

    }

    private List<Object> getListParams(List<Object> list, int i) {
        List<Object> paramList = new ArrayList<Object>();
        if (((LinkedTreeMap)list.get(i)).get("latitude")==null)
            return null;
        double latitude = (double)((LinkedTreeMap)list.get(i)).get("latitude");
        double longitude = (double)((LinkedTreeMap)list.get(i)).get("longitude");
        String imageUrl = (String)((LinkedTreeMap)list.get(i)).get("image_url");
        int date = dateCounter % Api500px.getPhotoHoldingDays();

        paramList.add(latitude);
        paramList.add(longitude);
        paramList.add(imageUrl);
        paramList.add(date);
        return paramList;
    }

    private List<Object> parseJsonToPhotoList(String feed) {
        Map<String, Object> feedMap = JsonUtils.getMappedData(feed);
        List<Object> photoList = null;
        if (feedMap != null) {
            if (feedMap.containsKey("photos")) {
                photoList = (ArrayList)feedMap.get
                        ("photos");
            }
        }
        return photoList;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
