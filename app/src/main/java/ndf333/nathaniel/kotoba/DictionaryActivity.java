package ndf333.nathaniel.kotoba;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DictionaryActivity extends LaunchScreen {

    private DatabaseHelper dbHelper;
    private EditText search;
    private CheckBox definitions;
    private CheckBox examples;
    private RecyclerView searchResults;
    private SearchAdapter searchAdapter;

    protected LinearLayoutManager manager;

    private boolean showDefinitions;
    private boolean showExamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_dictionary);
        super.onCreateDrawer();

        search = (EditText) findViewById(R.id.searchTerm);
        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            newSearch();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        definitions = (CheckBox) findViewById(R.id.showDefinitions);
        examples = (CheckBox) findViewById(R.id.showExamples);

        showDefinitions = true;
        showExamples = false;

        searchResults = (RecyclerView) findViewById(R.id.searchResults);

        manager = new LinearLayoutManager(getApplicationContext());
        searchResults.setLayoutManager(manager);
        searchResults.setItemAnimator(new DefaultItemAnimator());

        searchAdapter = new SearchAdapter(this);

        searchResults.setAdapter(searchAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(searchResults.getContext(),
                manager.getOrientation());
        searchResults.addItemDecoration(dividerItemDecoration);

        Net.init(this);

        dbHelper = new DatabaseHelper(this);
        //dictionary activity stuff
    }

    public void onCheckBoxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked. Because at least one option must be checked for a valid search, deselecting
        // an option when it is the only option selected will always toggle on the other one.
        switch(view.getId()) {
            case R.id.showDefinitions:
                if (checked) {
                    showDefinitions = true;
                } else {
                    if (showExamples == true) {
                        showDefinitions = false;
                    } else {
                        showDefinitions = false;
                        showExamples = true;
                        examples.setChecked(true);
                    }
                }
                break;
            case R.id.showExamples:
                if (checked) {
                    showExamples = true;
                } else {
                    if (showDefinitions == true) {
                        showExamples = false;
                    } else {
                        showExamples = false;
                        showDefinitions = true;
                        definitions.setChecked(true);
                    }
                }
                break;
        }
    }

    //Begin searching the dictionary.
    public void newSearch() {

        searchAdapter.clearAll();

        String searchTerm = search.getText().toString();

        //parse the search and decide what we're gonna do with it.

        boolean containsKanji = false;
        boolean containsKana = false;
        boolean containsEnglish = false;
        for (int i = 0; i < searchTerm.length(); i++) {
            Character c = searchTerm.charAt(i);
            if ((Character.UnicodeBlock.of(c)==Character.UnicodeBlock.HIRAGANA) || (Character.UnicodeBlock.of(c)==Character.UnicodeBlock.KATAKANA)) {
                containsKana = true;
            }
            //for some reason, Character.isLetter counts Japanese letters as valid, so gotta do this...
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                containsEnglish = true;
            }
            //the big one. checks if its any of chinese, japanese, and for some reason, korean characters.
            if ((Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
                    || (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)) {
                containsKanji = true;
            }

        }

        if (showDefinitions) {
            //3 invalid search cases. contains nothing, or contains all 3, or contains english mixed with japanese. In this case we should let the user
            //know they're an idiot.
            if ((containsEnglish && containsKana && containsKanji) || (!containsEnglish && !containsKana && !containsKanji)
                    || (containsEnglish && (containsKana || containsKanji))) {
                Log.d("Kotoba", "Bad search");
                //i think eventually I want this to be a pop-up fragment that advises the user to change search conditions.
                Toast.makeText(getApplicationContext(), "Please revise your search term and try again.", Toast.LENGTH_LONG);

                //A kana only search. In this case, we will query jisho.org for definitions, and the usage tables for sentence usage examples.
            } else if (!containsEnglish && !containsKanji && containsKana) {
                Log.d("Kotoba", "kana");
                queryJisho(searchTerm);

                //Pure Kanji // Kana + Kanji mix gets directed here.
            } else if ((containsKanji && containsKana && !containsEnglish) || (containsKanji && !containsKana && !containsEnglish)) {
                Log.d("Kotoba", "kana/kanji");

                ArrayList<Object> result = dbHelper.queryKanji(searchTerm);

                searchAdapter.addAll(result);

                queryJisho(searchTerm);

                //An english only search. We can use the database here, but again kick to jisho if nothing comes up.
            } else if (containsEnglish && !containsKana && !containsKanji) {
                Log.d("Kotoba", "english");

                ArrayList<Object> result = dbHelper.queryEnglish(searchTerm);

                searchAdapter.addAll(result);

                queryJisho(searchTerm);
            }
        }

        //After completing the search-specific queries, we can search example usages, which is completely
        //handled by the queryExamples method in DatabaseHelper
        if (showExamples) {
            ArrayList<Object> result = dbHelper.queryExamples(searchTerm);
            searchAdapter.addAll(result);
        }

    }


    //If the database can't help us (katakana / hiragana term), we will just fetch JSON from Jisho.orgs API
    //Unfortunately, this API is rather limited, so we might not get all the detailed info we want.
    //However, we can still search the sentences and other usage tables for appearances of this search term
    public void queryJisho(String searchTerm) {

        //we have to encode the string, otherwise including japanese characters in the search string
        //throws everything way out of wack.
        try {
            searchTerm = java.net.URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://jisho.org/api/v1/search/words?keyword=" + searchTerm;

        Log.d("Kotoba", "search url: " + url);

        //later: can append #jlpt-n3 to get words by JLPT level

        //!!Should make sure entries are valid by making sure nothing is null, because apparently sometimes words have no reading
        // on the jisho api...

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Object> results = parseJisho(response);
                        searchAdapter.addAll(results);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Kotoba", "error occurred");
                    }
                });

        Net.getInstance().addToRequestQueue(jsObjRequest, "req");
    }


    //Parse the JSONObject returned from our request to Jisho.org and construct
    //simple dictionary entries from the resultant data.
    public ArrayList<Object> parseJisho(JSONObject response) {
        ArrayList<Object> parsed = new ArrayList<>();

        int jsonIndex = 0;

        //hierarchy is big array called data.
        try {
            if (response.isNull("data")) {
                Log.d("Kotoba", "data null");
            }
            JSONArray jA = response.getJSONArray("data");
            while (jsonIndex < jA.length()) {
                SimpleDictionaryEntry thisEntry = new SimpleDictionaryEntry();

                JSONObject obj = jA.getJSONObject(jsonIndex);
                //Log.d("Kotoba", obj.toString());

                JSONArray japanese = obj.getJSONArray("japanese");
                JSONObject wordNreading = japanese.getJSONObject(0);

                if (!wordNreading.isNull("word")) {
                    thisEntry.term = wordNreading.getString("word");
                } else {
                    thisEntry.term = wordNreading.getString("reading");
                }

                if (!wordNreading.isNull("reading")) 
                    thisEntry.reading = wordNreading.getString("reading");


                JSONArray senses = obj.getJSONArray("senses");
                JSONObject english = senses.getJSONObject(0);
                JSONArray defs = english.getJSONArray("english_definitions");
                int engIndex = 1;
                String English = defs.getString(0);
                while (engIndex < defs.length()) {
                    English += ", " + defs.getString(engIndex);
                    engIndex++;
                }
                thisEntry.meaning = English;

                parsed.add(thisEntry);

                jsonIndex++;
            }
        } catch (Exception e) {
            Log.d("Kotoba", "parse error");
        }
        return parsed;
    }

    @Override
    public void onBackPressed() {
        Intent toHome = new Intent(getApplicationContext(), LaunchScreen.class);
        toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toHome);
        finish();
    }

}
