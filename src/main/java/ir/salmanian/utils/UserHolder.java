package ir.salmanian.utils;

import ir.salmanian.models.User;

public class UserHolder {
    private User user;
    private  static UserHolder instance;

    private UserHolder(){

    }

    public static UserHolder getInstance(){
        if(instance == null)
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
