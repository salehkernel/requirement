package ir.salmanian.services;

import ir.salmanian.db.DatabaseManagement;
import ir.salmanian.models.User;
import ir.salmanian.utils.Cryptography;

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

    public User getUserByUsername(String username) {
        return DatabaseManagement.getInstance().findByUsername(username);
    }

    public void registerUser(User user) {
        user.setPassword(Cryptography.hash256(user.getPassword()));
        DatabaseManagement.getInstance().saveUser(user);
    }

    public boolean usernameExists(String username) {
        return DatabaseManagement.getInstance().findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        return DatabaseManagement.getInstance().findByEmail(email) != null;
    }

    public Integer login(String username, String password) {
        User user = DatabaseManagement.getInstance().findByUsername(username);
        if (user == null)
            return 0;
        if (user.getPassword().equals(Cryptography.hash256(password)))
            return 1;
        return -1;
    }


}
