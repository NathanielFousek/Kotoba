package ndf333.nathaniel.kotoba;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nathaniel on 2/28/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> searchData = new ArrayList<>();
    private Context context;

    private final int TERM = 0, SENTENCE = 1;


    public class SimpleEntryViewHolder extends RecyclerView.ViewHolder {

        private SimpleDictionaryEntry entry;
        private TextView term;
        private TextView reading;
        private TextView meaning;

        public SimpleEntryViewHolder(View theView) {
            super(theView);
            term = (TextView) theView.findViewById(R.id.simpleTerm);
            reading = (TextView) theView.findViewById(R.id.simpleReading);
            meaning = (TextView) theView.findViewById(R.id.simpleMeaning);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //bundle what we need and start new activity.
                    Intent toEntryActivity = new Intent(context, DetailedEntryActivity.class);
                    toEntryActivity.putExtra("entry", entry);
                    context.startActivity(toEntryActivity);
                }
            });
        }
    }

    public class SentenceEntryViewHolder extends RecyclerView.ViewHolder {

        private TextView english;
        private TextView japanese;

        public SentenceEntryViewHolder(View theView) {
            super(theView);

            english = theView.findViewById(R.id.sentenceEnglish);
            japanese = theView.findViewById(R.id.sentenceJapanese);

            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //bundle what we need and start new activity.
                    Intent toSentenceActivity = new Intent(context, DetailedSentenceActivity.class);
                    toSentenceActivity.putExtra("english", english.getText().toString());
                    toSentenceActivity.putExtra("japanese", japanese.getText().toString());
                    context.startActivity(toSentenceActivity);
                }
            });
        }

    }

    public SearchAdapter(Context context) {
        this.context = context;
    }

    public int getItemViewType(int position) {
        if (searchData.get(position) instanceof SimpleDictionaryEntry) {
            return TERM;
        } else if (searchData.get(position) instanceof SentenceDictionaryEntry) {
            return SENTENCE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TERM:
                View v1 = inflater.inflate(R.layout.simple_dictionary_entry, parent, false);
                viewHolder = new SimpleEntryViewHolder(v1);
                break;
            case SENTENCE:
                View v2 = inflater.inflate(R.layout.sentence_dictionary_entry, parent, false);
                viewHolder = new SentenceEntryViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new SimpleEntryViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TERM:
                SimpleEntryViewHolder vh1 = (SimpleEntryViewHolder) holder;
                configureSimpleViewHolder(vh1, position);
                break;
            case SENTENCE:
                SentenceEntryViewHolder vh2 = (SentenceEntryViewHolder) holder;
                configureSentenceViewHolder(vh2, position);
                break;
            default:
                SimpleEntryViewHolder vh = (SimpleEntryViewHolder) holder;
                configureSimpleViewHolder(vh, position);
                break;
        }
    }

    private void configureSimpleViewHolder(SimpleEntryViewHolder vh, int position) {
        SimpleDictionaryEntry entry = (SimpleDictionaryEntry) searchData.get(position);

        vh.entry = entry;
        vh.term.setText(entry.term);
        vh.reading.setText(entry.reading);
        vh.meaning.setText(entry.meaning);

    }

    private void configureSentenceViewHolder(SentenceEntryViewHolder vh, int position) {
        SentenceDictionaryEntry entry = (SentenceDictionaryEntry) searchData.get(position);

        vh.japanese.setText(entry.japanese);
        vh.english.setText(entry.english);
    }

    public void add(Object searchResult) {
        int newPos = searchData.size();
        searchData.add(searchResult);
        notifyItemInserted(newPos);
        //should change to notify item inserted later
    }

    public void addAll(ArrayList<Object> newEntries) {
        for (Object o : newEntries) {
            add(o);
        }
    }

    public void clearAll() {
        searchData = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchData.size();
    }
}
