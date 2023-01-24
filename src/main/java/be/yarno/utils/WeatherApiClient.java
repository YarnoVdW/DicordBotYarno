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
    /**
     * @param location the location of the weather
     * @param apiKey   the api key to use the api
     * @return the weather of the location
     * @throws IOException if the connection to the api fails
     */
    public JSONObject getCurrentWeather(String location, String apiKey) throws IOException {
        String API_URL = "http://pro.openweathermap.org/data/2.5/weather?q=";
        URL url = new URL(API_URL + location + ",be&appid=" + apiKey);
        return makeConnection(url);
    }

    /**
     * Method to retrieve the forecast of the location
     *
     * @param location the location of the weather
     * @param apiKey   the api key to use the api
     * @return the weather of the location
     */

    public JSONObject getWeatherForecast(String location, String apiKey) throws IOException {
        String API_URL = "https://api.openweathermap.org/data/2.5/forecast/daily?q=";
        URL url = new URL(API_URL + location + ",be&appid=" + apiKey + "&cnt=7");
        return makeConnection(url);
    }

    /**
     * Method to make the connection to the api
     *
     * @param url the url to connect to
     * @return the json object of the response
     */
    private JSONObject makeConnection(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println(responseCode);
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
