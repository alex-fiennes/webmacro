package chat.components;

import java.util.*;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.Destroyed;
import chat.types.Message;

/**
 * Dispatches messages.
 *
 * @author Petr Toman
 */
public class MessageCenter implements Destroyed {
    /** Special recipient name for all users in room. */
    public static final String THIS_ROOM = "this room";

    /**
     * Key = user name, value = list of messages.
     */
    private static Hashtable messages = new Hashtable();

    private final int MAX_MESSAGES = 20;
    private ActionServlet servlet;
    private Rooms rooms;
    private String user;

    public MessageCenter(ActionServlet as) {
        user = ((Authenticator) as.getComponent("authenticator", true)).getUser();
        messages.put(user, Collections.synchronizedList(new LinkedList()));
        rooms = (Rooms) as.getComponent("rooms", true);
    }

    /**
     * Adds message to user's list of messages.
     */
    private void addMessage(List messages, Message message) {
        messages.add(0, message);
        if (messages.size() > MAX_MESSAGES) messages.remove(messages.size()-1);
    }

    /**
     * Dispatch message to user(s).
     */
    private void dispatchMessage(String recipient, String message, boolean isAction) {
        if ("".equals(message.trim())) return;

        if (!THIS_ROOM.equals(recipient.trim())) {
            // dispatch to recipient only
            Message msg = new Message(new Date(), user, message, isAction, true);
            List userMessages = (List) messages.get(recipient);

            if (userMessages == null) {
                myAction("Cannot send message to user " + recipient + " (perhaps logged off)");
            } else {
                addMessage(userMessages, msg);

                // myAction? -> don't dispatch to other users
                if (!isAction) {
                    userMessages = (List) messages.get(user);
                    addMessage(userMessages, msg);
                }
            }
        } else {
            // dispatch to all users in room
            Message msg = new Message(new Date(), user, message, isAction, false);

            Object[] users = rooms.getUsersInCurrentRoom();

            for (int i=0; i < users.length; i++) {
                List userMessages = (List) messages.get(users[i]);
                if (userMessages != null) addMessage(userMessages, msg);
            }
        }
    }

    /**
     * Sends message to recepient.
     *
     * @param recipient nickname or "this room" for all users in current room
     * @param message text of message
     */
    public void send(String recipient, String message) {
        dispatchMessage(recipient, message, false);
    }

    /**
     * Send action message to "this room".
     */
    public void action(String message) {
        dispatchMessage(THIS_ROOM, message, true);
    }

    /**
     * Send action message to this user.
     */
    void myAction(String message) {
        dispatchMessage(user, message, true);
    }

    /**
     * Clears user's message list.
     */
    public void clear() {
        List list = (List) messages.get(user);
        if (list != null) list.clear();
    }

    /**
     * Returns all user's messages.
     */
    public Object[] getMessages() {
        return ((List) messages.get(user)).toArray();
    }

    /**
     * Cleanup: remove messages.
     */
    public void destroy() {
        messages.remove(user);
    }
}
