package dao;

import java.sql.SQLException;
import java.util.List;

import bean.Api500px;
import utils.JdbcUtils;

/**
 * Created by ruoyan on 2/10/15.
 */
public class PhotoUpdater {
    private JdbcUtils utils;
    private int date;

    public PhotoUpdater(JdbcUtils utils, int date) {
        this.utils = utils;
        this.date = date;
    }

/*    public boolean updatePhotoDB(List<Object> params) {
        //deleteStalePhoto();
        boolean flag = false;
        String sql = "insert into photoInfo(latitude,longitude,url,date) values(?,?,?,?)";

        try {
            flag = utils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }*/

    public boolean updatePhotoDB(List<List<Object>> paramsList) {
        deleteStalePhoto();
        boolean flag = false;
        String sqlArray[] = new String[paramsList.size()];
        int counter = 0;
        for (List<Object> params:paramsList) {
            String sql = "insert into photoInfo(latitude,longitude,url,date) "+"values (";
            for (Object attribute:params) {
                if (attribute.getClass().toString().equals("class java.lang.String"))
                    sql += "'" + attribute + "',";
                else
                    sql += attribute.toString()+",";
            }
            sql = sql.substring(0,sql.length()-1);
            sql += ")";
            System.out.println(sql);
            sqlArray[counter] = sql;
            counter++;
        }
        try {
            flag = utils.updateByBatch(sqlArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean deleteStalePhoto() {
        boolean flag = false;
        String sql = "delete from photoInfo where date='"+date%Api500px.getPhotoHoldingDays()+"'";
        try {
            flag = utils.updateByPreparedStatement(sql,null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
