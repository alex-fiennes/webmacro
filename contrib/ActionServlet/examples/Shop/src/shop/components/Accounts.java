package shop.components;

import java.sql.*;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.ConversionException;
import shop.types.Email;
import shop.types.User;

/**
 * Provides access to customer's accounts.
 *
 * @author Petr Toman
 */
public class Accounts extends SimpleDbComponent {
    public static final int OK = 0;
    public static final int ACCOUNT_EXISTS = 1;
    public static final int BAD_PASSWORD_CONFIRM = 2;
    public static final int FIELDS_NOT_FILLED = 3;
    public static final int NOT_LOGGED_IN= 4;
    public static final int ACCOUNT_DOES_NOT_EXIST = 5;
    public static final int NOT_ADMIN = 6;
    public static final int BAD_OLD_PASSWORD = 7;

    /** Last edited user.*/
    public User user;

    /** Last used user name.*/
    public String userName;

    private ConnectionManager cm;
    private Authenticator auth;

    public Accounts(ActionServlet as, String name) {
        super(as, name);
        cm = (ConnectionManager) as.getComponent("jdbc", true);
        auth = (Authenticator) as.getComponent("authenticator", true);
    }

    /**
     * Creates a new account.
     */
    public int createNewAccount(User user, String password, String confirm)
    throws SQLException {
        this.user = user;

        if ("".equals(password.trim()) ||
            "".equals(confirm.trim()) ||
            "".equals(user.userName) ||
            "".equals(user.firstName) ||
            "".equals(user.surname) ||
            "".equals(user.street) ||
            "".equals(user.city) ||
            "".equals(user.postalCode) ||
            "".equals(user.state)) return FIELDS_NOT_FILLED;

        if (!confirm.trim().equals(password.trim())) return BAD_PASSWORD_CONFIRM;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts where userName='" + user.userName + "'");

            if (rs.next()) return ACCOUNT_EXISTS;
            stmt.executeUpdate("insert into accounts values ('"+user.userName+"','"+password.trim()+"','"+
                               user.email+"','"+user.firstName+"','"+ user.surname+"','"+user.street+"','"+
                               user.city+"','"+user.postalCode+"','"+user.state+"')");
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return OK;
    }

    /**
     * Removes account.
     */
    public int removeAccount(String user) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("delete from accounts where userName='"+user+"'");
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return OK;
    }

    /**
     * Shows account data.
     */
    public int showAccount() throws SQLException, ConversionException {
        String userName = auth.getUser();
        return showAccount(userName);
    }

    /**
     * Shows account data of given user.
     */
    public int showAccount(String userName) throws SQLException, ConversionException {
        if (!auth.isLoggedIn()) return NOT_LOGGED_IN;
        if (!userName.equals(auth.getUser()) && !auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts where userName='" + userName + "'");
            if (!rs.next()) return ACCOUNT_DOES_NOT_EXIST;

            user = new User(userName,
                            new Email(rs.getString("email")),
                            rs.getString("firstName"),
                            rs.getString("surname"),
                            rs.getString("street"),
                            rs.getString("city"),
                            rs.getString("postalCode"),
                            rs.getString("state"));
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return OK;
    }

    /**
     * Sets password.
     */
    public int setPassword(String oldPassword, String newPassword, String confirm)
    throws SQLException, ConversionException {
        String userName = auth.getUser();
        return setPassword(userName, oldPassword, newPassword, confirm);
    }

    /**
     * Sets password for given user.
     */
    public int setPassword(String userName, String oldPassword, String newPassword, String confirm)
    throws SQLException, ConversionException {
        this.userName = userName;

        if (!auth.isLoggedIn()) return NOT_LOGGED_IN;
        if (!userName.equals(auth.getUser()) && !auth.isAdmin()) return NOT_ADMIN;

        if ("".equals(oldPassword.trim()) ||
            "".equals(newPassword.trim()) ||
            "".equals(confirm.trim())) return FIELDS_NOT_FILLED;

        if (!confirm.trim().equals(newPassword.trim())) return BAD_PASSWORD_CONFIRM;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts where userName='" + userName + "'");

            if (!rs.next()) return ACCOUNT_DOES_NOT_EXIST;
            if (!oldPassword.equals(rs.getString("password")) && !auth.isAdmin())
                return BAD_OLD_PASSWORD;

            stmt.executeUpdate("update accounts set password='" + newPassword + "' where userName='" + userName + "'");
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return OK;
    }

    /**
     * Updates account data for given user.
     */
    public int editAccount(User user)
    throws SQLException {
        this.user = user;

        if (!auth.isLoggedIn()) return NOT_LOGGED_IN;
        if (!user.userName.equals(auth.getUser()) && !auth.isAdmin()) return NOT_ADMIN;

        if ("".equals(user.firstName) ||
            "".equals(user.surname) ||
            "".equals(user.street) ||
            "".equals(user.city) ||
            "".equals(user.postalCode) ||
            "".equals(user.state)) return FIELDS_NOT_FILLED;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from accounts where userName='" + user.userName + "'");

            if (!rs.next()) return ACCOUNT_DOES_NOT_EXIST;
            String password = rs.getString("password");

            stmt.executeUpdate("delete from accounts where userName='" + user.userName + "'");

            stmt.executeUpdate("insert into accounts values ('"+user.userName+"','"+password+"','"+
                               user.email+"','"+user.firstName+"','"+ user.surname+"','"+user.street+"','"+
                               user.city+"','"+user.postalCode+"','"+user.state+"')");
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return OK;
    }
}
