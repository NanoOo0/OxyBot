import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;

public class App{
    MongoClient client = new MongoClient(new MongoClientURI(Config.DB_URI));
    MongoDatabase database = client.getDatabase("data");
    MongoCollection<Document> collection = database.getCollection("tickets");


    public static void main(String[] args) throws Exception {
        Connection connection = null;
        connection = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);

        JDA jda = JDABuilder.createDefault(Config.BOT_TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListener())
                .addEventListeners(new Ticket())
                .setActivity(Activity.playing("попе пальчиком"))
                .build();
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Calculate ping of the bot"),
                Commands.slash("hello", "Hello player"),
                Commands.slash("top", "top"),
                Commands.slash("createmsg", "Создание сообщения"),
                Commands.slash("statsmsg", "Создание сообщения со статистикой"),
                Commands.slash("updatestats", "Обновление статистики"),
                Commands.slash("ticket_ban", "Блокировка юзера")
                        .addOption(OptionType.STRING, "user_name", "ник пользователя", true)
                        .addOption(OptionType.STRING, "reason", "причина", true),
                Commands.slash("test", "test"),
                Commands.slash("testmenu", "test")

                ).queue();
    }
}
