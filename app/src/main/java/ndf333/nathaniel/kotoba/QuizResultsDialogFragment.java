package ndf333.nathaniel.kotoba;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class QuizResultsDialogFragment extends DialogFragment {

    public interface QuizDialogListener {
        public void onDialogPositiveClicked(DialogFragment dialog);
    }

    QuizResultsDialogFragment.QuizDialogListener mListener;

    public static QuizResultsDialogFragment newInstance(int questionsCorrect, int questionAsked) {
        QuizResultsDialogFragment dialog = new QuizResultsDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("qCorrect", questionsCorrect);
        args.putInt("qAsked", questionAsked);

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int questionsCorrect = getArguments().getInt("qCorrect");
        final int questionsAsked = getArguments().getInt("qAsked");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View theView = inflater.inflate(R.layout.quiz_dialog, null);

        TextView score = theView.findViewById(R.id.score);
        score.setText(questionsCorrect + "/" + questionsAsked);

        TextView blurb = theView.findViewById(R.id.blurb);
        //do some math to set blurb
        if (questionsCorrect == 0) {
            blurb.setText("Everybody starts somewhere!");
        } else {
            double percentageCorrect = (questionsCorrect / questionsAsked);
            if (percentageCorrect > .9) {
                blurb.setText("Excellent work!");
            } else if (percentageCorrect < .9 || percentageCorrect > .7) {
                blurb.setText("Not bad at all! Keep at it!");
            } else if (percentageCorrect < .7 || percentageCorrect > .5) {
                blurb.setText("You're getting there! Keep at it!");
            } else {
                blurb.setText("Keep practicing, you can do it!");
            }
        }

        builder.setView(theView)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClicked(QuizResultsDialogFragment.this);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (QuizResultsDialogFragment.QuizDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
