package shop.components;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.webmacro.as.ActionServlet;
import shop.types.Item;

/**
 * Provides access to customer's orders.
 *
 * @author Petr Toman
 */
public class Orders extends SimpleDbComponent implements Errors {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("d.M.yyyy HH:mm", Locale.US);
    private ConnectionManager cm;
    private Authenticator auth;
    private String sessionId;

    public Orders(ActionServlet as, String name) {
        super(as, name);
        cm = (ConnectionManager) as.getComponent("jdbc", true);
        auth = (Authenticator) as.getComponent("authenticator", true);
        sessionId = as.getSessionId();
    }

    /**
     * Orders items in cart.
     */
    public int order() throws SQLException {
        if (!auth.isLoggedIn()) return NOT_LOGGED_IN;

        String str = "";
        int totalPrice = 0;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from carts where session='"+sessionId+"'");
            while(rs.next()) {
                String item = rs.getString("item");
                int count = rs.getInt("amount");
                double price = rs.getDouble("price");
                totalPrice += count * price;
                str += count + "x " + item + " (" + price + "),<BR>";
            }
            if ("".equals(str)) return SHOPPING_CART_EMPY;
            str = str.substring(0, str.length()-5);

            stmt.executeUpdate("insert into orders values ('"+System.currentTimeMillis()+"','"+auth.getUser()+"','"+
                               dateFormatter.format(new Date())+"','"+str+"',"+totalPrice+",'PENDING')");
            stmt.executeUpdate("delete from carts where session='"+sessionId+"'");
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
