package chat.components;

import java.util.*;
import org.webmacro.as.ActionServlet;
import org.webmacro.as.Destroyed;
import chat.types.Room;

/**
 * Provides access to rooms.
 *
 * @author Petr Toman
 */
public class Rooms implements Destroyed {
    /** Default room name. */
    public static final String DEFAULT_ROOM = "main";
    private static final Object[] EMPTY = new Object[0];

    /**
     * Key = room name, value = list of users.
     */
    private static Map rooms = Collections.synchronizedMap(new TreeMap());

    private ActionServlet servlet;
    private MessageCenter mc;
    private String user;

    /**
     * User's current room.
     */
    private String currentRoom = "";

    public Rooms(ActionServlet as) {
        servlet = as;
        user = ((Authenticator) as.getComponent("authenticator", true)).getUser();
    }

    /**
     * Moves user to the given room creating it if necessary.
     */
    public synchronized void joinRoom(String roomName) {
        if (mc == null) mc = (MessageCenter) servlet.getComponent("MessageCenter", true);
        roomName = roomName.trim();

        if (!currentRoom.equals(roomName) && !"".equals(roomName)) {
            Room room = (Room) rooms.get(roomName);

            if (room != null && room.isLocked) {
                mc.myAction("You cannot enter locked room " + roomName);
            } else {
                leaveRoom();

                if (room == null) {
                    room = new Room(roomName, Collections.synchronizedSortedSet(new TreeSet()));
                    rooms.put(roomName, room);
                }

                room.users.add(user);
                currentRoom = roomName;

                mc.clear();
                mc.action(user + " has entered room " + roomName);
            }
        }
    }

    /**
     * Removes user from the current room.
     */
    public synchronized void leaveRoom() {
        if (!"".equals(currentRoom)) {
            mc.action(user + " has left room " + currentRoom);

            Room room = (Room) rooms.get(currentRoom);

            room.users.remove(user);
            if (room.users.isEmpty() && !DEFAULT_ROOM.equals(currentRoom))
                rooms.remove(currentRoom);

            currentRoom = "";
        }
    }

    /**
     * Sets current room lock.
     */
    public void setLock(boolean lock) {
        if (!DEFAULT_ROOM.equals(currentRoom)) {
            Room room = (Room) rooms.get(currentRoom);

            if (lock == room.isLocked) {
                mc.myAction("This room is aleady " + (lock?"locked":"unlocked"));
            } else {
                room.isLocked = lock;
                mc.action(user + " has " + (lock?"locked":"unlocked") + " this room");
            }
        } else mc.myAction("Cannot lock this room");
    }

    /**
     * Returns current room.
     */
    public Room getCurrentRoom() {
        return (Room) rooms.get(currentRoom);
    }

    /**
     * Returns sorted list of all available chat rooms.
     */
    public Object[] getRooms() {
        return rooms.values().toArray();
    }

    /**
     * Returns sorted list of users in current room.
     */
    public Object[] getUsersInCurrentRoom() {
        Room room = (Room) rooms.get(currentRoom);

        if (room == null) return EMPTY;
        return room.users.toArray();
    }

    /**
     * Cleanup: leave room.
     */
    public void destroy() {
        leaveRoom();
    }
}
