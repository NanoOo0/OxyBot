import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.managers.GuildManager;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.awt.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Consumer;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class Ticket extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        System.out.println("join");
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        EmbedBuilder embedstats = new EmbedBuilder();
        embedstats.setTitle("Статистика");
        try {
            embedstats.addField("Всего тикетов: ", String.valueOf(SQLDataSource.getTicketsId()), true);
        } catch (SQLException e) {
        }
        try {
            embedstats.addField("Открытых тикетов: ", String.valueOf(SQLDataSource.openTickets()), true);
        } catch (SQLException e) {
        }
        try {
            embedstats.addField("Закрытых тикетов: ", String.valueOf(SQLDataSource.closedTickets()), true);
        } catch (SQLException e) {
        }
        embedstats.setColor(Color.CYAN);
        TextChannel channel = event.getGuild().getTextChannelById("1077908602284675092");
        assert channel != null;
        channel.editMessageEmbedsById("1077909431783792720", embedstats.build()).queue();

    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onGenericSelectMenuInteraction(GenericSelectMenuInteractionEvent event) {
        super.onGenericSelectMenuInteraction(event);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "testmenu":
//                EntitySelectMenu e =  EntitySelectMenu.create("id", EntitySelectMenu.SelectTarget.CHANNEL).setPlaceholder("ts");
                event.reply("s").addActionRow(EntitySelectMenu.create("id", EntitySelectMenu.SelectTarget.CHANNEL).build()).queue();
                break;
            case "test":
                TextInput subject = TextInput.create("subject", "Subject", TextInputStyle.SHORT)
                        .setPlaceholder("Subject of this ticket")
                        .setMinLength(1)
                        .setMaxLength(100) // or setRequiredRange(10, 100)
                        .build();

                TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Your concerns go here")
                        .setMinLength(1)
                        .setMaxLength(1000)
                        .build();

                Modal modal = Modal.create("modmail", "Modmail")
                        .addActionRows(ActionRow.of(subject), ActionRow.of(body))
                        .build();

                event.replyModal(modal).queue();
                break;
            case "ticket_ban":

                String user_name = event.getOption("user_name").getAsString();
                String reason = event.getOption("reason").getAsString();
                String moder_name = event.getUser().getName();
                String user_id = event.getGuild().getMembersByName(user_name, true).get(0).getId();

                try {SQLDataSource.banUser(user_id, user_name, reason, moder_name);
                    event.reply("Пользователь забанен").setEphemeral(true).queue();
                } catch (SQLException e) {
                    event.reply("Произошла ошибка").setEphemeral(true).queue();
                }
                break;
            case "updatestats":
                EmbedBuilder embedstatss = new EmbedBuilder();
                embedstatss.setTitle("Статистика");
                try {
                    embedstatss.addField("Всего тикетов: ", String.valueOf(SQLDataSource.getTicketsId()), true);
                } catch (SQLException e) {
                }
                try {
                    embedstatss.addField("Открытых тикетов: ", String.valueOf(SQLDataSource.openTickets()), true);
                } catch (SQLException e) {
                }
                try {
                    embedstatss.addField("Закрытых тикетов: ", String.valueOf(SQLDataSource.closedTickets()), true);
                } catch (SQLException e) {
                }
                embedstatss.setColor(Color.CYAN);
                TextChannel channel = event.getGuild().getTextChannelById("1077908602284675092");
                assert channel != null;
                channel.editMessageEmbedsById("1077909431783792720", embedstatss.build()).queue();
                event.reply("Статистика обновлена").setEphemeral(true).queue();
                break;
            case "createmsg":
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Поддержка");
                embed.setDescription("Чтобы создать тикет нажми на реакцию ниже.");
                embed.setColor(Color.CYAN);
                event.getChannel().asTextChannel().sendMessageEmbeds(embed.build())
                        .addActionRow(Button.primary("open", Emoji.fromUnicode("U+1F4E9")))
                        .queue();
                event.reply("Сообщение с тикетами создано!").setEphemeral(true).queue();
                break;
            case "statsmsg":
                EmbedBuilder embedstats = new EmbedBuilder();
                embedstats.setTitle("Статистика");
                try {
                    embedstats.addField("Всего тикетов: ", String.valueOf(SQLDataSource.getTicketsId()), true);
                } catch (SQLException e) {
                }
                try {
                    embedstats.addField("Открытых тикетов: ", String.valueOf(SQLDataSource.openTickets()), true);
                } catch (SQLException e) {
                }
                try {
                    embedstats.addField("Закрытых тикетов: ", String.valueOf(SQLDataSource.closedTickets()), true);
                } catch (SQLException e) {
                }
                embedstats.setColor(Color.CYAN);
                String channel_id = event.getChannel().getId();
                event.getChannel().asTextChannel().sendMessageEmbeds(embedstats.build())
                        .queue((message) -> {
                            long message_id = message.getIdLong();
                            int id = 0;
                            try {id = SQLDataSource.getStatsMsgId();} catch (SQLException e) {e.printStackTrace();}
                            id++;
                            try {SQLDataSource.statsMsg(id, channel_id, message_id);} catch (SQLException e) {e.printStackTrace();}
                        });
                event.reply("Сообщение со статистикой создано!").setEphemeral(true).queue();
                break;
        }
    }
    private boolean check(@NotNull String user) throws SQLException {
        if(SQLDataSource.getTicketsOnUser(user) == 0){
            return true;
        }
        else {
            return false;
        }
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (event.getComponentId().equals("open")) {

            boolean b;
            boolean c;
            try {
                b = check("'"+event.getUser().getName()+"'");
                c = SQLDataSource.checkUserBan("'"+event.getUser().getName()+"'");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//            event.getMember().getRoles().equals(event.getGuild().getRolesByName("админка", true))
            if(c && b) {

                String s = event.getMember().getUser().getName();
                try {
                    SQLDataSource.shouldSelectData((SQLDataSource.getTicketsId() + 1), s, event.getMember().getId(), "open");
                } catch (SQLException e) {
                }


                Category category = event.getGuild().getCategoryById("1077910622433460254");
                TextChannel ticket = null;
                try {
                    ticket = event.getGuild().createTextChannel("ticket-" + SQLDataSource.getTicketsId())
                            .setParent(category)
                            .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                            .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                            .complete();
                } catch (SQLException e) {
                }
//            ChannelManager cm = ticket.getManager()
//                    .putPermissionOverride(event.getMember(), 3072L, 8192L)
//                    .putPermissionOverride(event.getGuild().getRolesByName("@everyone", true).get(0), 0L, 1024L);
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Поддержка");
                embed.setDescription("текст для тикетов");
                embed.setColor(Color.CYAN);
                ticket.sendMessage(event.getMember().getAsMention() + ", вы создали тикет!").setEmbeds(embed.build())
                        .addActionRow(Button.primary("close", Emoji.fromUnicode("U+1F512")))
                        .queue();
                event.deferEdit().queue();
            }
            else {
                if(!c){event.reply("Вы в черном списке тикетов!").setEphemeral(true).queue();}
                if(!b){event.reply("У вас есть открытый тикет!").setEphemeral(true).queue();}

            }
        } else if (event.getComponentId().equals("close")) {
            String channel_name = event.getChannel().getName();
            String ticket_id = getTicketNumder(channel_name);
            String result = "";
            try {
                result = SQLDataSource.onTicketStatus(Integer.parseInt(ticket_id));
            } catch (SQLException e) {}
            if(result.equals("open")) {

                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("Вы уверены, что хотите закрыть тикет?");
                embed.setColor(Color.CYAN);

                event.getChannel().asTextChannel().sendMessageEmbeds(embed.build())
                        .addActionRow(Button.primary("closed", "Закрыть"), Button.primary("cancel", "Отмена")).queue();
//            event.deferEdit().queue();
            }else {
                event.reply("Тикет уже закрыт!").setEphemeral(true).queue();
            }
        } else if (event.getComponentId().equals("closed")) {


            String channel_name = event.getChannel().getName();
            String ticket_id = getTicketNumder(channel_name);


            try {
                SQLDataSource.ticketClosed(ticket_id);

                event.getMessage().delete().queue();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setDescription("Тикет закрыт " + event.getMember().getAsMention());
                embed.setColor(Color.CYAN);
                event.getChannel().asTextChannel().sendMessageEmbeds(embed.build()).queue();

                EmbedBuilder embed1 = new EmbedBuilder();
                embed1.setDescription("Support team ticket control");
                embed1.setColor(Color.CYAN);

                event.getChannel().asTextChannel().sendMessageEmbeds(embed1.build())
                        .addActionRow(Button.primary("reopen", Emoji.fromUnicode("U+1F513")), Button.primary("delete", Emoji.fromUnicode("U+26D4")))
                        .queue();
                event.getChannel().asTextChannel().getManager().setName("Closed-" + ticket_id).queue();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("ты даун");
            }
        } else if (event.getComponentId().equals("cancel")) {
            event.getMessage().delete().queue();
        } else if (event.getComponentId().equals("delete")) {
//            event.deferEdit().queue();
            event.getChannel().asTextChannel().sendMessage("Тикет удалится через 10 секунд!")
                    .queue();
            wait(5000);
            event.getChannel().asTextChannel().delete()
                    .queue();
        } else if (event.getComponentId().equals("reopen")) {
            String channel_name = event.getChannel().getName();
            String ticket_id = getTicketNumder(channel_name);
            try {
                SQLDataSource.ticketReopen(ticket_id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            event.getMessage().delete().queue();
            EmbedBuilder embed1 = new EmbedBuilder();
            embed1.setDescription("Тикет открыт " + event.getMember().getAsMention());
            embed1.setColor(Color.CYAN);
            event.getChannel().asTextChannel().sendMessageEmbeds(embed1.build()).queue();
            event.getChannel().asTextChannel().getManager().setName("ticket-" + ticket_id).queue();

        }
    }

    private static String getTicketNumder(String string) {
        if (string.lastIndexOf("-") != -1 && string.lastIndexOf("-") != 0)
            return string.substring(string.lastIndexOf("-") + 1);
        else return "";
    }
}
