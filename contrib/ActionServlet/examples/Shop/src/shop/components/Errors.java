package shop.components;

/**
 * Common error codes.
 */
public interface Errors {
    public static final int OK                         = 0;

    public static final int ACCOUNT_DOES_NOT_EXIST     = 1;
    public static final int ACCOUNT_EXISTS             = 2;
    public static final int BAD_OLD_PASSWORD           = 3;
    public static final int BAD_PASSWORD_CONFIRM       = 4;
    public static final int BAD_USERNAME_OR_PASSWORD   = 5;
    public static final int CANNOT_CHANGE_STATUS       = 6;
    public static final int FIELDS_NOT_FILLED          = 7;
    public static final int ITEM_EXISTS                = 8;
    public static final int NOT_ADMIN                  = 9;
    public static final int NOT_AVAILABLE              = 10;
    public static final int NOT_LOGGED_IN              = 11;
    public static final int NOT_SO_MANY_ITEMS_IN_CART  = 12;
    public static final int NOT_SO_MANY_ITEMS_IN_GOODS = 13;
    public static final int NO_SUCH_ID                 = 14;
    public static final int NO_SUCH_ITEM               = 15;
    public static final int SHOPPING_CART_EMPY         = 16;
    public static final int ZERO_OR_NEGATIVE_COUNT_ERR = 17;
}
