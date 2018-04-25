package ndf333.nathaniel.kotoba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Nathaniel on 3/9/2018.
 */

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private ArrayList<Deck> decks = new ArrayList<>();
    private Context context;
    private SwipeDetector swipeDetector;

    public class DeckViewHolder extends RecyclerView.ViewHolder {

        //private Deck deck;
        private TextView name;
        private TextView num;
        private TextView description;

        public DeckViewHolder(View theView) {
            super(theView);

            name = (TextView) theView.findViewById(R.id.deckName);
            num = (TextView) theView.findViewById(R.id.deckNumCards);
            description = (TextView)theView.findViewById(R.id.deckDescription);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //check swipes
                    if (swipeDetector.swipeDetected()) {
                        Log.d("RedFetch", "swipe detected");
                        if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Deck d = decks.get(getLayoutPosition());
                                            //Toast.makeText(context, "deck to delete: " + d.deckName, Toast.LENGTH_LONG).show();

                                            //remove it from the adapter
                                            String name = decks.get(getLayoutPosition()).deckName;
                                            remove(getLayoutPosition());

                                            //wipe it from the firebase database
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String cleanEmail = user.getEmail().replaceAll(Pattern.quote("."),",");

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(cleanEmail).child("userDecks");
                                            ref.child(name).removeValue();

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Delete this deck?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                            return;

                        }
                    } else {
                        //what we do on clicks
                        Intent toEdit = new Intent(context, EditDeckActivity.class);
                        toEdit.putExtra("deck", decks.get(getLayoutPosition()));
                        toEdit.putExtra("pos", getLayoutPosition());
                        ((Activity) context).startActivityForResult(toEdit, 123);
                    }
                }
            });
        }
    }

    public DeckAdapter(Context context, RecyclerView rv) {

        this.context = context;
        swipeDetector = new SwipeDetector();
        rv.addOnItemTouchListener(swipeDetector);
        //Log.d("kotoba", "creating deck adapter");
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Log.d("kotoba", "creating viewholder");

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.simpledeck_view, parent, false);
        DeckViewHolder holder = new DeckViewHolder(v1);
        return holder;
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {

        //Log.d("kotoba", "binding viewholder");

        Deck deck = decks.get(position);

        //holder.deck = deck;
        holder.name.setText(deck.deckName);
        holder.description.setText(deck.description);
        holder.num.setText(String.valueOf(deck.numCards));
    }

    public ArrayList<Deck> getAll() {
        return decks;
    }

    public void clear() {
        decks = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void set(Deck changedDeck, int pos) {
        decks.set(pos, changedDeck);
        notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    public void add(Deck d) {
        int newPos = decks.size();
        decks.add(d);
        notifyItemInserted(newPos);
    }

    public void remove(int position) {
        decks.remove(position);
        notifyItemRangeRemoved(position, 1);
    }
}
