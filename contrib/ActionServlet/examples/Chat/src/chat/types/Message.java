package chat.types;

import java.util.*;

/**
 * Holds message data.
 *
 * @author Petr Toman
 */
public class Message {
    public final Date date;
    public final String sender;
    public final String text;
    public final boolean isAction;
    public final boolean isPrivate;

    public Message(Date date, String sender, String text,
                   boolean isAction, boolean isPrivate) {
        this.date = date;
        this.sender = sender;
        this.isAction = isAction;
        this.isPrivate = isPrivate;

        // convert '<' and '>' to '&lt;' and '&gt;'
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(text, "<>", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("<".equals(token)) sb.append("&lt;");
                else if (">".equals(token)) sb.append("&gt;");
                    else sb.append(token);
        }

        this.text = sb.toString();
    }
}
