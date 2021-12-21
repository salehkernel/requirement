package ir.salmanian.utils;

import ir.salmanian.models.User;

/**
 * UserHolder class is used to store User object in memory when authentication occurred
 * in {@link ir.salmanian.controllers.LoginController} in order to be used in future required
 * times and positions like adding new project or searching in user projects.
 * This class uses singleton design pattern.
 */
public class UserHolder {
    private User user;
    private static UserHolder instance;

    private UserHolder() {

    }

    public static UserHolder getInstance() {
        if (instance == null)
            instance = new UserHolder();
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
