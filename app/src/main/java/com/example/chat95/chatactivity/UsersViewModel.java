package com.example.chat95.chatactivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat95.data.User;

public class UsersViewModel extends ViewModel {
    private static MutableLiveData<User> user = new MutableLiveData<>();


    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

}
