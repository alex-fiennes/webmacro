package chat.components;

import java.sql.*;
import java.util.*;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.Destroyed;

/**
 * Authenticates users.
 *
 * @author Petr Toman
 */
public class Authenticator implements Destroyed {
    public static final int OK = 0;
    public static final int BAD_USERNAME_OR_PASSWORD = 1;

    /** List of logged in users. */
    private static final Set users = Collections.synchronizedSortedSet(new TreeSet());

    private String user;
    private Properties prop = new Properties();
    private ActionServlet servlet;

    public Authenticator(ActionServlet as) {
        servlet = as;
        try {
           Class.forName("jdbc.SimpleText.SimpleTextDriver");
        } catch(Exception e) {}
        prop.put("Directory", as.getProperty("db.directory"));
    }

    /**
     * Authenticates user.
     */
    public synchronized int login(String nickName, String password) throws SQLException {
        nickName = nickName.trim();
        password = password.trim();

        // validate nickname and password
        if ("".equals(nickName) || "".equals(password) ||
            MessageCenter.THIS_ROOM.equals(nickName.trim()))
            return BAD_USERNAME_OR_PASSWORD;

        for (int i=0; i < nickName.length(); i++)
            if (!Character.isLetter(nickName.charAt(i)) &&
                !Character.isDigit(nickName.charAt(i)) &&
                nickName.charAt(i) != '_') return BAD_USERNAME_OR_PASSWORD;

        Connection con = null;
        Out: try {
            con = DriverManager.getConnection("jdbc:SimpleText", prop);
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from users where nickname='" + nickName + "'");

            if (rs.next()) {
                if(password.equals(rs.getString(2))) {
                    if (user != null) logoff();
                } else return BAD_USERNAME_OR_PASSWORD;
            } else {
                stmt.executeUpdate("insert into users values('"+nickName+"','"+password+"')");
            }
            con.commit();
        } catch(SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }

        // assign unique nickname to user
        user = nickName;
        int i = 2;
        while (users.contains(user))
            user = nickName + " #" + i++;
        users.add(user);

        return OK;
    }

    /**
     * Logs user off.
     */
    public synchronized void logoff() {
        if (isLoggedIn()) {
            users.remove(user);
            user = null;
        }
    }

    /**
     * Returns logged in user name.
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns sorted list of logged in users' names.
     */
    public Object[] getUsers() {
        return users.toArray();
    }

    /**
     * Returns true if the user is logged in.
     */
    public boolean isLoggedIn() {
        return user != null;
    }

    /**
     * Cleanup: logs user off.
     */
    public void destroy() {
        logoff();
    }
}
