package de.eintosti.labyinstagram.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author einTosti
 */
public class FollowerUtil {
    private JsonParser jsonParser;

    public FollowerUtil() {
        jsonParser = new JsonParser();
    }

    public int getFollowers(String username) throws FollowerUtilException {
        HttpURLConnection connection;

        try {
            URL url = new URL("https://www.instagram.com/" + username);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (Exception e) {
            throw new FollowerUtilException("No connection");
        }
        String response = getResponse(connection);
        if (response.isEmpty()) throw new FollowerUtilException("Empty response");
        if (!response.contains("edge_followed_by")) throw new FollowerUtilException("Invalid response");

        int beginDataBlock = response.indexOf("edge_followed_by");
        int beginJson = response.indexOf('{', beginDataBlock);
        int endJson = response.indexOf('}', beginDataBlock) + 1;

        String jsonString = response.substring(beginJson, endJson);
        JsonElement jsonElement = jsonParser.parse(jsonString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return jsonObject.getAsJsonPrimitive("count").getAsInt();
    }

    private String getResponse(HttpURLConnection connection) throws FollowerUtilException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
        } catch (Exception e) {
            throw new FollowerUtilException("No user found");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}