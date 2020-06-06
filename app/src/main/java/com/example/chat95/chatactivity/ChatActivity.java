package com.example.chat95.chatactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.chat95.R;
import com.example.chat95.cryptology.Des;
import com.example.chat95.cryptology.KeyGenerator;
import com.example.chat95.cryptology.TestDes;
import com.example.chat95.cryptology.TextSplitter;
import com.example.chat95.data.User;
import com.example.chat95.databinding.ActivityChatBinding;
import com.example.chat95.login.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static User loggedUser;
    private ActivityChatBinding binding;
    private static NavController navController;
    private static DatabaseReference currentUserDatabaseRef;
    private static FirebaseUser currentUser;
    public static Intent callingIntent;
    private ValueEventListener userDetailsListener;
    private static UsersViewModel usersViewModel;
    private static FirebaseAuth fireBaseAuth;
    private ImageButton search_users_button;
    private ChatViewModel mChatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseApp.initializeApp(this);
        fireBaseAuth = FirebaseAuth.getInstance();
        currentUser = fireBaseAuth.getCurrentUser();
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        callingIntent=getIntent();


        navController = Navigation.findNavController(this, R.id.chat_nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        mChatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart: toHextString Test:  "+Long.toHexString());
        LocalDataBase.setMyDAO(AppDatabase.getAppDatabase(getApplicationContext()).ConversationDAO());
        String decrypted;
        Des des=new Des();
        String resultText;
/*        String text = " naor ohana   or magogi  ... 10 ";
        //String text= des.convertAsciiToHex("OrMag  B");
        String key = "AABB09182736CCDD";
        resultText=des.encrypt(text,key);
        Log.d(TAG, "onStart: encrypt: encryptedText (in hex): "+resultText);


        decrypted=des.decrypt(resultText,key);
        Log.d(TAG, "onStart: decryptedText: "+decrypted);*/

        if (fireBaseAuth.getCurrentUser()== null) {    // if the user is not logged in
            sendUserToLogin();
        }
        //
        else {
            search_users_button=findViewById(R.id.search_users_button);
            setListeners();

            currentUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(fireBaseAuth.getUid());

            userDetailsListener = currentUserDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loggedUser = dataSnapshot.getValue(User.class);
                    usersViewModel.setUser(loggedUser);
                    String myProfileImage = loggedUser.getProfileImage();
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

    public static UsersViewModel getUsersViewModel() {
        return usersViewModel;
    }

    public static void setUsersViewModel(UsersViewModel usersViewModel) {
        ChatActivity.usersViewModel = usersViewModel;
    }
    public static FirebaseAuth getFireBaseAuth() {
        return fireBaseAuth;
    }

    public static FirebaseUser getCurrentUser() {
        return currentUser;
    }

    void setListeners(){
/*        search_users_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_chatListFragment_to_searchUsersFragment);
            }
        });*/
    }


}
