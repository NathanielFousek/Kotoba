package ndf333.nathaniel.kotoba;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by Nathaniel on 2/26/2018.
 */

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "kanjidb.sqlite";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = getReadableDatabase();

        //new getDB().execute();
    }

    //No idea if I'm using AsyncTask right.
//    public class getDB extends AsyncTask {
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            db = getReadableDatabase();
//            return db;
//        }
//    }

    public ArrayList<Object> queryKanji(String searchTerm) {
        ArrayList<Object> result = new ArrayList<>();

        //Single length search term, we handle that here
        if (searchTerm.length() == 1) {
            String query = "SELECT distinct okurigana, reading, meaning from edict where kanji=\""  + searchTerm + "\""
                            + " union " +
                            "SELECT distinct jukugo, reading, meaning FROM jukugo where kanji=\""  + searchTerm + "\" "
                            + " union " +
                            "SELECT distinct jukugo, reading, meaning FROM yojijukugo where kanji=\""  + searchTerm + "\" ";

            Cursor c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    SimpleDictionaryEntry newEntry = new SimpleDictionaryEntry();
                    newEntry.term = c.getString(c.getColumnIndex("okurigana"));
                    newEntry.reading = c.getString(c.getColumnIndex("reading"));
                    newEntry.meaning = c.getString(c.getColumnIndex("meaning"));

                    result.add(newEntry);
                    c.moveToNext();
                }
            }
        } else {
           String query =   "select okurigana, reading, meaning from edict where okurigana like \"%" + searchTerm + "%\""
                            + " union " +
                            "select jukugo, reading, meaning from jukugo where jukugo like \"%" + searchTerm + "%\""
                            + " union " +
                            "select compverb, reading, meaning from compverbs where compverb like \"%" + searchTerm + "%\""
                            + " union " +
                            "select jukugo, reading, meaning from yojijukugo where jukugo like \"%" + searchTerm + "%\"";


            Cursor c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    SimpleDictionaryEntry newEntry = new SimpleDictionaryEntry();
                    newEntry.term = c.getString(c.getColumnIndex("okurigana"));
                    newEntry.reading = c.getString(c.getColumnIndex("reading"));
                    newEntry.meaning = c.getString(c.getColumnIndex("meaning"));

                    result.add(newEntry);
                    c.moveToNext();
                }
            }
            c.close();

        }
        return result;
    }

    public ArrayList<Object> queryEnglish(String searchTerm) {
        ArrayList<Object> result = new ArrayList<>();

        String query =  "select okurigana, reading, meaning from edict where (meaning LIKE \"% " + searchTerm +"%\" OR meaning LIKE \"% " +searchTerm + ";%\")"
                        + " union " +
                        "select jukugo, reading, meaning from jukugo where (meaning LIKE \"% " + searchTerm +"%\" OR meaning LIKE \"% " +searchTerm + ";%\")"
                        + " union " +
                        "select compverb, reading, meaning from compverbs where (meaning LIKE \"% " + searchTerm +"%\" OR meaning LIKE \"% " +searchTerm + ";%\")"
                        + " union " +
                        "select jukugo, reading, meaning from yojijukugo where (meaning LIKE \"% " + searchTerm +"%\" OR meaning LIKE \"% " +searchTerm + ";%\")";

        Log.d("Kotoba", "English only query: " + query);

        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                SimpleDictionaryEntry newEntry = new SimpleDictionaryEntry();
                newEntry.term = c.getString(c.getColumnIndex("okurigana"));
                newEntry.reading = c.getString(c.getColumnIndex("reading"));
                newEntry.meaning = c.getString(c.getColumnIndex("meaning"));

                result.add(newEntry);
                c.moveToNext();
            }
        }
        c.close();

        return result;
    }


    public ArrayList<Object> queryExamples(String searchTerm) {
        ArrayList<Object> result = new ArrayList<>();

        /*  these two queries should gather all relevant sentences, whether or not the search term
            was in english, japanese or kana
            query1
            select english, japanese from sentences where japanese like "%term%"

            query2
            select english, japanese from sentences where english like "%term%"

         */

        //For some reason, doing a union altered the output so i'll just do two queries

        String[] queries = new String[4];
        queries[0] = "SELECT distinct english, japanese from sentences where japanese like \"%" + searchTerm + "%\"";
        queries[1] = "SELECT distinct english, japanese from sentences where english like \"%" + searchTerm + "%\"";
        queries[2] = "SELECT distinct kotowaza, reading, meaning from kotowaza where kotowaza like \"%%" + searchTerm + "%\"";
        queries[3] = "SELECT distinct kotowaza, reading, meaning from kotowaza where meaning like \"%" + searchTerm + "%\"";

        for (int i = 0; i < 2; i++) {
            Cursor c = db.rawQuery(queries[i], null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    SentenceDictionaryEntry newEntry = new SentenceDictionaryEntry();
                    newEntry.japanese = c.getString(c.getColumnIndex("japanese"));
                    newEntry.english = c.getString(c.getColumnIndex("english"));

                    result.add(newEntry);
                    c.moveToNext();
                }
            }
            c.close();
        }
        for (int i = 2; i < 4; i++) {
            Cursor c = db.rawQuery(queries[i], null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    SentenceDictionaryEntry newEntry = new SentenceDictionaryEntry();
                    newEntry.japanese = c.getString(c.getColumnIndex("kotowaza"));
                    newEntry.english = c.getString(c.getColumnIndex("meaning"));
                    newEntry.reading = c.getString(c.getColumnIndex("reading"));

                    result.add(newEntry);
                    c.moveToNext();
                }
            }
            c.close();
        }


        return result;
    }

    //These helper methods are used to gather more detailed information about terms for viewing
    //in the DetailedEntryActivity.

    //this takes a single kanji character as input, and returns a cursor holding
    //the corresponding kunyomi, onyomi, and jlpt level columns
    public Cursor queryKunOmJLPT(String kanji) {

        String query = "SELECT kunyomi, onyomi, jlpt from kanjidict where kanji=\"" + kanji + "\"";
        Log.d("Kotoba", "KunOm Query: " + query);
        Cursor re = db.rawQuery(query, null);
        return re;
    }

    //this takes a single kanji character as input, and returns a cursor holding
    //the corresponding synonym column
    public Cursor querySynonyms(String kanji) {

        String query = "SELECT synonyms from synonyms where kanji=\"" + kanji + "\"";
        Log.d("Kotoba", "Syn Query: " + query);
        Cursor re = db.rawQuery(query, null);
        return re;
    }

    //this takes a single kanji character as input, and returns a cursor holding
    //the corresponding antonym column
    public Cursor queryAntonyms(String kanji) {

        String query = "SELECT antonyms from antonyms where kanji=\"" + kanji + "\"";
        Log.d("Kotoba", "Ant Query: " + query);
        Cursor re = db.rawQuery(query, null);
        return re;
    }

    public Cursor queryFrequency(String kanji) {

        String query = "select frequency from edict where okurigana=\"" + kanji + "\"";
        Log.d("Kotoba", "Freq Query: " + query);
        Cursor re = db.rawQuery(query, null);
        return re;
    }

    public Cursor queryMultiJLPTFreq(String term) {

        //gonna need to change this to pull from more tables!!!!!!
        String query = "select jlpt, frequency from jukugo where jukugo=\"" + term + "\"";
        Log.d("Kotoba", "Multi Query: " + query);
        Cursor re = db.rawQuery(query, null);
        return re;
    }

    public String queryCodepoints(String kanji) {

        String query = "SELECT decimal from codepoints where kanji=\"" + kanji + "\"";
        Cursor c = db.rawQuery(query, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            String decimal = c.getString(c.getColumnIndex("decimal"));
            return decimal;
        } else {
            return "noIMG";
        }
    }

    public SimpleDictionaryEntry queryWordOfTheDay(int ran) {

        String query = "SELECT jukugo, reading, meaning from jukugo where ID=" + ran;
        Cursor c = db.rawQuery(query, null);

        SimpleDictionaryEntry word = new SimpleDictionaryEntry();

        if (c.getCount() > 0) {
            c.moveToFirst();
            word.term = c.getString(c.getColumnIndex("jukugo"));
            word.reading = c.getString(c.getColumnIndex("reading"));
            word.meaning = c.getString(c.getColumnIndex("meaning"));
        }

        return word;
    }

}
