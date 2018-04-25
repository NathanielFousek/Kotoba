package ndf333.nathaniel.kotoba;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity implements QuizResultsDialogFragment.QuizDialogListener{

    private Deck deck;

    private TextView question;
    private TextView questionCounter;

    private Button choiceA;
    private Button choiceB;
    private Button choiceC;

    private int currentIndex;

    private Random ran;

    private answerChoices correctAnswer;

    private int questsionsAsked;
    private int questsionsCorrect;

    @Override
    public void onDialogPositiveClicked(DialogFragment dialog) {
        finish();
    }


    private enum answerChoices {
        A, B, C
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent starter = getIntent();
        deck = starter.getParcelableExtra("deck");

        //shuffle the deck so the questions aren't always in the same order.
        Collections.shuffle(deck.cards);

        setTitle(deck.deckName);

        question = (TextView) findViewById(R.id.question);
        questionCounter = (TextView) findViewById(R.id.questionCounter);

        choiceA = (Button) findViewById(R.id.answerChoiceA);
        choiceB = (Button) findViewById(R.id.answerChoiceB);
        choiceC = (Button) findViewById(R.id.answerChoiceC);

        ran = new Random();

        currentIndex = 0;
        questsionsAsked = 0;
        questsionsCorrect = 0;
        showNextQuestion(currentIndex);
    }

    //Generate and show a question for the next card.
    public void showNextQuestion(int testIndex) {

        questsionsAsked += 1;
        //update the question counter
        questionCounter.setText((testIndex + 1) + "/" + deck.cards.size());

        Card testCard = deck.cards.get(testIndex);

        //Should we test the user over the front or back of this card?
        boolean testFront = ran.nextBoolean();

        if (testFront) {
            question.setText(testCard.front);

            //generate a random number 0-2 to pick which button will hold the correct answer.
            //the other 2 buttons will have another random card's data on it.
            int correctPos = ran.nextInt(3);

            if (correctPos == 0) {  //answer choice A correct
                correctAnswer = answerChoices.A;
                choiceA.setText(testCard.back);

                //set B and C to wrong answers.
                int wrong1 = -1;
                int wrong2 = -1;

                //we don't want to randomly select the same card we're testing.
                while (wrong1 == testIndex || wrong1 == -1) {
                    wrong1 = ran.nextInt(deck.cards.size());
                }

                //same for wrongB, but we also don't want to grab the same wrong choice
                //as the previous wrong answer
                while (wrong2 == testIndex || wrong2 == wrong1 || wrong2 == - 1) {
                    wrong2 = ran.nextInt(deck.cards.size());
                }

                Card wrongB = deck.cards.get(wrong1);
                Card wrongC = deck.cards.get(wrong2);

                choiceB.setText(wrongB.back);
                choiceC.setText(wrongC.back);

            } else if (correctPos == 1) { //answer choice B correct
                correctAnswer = answerChoices.B;
                choiceB.setText(testCard.back);

                //set A and C to wrong answers.
                int wrong1 = -1;
                int wrong2 = -1;

                while (wrong1 == testIndex || wrong1 == -1) {
                    wrong1 = ran.nextInt(deck.cards.size());
                }

                while (wrong2 == testIndex || wrong2 == wrong1 || wrong2 == - 1) {
                    wrong2 = ran.nextInt(deck.cards.size());
                }

                Card wrongA = deck.cards.get(wrong1);
                Card wrongC = deck.cards.get(wrong2);

                choiceA.setText(wrongA.back);
                choiceC.setText(wrongC.back);

            } else if (correctPos == 2) { //answer choice C correct
                correctAnswer = answerChoices.C;
                choiceC.setText(testCard.back);

                //set A and B to wrong answers.
                int wrong1 = -1;
                int wrong2 = -1;

                while (wrong1 == testIndex || wrong1 == -1) {
                    wrong1 = ran.nextInt(deck.cards.size());
                }

                while (wrong2 == testIndex || wrong2 == wrong1 || wrong2 == - 1) {
                    wrong2 = ran.nextInt(deck.cards.size());
                }

                Card wrongA = deck.cards.get(wrong1);
                Card wrongB = deck.cards.get(wrong2);

                choiceA.setText(wrongA.back);
                choiceB.setText(wrongB.back);

            }

        } else {
            question.setText(testCard.back);

            int correctPos = ran.nextInt(3);

            if (correctPos == 0) {  //answer choice A correct
                correctAnswer = answerChoices.A;
                choiceA.setText(testCard.front);

                //set B and C to wrong answers.
                int wrong1 = -1;
                int wrong2 = -1;

                //we don't want to randomly select the same card we're testing.
                while (wrong1 == testIndex || wrong1 == -1) {
                    wrong1 = ran.nextInt(deck.cards.size());
                }

                //same for wrongB, but we also don't want to grab the same wrong choice
                //as the previous wrong answer
                while (wrong2 == testIndex || wrong2 == wrong1 || wrong2 == - 1) {
                    wrong2 = ran.nextInt(deck.cards.size());
                }

                Card wrongB = deck.cards.get(wrong1);
                Card wrongC = deck.cards.get(wrong2);

                choiceB.setText(wrongB.front);
                choiceC.setText(wrongC.front);

            } else if (correctPos == 1) { //answer choice B correct
                correctAnswer = answerChoices.B;
                choiceB.setText(testCard.front);

                //set A and C to wrong answers.
                int wrong1 = -1;
                int wrong2 = -1;

                while (wrong1 == testIndex || wrong1 == -1) {
                    wrong1 = ran.nextInt(deck.cards.size());
                }

                while (wrong2 == testIndex || wrong2 == wrong1 || wrong2 == - 1) {
                    wrong2 = ran.nextInt(deck.cards.size());
                }

                Card wrongA = deck.cards.get(wrong1);
                Card wrongC = deck.cards.get(wrong2);

                choiceA.setText(wrongA.front);
                choiceC.setText(wrongC.front);

            } else if (correctPos == 2) { //answer choice C correct
                correctAnswer = answerChoices.C;
                choiceC.setText(testCard.front);

                //set A and B to wrong answers.
                int wrong1 = -1;
                int wrong2 = -1;

                while (wrong1 == testIndex || wrong1 == -1) {
                    wrong1 = ran.nextInt(deck.cards.size());
                }

                while (wrong2 == testIndex || wrong2 == wrong1 || wrong2 == - 1) {
                    wrong2 = ran.nextInt(deck.cards.size());
                }

                Card wrongA = deck.cards.get(wrong1);
                Card wrongB = deck.cards.get(wrong2);

                choiceA.setText(wrongA.front);
                choiceB.setText(wrongB.front);

            }
        }

    }

    public void checkAnswer(answerChoices choice) {
        //did the user get the question right?

        //make all buttons unclickable after clicking one so can't select multiple answers and confuse the system
        choiceA.setClickable(false);
        choiceB.setClickable(false);
        choiceC.setClickable(false);

        if (choice.equals(correctAnswer)) {

            questsionsCorrect += 1;
            //Toast.makeText(getApplicationContext(),"You got it right!", Toast.LENGTH_SHORT).show();

            switch (correctAnswer) {
                case A:
                    choiceA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
                    break;
                case B:
                    choiceB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
                    break;
                case C:
                    choiceC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
                    break;

            }

        } else {
            //Toast.makeText(getApplicationContext(),"You got it wrong!", Toast.LENGTH_SHORT).show();

            switch (choice) {
                case A:
                    choiceA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wrongAnswerColor));
                    break;
                case B:
                    choiceB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wrongAnswerColor));
                    break;
                case C:
                    choiceC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.wrongAnswerColor));
                    break;
            }

            switch (correctAnswer) {
                case A:
                    choiceA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
                    break;
                case B:
                    choiceB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
                    break;
                case C:
                    choiceC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
                    break;

            }

            }

        //If we just answered the last question, score the quiz.
        if (currentIndex == deck.cards.size() - 1) {
            scoreClicked(null);
        } else {
            //wait a little bit and then show the next question.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //after two seconds (should be long enough for user to see they were right / wrong, reset
                    // the colors of buttons, make them clickable again, and move to next question
                    choiceA.setClickable(true);
                    choiceA.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor));
                    choiceB.setClickable(true);
                    choiceB.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor));
                    choiceC.setClickable(true);
                    choiceC.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor));

                    currentIndex += 1;

                    showNextQuestion(currentIndex);
                }
            }, 1000);
        }
    }


    //When the user clicks the "score" button, the quiz should end and report how many questions
    //the user answered correctly. Then, the activity finishes.
    public void scoreClicked(View theView) {

        //Toast.makeText(getApplicationContext(), "You answered " + questsionsCorrect + " out of " + questsionsAsked + " correctly!", Toast.LENGTH_LONG).show();
        QuizResultsDialogFragment dialog = QuizResultsDialogFragment.newInstance(questsionsCorrect, questsionsAsked);
        dialog.show(getFragmentManager(), "dialog");

    }

    public void aClicked(View theView) {
        checkAnswer(answerChoices.A);
    }

    public void bClicked(View theView) {
        checkAnswer(answerChoices.B);
    }

    public void cClicked(View theView) {
        checkAnswer(answerChoices.C);
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
}
