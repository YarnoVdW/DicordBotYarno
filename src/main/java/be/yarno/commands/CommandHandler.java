package be.yarno.commands;

import be.yarno.utils.NewsApi;
import be.yarno.utils.WeatherApiClient;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class CommandHandler {
    private final WeatherApiClient weatherApiClient = new WeatherApiClient();
    private final NewsApi newsApi = new NewsApi();
    private boolean hasAlreadySend = false;

    /**
     * Method to handle all the commands
     *
     * @param event the event that is triggered when a message is sent
     */
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");
        switch (message[0]) {
            case "/sum" -> handleSumCommand(event, message);
            case "/ping" -> handlePingCommand(event);
            case "/hi" -> handleHiCommand(event);
            case "/shutdown" -> handleShutdownCommand(event);
            case "/weather" -> handleWeatherCommand(event, message);
            case "/help" -> handleHelpCommand(event);
            case "/news" -> handleNewsCommand(event);
            case "/flieter" -> handleFlieterCommand(event);
            case "/pong" -> handlePongCommand(event);
            case "/forecast" -> handleForecastCommand(event, message);
            default -> event.getChannel().sendMessage("Invalid command").queue();
        }
        this.imranSendAMessage(event);
    }

    /**
     * Method to handle the news command
     *
     * @param event the event that is triggered when a message is sent
     */
    private void handleNewsCommand(MessageReceivedEvent event) {
        String titles = null;
        try {
            titles = newsApi.getHeadlines().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        event.getChannel().sendMessage(titles).queue();
    }

    /**
     * Method to handle the help command
     *
     * @param event the event that is triggered when a message is sent
     */
    private void handleHelpCommand(MessageReceivedEvent event) {
        String help = """
                You can use the following commands:\s
                \t● /sum <list of number>
                \t● /ping
                \t● /pong
                \t● /hi
                \t● /weather <City>
                \t● /news
                \t● /flieter
                \t● /help""";
        event.getChannel().sendMessage(help).queue();
    }

    /**
     * @param event   the event that is triggered when a message is sent
     * @param message the message that is sent
     */
    private void handleWeatherCommand(MessageReceivedEvent event, String[] message) {
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
     *
     * @param jsonObject the json object that contains the weather information
     * @return the weather condition
     */
    private String getWeatherCondition(JSONObject jsonObject) {
        return jsonObject.getJSONArray("weather")
                .getJSONObject(0).getString("main");
    }

    /**
     *
     * @param jsonObject the json object that contains the weather information
     * @return the temperature
     */
    private int getTemperature(JSONObject jsonObject) {
        int temp = jsonObject.getJSONObject("main").getInt("temp");
        return (int) (temp - 273.15);
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
            int tempDay = (int) (temp.getDouble("day")-273.15);


            JSONArray weather = forecast.getJSONArray("weather");
            String condition = weather.getJSONObject(0).getString("main");
            String weatherEmoji = getWeatherEmoji(condition);


            forecastMessage.append(formattedDate).append(" - ")
                    .append(tempDay)
                    .append("°C, ")
                    .append(condition)
                    .append(" ").append(weatherEmoji).append("\n");
        }
        event.getChannel().sendMessage(forecastMessage.toString()).queue();
    }

    /**
     *
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

    /**
     * Method to handle the shutdown command
     *
     * @param event the event that is triggered when a message is sent
     */
    private void handleShutdownCommand(MessageReceivedEvent event) {
        if (!(event.getMessage().getAuthor().getName().equals("BoerYakke")
                || event.getMessage().getAuthor().getName().equals("wurrycorst"))) return;
        event.getChannel().sendMessage("I'm going to sleep now!").queue(message -> {
                    JDA jda = event.getJDA();
                    jda.shutdown();
                    System.exit(0);
                }
        );

    }

    private void handleHiCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Hello, " + event.getAuthor().getName() + "!").queue();
    }

    private void handlePingCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }

    private void handlePongCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Ping!").queue();
    }

    private void handleSumCommand(MessageReceivedEvent event, String[] message) {
        int result = 0;
        for (int i = 1; i < message.length; i++) {

            try {
                result += Integer.parseInt(message[i]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Pakt is een klender getal jom snul").queue();
                return;
            }

        }
        int finalResult = result;
        event.getAuthor().openPrivateChannel().queue((channel ->
                channel.sendMessage("The sum is " + finalResult).queue()));
    }

    private void imranSendAMessage(MessageReceivedEvent event) {
        if (event.getAuthor().getName().equals("Imran") && !hasAlreadySend) {
            hasAlreadySend = true;
            event.getChannel().sendMessage("OMG GUYS ITS OUR LORD AND SAVIOR IMRAN ALL HEIL IMRAN").queue();
        }
    }

    private void handleFlieterCommand(MessageReceivedEvent event) {
        String[] array = {"Mijn robot penisje is groter dan de uwe", "flieterdieter", "TAALGEBRUIK!", "wajom" +
                " ME has biggest pienies"};

        Random r = new Random();
        event.getChannel().sendMessage(array[r.nextInt(5)]).queue();
    }
}
