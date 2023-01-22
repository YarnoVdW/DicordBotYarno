package be.yarno.eventlistener;

import be.yarno.commands.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class BotYarnoEventListener extends ListenerAdapter {

    private final CommandHandler handler = new CommandHandler();

    /**
     * Method to handle the event when a message is sent
     * @param event the event that is triggered when a message is sent
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        handler.onMessageReceived(event);

    }


}
