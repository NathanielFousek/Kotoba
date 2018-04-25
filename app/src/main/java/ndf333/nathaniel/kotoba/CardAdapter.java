package ndf333.nathaniel.kotoba;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Nathaniel on 3/9/2018.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private ArrayList<Card> cards = new ArrayList<>();
    private Context context;
    private SwipeDetector swipeDetector;

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private Card myCard;
        private TextView front;
        private TextView back;

        public CardViewHolder(View theView) {
            super(theView);

            front = (TextView) theView.findViewById(R.id.cardFront);
            back = (TextView) theView.findViewById(R.id.cardBack);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (swipeDetector.swipeDetected()) {
                        Log.d("RedFetch", "swipe detected");
                        if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Deck d = decks.get(getLayoutPosition());
                                            //Toast.makeText(context, "deck to delete: " + d.deckName, Toast.LENGTH_LONG).show();
                                            remove(getLayoutPosition());
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Delete this card?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                            return;
                        }
                    } else {
                        DialogFragment newFragment = EditCardDialogFragment.newInstance(cards.indexOf(myCard), myCard);
                        FragmentManager mg = ((Activity) context).getFragmentManager();
                        newFragment.show(mg, "create");
                    }
                }
            });
        }
    }

    public CardAdapter(Context context, RecyclerView rv) {

        this.context = context;
        swipeDetector = new SwipeDetector();
        rv.addOnItemTouchListener(swipeDetector);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.simplecard_view, parent, false);
        CardViewHolder holder = new CardViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Card card = cards.get(position);

        holder.myCard = card;
        holder.front.setText(card.front);
        holder.back.setText(card.back);

    }

    public void add(Card c) {
        int newPos = cards.size();
        cards.add(c);
        notifyItemInserted(newPos);
    }

    public ArrayList<Card> getAll() {
        return cards;
    }

    public void remove(int position) {
        cards.remove(position);
        notifyItemRangeRemoved(position, 1);
    }

    public void addAll(ArrayList<Card> cards) {
        for (Card c : cards) {
            add(c);
        }
    }

    public void editCard(int pos, String newFront, String newBack) {

        Card c = cards.get(pos);
        c.front = newFront;
        c.back = newBack;
        cards.set(pos, c);
        notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
