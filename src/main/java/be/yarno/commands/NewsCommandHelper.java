package be.yarno.commands;

import be.yarno.utils.NewsApi;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.ExecutionException;

/**
 * Van de Weyer Yarno
 * 24/01/2023
 */
public class NewsCommandHelper {

    private final NewsApi newsApi = new NewsApi();

    /**
     * Method to handle the general commands
     *
     * @param event the event that is triggered when a message is sent
     */
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");
        if ("/news".equals(message[0])) {
            handleNewsCommand(event);
        }
    }

    /**
     * Method to handle the news command
     *
     * @param event the event that is triggered when a message is sent
     */
    private void handleNewsCommand(MessageReceivedEvent event) {
        String titles;
        try {
            titles = newsApi.getHeadlines().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        event.getChannel().sendMessage(titles).queue();
    }
}
