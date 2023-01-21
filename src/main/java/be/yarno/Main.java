package be.yarno;

import be.yarno.eventlistener.BotYarnoEventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Van de Weyer Yarno
 * ${DATE}
 */
public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String token = dotenv.get("DISCORD_TOKEN");
        BotYarnoEventListener botYarnoEventListener = new BotYarnoEventListener();

        JDA jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();
        try {
            jda.awaitReady();
            jda.addEventListener(botYarnoEventListener);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}