package com.example.chat95.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.chat95.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {
    private static final ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();

    public static ForgotPasswordFragment getInstance() {
        return forgotPasswordFragment;
    }
    private EditText userEmailInput;
    private Button sendPasswordResetEmail;
    private LinearLayout linearLayout;
    private FirebaseAuth firebaseAuth;
    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendPasswordResetEmail=view.findViewById(R.id.forgot_password);
        userEmailInput=view.findViewById(R.id.password_reset_email_edittext);
        linearLayout=view.findViewById(R.id.forgot_password_layout);
        linearLayout.setAlpha(1);
        firebaseAuth= FirebaseAuth.getInstance();
        setListeners();

    }


    void setListeners(){
        sendPasswordResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==sendPasswordResetEmail.getId()){
                    String email = userEmailInput.getText().toString();
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Password reset email has been sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
