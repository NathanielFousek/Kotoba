package ndf333.nathaniel.kotoba;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ViewDecksActivity extends LaunchScreen implements CreateDeckDialogFragment.NoticeDialogListener{


    //in this activity, a user can view their decks, and click one to go to a detailed deck activity, where they can view all cards in this deck
    //and add cards to the deck.

    private RecyclerView rv;
    private DeckAdapter theAdapter;
    private DatabaseReference dbase;
    private FirebaseUser user;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);
        super.onCreateDrawer();

        dbase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");
        ref = FirebaseDatabase.getInstance().getReference(cleanEmail).child("userDecks");

        FloatingActionButton fab = findViewById(R.id.DeckFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDeckClicked(view);
            }
        });


        rv = (RecyclerView) findViewById(R.id.viewDecks);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());

        theAdapter = new DeckAdapter(this, rv);

        rv.setAdapter(theAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                manager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
    }

    private void readUserDecks() {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Deck deck = d.getValue(Deck.class);
                    theAdapter.add(deck);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void addDeckClicked(View theView) {

        DialogFragment newFragment = new CreateDeckDialogFragment();
        newFragment.show(getFragmentManager(), "create");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String newName, String newDescription) {
        View dialogView = dialog.getView();

        Deck newDeck = new Deck(newName, newDescription);

        theAdapter.add(newDeck);
    }

    //save everything to the database.
    protected void onStop() {
        super.onStop();

        for (final Deck d: theAdapter.getAll()) {
            final String name = d.deckName;

            ref.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //existing deck
                        Log.d("Kotoba", "this deck was already here: " + name);
                        //we don't need to do anything!
                    } else {
                        Log.d("Kotoba", "this deck was new: " + name);
                        ref.child(name).setValue(d);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    protected void onResume() {

        Log.d("Kotoba", "Flow: Resuming view decks activity");

        super.onResume();

        theAdapter.clear();

        readUserDecks();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("Kotoba", "not altering card");
        if (requestCode == 123) {
            Deck changedDeck = data.getParcelableExtra("changedDeck");
            int pos = data.getIntExtra("pos", 0);

            theAdapter.set(changedDeck, pos);
            Log.d("Kotoba", "altering card");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {
        Intent toHome = new Intent(getApplicationContext(), LaunchScreen.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        finish();
    }
}
