package logicalguess.util;

import okhttp3.*;
import org.jfree.chart.ui.UIUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static JSONObject get(OkHttpClient client, String url, Map<String, String> params) throws IOException {
        LOGGER.info("URL " + url);
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .build();
        Response response = client.newCall(request).execute();
        LOGGER.info(response.toString());
        return new JSONObject(response.body().string());
    }

    public static JSONObject post(OkHttpClient client, String url, JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        LOGGER.info(response.toString());
        return new JSONObject(response.body().string());
    }

    public static JSONObject post(OkHttpClient client, String url, Map<String, String> params, JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json.toString());

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        LOGGER.info(response.toString());
        return new JSONObject(response.body().string());
    }

    public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        return new JSONObject(content);
    }

    public static JSONObject getCounts(JSONObject result) {
        return result.getJSONArray("qasms").getJSONObject(0).getJSONObject("result")
                .getJSONObject("data").getJSONObject("counts");
    }

    public static void plotCounts(String title, JSONObject counts, StatsBarChart.Type type) {
        StatsBarChart demo = new StatsBarChart(title, counts, type);
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
