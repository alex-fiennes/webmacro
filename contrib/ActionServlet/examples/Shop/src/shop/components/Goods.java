package shop.components;

import java.sql.*;
import org.webmacro.as.ActionServlet;
import shop.types.Item;

/**
 * Provides access to offered goods.
 *
 * @author Petr Toman
 */
public class Goods extends SimpleDbComponent {
    public static final int OK = 0;
    public static final int ITEM_EXISTS = 1;
    public static final int NO_SUCH_ITEM = 2;
    public static final int NOT_SO_MANY_ITEMS_IN_GOODS = 3;
    public static final int NOT_ADMIN = 4;

    /** Last added/edited item.*/
    public Item item;

    private ConnectionManager cm;
    private Authenticator auth;

    public Goods(ActionServlet as, String name) {
        super(as, name);
        cm = (ConnectionManager) as.getComponent("jdbc", true);
        auth = (Authenticator) as.getComponent("authenticator", true);
    }

    /**
     * Increases amount of given item in goods.
     */
    public int addAmount(String item, int count) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from goods where item='" + item + "'");
            if (!rs.next()) return NO_SUCH_ITEM;

            int oldCount = rs.getInt("amount");
            double price = rs.getDouble("price");

            stmt.executeUpdate("update goods set amount="+(oldCount+count)+" where item='" + item + "'");
            this.item = new Item(item, oldCount+count, price);
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
     * Decreases amount of given item in goods.
     */
    public int removeAmount(String item, int count) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from goods where item='" + item + "'");
            if (!rs.next()) return NO_SUCH_ITEM;

            int oldCount = rs.getInt("amount");
            double price = rs.getDouble("price");
            if (oldCount < count) return NOT_SO_MANY_ITEMS_IN_GOODS;

            stmt.executeUpdate("update goods set amount="+(oldCount-count)+" where item='" + item + "'");
            this.item = new Item(item, oldCount-count, price);
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
     * Sets price of given item in goods.
     */
    public int setPrice(String item, double price) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from goods where item='" + item + "'");
            if (!rs.next()) return NO_SUCH_ITEM;

            int count = rs.getInt("amount");

            stmt.executeUpdate("update goods set price="+price+" where item='" + item + "'");
            this.item = new Item(item, count, price);
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
     * Adds a new item to goods.
     */
    public int addNewItem(Item item) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        this.item = item;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from goods where item='" + item.name + "'");
            if (rs.next()) return ITEM_EXISTS;

            stmt.executeUpdate("insert into goods values ('"+item.name+"',"+item.count+","+item.price+")");
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
     * Removes item from goods.
     */
    public int removeItem(String item) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("delete from goods where item='" + item + "'");
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
