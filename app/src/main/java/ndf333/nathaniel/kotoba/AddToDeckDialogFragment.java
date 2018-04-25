package ndf333.nathaniel.kotoba;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Nathaniel on 3/15/2018.
 */

public class AddToDeckDialogFragment extends DialogFragment {

    private static ArrayList<Deck> decks;
    private static Card toAdd;

    public static AddToDeckDialogFragment newInstance(ArrayList<Deck> d, Card card) {
        AddToDeckDialogFragment dialog = new AddToDeckDialogFragment();

        decks = d;

        toAdd = card;

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] names = new String[decks.size()];

        for (int i = 0; i < decks.size(); i++) {
            names[i] = decks.get(i).deckName;
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View theView = inflater.inflate(R.layout.deckselect_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCustomTitle(theView)
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //add the new card to this deck
                        decks.get(i).add(toAdd);

                        //get dbase reference
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(cleanEmail).child("userDecks");

                        //update firebase
                        HashMap<String, Object> updates = new HashMap<String, Object>();
                        updates.put("cards", decks.get(i).cards);
                        updates.put("numCards", decks.get(i).numCards);
                        ref.child(decks.get(i).deckName).updateChildren(updates);
                    }
                });
        return builder.create();
    }

}
