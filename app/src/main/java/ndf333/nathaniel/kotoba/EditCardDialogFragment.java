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

import org.w3c.dom.Text;

/**
 * Created by Nathaniel on 3/10/2018.
 */

public class EditCardDialogFragment extends DialogFragment {

    public interface EditDialogListener {
        public void onCardEdit(DialogFragment dialog, int pos, String newFront, String newBack);
        public void onCardEditCancel(DialogFragment dialog);
    }

    EditCardDialogFragment.EditDialogListener mListener;

    public static EditCardDialogFragment newInstance(int pos, Card c) {
        EditCardDialogFragment dialog = new EditCardDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("pos", pos);
        args.putParcelable("card", c);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int pos = getArguments().getInt("pos");
        final Card c = getArguments().getParcelable("card");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View theView = inflater.inflate(R.layout.editcard_dialog, null);
        TextView newFront = theView.findViewById(R.id.editFront);
        newFront.setText(c.front);
        TextView newBack = theView.findViewById(R.id.editBack);
        newBack.setText(c.back);

        builder.setView(theView)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        TextView newFront = getDialog().findViewById(R.id.editFront);
                        TextView newBack = getDialog().findViewById(R.id.editBack);
                        mListener.onCardEdit(EditCardDialogFragment.this, pos, newFront.getText().toString(), newBack.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onCardEditCancel(EditCardDialogFragment.this);
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
            mListener = (EditCardDialogFragment.EditDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
