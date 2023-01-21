package be.yarno.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class WeatherApiClient {
    private final String API_URL = "http://pro.openweathermap.org/data/2.5/weather?q=";
    public JSONObject getWeather(String location, String apiKey) throws IOException {
        URL url = new URL(API_URL + location + ",be&appid=" + apiKey);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return new JSONObject(response.toString());
        } else {
            return null;
        }
    }
}
