package shop.components;

import java.sql.*;
import org.webmacro.as.ActionServlet;

/**
 * Manages orders.
 *
 * @author Petr Toman
 */
public class AdminOrders extends SimpleDbComponent implements Errors {
    private ConnectionManager cm;
    private Authenticator auth;

    public AdminOrders(ActionServlet as, String name) {
        super(as, name);
        cm = (ConnectionManager) as.getComponent("jdbc", true);
        auth = (Authenticator) as.getComponent("authenticator", true);
    }

    /**
     * Sets order status to "EXPEDED".
     */
    public int expede(String id) throws SQLException {
        return setStatus(id, "EXPEDED");
    }

    /**
     * Sets order status to "REJECTED".
     */
    public int reject(String id) throws SQLException {
        return setStatus(id, "REJECTED");
    }

    /**
     * Sets order status.
     */
    private int setStatus(String id, String newStatus) throws SQLException {
        if (!auth.isAdmin()) return NOT_ADMIN;

        Connection con = null;
        boolean rolledback = false;
        try {
            con = cm.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from orders where id='" + id + "'");
            if (!rs.next()) return NO_SUCH_ID;
            String status = rs.getString("status");
            if (!"PENDING".equals(status)) return CANNOT_CHANGE_STATUS;

            stmt.executeUpdate("update orders set status='" + newStatus + "' where id='" + id + "'");
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
