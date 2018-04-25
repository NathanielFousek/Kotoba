package ndf333.nathaniel.kotoba;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class DetailedEntryActivity extends AppCompatActivity {

    public static enum Type {
        Single, Multi
    }

    private Type type;

    private DatabaseHelper dbHelper;

    private TextView term;
    private TextView meaning;
    private TextView reading;
    private TextView kunyomi;
    private TextView onyomi;
    private TextView synonyms;
    private TextView antonyms;
    private TextView jlpt;
    private TextView frequency;
    private RecyclerView rv;
    private SearchAdapter theAdapter;
    private AddToDeckDialogFragment add;

    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);

        Intent starter = getIntent();
        SimpleDictionaryEntry baseEntry = starter.getParcelableExtra("entry");

        //Choose which layout to inflate based on the type of baseEntry
        final String baseTerm = baseEntry.term;
        determineType(baseTerm);

        if (type == Type.Single) {
            setContentView(R.layout.activity_detailed_entry);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.DetailedEntryFab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDetailedEntryToDeck(view);
                }
            });

            Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);

            //Time to populate the view with all the necessary information.
            term = (TextView) findViewById(R.id.DetailedTerm);

            meaning = (TextView) findViewById(R.id.DetailedMeaning);
            meaning.setText(baseEntry.meaning);

            reading = (TextView) findViewById(R.id.DetailedReading);
            reading.setText(baseEntry.reading);

            kunyomi = (TextView) findViewById(R.id.Kunyomi);
            onyomi = (TextView) findViewById(R.id.Onyomi);
            synonyms = (TextView) findViewById(R.id.Synonyms);
            antonyms = (TextView) findViewById(R.id.Antonyms);
            jlpt = (TextView) findViewById(R.id.JLPTLevel);
            frequency = (TextView) findViewById(R.id.Frequency);

            populateSingleExtras(baseTerm);
        } else {

            setContentView(R.layout.detailed_entry_multi);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.DetailedEntryMultiFab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDetailedEntryToDeck(view);
                }
            });

            Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);

            term = (TextView) findViewById(R.id.DetailedTerm);

            meaning = (TextView) findViewById(R.id.DetailedMeaning);
            meaning.setText(baseEntry.meaning);

            reading = (TextView) findViewById(R.id.DetailedReading);
            reading.setText(baseEntry.reading);

            jlpt = (TextView) findViewById(R.id.JLPTLevel);
            frequency = (TextView) findViewById(R.id.Frequency);

            populateMultiExtras(baseTerm);
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();
        for (int i = 0; i < baseTerm.length(); i++) {
            char thisChar = baseTerm.charAt(i);
            sb.append(thisChar);
            sb.setSpan(new CustomClickableSpan(thisChar), sb.length() - 1, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        term.setText(sb);
        term.setHighlightColor(Color.TRANSPARENT);
        term.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //This custom class allows user to click a specific kanji and get a more detailed image with stroke order,
    //if applicable
    private class CustomClickableSpan extends ClickableSpan {

        public char myChar;
        public CustomClickableSpan(char myChar) {
            this.myChar = myChar;
        }

        @Override
        public void onClick(View view) {
            Dialog builder = new Dialog(DetailedEntryActivity.this);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
            String codepoint = dbHelper.queryCodepoints(Character.toString(myChar));

            if (codepoint.equals("noIMG")) {
                TextView textView = new TextView(DetailedEntryActivity.this);
                textView.setText(Character.toString(myChar));
                textView.setTextSize(128);
                textView.setBackgroundColor(Color.WHITE);

                builder.addContentView(textView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
            } else {
                ImageView imageView = new ImageView(DetailedEntryActivity.this);
                Resources resources = getApplicationContext().getResources();
                final int resourceId = resources.getIdentifier("i" + codepoint, "drawable",
                        getApplicationContext().getPackageName());
                imageView.setImageDrawable(resources.getDrawable(resourceId));

                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
            }
        }

        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }

    public void populateSingleExtras(String term) {

        Cursor kunOn = dbHelper.queryKunOmJLPT(term);
        kunOn.moveToFirst();
        kunyomi.setText(kunOn.getString(kunOn.getColumnIndex("kunyomi")));
        onyomi.setText(kunOn.getString(kunOn.getColumnIndex("onyomi")));

        if (kunOn.getString(kunOn.getColumnIndex("jlpt")) != null) {
            jlpt.setText(kunOn.getString(kunOn.getColumnIndex("jlpt")));
        } else {
            jlpt.setText("~");
        }

        kunOn.close();

        Cursor syn = dbHelper.querySynonyms(term);
        if (syn.getCount() > 0) {
            syn.moveToFirst();
            synonyms.setText(syn.getString(syn.getColumnIndex("synonyms")));
            syn.close();
        } else {
            synonyms.setText("~");
            syn.close();
        }

        Cursor ant = dbHelper.queryAntonyms(term);
        if (ant.getCount() > 0) {
            ant.moveToFirst();
            antonyms.setText(ant.getString(ant.getColumnIndex("antonyms")));
            ant.close();
        } else {
            antonyms.setText("~");
            ant.close();
        }

        Cursor freq = dbHelper.queryFrequency(term);
        if (freq.getCount() > 0) {
            freq.moveToFirst();
            frequency.setText(freq.getString(freq.getColumnIndex("frequency")));
            freq.close();
        } else {
            frequency.setText("~");
            freq.close();
        }
    }

    public void populateMultiExtras(String term) {

        Cursor mult = dbHelper.queryMultiJLPTFreq(term);
        if (mult.getCount() > 0) {
            mult.moveToFirst();
            if (mult.getString(mult.getColumnIndex("jlpt")) != null) {
                jlpt.setText(mult.getString(mult.getColumnIndex("jlpt")));
            } else {
                jlpt.setText("~");
            }
            if (mult.getString(mult.getColumnIndex("frequency")) != null) {
                frequency.setText(mult.getString(mult.getColumnIndex("frequency")));
            } else {
                frequency.setText("~");
            }
        }
    }

    public void determineType(String term) {
        if (term.length() == 1) {
            type = Type.Single;
        } else {
            type = Type.Multi;
        }
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
            case R.id.show_examples_option:
                Intent toExamples = new Intent(this, ShowExamplesActivity.class);
                toExamples.putExtra("word", term.getText().toString());
                startActivity(toExamples);


        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_examples_menu, menu);
        return true;
    }

    public void addDetailedEntryToDeck(View view) {

        //construct the new card
        String newFront = term.getText().toString();
        String newBack = reading.getText().toString() + "\n" + meaning.getText().toString();
        final Card newCard = new Card(newFront, newBack);


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
