package ndf333.nathaniel.kotoba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class LaunchScreen extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private static final int RC_SIGN_IN = 123;
    private DatabaseReference dbase;
    private TextView navHeader;
    private DeckSelectDialogFragment select;

    private SearchAdapter theAdapter;

    private FirebaseUser user;
    private String cleanEmail;

    private wordOfTheDay todaysWord;

    private TextView wordTerm;
    private TextView wordReading;
    private TextView wordMeaning;

    protected void onCreateDrawer() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView nv = findViewById(R.id.nav_view);

        View headerView = nv.getHeaderView(0);
        navHeader = (TextView) headerView.findViewById(R.id.navHeader);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);

                if (menuItem.getItemId() == R.id.Home) {

                    Intent toHome = new Intent(getApplicationContext(), LaunchScreen.class);
                    toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toHome);
                    finish();

                } else if (menuItem.getItemId() == R.id.Dictionary) {

                    Intent toDic = new Intent(getApplicationContext(), DictionaryActivity.class);
                    toDic.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toDic);
                    finish();

                } else if (menuItem.getItemId() == R.id.Decks) {

                    Intent toDecks = new Intent(getApplicationContext(), ViewDecksActivity.class);
                    toDecks.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toDecks);
                    finish();

                } else if (menuItem.getItemId() == R.id.Study) {

                    launchStudyActivity(getApplicationContext());
                } else if (menuItem.getItemId() == R.id.About) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LaunchScreen.this);
                    // Get the layout inflater
                    LayoutInflater inflater = getLayoutInflater();

                    View theView = inflater.inflate(R.layout.about_dialog, null);


                    builder.setView(theView);

                    builder.create().show();

                } else if (menuItem.getItemId() == R.id.Logout) {

                    AuthUI.getInstance()
                            .signOut(getApplicationContext())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    //return to sign in screen
                                    Intent toHome = new Intent(getApplicationContext(), LaunchScreen.class);
                                    startActivity(toHome);
                                }
                            });

                }

                // close drawer when item is tapped
                drawerLayout.closeDrawers();

                return true;
            }
        });
    }

    private void launchStudyActivity(final Context c) {
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
                    select = DeckSelectDialogFragment.newInstance(c, decks);
                    select.show(getFragmentManager(), "select");
                } else {
                    Toast.makeText(getApplicationContext(), "You have no decks to study! Create a deck under the decks tab.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        onCreateDrawer();

//        RecyclerView rv = (RecyclerView) findViewById(R.id.wordView);
//
//        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
//        rv.setLayoutManager(manager);
//        rv.setItemAnimator(new DefaultItemAnimator());
//
//        theAdapter = new SearchAdapter(this);
//
//        rv.setAdapter(theAdapter);
        LinearLayout wordOfDayLayout = findViewById(R.id.wordOfDayLayout);
        wordOfDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEntryActivity = new Intent(getApplicationContext(), DetailedEntryActivity.class);
                toEntryActivity.putExtra("entry", todaysWord.word);
                startActivity(toEntryActivity);
            }
        });

        wordTerm = (TextView) findViewById(R.id.wordOfTheDayTerm);
        wordReading = (TextView) findViewById(R.id.wordOfTheDayReading);
        wordMeaning = (TextView) findViewById(R.id.wordOfTheDayMeaning);
    }

    @Override
    protected void onPause() {

        if (cleanEmail != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(cleanEmail);
            userRef.child("wordOfTheDay").setValue(todaysWord);
        }

        super.onPause();
    }

    public void getOldWordOfTheDay() {

        DatabaseReference word = FirebaseDatabase.getInstance().getReference(cleanEmail).child("wordOfTheDay");
        word.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wordOfTheDay oldWord = dataSnapshot.getValue(wordOfTheDay.class);
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if (dataSnapshot.exists()) {
                    if (oldWord.date.equals(currentDate)) {
                        //theAdapter.add(oldWord.word);
                        updateTextViews(oldWord.word);
                        todaysWord = oldWord;
                    } else {
                        getNewWordOfTheDay();
                    }
                } else {
                    getNewWordOfTheDay();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getNewWordOfTheDay() {

        Random rand = new Random();
        int seed = rand.nextInt(16600) + 1;
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        SimpleDictionaryEntry entry = dbHelper.queryWordOfTheDay(seed);
        //theAdapter.add(entry);
        updateTextViews(entry);

        todaysWord = new wordOfTheDay();
        todaysWord.word = entry;
        todaysWord.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public void updateTextViews(SimpleDictionaryEntry word) {
        wordTerm.setText(word.term);
        wordReading.setText(word.reading);
        wordMeaning.setText(word.meaning);
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        dbase = FirebaseDatabase.getInstance().getReference();
        if (auth.getCurrentUser() != null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");
            navHeader.setText(user.getEmail());
            getOldWordOfTheDay();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();

                //firebase best practice replace . with ,
                cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");

                navHeader = (TextView) findViewById(R.id.navHeader);
                navHeader.setText(user.getEmail());

                getOldWordOfTheDay();

            } else {
                //pick what we do when sign in fails
            }
        }
    }

    public void launchDictionary(View view) {
        Intent toDic = new Intent(getApplicationContext(), DictionaryActivity.class);
        startActivity(toDic);
    }

    public void launchStudy(View view) {
        launchStudyActivity(this);
    }

    public void launchDecks(View view) {
        Intent toDecks = new Intent(getApplicationContext(), ViewDecksActivity.class);
        startActivity(toDecks);
    }

    public void showAbout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LaunchScreen.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View theView = inflater.inflate(R.layout.about_dialog, null);


        builder.setView(theView);

        builder.create().show();

    }

}
