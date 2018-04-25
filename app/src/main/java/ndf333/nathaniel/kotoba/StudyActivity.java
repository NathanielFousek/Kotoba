package ndf333.nathaniel.kotoba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudyActivity extends LaunchScreen {

    private Deck deck;

    private TextView cardDisplay;
    private TextView cardCounter;

    private ArrayList<Card> myCards;

    private int currentIndex;
    private boolean showingFront;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);
        super.onCreateDrawer();

        Intent starter = getIntent();
        deck = starter.getParcelableExtra("deck");

        myCards = deck.cards;

        setTitle(deck.deckName);

        cardDisplay = (TextView) findViewById(R.id.cardDisplay);
        cardCounter = (TextView) findViewById(R.id.cardCounter);



        final GestureDetector detect = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                        return false;
                    }
                    // right to left swipe
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        showNextCard();
                    }
                    // left to right swipe
                    else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        showPreviousCard();
                    }
                } catch (Exception e) {

                }
                return false;
            }

            public boolean onSingleTapUp(MotionEvent e) {
                flipCard();
                return false;
            }
        });

        cardDisplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detect.onTouchEvent(motionEvent);
                return true;
            }
        });


        if (savedInstanceState == null) {
            currentIndex = 0;
            showingFront = true;
            cardCounter.setText("1/" + myCards.size());
        } else {
            currentIndex = savedInstanceState.getInt("currentIndex");
            showingFront = savedInstanceState.getBoolean("showingFront");
            cardCounter.setText((currentIndex + 1) + "/" + myCards.size());
        }

        if (showingFront) {
            cardDisplay.setText(myCards.get(currentIndex).front);
        } else {
            cardDisplay.setText(myCards.get(currentIndex).back);
        }

    }


    //flip the current card
    //maybe figure out how to get a cool animation here.
    public void flipCard() {
        Log.d("kotoba", "card clicked");
        Card currentCard = myCards.get(currentIndex);
        if (showingFront) {
            cardDisplay.setText(currentCard.back);
            showingFront = false;
        } else {
            cardDisplay.setText(currentCard.front);
            showingFront = true;
        }
    }

    public void showNextCard() {
        int nextIndex;
        if (currentIndex == myCards.size() - 1) {
            nextIndex = 0;
            cardCounter.setText("1/" + myCards.size());
        } else {
            nextIndex = currentIndex + 1;
            cardCounter.setText((nextIndex + 1) + "/" + myCards.size());
        }

        Card nextCard = myCards.get(nextIndex);

        cardDisplay.setText(nextCard.front);
        showingFront = true;

        currentIndex = nextIndex;
    }

    public void showPreviousCard() {
        int previousIndex;
        if (currentIndex == 0) {
            previousIndex = myCards.size() - 1;
            cardCounter.setText(myCards.size() + "/" + myCards.size());
        } else {
            previousIndex = currentIndex - 1;
            cardCounter.setText((previousIndex + 1) + "/" + myCards.size());
        }

        Card previousCard = myCards.get(previousIndex);

        cardDisplay.setText(previousCard.front);
        showingFront = true;

        currentIndex = previousIndex;

    }

    @Override
    public void onBackPressed() {
        Intent toHome = new Intent(getApplicationContext(), LaunchScreen.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.study_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.quiz_option:
                Intent toQuiz = new Intent(getApplicationContext(), QuizActivity.class);
                toQuiz.putExtra("deck", deck);
                startActivity(toQuiz);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("currentIndex", currentIndex);
        savedInstanceState.putBoolean("showingFront", showingFront);
    }
}
