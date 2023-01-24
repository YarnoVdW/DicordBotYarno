package be.yarno.commands;

import be.yarno.utils.WeatherApiClient;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * Van de Weyer Yarno
 * 24/01/2023
 */
public class WeatherCommandHelper {
    private final WeatherApiClient weatherApiClient = new WeatherApiClient();

    /**
     * @param event The event that is triggered when a message is sent
     */
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");
        switch (message[0]) {
            case "/weather" -> handleWeatherCommand(event);
            case "/forecast" -> handleForecastCommand(event, message);
        }

    }

    /**
     * @param event   the event that is triggered when a message is sent
     */
    private void handleWeatherCommand(MessageReceivedEvent event) {
        String location = event.getMessage().getContentRaw().replace("/weather", "").trim();
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        final String apiKey = dotenv.get("WEATHER_TOKEN");

        try {
            JSONObject jsonObject = weatherApiClient.getCurrentWeather(location, apiKey);
            if (jsonObject == null) {
                event.getChannel().sendMessage("The given city was not found!").queue();
                return;
            }
            String weather = getWeatherCondition(jsonObject);
            int temp = getTemperature(jsonObject);
            String weatherEmoji = getWeatherEmoji(getWeatherCondition(jsonObject));
            event.getChannel().sendMessage("The weather in " + location + " is " + weather + " "
                    + weatherEmoji + " with a temperature of " + temp + " \u2103").queue();
        } catch (IOException e) {
            event.getChannel().sendMessage("Error getting weather information for location " + location).queue();
        }
    }


    /**
     * Method to retrieve the forecast of a given location
     *
     * @param event The event that is triggered
     * @param args  The arguments of the message
     */
    private void handleForecastCommand(MessageReceivedEvent event, String[] args) {
        String location = event.getMessage().getContentRaw().replace("/forecast", "").trim();
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        final String apiKey = dotenv.get("WEATHER_TOKEN");

        if (args.length != 2) {
            event.getChannel().sendMessage("Invalid number of arguments. Use /forecast [city]").queue();
            return;
        }
        JSONObject jsonObject;
        try {
            jsonObject = weatherApiClient.getWeatherForecast(location, apiKey);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject city = jsonObject.getJSONObject("city");
        String locationName = city.getString("name");
        JSONArray forecastList = jsonObject.getJSONArray("list");
        StringBuilder forecastMessage = new StringBuilder();
        forecastMessage.append("Forecast for ").append(locationName).append(":\n\n");
        for (int i = 0; i < forecastList.length(); i++) {
            JSONObject forecast = forecastList.getJSONObject(i);
            // get the timestamp for the forecast
            long timestamp = forecast.getLong("dt");
            // convert the timestamp to a date
            Date date = new Date(timestamp * 1000);
            // format date to 23 Jan
            String formattedDate = String.format("%td %tb", date, date);

            // get the temperature and weather condition
            JSONObject temp = forecast.getJSONObject("temp");
            int tempDay = (int) (temp.getDouble("day") - 273.15);


            JSONArray weather = forecast.getJSONArray("weather");
            String condition = weather.getJSONObject(0).getString("main");
            String weatherEmoji = getWeatherEmoji(condition);


            forecastMessage.append(formattedDate).append(" It will be ")
                    .append(tempDay)
                    .append("°C, with a chance of ")
                    .append(condition)
                    .append(" ").append(weatherEmoji).append("\n");
        }
        event.getChannel().sendMessage(forecastMessage.toString()).queue();
    }

    /**
     * @param jsonObject the json object that contains the weather information
     * @return the weather condition
     */
    private String getWeatherCondition(JSONObject jsonObject) {
        return jsonObject.getJSONArray("weather")
                .getJSONObject(0).getString("main");
    }

    /**
     * @param jsonObject the json object that contains the weather information
     * @return the temperature
     */
    private int getTemperature(JSONObject jsonObject) {
        int temp = jsonObject.getJSONObject("main").getInt("temp");
        return (int) (temp - 273.15);
    }

    /**
     * @param condition the condition of the weather
     * @return the emoji unicode of the weather
     */
    private String getWeatherEmoji(String condition) {
        return switch (condition) {
            case "Clouds" -> "\u2601";
            case "Clear" -> "\u2600";
            case "Snow" -> "\u2744";
            case "Rain" -> "\u2614";
            case "Thunderstorm" -> "\u26C8";
            case "Drizzle" -> "\uD83D\uDCA7";
            case "Mist" -> "\uD83C\uDF2B";
            default -> "";
        };
    }
}
