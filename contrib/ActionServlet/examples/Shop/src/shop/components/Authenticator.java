package shop.components;

import java.sql.*;
import org.webmacro.as.ActionServlet;

/**
 * Authenticates customers/users.
 *
 * @author Petr Toman
 */
public class Authenticator implements Errors {
    /** This user. */
    private String user;

    private ConnectionManager cm;

    public Authenticator(ActionServlet as) {
        cm = (ConnectionManager) as.getComponent("jdbc", true);
    }

    /**
     * Authenticates user.
     */
    public int login(String userName, String password) throws SQLException {
        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts where username='" + userName + "'");

            if (rs.next() && password.equals(rs.getString("password"))) {
                if (isLoggedIn()) logoff();
                user = userName;
                return OK;
            }
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return BAD_USERNAME_OR_PASSWORD;
    }

    /**
     * Logs user off.
     */
    public void logoff() {
        user = null;
    }

    /**
     * Returns logged in user name.
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns true if the user is logged in.
     */
    public boolean isLoggedIn() {
        return user != null;
    }

    /**
     * Returns true if the user is administrator.
     */
    public boolean isAdmin() {
        return isLoggedIn() && "admin".equals(user);
    }
}
