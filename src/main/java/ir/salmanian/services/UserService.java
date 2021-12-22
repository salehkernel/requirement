package ir.salmanian.services;

import ir.salmanian.db.DatabaseManagement;
import ir.salmanian.models.User;
import ir.salmanian.utils.Cryptography;

/**
 * A service layer class for handling users.
 * this class uses singleton design pattern.
 */
public class UserService {
    private static UserService instance;

    private UserService() {

    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * This method is used to find the user by his/her username
     * @param username the intended username
     * @return the User instance whose username is equal to intended username, null if the username
     * is not exist
     */
    public User getUserByUsername(String username) {
        return DatabaseManagement.getInstance().findByUsername(username);
    }

    /**
     * This method is used to register a new user
     * @param user the new user
     */
    public void registerUser(User user) {
        user.setPassword(Cryptography.hash256(user.getPassword()));
        DatabaseManagement.getInstance().saveUser(user);
    }

    /**
     * This method is used to check if a username exist or not.
     * @param username the intended username
     * @return true if the username exist, false otherwise
     */
    public boolean usernameExists(String username) {
        return DatabaseManagement.getInstance().findByUsername(username) != null;
    }

    /**
     * This method is used to check if an email exist or not.
     * @param email the intended email address.
     * @return true if the email address exist, false otherwise
     */
    public boolean emailExists(String email) {
        return DatabaseManagement.getInstance().findByEmail(email) != null;
    }

    /**
     * This method is used to authenticate users.
     * @param username
     * @param password
     * @return return 1 if username and password are correct, 0 if the username does not exist
     * and -1 otherwise.
     */
    public Integer login(String username, String password) {
        User user = DatabaseManagement.getInstance().findByUsername(username);
        if (user == null)
            return 0;
        if (user.getPassword().equals(Cryptography.hash256(password)))
            return 1;
        return -1;
    }


}
