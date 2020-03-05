package com.example.chung_pike;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CrisisDialogFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String msg = "Hypertensive Crisis! Consult your doctor immediately!";

        builder.setMessage(msg)
                .setPositiveButton(R.string.accept_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // user cast your vote
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
