package Main;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main{
    public static String prefix = ".";
    private static JDA jda;

    public static void main(String[] args){
        JDABuilder jdaBuilder = JDABuilder.createDefault("NjEzNTgxMjk0MjY1MTcxOTc5.XVzAHA.DtXfwitjD7oI2osHGueXc57yB88");
        EventWaiter waiter = new EventWaiter();
        jdaBuilder.enableCache(CacheFlag.VOICE_STATE);
        jdaBuilder.setActivity(Activity.watching(".help"));
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.addEventListeners(waiter);
        jdaBuilder.addEventListeners(new GuildEvents());
        jdaBuilder.addEventListeners(new MessageListener(waiter));
        try {
            jda = jdaBuilder.build().awaitReady();
            jda.upsertCommand("music", "Run the music command list")
                    .addSubcommands(new SubcommandData("add", "Add track to queue")
                            .addOption(OptionType.STRING, "youtube", "Enter link or query"))
                    .addSubcommands(new SubcommandData("pause", "Toggle pause"))
                    .addSubcommands(new SubcommandData("skip", "Skip current track"))
                    .addSubcommands(new SubcommandData("loop", "Loop current track"))
                    .addSubcommands(new SubcommandData("queue", "List tracks on queue"))
                    .addSubcommands(new SubcommandData("np", "Display 'Now Playing' track"))
                    .addSubcommands(new SubcommandData("volume", "Display current volume")
                            .addOptions(new OptionData(OptionType.INTEGER, "integer", "Enter volume level 1-100")/*.setRequiredRange(1,100)*/))
                    .addSubcommands(new SubcommandData("remove", "Remove track at position x")
                            .addOptions(new OptionData(OptionType.INTEGER, "integer", "Enter position 1-20").setRequiredRange(0,20)))
                    .addSubcommands(new SubcommandData("clear", "Clear the queue"))
                    .addSubcommands(new SubcommandData("disconnect", "Disconnect the bot from voice"))
                    .queue();
        }
        catch (LoginException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
