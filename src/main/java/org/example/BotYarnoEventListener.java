package org.example;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.Buffer;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class BotYarnoEventListener extends ListenerAdapter {

    private boolean running = true;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");
        if (message[0].equals("/sum")) this.sendMessageWithSum(event, message);
        if (message[0].equals("/ping")) this.pongMessage(event);
        if (message[0].equals("/hi")) this.friendlyMessage(event);
        if (message[0].equals("/shutdown")) this.shutdownMessage(event);
        if (message[0].equals("/weather")) this.getWeather(event);
    }

    private void pongMessage(MessageReceivedEvent event) {

        event.getChannel().sendMessage("Pong!").queue();

    }

    private void sendMessageWithSum(MessageReceivedEvent event, String[] message) {

        int result = 0;
        for (int i = 1; i < message.length; i++) {
            result += Integer.parseInt(message[i]);
        }
        int finalResult = result;
        event.getAuthor().openPrivateChannel().queue((channel ->
                channel.sendMessage("The sum is " + finalResult).queue()));

    }

    private void friendlyMessage(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Hello, " + event.getAuthor().getName() + "!").queue();
    }

    private void shutdownMessage(MessageReceivedEvent event) {
        event.getChannel().sendMessage("shutting down").queue();
        this.setRunning(false);
    }


    private void getWeather(MessageReceivedEvent event) {
        String location = event.getMessage().getContentRaw().replace("/weather", "").trim();
        String apiKey = "979799172c2fdf7913c115246c4221f1";
        String url = "http://pro.openweathermap.org/data/2.5/weather?q=" + location + ",be" + "&appid=" + apiKey;
        System.out.println(url);
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int response = con.getResponseCode();
            System.out.println(response);
            if (response == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String input;
                StringBuilder responseBuffer = new StringBuilder();
                while ((input = in.readLine()) != null) {
                    responseBuffer.append(input);
                }
                in.close();
                JSONObject jsonObject = new JSONObject(responseBuffer.toString());
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                int temp = jsonObject.getJSONObject("main").getInt("temp");
                int celcius = (int) (temp - 273.15);
                String weatherEmoji = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                String emoji = switch (weatherEmoji) {
                    case "Clouds" -> "\u2601";
                    case "Clear" -> "\u2600";
                    case "Snow" -> "\u2744";
                    case "Rain" -> "\u2614";
                    default -> "";
                };


                event.getChannel().sendMessage("The weather in " + location + " is " + weather + " " + emoji + " with " +
                        "a temperature of " + celcius + "°C").queue();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


}
