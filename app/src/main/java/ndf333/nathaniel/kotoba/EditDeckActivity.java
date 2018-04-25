package ndf333.nathaniel.kotoba;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class EditDeckActivity extends AppCompatActivity implements CreateCardDialogFragment.NoticeDialogListener, EditCardDialogFragment.EditDialogListener{


    private RecyclerView rv;
    private Deck thisDeck;
    private int deckPos;

    private FirebaseUser user;
    private DatabaseReference dbase;
    private DatabaseReference ref;

    private CardAdapter theAdapter;

    //in this activity, we display all cards in the currently selected deck, and allow the user to edit / add cards
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        dbase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");
        ref = FirebaseDatabase.getInstance().getReference(cleanEmail).child("userDecks");

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent starter = getIntent();

        thisDeck = starter.getParcelableExtra("deck");
        deckPos = starter.getIntExtra("pos", 0);

        setTitle(thisDeck.deckName);

        FloatingActionButton fab = findViewById(R.id.CardFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCardClicked(view);
            }
        });

        rv = (RecyclerView) findViewById(R.id.viewCards);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(manager);
        rv.setItemAnimator(new DefaultItemAnimator());

        theAdapter = new CardAdapter(this, rv);

        rv.setAdapter(theAdapter);

        theAdapter.addAll(thisDeck.cards);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(),
                manager.getOrientation());
        rv.addItemDecoration(dividerItemDecoration);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_deck_menu, menu);
        return true;
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("changedDeck", thisDeck);
        intent.putExtra("pos", deckPos);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.study_option:
                Intent toStudy = new Intent(this, StudyActivity.class);
                toStudy.putExtra("deck", thisDeck);
                startActivity(toStudy);
        }
        return true;
    }

    @Override
    protected void onPause() {

        //update firebase db

        HashMap<String, Object> updates = new HashMap<String, Object>();
        updates.put("cards", theAdapter.getAll());
        updates.put("numCards", theAdapter.getItemCount());
        ref.child(thisDeck.deckName).updateChildren(updates);

        super.onPause();

    }

    public void addCardClicked(View view) {

        DialogFragment newFragment = new CreateCardDialogFragment();
        newFragment.show(getFragmentManager(), "create");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String newFront, String newBack) {

        Card newCard = new Card(newFront, newBack);
        theAdapter.add(newCard);
        thisDeck.numCards++;
        thisDeck.add(newCard);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onCardEdit(DialogFragment dialog, int pos, String newFront, String newBack) {
        theAdapter.editCard(pos, newFront, newBack);
    }

    @Override
    public void onCardEditCancel(DialogFragment dialog) {

    }


}
