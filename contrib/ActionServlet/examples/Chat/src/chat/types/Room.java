package chat.types;

import java.util.Set;

/**
 * Holds room data.
 *
 * @author Petr Toman
 */
public class Room {
    public final String name;
    public final Set users;
    public boolean isLocked;

    public Room(String name, Set users) {
       this.name = name;
       this.users = users;
    }
}
