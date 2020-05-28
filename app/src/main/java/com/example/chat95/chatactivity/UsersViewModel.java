package com.example.chat95.chatactivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat95.data.User;

public class UsersViewModel extends ViewModel {
    private static MutableLiveData<User> user = new MutableLiveData<>();
    private static String userId;
    private static String chosenPhotoUrl;
    private static String userName;

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        UsersViewModel.userId = userId;
    }

    public static String getChosenPhotoUrl() {
        return chosenPhotoUrl;
    }

    public static void setChosenPhotoUrl(String chosenPhotoUrl) {
        UsersViewModel.chosenPhotoUrl = chosenPhotoUrl;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        UsersViewModel.userName = userName;
    }
}
