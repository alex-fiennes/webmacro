package org.webmacro.servlet;
import java.util.*;

/**
 * A utility class for templates loaded into the context as "List" by ListTool. 
 * It allows template designers to work with Java arrays and lists using
 * without having to distinguish between them.
 * 
 * @author Keats Kirsch
 * @version 1.0
 * @since Oct. 2000
 * @see ListTool
 */
public class ListUtil {

  /**
   * Private constructor for a singleton class
   */
  private ListUtil(){};
  private static ListUtil _singleton = new ListUtil();

  /**
   * @return the singleton instance of this class
   */
  public static ListUtil getInstance(){ return _singleton; }
  
  /**
   * 
   * @param o
   * @return true if the argument implements the java.util.List interface, false otherwise.
   */
  public boolean isList(Object o){
    if (o == null) return false;
    return o instanceof List;
  }
  
  /**
   * @param o
   * @return true if the argument is an array, otherwise false.
   */
  public boolean isArray(Object o){
    if (o == null) return false;
    return o.getClass().isArray();
  }
 
  /**
   * @param arg
   * @return false if the argument is a list or an array with at least one element,
   * true otherwise.
   */
  public static boolean isEmpty(Object arg){
    if (arg==null) return true;
    if (arg instanceof List) return ((List)arg).isEmpty();
    if (arg instanceof Object[]) return ((Object[])arg).length == 0;
    if (arg instanceof Iterator) return ((Iterator)arg).hasNext();
    if (arg instanceof Enumeration) 
      return ((Enumeration)arg).hasMoreElements();
    return true;
  }
  
  /**
   * Returns a List for any Object.  The action taken depends on the 
   * type of the argument:
   * <ul>
   * <li>arg implements List: return the argument unchanged.</li>
   * <li>arg is an array: wrap using Arrays.asList().</li>
   * <li>arg is an Iterator or Enumeration: all the 
   * elements are copied into a new ArrayList.</li>
   *   <ul>
   *     <li>If the Iterator is a ListIterator it is reset to the beginning</li>
   *     <li>Otherwise Iterators and Enumerations are exhausted by this method.</li>
   *   </ul>
   * <li>arg is null: a new empty List is returned</li>
   * <li>arg is anything else: it is added to a new list</li>
   * </ul>
   * 
   * @param arg
   */
  public static List toList(Object arg){
    List list = null;
    if (arg instanceof List){
      list = (List)arg;
    } else if (arg == null){ // return an empty list
      list = Arrays.asList(new Object[0]);
    } else if (arg instanceof Object[]){
      list = Arrays.asList((Object[])arg);
    } else if (arg instanceof Iterator){
      list = new ArrayList();
      Iterator iter = (Iterator)arg;
      while (iter.hasNext()){
        list.add(iter.next());
      }
      if (arg instanceof ListIterator){
        ListIterator li = (ListIterator)arg;
        while (li.hasPrevious()) li.previous();
      }
    } else if (arg instanceof Enumeration){
      list = new ArrayList();
      Enumeration enum = (Enumeration)arg;
      while (enum.hasMoreElements()){
        list.add(enum.nextElement());
      }
    } else {
      Object[] oa = new Object[1];
      oa[0] = arg;
      list = Arrays.asList(oa);
    }
    return list;
  }
  
  /**
   * Allows access to elements in an array by position.  Index is zero based.
   * 
   * @param oa
   * @param pos
   */
  public static Object getItem(Object[] oa, int pos){
    if ((pos < 0) || ((pos - 1) > oa.length)) 
      throw new IndexOutOfBoundsException(
        "Index must be between 0 and " + (oa.length - 1)
        + ", user specified " + pos);
    return oa[pos];
  }
  
  /**
   * Allows access to elements in a List by position.  Index is zero based.
   * 
   * @param list
   * @param pos
   */
  public static Object getItem(List list, int pos){
    if (pos < 0 || (pos + 1) > list.size()) 
      throw new IndexOutOfBoundsException(
        "Index must be between 0 and " + (list.size() - 1)
        + ", user specified " + pos);
    return list.get(pos);
  }
  
  /**
   * @param oa
   * @return number of elements in array argument
   */
  public static int size(Object[] oa){
    return oa.length;
  }
  
  /**
   * @param list
   * @return number of elements in List argument
   */
  public static int size(List list){
    return list.size();
  }
  
  /**
   * @param list
   * @param o
   * @return true if List argument contains the object argument, else false
   */
  public static boolean contains(List list, Object o){
    return list.contains(o);
  }
  
  /**
   * @param oa
   * @param o
   * @return true if Array argument contains the object argument, else false
   */
  public static boolean contains(Object[] oa, Object o){
    return contains(Arrays.asList(oa), o);
  }
  
  /** test harness */
  public static void main(String[] args){
    ListUtil lu = ListUtil.getInstance();    
    Object[] arr = {
      "ant", "bird", "cat", "dog", "elephant", "ferret", "gopher"
    };
    ArrayList l = new ArrayList(Arrays.asList(arr));
    java.io.PrintWriter out = 
      new java.io.PrintWriter(System.out, true);
      
    out.println("List/Array results");
    out.print("toList(): "); 
    out.println(lu.toList(l) + "/" + lu.toList(arr));    
    out.print("size: ");
    out.println(lu.size(l) + "/" + lu.size(arr));
    out.print("contains(\"bird\"): ");
    out.println(lu.contains(l, "bird") + "/" + lu.contains(arr, "bird"));
    out.print("contains(\"fish\"): ");
    out.println(lu.contains(l, "fish") + "/" + lu.contains(arr, "fish"));
    out.print("isArray: ");
    out.println(lu.isArray(l) + "/" + lu.isArray(arr));
    out.print("isList: ");
    out.println(lu.isList(l) + "/" + lu.isList(arr));
    out.print("getItem(5): ");
    out.println(lu.getItem(l, 5) + "/" + lu.getItem(arr, 5));
    out.print("getItem(0): ");
    try {
      out.println(lu.getItem(l, 0) + "/" + lu.getItem(arr, 0));
    } catch (Exception e){
      out.println(e);
    }
    out.println("toList(null): " + lu.toList(null));
    out.println("toList(\"a string\"): " + lu.toList("a string"));
    
    StringTokenizer st = new StringTokenizer(
      "This is a bunch of words!");
    List l2 = lu.toList(st);
    out.println("toList(Enumeration): " + l2);
    Iterator iter = l2.listIterator();
    List l3 = lu.toList(iter);
    out.println("toList(Iterator): " + l3 + ", iter.hasNext(): " + iter.hasNext());
  }
}
