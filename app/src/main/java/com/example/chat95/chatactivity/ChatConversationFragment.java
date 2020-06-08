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
import com.example.chat95.R;
import com.example.chat95.cryptology.Des;
import com.example.chat95.cryptology.KeyGenerator;
import com.example.chat95.cryptology.Rsa;
import com.example.chat95.data.ChatConversation;
import com.example.chat95.data.ChatMessage;
import com.example.chat95.data.Keys;
import com.example.chat95.data.PrivateKey;
import com.example.chat95.data.PublicKey;
import com.example.chat95.databinding.FragmentChatConversationBinding;
import com.example.chat95.utils.ConstantValues;
import com.example.chat95.utils.DateUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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

    private static String symmetricKey;
    private static PrivateKey privateKey;
    private static PublicKey foreignPublicKey;
    private DatabaseReference chatMessagesRef;
    private ValueEventListener approvedListener;
    private ValueEventListener messagesListener;


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


    /**
     * called when there is a certain existing conversation,that has been approved or not yet, by two users
     * isConversationPartiallyApproved- indicates if the conversation was approved by one user
     * isConversationTotallyApproved- indicates if the conversation was approved by both of the users, with emphasize on local storage
     *
     * @param chatConversation
     */
    private void getConversationDetails(ChatConversation chatConversation) {
        Boolean isConversationPartiallyApproved, isConversationTotallyApproved;
        chosenChatConversation = chatConversation;
        conversationId = chosenChatConversation.getConversationId();

        isConversationPartiallyApproved = chosenChatConversation.getApproved();
        if (isConversationPartiallyApproved) {
            Log.d(TAG, "getConversationDetails: conversation was partially approved");
            ConversationEntity conversationEntity = LocalDataBase.retrieveConversationData(conversationId);
            isConversationTotallyApproved = conversationEntity.isApproved();
            if (!isConversationTotallyApproved) {
                updateLocalDB(chatConversation);
                deleteKeysFromDB();
            } else {
                Log.d(TAG, "getConversationDetails: conversation was fully approved");
                updateCryptoConversationValues(conversationEntity.getSymmetricKey()
                        , new PublicKey(conversationEntity.getForeignE(), conversationEntity.getForeignN()),
                        new PrivateKey(conversationEntity.getP(), conversationEntity.getQ(), conversationEntity.getD()));
            }
            prepareDatabaseQuery();
        } else {
            Log.d(TAG, "getConversationDetails: conversation is not approved");
            setConversationApproval(isConversationPartiallyApproved, chosenChatConversation.getSender());
            //if this user is the sender, wait for the other user's approval
            if (chosenChatConversation.getSender().equals(ChatActivity.getFireBaseAuth().getUid())) {
                Log.d(TAG, "getConversationDetails: set approval listener");
                setAprrovalListener();
            }
        }
    }

    private void setAprrovalListener() {
        approvedListener = dbRef.child(ConstantValues.CHAT_CONVERSATIONS)
//        dbRef.child(ConstantValues.CHAT_CONVERSATIONS)
                .child(ChatActivity.getFireBaseAuth().getUid())
                .child(chosenUid).
                        child(ConstantValues.APPROVED)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onChildChanged: ");
                        if ((Boolean) dataSnapshot.getValue()) {
                            Log.d(TAG, "onChildChanged: before retrievedatafromDB");
                            retrieveConversationFromDB();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
               /* .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((Boolean) dataSnapshot.child(ConstantValues.APPROVED).getValue() == true) {
                            Log.d(TAG, "onDataChange: user approved conversation");
                            updateLocalDB(dataSnapshot.getValue(ChatConversation.class));

                            deleteKeysFromDB();

                            if (binding != null) {
                                Log.d(TAG, "onDataChange: prepare database query after user approved");
                                prepareUserInputField();
                                prepareDatabaseQuery();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
    }

    private void retrieveConversationFromDB() {
        dbRef.child(ConstantValues.CHAT_CONVERSATIONS)
                .child(ChatActivity.getFireBaseAuth().getUid())
                .child(chosenUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((Boolean) dataSnapshot.child(ConstantValues.APPROVED).getValue() == true) {
                    Log.d(TAG, "onDataChange: user approved conversation");
                    updateLocalDB(dataSnapshot.getValue(ChatConversation.class));
                    deleteKeysFromDB();

                    if (binding != null) {
                        Log.d(TAG, "onDataChange: prepare database query after user approved");
                        prepareUserInputField();
                        displayMessagesList();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteKeysFromDB() {
        //delete keys from firebase database
        HashMap childUpdates = new HashMap();
        childUpdates.put(String.format("/%s/%s/%s/%s",
                ConstantValues.CHAT_CONVERSATIONS
                , ChatActivity.getFireBaseAuth().getUid()
                , chosenUid, "publicKey")
                , null);
        childUpdates.put(String.format("/%s/%s/%s/%s",
                ConstantValues.CHAT_CONVERSATIONS
                , chosenUid
                , ChatActivity.getFireBaseAuth().getUid(), "publicKey")
                , null);
        childUpdates.put(String.format("/%s/%s/%s/%s",
                ConstantValues.CHAT_CONVERSATIONS
                , chosenUid
                , ChatActivity.getFireBaseAuth().getUid(), "kic")
                , null);

        childUpdates.put(String.format("/%s/%s/%s/%s",
                ConstantValues.CHAT_CONVERSATIONS
                , ChatActivity.getFireBaseAuth().getUid()
                , chosenUid, "kic")
                , null);
        dbRef.updateChildren(childUpdates);
    }

    private void updateLocalDB(ChatConversation chatConversation) {
        String KIC = chatConversation.getKic();
        PublicKey foreignPublicKey = chatConversation.getPublicKey();
        String symmetricKey;
        ConversationEntity conversationEntity = LocalDataBase.retrieveConversationData(conversationId);
        PrivateKey privateKey = new PrivateKey(conversationEntity.getP(), conversationEntity.getQ(), conversationEntity.getD());
        symmetricKey = Rsa.decrypt(KIC, privateKey);
        //save the final conversation data to local database
        LocalDataBase.updateFinalConversationData(conversationId, foreignPublicKey, symmetricKey, true);
        updateCryptoConversationValues(symmetricKey, foreignPublicKey, privateKey);
    }

    private void updateCryptoConversationValues(String symmetricKey, PublicKey foreignPublicKey, PrivateKey privateKey) {
        ChatConversationFragment.symmetricKey = symmetricKey;
        ChatConversationFragment.foreignPublicKey = foreignPublicKey;
        ChatConversationFragment.privateKey = privateKey;
        Log.d(TAG, String.format("updateCryptoConversationValues: symmetricKey = %s foreignPublicKey = %s  privateKey = %s", symmetricKey, foreignPublicKey, privateKey));
    }


    private void prepareUserInputField() {
        binding.chatUserInput.setVisibility(View.VISIBLE);
        binding.chatConversationRecyclerview.setVisibility(View.VISIBLE);
        binding.approveMessageLayout.setVisibility(View.GONE);
    }

    private void setStartConversationButton() {
//        binding.chatConversationRecyclerview.setVisibility(View.INVISIBLE);
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
//                checkIfConversationExists();
                Toast.makeText(getContext(), "New conversation request sent, waiting for other's side approval", Toast.LENGTH_LONG).show();
                binding.startConversationBtn.setVisibility(View.GONE);
                binding.chatUserInput.setVisibility(View.GONE);
                binding.approveBtn.setVisibility(View.GONE);
                binding.declineBtn.setVisibility(View.GONE);
                binding.infoAboutConversationApproval.setText("Waiting for the other side's approval");
                binding.approveMessageLayout.setVisibility(View.VISIBLE);
                createNewConversationInDB();
            }
        });

        binding.approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.declineBtn.setEnabled(false);
                binding.approveBtn.setEnabled(false);
                // TODO: 01/06/2020 crypto- replace temporary methods with real ones
                foreignPublicKey = chosenChatConversation.getPublicKey();
                symmetricKey = KeyGenerator.generateKey(48);
                String KIC = Rsa.encrypt(symmetricKey, foreignPublicKey);

                Keys keys = Rsa.createKeys();
                privateKey = keys.getPrivateKey();
                PublicKey publicKey = keys.getPublicKey();
                //save keys to local database
                LocalDataBase.InsertConversationData(new ConversationEntity(conversationId, publicKey.getE()
                        , publicKey.getN()
                        , privateKey.getD(), privateKey.getP(), privateKey.getQ(), symmetricKey, foreignPublicKey.getE(), foreignPublicKey.getN(), true));

                HashMap childUpdates = new HashMap();
                childUpdates.put(String.format("/%s/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , ChatActivity.getFireBaseAuth().getUid()
                        , chosenUid, "publicKey")
                        , publicKey);
                childUpdates.put(String.format("/%s/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , ChatActivity.getFireBaseAuth().getUid()
                        , chosenUid, "kic")
                        , KIC);
                childUpdates.put(String.format("/%s/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , chosenUid
                        , ChatActivity.getFireBaseAuth().getUid(), "kic")
                        , KIC);
                childUpdates.put(String.format("/%s/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , chosenUid
                        , ChatActivity.getFireBaseAuth().getUid(), "publicKey")
                        , publicKey);
                childUpdates.put(String.format("/%s/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , ChatActivity.getFireBaseAuth().getUid()
                        , chosenUid, ConstantValues.APPROVED)
                        , true);
                childUpdates.put(String.format("/%s/%s/%s/%s",
                        ConstantValues.CHAT_CONVERSATIONS
                        , chosenUid
                        , ChatActivity.getFireBaseAuth().getUid(), ConstantValues.APPROVED)
                        , true);
                dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(getContext(), "Conversation has been approved! say hi to your new pal", Toast.LENGTH_SHORT).show();
                        if (binding != null) {
                            binding.approveMessageLayout.setVisibility(View.GONE);
                            binding.chatUserInput.setVisibility(View.VISIBLE);
                            binding.chatConversationSendBtn.setVisibility(View.VISIBLE);
                            prepareDatabaseQuery();
                        }
                    }
                });
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

        // TODO: 02/06/2020 crypto, check if correct
        String cipherText = Des.encrypt(textMessage, symmetricKey);
        String signature = Rsa.signature(textMessage, privateKey);


        ChatMessage chatMessage = new ChatMessage(cipherText,
                ChatActivity.getFireBaseAuth().getUid(),
                chosenUid,
                DateUtils.getCurrentTimeString(), signature);
        //

//                    firebaseRecyclerAdapter.getSnapshots().

        DatabaseReference messageRef = dbRef.child(ConstantValues.CHAT_MESSAGES).child(conversationId).push();

        messageRef.setValue(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (firebaseRecyclerAdapter == null) {
                    prepareDatabaseQuery();
                }
            }
        });
        if (firebaseRecyclerAdapter != null) {
            binding.chatConversationRecyclerview.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
        }
    }

    private void createNewConversationInDB() {
        final Map<String, Object> childUpdates = new HashMap<>();
        dbRef = FirebaseDatabase.getInstance().getReference();
        conversationId = dbRef.child(ConstantValues.CHAT_MESSAGES).push().getKey();

        // TODO: 02/06/2020 crypto, check if correct
        Keys keys = Rsa.createKeys();
        PrivateKey privateKey = keys.getPrivateKey();
        PublicKey publicKey = keys.getPublicKey();
        //
        chosenChatConversation = new ChatConversation(publicKey, conversationId,
                ChatActivity.getFireBaseAuth().getUid(),
                chosenUid,
                ChatActivity.getLoggedUser().getProfileImage(),
                false,
                ChatActivity.getLoggedUser().getUserFullName(),
                ChatActivity.getFireBaseAuth().getUid(), "");

        childUpdates.put(String.format("/%s/%s/%s", ConstantValues.CHAT_CONVERSATIONS, chosenUid, ChatActivity.getCurrentUser().getUid()), chosenChatConversation);

        ChatConversation chatConversation = new ChatConversation(publicKey, conversationId,
                ChatActivity.getLoggedUser().getUserId(),
                chosenUid,
                photoUrl, false, userName, chosenUid, "");
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
                Log.d(TAG, "onSuccess: setApprovalListener");
                setAprrovalListener();
//                        prepareDatabaseQuery();
            }
        });
        // TODO: 02/06/2020 crypto, check if correct
        //save keys to local database
        LocalDataBase.InsertConversationData(new ConversationEntity(conversationId, publicKey.getE()
                , publicKey.getN()
                , privateKey.getD(), privateKey.getP(), privateKey.getQ(), null, null, null, false));
        //
    }

    private void prepareDatabaseQuery() {

        chatMessagesRef = FirebaseDatabase.getInstance().getReference()
                .child(ConstantValues.CHAT_MESSAGES).child(conversationId);
        if (approvedListener != null) {
            dbRef.removeEventListener(approvedListener);
        }
        if (messagesListener != null) {
            dbRef.removeEventListener(messagesListener);
        }
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
                String textMessage = Des.decrypt(model.getTextMessage(), symmetricKey);
                boolean isVerified = Rsa.verify(textMessage, model.getSignature(), foreignPublicKey);
                if (isVerified) {
                    holder.messageDate.setText(model.getTimeStamp());
                    holder.textMessage.setText(textMessage);
                    Drawable background;
                    if (model.getSenderId().equals(ChatActivity.getFireBaseAuth().getUid())) {
                        background = getResources().getDrawable(R.drawable.message_form_blue);
                        holder.textMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    } else {
                        background = getResources().getDrawable(R.drawable.message_form_grey);
                        holder.textMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    }
                    holder.textMessage.setBackground(background);
                } else {
                    weAreAngry();
                }
            }
        };
        checkForMessages();

    }

    private void checkForMessages() {
        chatMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), "No messages to display.", Toast.LENGTH_LONG).show();
                } else {
                    displayMessagesList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayMessagesList() {
        binding.chatConversationRecyclerview.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        if (firebaseRecyclerAdapter.getItemCount() >= 1)
            binding.chatConversationRecyclerview.scrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
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

    private void weAreAngry() {
        Toast.makeText(getContext(), "Conversation was hacked!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dbRef != null && approvedListener != null) {
//            dbRef.removeEventListener(approvedListener);
        }
    }
}
