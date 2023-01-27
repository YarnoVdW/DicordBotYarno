package be.yarno.eventlistener;

import be.yarno.commands.GeneralCommandHelper;
import be.yarno.commands.NewsCommandHelper;
import be.yarno.commands.ReminderCommandHelper;
import be.yarno.commands.WeatherCommandHelper;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class BotYarnoEventListener extends ListenerAdapter {

    private final GeneralCommandHelper generalCommandHelper = new GeneralCommandHelper();
    private final WeatherCommandHelper weatherCommandHelper = new WeatherCommandHelper();
    private final NewsCommandHelper newsCommandHelper = new NewsCommandHelper();
    private final ReminderCommandHelper reminderCommandHelper = new ReminderCommandHelper();

    /**
     * Method to handle the event when a message is sent
     *
     * @param event the event that is triggered when a message is sent
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        generalCommandHelper.onMessageReceived(event);
        weatherCommandHelper.onMessageReceived(event);
        newsCommandHelper.onMessageReceived(event);
        reminderCommandHelper.onMessageReceived(event);

    }



}
