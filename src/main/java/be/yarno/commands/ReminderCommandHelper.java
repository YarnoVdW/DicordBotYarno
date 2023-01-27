package be.yarno.commands;

import com.google.common.base.CharMatcher;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Van de Weyer Yarno
 * 27/01/2023
 */
public class ReminderCommandHelper {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("/")) return;
        String[] message = event.getMessage().getContentRaw().split(" ");

        if ("/remind".equals(message[0])) {
            handleReminderCommand(event);
        }

    }

    private void handleReminderCommand(MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = service.schedule(() -> event.getAuthor()
                .openPrivateChannel()
                .queue((channel -> channel.sendMessage(getReminder(message))
                        .queue())), getMinutes(message), getAppropriateDelayForm(message));


        if (scheduledFuture.isDone()) {
            service.shutdown();
        }

    }

    /**
     *
     * @param message the message from where the delay form has to be extracted
     * @return the TimeUnit form
     */
    private TimeUnit getAppropriateDelayForm(String message) {
        String[] parts = message.split(" ");
        String delayForm = parts[parts.length-1];
        return switch (delayForm) {
            case "uur" -> TimeUnit.HOURS;
            case "dagen", "dag" -> TimeUnit.DAYS;
            default -> TimeUnit.MINUTES;
        };

    }


    /**
     * @param message the message from where the reminder has to be extracted
     * @return the reminder
     */
    private String getReminder(String message) {
        String[] parts = message.split(" ");
        StringBuilder reminder = new StringBuilder();
        for (int i = 1; i < parts.length - 3; i++) {
            reminder.append(parts[i]).append(" ");
        }
        reminder.append("!");
        return reminder.toString();
    }

    /**
     * @param message the message from where the minutes have to be extracted
     * @return the delay in minutes
     */
    private int getMinutes(String message) {
        return Integer.parseInt(CharMatcher.inRange('0', '9').retainFrom(message));
    }
}
