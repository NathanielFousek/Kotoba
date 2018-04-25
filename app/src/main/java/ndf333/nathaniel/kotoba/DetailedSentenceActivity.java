package ndf333.nathaniel.kotoba;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class DetailedSentenceActivity extends AppCompatActivity {

    private TextView japanese;
    private TextView english;
    private AddToDeckDialogFragment add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_sentence);

        FloatingActionButton fab = findViewById(R.id.DetailedSentenceFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDetailedSentenceToDeck(view);
            }
        });

        Intent starter = getIntent();
        String eng = starter.getStringExtra("english");
        String jap = starter.getStringExtra("japanese");

        english = (TextView) findViewById(R.id.DetailedEnglish);
        japanese = (TextView) findViewById(R.id.DetailedJapanese);

        english.setText(eng);
        japanese.setText(jap);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

        }
        return true;
    }

    public void addDetailedSentenceToDeck(View view) {

        //construct the new card
        final Card newCard = new Card(japanese.getText().toString(), english.getText().toString());

        //should we check if the deck already has a card for this? probably

        //read decks, let user select the deck to add it to
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(cleanEmail).child("userDecks");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Deck> decks = new ArrayList<Deck>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Deck deck = d.getValue(Deck.class);
                    decks.add(deck);
                }
                if (decks.size() > 0) {
                    add = AddToDeckDialogFragment.newInstance(decks, newCard);
                    add.show(getFragmentManager(), "add");
                } else {
                    //show fragment asking to create a deck
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
