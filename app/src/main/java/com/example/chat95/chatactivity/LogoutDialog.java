package com.example.chat95.chatactivity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


import com.example.chat95.R;
import com.example.chat95.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoutDialog extends DialogFragment {
    FirebaseAuth firebaseAuth;

    public LogoutDialog() {
        // Required empty public constructor
    }

    public LogoutDialog(String title) {
        Bundle args = new Bundle();
        args.putString("TitleKey", title);
        setArguments(args);
        firebaseAuth = FirebaseAuth.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout_dialog, container, false);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        View myLayout = getActivity().getLayoutInflater().inflate(R.layout.fragment_logout_dialog, null);
        Bundle bndl = getArguments();
        String title = bndl.getString("TitleKey", "Logging out");
        dlg.setTitle(title);
        dlg.setView(myLayout);
        dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //android.os.Process.killProcess(android.os.Process.myPid());
                firebaseAuth.signOut();
                //(MainActivity)getActivity().
                Intent intent = new Intent((ChatActivity) getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        dlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //handle cancel situation...
            }
        });
        //edtName = myLayout.findViewById(R.id.edName);
        return dlg.create();
    }

}
