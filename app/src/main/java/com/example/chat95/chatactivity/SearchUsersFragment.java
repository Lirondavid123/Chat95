package com.example.chat95.chatactivity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.chat95.R;
import com.example.chat95.data.User;
import com.example.chat95.utils.ConstantValues;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchUsersFragment extends Fragment {
    private static UsersViewModel mViewModel;
    private RecyclerView searchedUsersRecycler;
    static FirebaseRecyclerAdapter<User, SearchedUsersViewHolder> firebaseRecyclerAdapter;
    private Query query;
    private EditText searchBar;
    private Toolbar toolbar;

    public SearchUsersFragment() {
    }

    public static SearchUsersFragment newInstance() {
        return new SearchUsersFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchedUsersRecycler = view.findViewById(R.id.searched_users_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        searchedUsersRecycler.setLayoutManager(linearLayoutManager);
        searchBar = view.findViewById(R.id.search_bar);
        setBarListener();
        searchBar.setText("");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("SearchUsersFragment", "On Stop");
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    void setBarListener() {
        Log.d("SearchUsersFragment", "Set Bar Listener");
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString().equals(""))) {

                    if (firebaseRecyclerAdapter != null) {
                        firebaseRecyclerAdapter.stopListening();
                    }
                    query = FirebaseDatabase.getInstance().getReference().child(ConstantValues.USERS)
                            .orderByChild("userFirstName").startAt(s.toString()).endAt(s.toString() + "\uf8ff");

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChildren()) {
                                //Toast.makeText(getActivity(), "no matches were found", Toast.LENGTH_SHORT).show();
                            } else {
                                setSearchedUsersList(query);

                                //Toast.makeText(getActivity(), query.getRef().get, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }


    public void setSearchedUsersList(Query query) {
        //Toast.makeText(getActivity(), "setSearchedUsersList", Toast.LENGTH_SHORT).show();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions
                .Builder<User>()
                .setQuery(query, User.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, SearchedUsersViewHolder>(options) {
            @NonNull
            @Override
            public SearchedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("SearchUsersFragment", "On Create view holder");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_in_recycler, parent, false);
                SearchedUsersViewHolder viewHolder = new SearchedUsersViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull SearchedUsersViewHolder holder, int position, @NonNull final User model) {
                //holder.userNode = model;
                Log.d("SearchUsersFragment", "On bind");
                if (model == null) {
                } else {
                }
                Glide.with(getActivity()).load(model.getProfileImage()).placeholder(R.drawable.empty_profile_image).into(holder.userPhoto);
                holder.userName.setText(model.getUserFirstName() + " " + model.getUserLastName());
                final boolean isCurrentUserNode=model.getUserId().equals(ChatActivity.getFireBaseAuth().getUid());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!isCurrentUserNode){
                                openChatConversation(model);}
                            else {
                                Toast.makeText(getContext(), "Cannot open a conversation with yourself", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
        };
        searchedUsersRecycler.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    private void openChatConversation(User model) {
        mViewModel.setUserId(model.getUserId());
        mViewModel.setChosenPhotoUrl(model.getProfileImage());
        mViewModel.setUserName(model.getUserFullName());
        Bundle bundle = new Bundle();
        bundle.putBoolean("doesConversationExist", false);
        Navigation.findNavController(getView()).navigate(R.id.chatConversationFragment, bundle);
    }


    public static class SearchedUsersViewHolder extends RecyclerView.ViewHolder {
        User userNode;
        ImageView userPhoto;
        TextView userName;

        public SearchedUsersViewHolder(@NonNull final View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.searched_user_profile_image);
            userName = itemView.findViewById(R.id.searched_user_user_name);
        }
    }
}
