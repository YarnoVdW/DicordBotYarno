package be.yarno.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.Random;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class GeneralCommandHelper {
    private boolean hasAlreadySend = false;

    /**
     * Method to handle the general commands
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
            case "/help" -> handleHelpCommand(event);
            case "/flieter" -> handleFlieterCommand(event);
            case "/pong" -> handlePongCommand(event);
        }
        this.imranSendAMessage(event);
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
        String[] array = {"flieterdieter", "TAALGEBRUIK!"};

        Random r = new Random();
        event.getChannel().sendMessage(array[r.nextInt(5)]).queue();
    }

}
