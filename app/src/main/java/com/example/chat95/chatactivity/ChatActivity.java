package com.example.chat95.chatactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.chat95.R;
import com.example.chat95.data.User;
import com.example.chat95.databinding.ActivityChatBinding;
import com.example.chat95.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static User loggedUser;
    private ActivityChatBinding binding;
    private static NavController navController;
    private static DatabaseReference currentUserDatabaseRef;
    private static FirebaseUser currentUser;
    public static Intent callingIntent;
    private ValueEventListener userDetailsListener;
    private static UsersViewModel mViewModel;
    private static FirebaseAuth fireBaseAuth;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fireBaseAuth = FirebaseAuth.getInstance();
        currentUser = fireBaseAuth.getCurrentUser();
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        callingIntent=getIntent();

        navController = Navigation.findNavController(this, R.id.chat_nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        mViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "inside on Start");
        if (fireBaseAuth.getCurrentUser()== null) {    // if the user is not logged in
            sendUserToLogin();
        }
        //
        else {

            currentUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(fireBaseAuth.getUid());

            userDetailsListener = currentUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loggedUser = dataSnapshot.getValue(User.class);
                    mViewModel.setUser(loggedUser);
                    String myProfileImage = loggedUser.getProfileImage();
                    loggedUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }
    }
                public void sendUserToLogin() {
                    Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUserDatabaseRef != null && userDetailsListener != null) {
            currentUserDatabaseRef.removeEventListener(userDetailsListener);
        }
    }
    public static User getLoggedUser() {
        return loggedUser;
    }

    public static UsersViewModel getmViewModel() {
        return mViewModel;
    }

    public static void setmViewModel(UsersViewModel mViewModel) {
        ChatActivity.mViewModel = mViewModel;
    }
    public static FirebaseAuth getFireBaseAuth() {
        return fireBaseAuth;
    }

    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
