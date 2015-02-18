package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by ruoyan on 2/8/15.
 */
public class JsonUtils {
    public static Map<String, Object> getMappedData(
            String jsonString) {
        Map<String, Object> result = null;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(jsonString,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Map<String, Object>> getListedData(String jsonString) {
        List<Map<String, Object>> result = null;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
