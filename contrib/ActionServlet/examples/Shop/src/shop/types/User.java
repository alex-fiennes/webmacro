package shop.types;

/**
 * Holds customer/user data.
 */
public class User {
    public String userName;
    public Email email;
    public String firstName;
    public String surname;
    public String street;
    public String city;
    public String postalCode;
    public String state;

    public User(String userName,
                Email email,
                String firstName,
                String surname,
                String street,
                String city,
                String postalCode,
                String state) {
        this.userName = userName.trim();
        this.email = email;
        this.firstName = firstName.trim();
        this.surname = surname.trim();
        this.street = street.trim();
        this.city = city.trim();
        this.postalCode = postalCode.trim();
        this.state = state.trim();
    }
}
