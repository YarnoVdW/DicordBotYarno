package be.yarno.commands;

import be.yarno.utils.WeatherApiClient;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class CommandHandler {
    private final WeatherApiClient weatherApiClient = new WeatherApiClient();
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");
        switch (message[0]) {
            case "/sum" -> handleSumCommand(event, message);
            case "/ping" -> handlePingCommand(event);
            case "/hi" -> handleHiCommand(event);
            case "/shutdown" -> handleShutdownCommand(event);
            case "/weather" -> handleWeatherCommand(event, message);
            default -> event.getChannel().sendMessage("Invalid command").queue();
        }
    }

    private void handleWeatherCommand(MessageReceivedEvent event, String[] message) {
        String location = event.getMessage().getContentRaw().replace("/weather", "").trim();
        String apiKey = "979799172c2fdf7913c115246c4221f1";
        try {
            JSONObject jsonObject = weatherApiClient.getWeather(location, apiKey);
            String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            int temp = jsonObject.getJSONObject("main").getInt("temp");
            int celsius = (int) (temp - 273.15);
            String weatherEmoji = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            String emoji = switch (weatherEmoji) {
                case "Clouds" -> "\u2601";
                case "Clear" -> "\u2600";
                case "Snow" -> "\u2744";
                case "Rain" -> "\u2614";
                case "Thunderstorm" -> "\u26C8";
                default -> "";
            };
            event.getChannel().sendMessage("The weather in " + location + " is " + weather + " " + emoji + " with a temperature of " + celsius + " \u2103").queue();
        } catch (IOException e) {
            event.getChannel().sendMessage("Error getting weather information for location " + location).queue();
        }
    }


    private void handleShutdownCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Shutting down...").queue();
        JDA jda = event.getJDA();
        jda.shutdown();
    }

    private void handleHiCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Hello, " + event.getAuthor().getName() + "!").queue();
    }

    private void handlePingCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }

    private void handleSumCommand(MessageReceivedEvent event, String[] message) {
        int result = 0;
        for (int i = 1; i < message.length; i++) {
            result += Integer.parseInt(message[i]);
        }
        int finalResult = result;
        event.getAuthor().openPrivateChannel().queue((channel ->
                channel.sendMessage("The sum is " + finalResult).queue()));
    }
}