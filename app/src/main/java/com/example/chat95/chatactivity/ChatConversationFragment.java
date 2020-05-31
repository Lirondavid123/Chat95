package com.example.chat95.chatactivity;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.chat95.utils.ConstantValues;
import com.example.chat95.R;
import com.example.chat95.data.ChatConversation;
import com.example.chat95.data.ChatMessage;
import com.example.chat95.databinding.FragmentChatConversationBinding;
import com.example.chat95.utils.DateUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatConversationFragment extends Fragment {
    private static final String TAG = "ChatConversationFragment";
    private FragmentChatConversationBinding binding;
    private NavController navController;
    private String chosenUid;
    private DatabaseReference dbRef;
    private String userName;
    private String photoUrl;
    private ChatConversation chosenChatConversation;
    private ChatViewModel chatViewModel;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> firebaseRecyclerAdapter;
    private boolean doesConversationExist;
    private String conversationId;
    private FirebaseFunctions mFunctions;
    private UsersViewModel mUsersViewModel;


    public ChatConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatConversationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //handle navigation components
        navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.chatToolbar, navController, appBarConfiguration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.chatConversationRecyclerview.setLayoutManager(linearLayoutManager);

// TODO: 28/05/2020 create appropriate cloud functions
//        mFunctions = FirebaseFunctions.getInstance();
        setListeners();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        mUsersViewModel = ViewModelProviders.of(getActivity()).get(UsersViewModel.class);
        chosenUid = mUsersViewModel.getUserId();
        photoUrl = mUsersViewModel.getChosenPhotoUrl();
        userName = mUsersViewModel.getUserName();
        initToolBar();
        binding.chatToolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        Bundle bundle = getArguments();

        if (!bundle.getBoolean("doesConversationExist")) {
            //user seleceted this conversation with search option,conversation might not exist
            doesConversationExist = false;
            checkIfConversationExists();
        } else {
            //user selected this conversation in the chat list, conversation exists
            doesConversationExist = true;
            getConversationDetails(chatViewModel.getChosenChatConversation().getValue());
        }
    }

    private void getConversationDetails(ChatConversation chatConversation) {
        Boolean isConversationApproved;
        chosenChatConversation = chatConversation;
        conversationId = chosenChatConversation.getConversationId();
        isConversationApproved = chosenChatConversation.isApproved();
        setConversationApproval(isConversationApproved, chosenChatConversation.getSender());
        if (isConversationApproved) {
            prepareDatabaseQuery();
        }

//        }
    }

    private void setStartConversationButton() {
        binding.chatConversationRecyclerview.setVisibility(View.INVISIBLE);
        binding.chatUserInput.setVisibility(View.GONE);
        binding.startConversationBtn.setVisibility(View.VISIBLE);
    }

    private void setConversationApproval(Boolean isConversationApproved, String sender) {
        //the user needs to approve/decline the conversation
        if (!isConversationApproved) {
            binding.chatUserInput.setVisibility(View.GONE);
            binding.approveMessageLayout.setVisibility(View.VISIBLE);
            if (!sender.equals(chosenUid)) {
                binding.approveBtn.setVisibility(View.GONE);
                binding.declineBtn.setVisibility(View.GONE);
                binding.infoAboutConversationApproval.setText("Waiting for the other side's approval");
            }
        }
    }

    private void checkIfConversationExists() {
        final DatabaseReference userConversationRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_CONVERSATIONS).child(ChatActivity.getFireBaseAuth().getUid());
        userConversationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //conversation does not exist
                if (!dataSnapshot.hasChild(chosenUid)) {
                    Log.d(TAG, "onDataChange: conversation does not exist");
                    doesConversationExist = false;
                    setStartConversationButton();
                } //conversation exists
                else {
                    doesConversationExist = true;
                    Log.d(TAG, "onDataChange: conversation exist, conversation id: " + dataSnapshot.child(chosenUid).getValue(ChatConversation.class).getConversationId());
                    getConversationDetails(dataSnapshot.child(chosenUid).getValue(ChatConversation.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolBar() {
        binding.chatConversationReceiverName.setText(userName);
        Glide.with(getActivity()).load(photoUrl).placeholder(R.drawable.empty_profile_image).into(binding.chatConversationReceiverPhoto);
    }

    private void setListeners() {

        binding.chatUserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.trim().equals("")) {
                    binding.chatConversationSendBtn.setVisibility(View.INVISIBLE);
                } else {
                    binding.chatConversationSendBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.chatConversationSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        binding.startConversationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "New conversation request sent, waiting for other's side approval", Toast.LENGTH_LONG).show();
                binding.startConversationBtn.setVisibility(View.GONE);
                binding.chatUserInput.setVisibility(View.GONE);
                binding.approveBtn.setVisibility(View.GONE);
                binding.declineBtn.setVisibility(View.GONE);
                binding.infoAboutConversationApproval.setText("Waiting for the other side's approval");
                binding.approveMessageLayout.setVisibility(View.VISIBLE);
// TODO: 31/05/2020 remove comment, this is for debug 
//                createNewConversationInDB();


/*                startNewChatConversation().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            binding.startConversationBtn.setVisibility(View.GONE);
                            binding.chatUserInput.setVisibility(View.GONE);
                            binding.approveMessageLayout.setVisibility(View.VISIBLE);
                            binding.infoAboutConversationApproval.setText("Waiting for the other side's approval");
                            Toast.makeText(getActivity(), "Conversation invitation sent! Waiting for the other side's approval...", Toast.LENGTH_LONG).show();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Log.d(TAG, "onComplete: task not succeful starting conversation " + errorMessage);
                            Toast.makeText(getActivity(), "sorry something went bad...try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });*/
            }
        });
        binding.approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.declineBtn.setEnabled(false);
                binding.approveBtn.setEnabled(false);
                // TODO: 29/05/2020 delete after adding the cloud function  
                //
                binding.approveMessageLayout.setVisibility(View.GONE);
                binding.chatUserInput.setVisibility(View.VISIBLE);
                binding.chatConversationSendBtn.setVisibility(View.VISIBLE);
                //
                // TODO: 28/05/2020 edit the cloud function approveChatConversation for this project
/*                approveChatConversation().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            binding.approveMessageLayout.setVisibility(View.GONE);
                            binding.chatUserInput.setVisibility(View.VISIBLE);
                            binding.chatConversationSendBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Conversation Approved! There's nothing like a fresh new conversation...", Toast.LENGTH_LONG).show();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Log.d(TAG, "onComplete: task not succeful approving conversation " + errorMessage);
                            Toast.makeText(getActivity(), "sorry something went bad...try again later", Toast.LENGTH_LONG).show();
                            binding.approveBtn.setEnabled(true);
                            binding.declineBtn.setEnabled(true);
                        }
                    }
                });*/
            }
        });
    }

    private Task<String> startNewChatConversation() {

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("senderId", ChatActivity.getFireBaseAuth().getUid());
        data.put("senderName", ChatActivity.getLoggedUser().getUserFullName());
        data.put("receiverName", userName);
        data.put("receiverId", chosenUid);

        return mFunctions
                .getHttpsCallable("approveChatConversation")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private Task<String> approveChatConversation() {

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("sender", chosenUid);
        data.put("senderName", userName);
        data.put("receiverName", ChatActivity.getLoggedUser().getUserFullName());

        return mFunctions
                .getHttpsCallable("approveChatConversation")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void sendMessage() {
        final String textMessage = binding.chatUserInput.getText().toString();
        binding.chatUserInput.setText("");

//        final Map<String, Object> childUpdates = new HashMap<>();
        if (!doesConversationExist) {
            Log.d(TAG, "sendMessage: conversation does not exist");
            createNewConversationInDB();
        } else {
            Log.d(TAG, "sendMessage: conversation exists");
            addMessage(textMessage, chosenChatConversation.getConversationId());
        }
    }

    private void addMessage(String textMessage, String conversationId) {
        ChatMessage chatMessage = new ChatMessage(textMessage,
                ChatActivity.getFireBaseAuth().getUid(),
                chosenUid,
                DateUtils.getCurrentTimeString());
        DatabaseReference messageRef = dbRef.child(ConstantValues.CHAT_MESSAGES).child(conversationId).push();

        messageRef.setValue(chatMessage);
        binding.chatConversationRecyclerview.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
    }

    //    private void createNewConversationInDB(final Map<String, Object> childUpdates, String textMessage) {
    private void createNewConversationInDB() {
        final Map<String, Object> childUpdates = new HashMap<>();
        dbRef = FirebaseDatabase.getInstance().getReference();
        conversationId = dbRef.child(ConstantValues.CHAT_MESSAGES).push().getKey();


/*        String newMessageId = dbRef.child(ConstantValues.CHAT_MESSAGES).child(conversationId).push().getKey();

        ChatMessage chatMessage = new ChatMessage(textMessage,
                ChatActivity.getLoggedUser().getUserId(),
                chosenUid,
                DateUtils.getCurrentTimeString());*/


        chosenChatConversation = new ChatConversation(conversationId,
                ChatActivity.getFireBaseAuth().getUid(),
                chosenUid,
                ChatActivity.getLoggedUser().getProfileImage(), false, ChatActivity.getLoggedUser().getUserFullName(), ChatActivity.getFireBaseAuth().getUid());


//        childUpdates.put(String.format("/%s/%s/%s", ConstantValues.CHAT_MESSAGES, conversationId, newMessageId), chatMessage);
        childUpdates.put(String.format("/%s/%s/%s", ConstantValues.CHAT_CONVERSATIONS, chosenUid, ChatActivity.getCurrentUser().getUid()), chosenChatConversation);

        ChatConversation chatConversation = new ChatConversation(conversationId,
                ChatActivity.getLoggedUser().getUserId(),
                chosenUid,
                photoUrl, false, userName, chosenUid);
        chatViewModel.setChosenChatConversation(chatConversation);
        childUpdates.put(String.format("/%s/%s/%s",
                ConstantValues.CHAT_CONVERSATIONS
                , ChatActivity.getCurrentUser().getUid()
                , chosenUid)
                , chatConversation);
        dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                doesConversationExist = true;
//                        prepareDatabaseQuery();
            }
        });
/*        DatabaseReference userPhotoRef = dbRef
                .child(ConstantValues.USERS)
                .child(chosenUid);*/
      /*  userPhotoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String photoUrl = (String) dataSnapshot.child(ConstantValues.PROFILE_PHOTO).getValue();
                String userName = String.format("%s %s", dataSnapshot.child(ConstantValues.USER_FIRST_NAME).getValue(), dataSnapshot.child(ConstantValues.USER_LAST_NAME).getValue());

                ChatConversation chatConversation = new ChatConversation(conversationId,
                        ChatActivity.getLoggedUser().getUserId(),
                        chosenUid,
                        photoUrl, false, userName, chosenUid);
                childUpdates.put(String.format("/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , ChatActivity.getCurrentUser().getUid()
                        , chosenUid)
                        , chatConversation);
                dbRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        doesConversationExist = true;
//                        prepareDatabaseQuery();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
    }

    private void prepareDatabaseQuery() {
        final Query chatMessagesRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_MESSAGES).child(conversationId);

        chatMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No messages to display.", Toast.LENGTH_LONG).show();
                    if (firebaseRecyclerAdapter != null) {
                        firebaseRecyclerAdapter.stopListening();
                    }
                } else {
                    displayMessagesList(binding.chatConversationRecyclerview, chatMessagesRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayMessagesList(final RecyclerView chatMessagesRecyclerView, Query chatMessagesRef) {
        FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions
                .Builder<ChatMessage>()
                .setQuery(chatMessagesRef, ChatMessage.class)
                .build();
        firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(options) {
            @NonNull
            @Override
            public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_node, parent, false);
                ChatMessageViewHolder viewHolder = new ChatMessageViewHolder(view);
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position, @NonNull final ChatMessage model) {

                holder.messageDate.setText(model.getTimeStamp());
                holder.textMessage.setText(model.getTextMessage());
                Drawable background;
                if (model.getSenderId().equals(ChatActivity.getFireBaseAuth().getUid())) {
                    background = getResources().getDrawable(R.drawable.message_form_blue);
                    holder.textMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    background = getResources().getDrawable(R.drawable.message_form_grey);
                    holder.textMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }
                holder.textMessage.setBackground(background);
            }

        };
        chatMessagesRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
        chatMessagesRecyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatViewModel.setChosenChatConversation(null);
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
        binding = null;
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageDate, textMessage;

        public ChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            messageDate = itemView.findViewById(R.id.message_date);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

}
