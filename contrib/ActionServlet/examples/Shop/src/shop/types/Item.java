package shop.types;

/**
 * Holds item data.
 */
public class Item {
    /** Item name.*/
    public String name;

    /** Item count.*/
    public int count;

    /** Unit price.*/
    public double price;

    public Item(String name, int count, double price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }
}
