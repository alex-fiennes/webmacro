package shop;

import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.ActionException;
import org.webmacro.as.ConversionException;
import org.webmacro.servlet.WebContext;
import org.webmacro.Template;
import org.webmacro.RethrowableException;
import shop.components.ConnectionManager;
import shop.components.SimpleDbComponent;
import shop.components.Authenticator;
import shop.components.Show;
import shop.types.Item;
import shop.types.EmailException;

/**
 * Does custom error handling, checks for user's permissions etc.
 */
public class ShopServlet extends ActionServlet {
    /**
     * Returns items from "lost" shopping carts back to goods.
     */
    public void start() throws ServletException {
        super.start();

        Connection con = null;
        try {
            con = ((ConnectionManager) getComponent("jdbc", true)).getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from carts");
            Vector items = new Vector();

            while (rs.next()) {
                Item item = new Item(rs.getString("item"), rs.getInt("count"), rs.getDouble("price"));
                items.addElement(item);
            }

            for (Enumeration e = items.elements(); e.hasMoreElements(); ) {
                Item item = (Item) e.nextElement();
                getLog("shop").info("Returning '" + item.name + "' ("+item.count+") back to goods");

                rs = stmt.executeQuery("select * from goods where item='"+item.name+"'");
                int oldCount = 0;
                double price = item.price;

                if (rs.next()) {
                    oldCount = rs.getInt("count");
                    price = rs.getDouble("price");
                    stmt.executeUpdate("delete from goods where item='" + item.name + "'");
                }

                stmt.executeUpdate("insert into goods values ('"+item.name+"',"+(oldCount+item.count)+","+price+")");
            }

            stmt = con.createStatement();
            stmt.executeUpdate("delete from carts");
            con.commit();
        } catch (SQLException e) {
            getLog("shop").debug("SQL Error: " + e.getMessage());
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {}
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {}
        }
    }

    /**
     * Returns localized template.
     */
    public Template getWMTemplate(String key) {
        if (key.length() > 3 && key.charAt(2) == '/')
            return super.getWMTemplate(key);

        try {
            String lang = ((Show)getComponent("show", true)).getLocale().getLanguage();
            return super.getWMTemplate(lang+"/"+key);
        } catch (IllegalStateException e) {
            return super.getWMTemplate("en"+"/"+key);
        }
    }

    /**
     * Does custom exception handling.
     */
    protected Template conversionError(WebContext context,
                                       String form,
                                       String action,
                                       ConversionException e) throws ActionException {
        if (e instanceof EmailException) {
            context.put("error", "BAD_EMAIL");
        } else if (e.detail instanceof NumberFormatException) {
            context.put("error", "NOT_A_NUMBER");
        } else super.conversionError(context, form, action, e);

        context.put("target", "error.wm");
        return getWMTemplate("main.wm");
    }

    /**
     * Custom SQL error report.
     */
    protected Template onException(WebContext context,
                                   String form,
                                   String action,
                                   ActionException e) {
        if (e.detail instanceof SQLException ||
           (e.detail instanceof RethrowableException &&
           ((RethrowableException) e.detail).getCaught() instanceof SQLException)) {
            context.put("error", "SQL_ERROR");
            context.put("target", "error.wm");
            return getWMTemplate("main.wm");
        }

        return super.onException(context, form, action, e);
    }

    /**
     * Pre-checks user's permissions.
     */
    protected Object beforeInvoke(WebContext context,
                                  String form,
                                  String action,
                                  Object[] convertedParams) {
        Authenticator authenticator = (Authenticator) getComponent("authenticator", true);

        if (action.startsWith("show") && convertedParams.length>0 &&
            ("accounts.inc".equals(convertedParams[0]) || "AdminOrders.inc".equals(convertedParams[0])) &&
            !authenticator.isAdmin()) {
            context.put("error", "NOT_ADMIN");
            context.put("target", "error.wm");
            return getWMTemplate("main.wm");
        }

        if (!authenticator.isLoggedIn() &&
            !action.startsWith("show") &&
            !"Login".equals(action) &&
            !"goods".equals(form) &&
            !"cart".equals(form) &&
            !"newAccount".equals(action)) {
            context.put("error", "NOT_LOGGED_IN");
            context.put("target", "ShopLogin.inc");
            return getWMTemplate("main.wm");
        }

        return null;
    }

    /**
     * (Re)initializes Orders component on login.
     */
    protected Object afterInvoke(Object retValue,
                                 WebContext context,
                                 String form,
                                 String action,
                                 Object[] convertedParams) {
        if ("Login".equals(action) && ((Integer) retValue).intValue() == Authenticator.OK) {
            Authenticator auth = (Authenticator) getComponent("authenticator", true);
            SimpleDbComponent orders = (SimpleDbComponent) getComponent("orders", true);
            orders.setSelect("select * from orders where user='" + auth.getUser() + "'");
            orders.setOrderBy("DATE");
        }

        return retValue;
    }
}
