package be.yarno.commands;

import be.yarno.utils.NewsApi;
import be.yarno.utils.WeatherApiClient;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class CommandHandler {
    private final WeatherApiClient weatherApiClient = new WeatherApiClient();
    private final NewsApi newsApi = new NewsApi();
    private boolean hasAlreadySend = false;

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");
        switch (message[0]) {
            case "/sum":
                handleSumCommand(event, message);
                break;
            case "/ping":
                handlePingCommand(event);
                break;
            case "/hi":
                handleHiCommand(event);
                break;
            case "/shutdown":
                handleShutdownCommand(event);
                break;
            case "/weather":
                handleWeatherCommand(event, message);
                break;
            case "/help":
                helpHandlerCommand(event);
                break;
            case "/news":
                newsHandlerCommand(event);
                break;
            default:
                event.getChannel().sendMessage("Invalid command").queue();
                break;
        }
        this.imranSendAMessage(event);
    }

    private void newsHandlerCommand(MessageReceivedEvent event) {
        String titles = null;
        try {
            titles = newsApi.getHeadlines().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        event.getChannel().sendMessage(titles).queue();
    }

    private void helpHandlerCommand(MessageReceivedEvent event) {
        String help = """
                You can use the following commands:\s
                \t● /sum <list of number>
                \t● /ping
                \t● /hi
                \t● /weather <City>
                \t● /help""";
        event.getChannel().sendMessage(help).queue();
    }

    private void handleWeatherCommand(MessageReceivedEvent event, String[] message) {
        String location = event.getMessage().getContentRaw().replace("/weather", "").trim();
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        final String apiKey = dotenv.get("WEATHER_TOKEN");

        try {
            JSONObject jsonObject = weatherApiClient.getWeather(location, apiKey);
            if (jsonObject == null) {
                event.getChannel().sendMessage("The given city was not found!").queue();
                return;
            }
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
        if (!event.getMessage().getAuthor().getName().equals("BoerYakke")) return;
        event.getChannel().sendMessage("Shutting down...").queue();
        JDA jda = event.getJDA();
        jda.shutdown();
        System.exit(0);
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

    private void imranSendAMessage(MessageReceivedEvent event) {
        if (event.getAuthor().getName().equals("Imran") && !hasAlreadySend) {
            hasAlreadySend = true;
            event.getChannel().sendMessage("OMG GUYS ITS OUR LORD AND SAVIOR IMRAN ALL HEIL IMRAN").queue();
        }
    }
}
