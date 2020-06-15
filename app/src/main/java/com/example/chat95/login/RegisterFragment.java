package com.example.chat95.login;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chat95.R;
import com.example.chat95.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment {
    private static final RegisterFragment registerFragment = new RegisterFragment();
    private View view;

    public static RegisterFragment getInstance() {
        return registerFragment;
    }

    private RegisterFragment() {

    }

    private EditText firstName, lastName, email, confirmEmail, password, confirmPassword;
    private Button clearFields, createAccount;

    private String firstNameString;
    private String lastNameString;
    private String emailString;
    private String confirmEmailString;
    private String passwordString;
    private String confirmPasswordString;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef, usersListRef;
    private List<String> usersList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstName = (EditText) view.findViewById(R.id.firstName);
        lastName = (EditText) view.findViewById(R.id.lastName);
        email = (EditText) view.findViewById(R.id.usernameRegister);
        confirmEmail = (EditText) view.findViewById(R.id.confirmUsername);
        password = (EditText) view.findViewById(R.id.passwordRegister);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        clearFields = view.findViewById(R.id.clearFieldsRegister);
        createAccount = view.findViewById(R.id.createAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersList = new ArrayList<String>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Creating account... Please wait");

        setListeners();
    }


    private class ClickListener implements View.OnClickListener {
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.clearFieldsRegister:
                    clearFields();
                    break;
                case R.id.createAccount:

                    if (checkFields()) {
                        progressDialog.show();
                        createAccount.setEnabled(false);
                        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String userID = task.getResult().getUser().getUid();
                                            saveUserDetails(userID);
                                        } else {
                                            String errorMessage = task.getException().getMessage();
                                            if (view != null) {
                                                progressDialog.dismiss();
                                                createAccount.setEnabled(true);
                                            }
                                            Toast.makeText(getActivity(), "Could not register: " + errorMessage, Toast.LENGTH_LONG).show();

                                        }

                                    }


                                });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void saveUserDetails(String userID) {
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        firstNameString = firstName.getText().toString();
        lastNameString = lastName.getText().toString();
        emailString = email.getText().toString();

        User newUser = new User(userID, firstNameString, lastNameString, emailString, "");

        userRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Registered successfully\nWelcome!", Toast.LENGTH_LONG).show();
                    }
                    if (view != null) {
                        progressDialog.dismiss();
                        getFragmentManager().popBackStack();
                    }

                } else {
                    String errorMessage = task.getException().getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Could not fill details: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                    if(view!=null){
                        progressDialog.dismiss();
                    }
                    createAccount.setEnabled(true);
                }
            }
        });
    }

    private boolean checkFields() {
        Toast.makeText(getActivity(), "chekc fields", Toast.LENGTH_SHORT);

        Boolean isFieldsValid = false;
        String firstNameString = firstName.getText().toString();
        String lastNameString = lastName.getText().toString();
        String emailString = email.getText().toString();
        String confirmEmailString = confirmEmail.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        isFieldsValid = true;
        if (firstNameString.equals("")) {
            firstName.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (lastNameString.equals("")) {
            lastName.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (emailString.equals("")) {
            email.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (confirmEmailString.equals("")) {
            confirmEmail.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (passwordString.length() < 8) {
            password.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (confirmPasswordString.length() < 8) {
            confirmPassword.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (!emailString.equals(confirmEmailString)) {
            confirmEmail.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (!passwordString.equals(confirmPasswordString)) {
            confirmPassword.setBackground(getResources().getDrawable(R.drawable.incorrect_input_register));
            isFieldsValid = false;
        }
        if (isFieldsValid == false)
            Toast.makeText(getActivity(), "There are incorrect fields", Toast.LENGTH_SHORT).show();


        return isFieldsValid;
    }

    private void clearFields() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        confirmEmail.setText("");
        password.setText("");
        confirmPassword.setText("");
    }

    public class RegisterTextWatcher implements android.text.TextWatcher {
        private int chosenEditText;

        public RegisterTextWatcher(int chosenEditText) {
            super();
            this.chosenEditText = chosenEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (chosenEditText == firstName.getId()) {
                if (firstName.getText().length() > 0) {
                    firstName.setBackground(getResources().getDrawable(R.drawable.input_field));
                }

            } else if (chosenEditText == lastName.getId()) {
                if (lastName.getText().length() > 0) {
                    lastName.setBackground(getResources().getDrawable(R.drawable.input_field));
                }

            } else if (chosenEditText == email.getId()) {
                if (email.getText().length() > 0) {
                    email.setBackground(getResources().getDrawable(R.drawable.input_field));
                }

            } else if (chosenEditText == confirmEmail.getId()) {
                if ((confirmEmail.getText().toString().equals(email.getText().toString())) && (confirmEmail.getText().length() > 0)) {
                    confirmEmail.setBackground(getResources().getDrawable(R.drawable.input_field));
                }
            } else if (chosenEditText == password.getId()) {

                //if (password.getBackground().equals(getResources().getDrawable(R.drawable.incorrect_input_register))) {
                if (password.getText().length() > 7) {
                    password.setBackground(getResources().getDrawable(R.drawable.input_field));
                }
            } else if (chosenEditText == confirmPassword.getId()) {
                if ((confirmPassword.getText().length() > 7) && (confirmPassword.getText().toString().equals(password.getText().toString()))) {
                    confirmPassword.setBackground(getResources().getDrawable(R.drawable.input_field));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void setListeners() {
        View.OnClickListener clickListener = new ClickListener();
        clearFields.setOnClickListener(clickListener);
        createAccount.setOnClickListener(clickListener);
        firstName.addTextChangedListener(new RegisterTextWatcher(firstName.getId()));
        lastName.addTextChangedListener(new RegisterTextWatcher(lastName.getId()));
        email.addTextChangedListener(new RegisterTextWatcher(email.getId()));
        confirmEmail.addTextChangedListener(new RegisterTextWatcher(confirmEmail.getId()));
        password.addTextChangedListener(new RegisterTextWatcher(password.getId()));
        confirmPassword.addTextChangedListener(new RegisterTextWatcher(confirmPassword.getId()));


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoginFragment loginFragment = LoginFragment.getInstance();
        loginFragment.setRegisterValue(true);
    }

}
