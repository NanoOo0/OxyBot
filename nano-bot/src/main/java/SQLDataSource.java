import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.awt.*;
import java.sql.*;
import java.util.Objects;


public class SQLDataSource {

    public static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
    }

    public static int getTicketsId() throws SQLException {
        int i = 0;
        Statement statement = SQLDataSource.getNewConnection().createStatement();
        ResultSet res = statement.executeQuery("SELECT MAX(ticket_id) FROM tickets");
        while (res.next()) {
            i = res.getInt(1);
        }
        return i;
    }
    public static int getStatsMsgId() throws SQLException {
        int i = 0;
        Statement statement = SQLDataSource.getNewConnection().createStatement();
        ResultSet res = statement.executeQuery("SELECT MAX(msg_id) FROM statsmsg");
        while (res.next()) {
            i = res.getInt(1);
        }
        return i;
    }
    public static int getTicketsOnUser(@NotNull String user_name) throws SQLException{
        int i = 0;
        Statement statement = SQLDataSource.getNewConnection().createStatement();
        ResultSet res = statement.executeQuery("SELECT COUNT(status) FROM tickets WHERE status = 'open' AND user_name = " + user_name);
        while (res.next()){
            i = res.getInt(1);
        }
        return i;
    }
    @Test
    public static String getStatsChannelID(String channel_id) throws SQLException{
        String str = "";
        Statement statement = SQLDataSource.getNewConnection().createStatement();
        ResultSet res = statement.executeQuery("SELECT message_id FROM statsmsg WHERE channel_id = " + channel_id);
        while (res.next()){
            str = res.getString(1);
        }
        return str;
    }

    public static void shouldSelectData(int id, String name, String user_id, String status) throws SQLException {
        Statement statement = SQLDataSource.getNewConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet res = statement.executeQuery("SELECT * FROM tickets");
        res.moveToInsertRow();
        res.updateInt("ticket_id", id);
        res.updateString("user_name", name);
        res.updateString("user_id", user_id);
        res.updateString("status", status);
        res.insertRow();
        res.moveToCurrentRow();
        statement.close();
    }
    public static void banUser (String user_id, String user_name, String reason, String moder_name) throws SQLException {
        Statement statement = SQLDataSource.getNewConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet res = statement.executeQuery("SELECT * FROM bans_users");
        res.moveToInsertRow();
        res.updateString("user_id", user_id);
        res.updateString("user_name", user_name);
        res.updateString("reason", reason);
        res.updateString("moder_name", moder_name);
        res.insertRow();
        res.moveToCurrentRow();
        statement.close();
    }
    public static boolean checkUserBan(String user_name) throws SQLException{
        String s = "";
        Statement statement = getNewConnection().createStatement();
        ResultSet res = statement.executeQuery("SELECT user_name FROM bans_users WHERE user_name = " + user_name);
        while (res.next()){
            s = "'"+res.getString(1)+"'";
            if(s.equals(user_name)){
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    public static void statsMsg(int id, String channel_id, long message_id) throws SQLException {
        Statement statement = SQLDataSource.getNewConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet res = statement.executeQuery("SELECT * FROM statsmsg");
        res.moveToInsertRow();
        res.updateInt("msg_id", id);
        res.updateString("channel_id", channel_id);
        res.updateLong("message_id", message_id);
        res.insertRow();
        res.moveToCurrentRow();
        statement.close();
    }

    @Test
    public static void ticketClosed(String ticket_id) throws SQLException {
        String sw = "UPDATE tickets SET status = ? WHERE ticket_id = " + ticket_id;
        PreparedStatement statement = SQLDataSource.getNewConnection().prepareStatement(sw);
        statement.setString(1, "closed");
        statement.executeUpdate();
        statement.close();
    }
    @Test
    public static void ticketReopen(String ticket_id) throws SQLException {
        String sw = "UPDATE tickets SET status = ? WHERE ticket_id = " + ticket_id;
        PreparedStatement statement = SQLDataSource.getNewConnection().prepareStatement(sw);
        statement.setString(1, "open");
        statement.executeUpdate();
        statement.close();
    }
@Test
    public static int openTickets() throws SQLException {
        int i = 0;
        String s = "SELECT COUNT(status) FROM tickets WHERE status = 'open'";
        Statement statement = getNewConnection().createStatement();
        ResultSet res = statement.executeQuery(s);
        while (res.next()) {
            i = res.getInt(1);
        }
        return i;
    }
    public static String onTicketStatus(int ticket_id) throws SQLException{
        String s = "";
        Statement statement = SQLDataSource.getNewConnection().createStatement();
        ResultSet res = statement.executeQuery("SELECT status FROM tickets WHERE ticket_id = " + ticket_id);
        while (res.next()){
            s = res.getString(1);
        }
        return s;
    }
    public static int closedTickets() throws SQLException {
        int i = 0;
        String s = "SELECT COUNT(status) FROM tickets WHERE status = 'closed'";
        Statement statement = getNewConnection().createStatement();
        ResultSet res = statement.executeQuery(s);
        while (res.next()) {
            i = res.getInt(1);
        }
        return i;
    }


}
