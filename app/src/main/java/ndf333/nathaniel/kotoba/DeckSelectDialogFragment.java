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
import java.util.regex.Pattern;

/**
 * Created by Nathaniel on 3/15/2018.
 */

public class DeckSelectDialogFragment extends DialogFragment {

    private static Context context;
    private static ArrayList<Deck> decks;
    private Deck toSend;

    public static DeckSelectDialogFragment newInstance(Context c, ArrayList<Deck> d) {
        DeckSelectDialogFragment dialog = new DeckSelectDialogFragment();

        context = c;

        decks = d;

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
                        Intent toStudy = new Intent(context, StudyActivity.class);
                        toStudy.putExtra("deck", decks.get(i));
                       // toStudy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(toStudy);
                    }
                });
        return builder.create();
    }

}
