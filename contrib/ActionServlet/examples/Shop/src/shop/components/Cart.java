package shop.components;

import java.sql.*;
import java.util.*;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.Destroyed;
import shop.types.Item;
import org.webmacro.datatable.SimpleTableRow;
import org.webmacro.datatable.DataTable;

/**
 * Represents customer's shopping cart.
 *
 * @author Petr Toman
 */
public class Cart extends SimpleDbComponent implements Destroyed, Errors {
    private ConnectionManager cm;
    private Authenticator auth;
    private String sessionId;

    /** Last added item.*/
    public Item addedItem;

    public Cart(ActionServlet as, String name) {
        super(as, name);
        cm = (ConnectionManager) as.getComponent("jdbc", true);
        auth = (Authenticator) as.getComponent("authenticator", true);
        sessionId = as.getSessionId();

        setSelect("select * from carts where session='"+sessionId+"'");
        setOrderBy("ITEM");
    }

    /**
     * Adds item to shopping cart.
     */
    public int addItem(String name, int count) throws SQLException {
        if (count < 1) return ZERO_OR_NEGATIVE_COUNT_ERR;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            int available = -1;
            double price = -1;

            ResultSet rs = stmt.executeQuery("select * from goods where item='" + name + "'");
            if (rs.next()) {
                available = rs.getInt("amount");
                price = rs.getDouble("price");
                if (available < count) return NOT_AVAILABLE;
                stmt.executeUpdate("update goods set amount=" + (available-count) + "where item='" + name + "'");
            } else return NO_SUCH_ITEM;

            rs = stmt.executeQuery("select * from carts where session='"+sessionId+"' and item='" + name + "'");
            int oldCount = 0;

            if (rs.next()) {
                 oldCount = rs.getInt("amount");
                 stmt.executeUpdate("delete from carts where session='"+sessionId+"' and item='" + name + "'");
            }
            stmt.executeUpdate("insert into carts values ('"+sessionId+"','"+name+"',"+(oldCount+count)+","+price+")");
            addedItem = new Item(name, count, price);
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
     * Removes item from shopping cart.
     */
    public int removeItem(String name, int count) throws SQLException {
        if (count < 1) return ZERO_OR_NEGATIVE_COUNT_ERR;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from carts where session='"+sessionId+"' and item='" + name + "'");
            int oldCount = -1;
            double price = -1;

            if (rs.next()) {
                 oldCount = rs.getInt("amount");
                 price = rs.getDouble("price");
                 if (oldCount < count) return NOT_SO_MANY_ITEMS_IN_CART;
                 stmt.executeUpdate("delete from carts where session='"+sessionId+"' and item='" + name + "'");
            }

            if (oldCount - count > 0)
                stmt.executeUpdate("insert into carts values ('"+sessionId+"','"+name+"',"+(oldCount-count)+","+price+")");

            int available = -1;

            rs = stmt.executeQuery("select * from goods where item='" + name + "'");
            if (rs.next()) {
                available = rs.getInt("amount");
                price = rs.getDouble("price");
                stmt.executeUpdate("delete from goods where item='" + name + "'");
            }
            stmt.executeUpdate("insert into goods values ('"+name+"',"+(available+count)+","+price+")");
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
     * Returns all items in cart.
     */
    private Object[] getItems() throws SQLException {
        Vector items = new Vector();

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from carts where session='"+sessionId+"'");
            while(rs.next())
                items.addElement(new Item(rs.getString("item"), rs.getInt("amount"), rs.getDouble("price")));
        } catch (SQLException e) {
            rolledback = true;
            con.rollback();
            throw e;
        } finally {
            if (!rolledback) con.commit();
            if (con != null) con.close();
        }

        return items.toArray();
    }

    /**
     * Returns price of all items in cart.
     */
    public double getTotalPrice() throws SQLException {
        Object[] items = getItems();
        double price = 0;

        for (int i=0; i<items.length; i++)
            price += ((Item)items[i]).price * ((Item)items[i]).count;

        return Math.ceil(price * 100) / 100d;
    }

    /**
     * Cleanup: return all items back to goods.
     */
    public void destroy() {
        try {
            Object[] items = getItems();

            for (int i=0; i < items.length; i++)
                removeItem(((Item)items[i]).name, ((Item)items[i]).count);
        } catch (SQLException e) {}
    }
}
