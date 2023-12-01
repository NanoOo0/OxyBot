
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MessageListener extends ListenerAdapter {

    private static final Path path = Paths.get("H:\\Desktop\\nano-bot\\nano-bot\\save\\file.txt");

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Category category = event.getGuild().getCategoryById("1051165453919199293");

        if(event.getMessage().getChannel().asTextChannel().getParentCategory().equals(category)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.addField("Выставите в настройках графики параметр", "Chunk Loading: Default", false);
            embed.setColor(event.getMember().getColor());

            List<Message.Attachment> attachments = null;
            try {
                attachments = event.getMessage().getAttachments();
            } catch (UnsupportedOperationException ignore) {
            }
            File downloadFile;
            if (attachments != null && !attachments.isEmpty()) {
                Message.Attachment attachment = attachments.get(0);
                String s = attachment.getFileName();
                if (getFileFormat(s).equals("txt")) {
                    downloadFile = new File("H:\\Desktop\\nano-bot\\nano-bot\\save" + "\\file.txt");
                    downloadFile.getParentFile().mkdirs();
                    attachment.downloadToFile(downloadFile)
                            .thenAccept(file -> System.out.println("Saved attachment"))
                            .exceptionally(t -> {
                                t.printStackTrace();
                                return null;
                            });
                } else {
                    System.out.println("File is not txt!");
                }

                try {
                    wait(2000);
                    File file = new File(path.toUri());
                    if (isFileExists(file)) {
                        System.out.println("File is exists!");
                        if (fileContainsWord("H:\\Desktop\\nano-bot\\nano-bot\\save\\file.txt", "Already" + " " + "tesselating!")) {
//                    event.getChannel().asTextChannel().sendMessage(" Chunk Loading Default").submit();
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();

                            System.out.println("Is ok!");

                            try {
                                Files.delete(path);
                                System.out.println("File is successfully deleted.");
                            } catch (IOException e) {
                                System.out.println("File deletion failed.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("not ok");
                            Files.delete(path);
                            System.out.println("File is successfully deleted.");
                        }
                    } else {
                        System.out.println("File in not exists!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private static boolean isFileExists(File file) {
        return file.exists() && !file.isDirectory();
    }

    private static String getFileFormat(String string) {
        String fileName = string;
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public boolean fileContainsWord(String fileName, String word) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName))).contains(word);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                long time = System.currentTimeMillis();
                event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                        ).queue(); // Queue both reply and edit
                break;
            case "hello":
                event.reply("Hello").setEphemeral(true)
                        .flatMap(v ->
                                event.getHook().editOriginalFormat("Hello")).queue();
                break;
            case "top":
                String str = event.getMember().getId();
                String ss = (" " + "<@" + str + ">");

                event.reply("Пошел нахуй," + ss).setEphemeral(false).queue();
                break;
//                event.getChannel().asTextChannel().sendMessage("ti pidor" + " " + "<@" + str + ">").queue();
        }
    }
}
