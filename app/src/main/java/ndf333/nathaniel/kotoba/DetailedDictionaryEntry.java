package ndf333.nathaniel.kotoba;

/**
 * Created by Nathaniel on 2/28/2018.
 */

public class DetailedDictionaryEntry {

    //A detailed dictionary entry contains much more information, therefore requiring lots more querying of the database / jisho API
    //Therefore, we should only generate detailed dictionary entries when a user clicks a SimpleDictionaryEntry.

    public static enum Type {
        Single, Multi, Kana
    }

    //this whole class might be useless cause we can just do everything in the activity created
    String term;
    String readings;
    String meanings;

    //time to decide what attributes I want a detailed entry to show, and in what format.

    //A constructor to convert a simple dictionary entry into a detailed dictionary entry.
    public DetailedDictionaryEntry(SimpleDictionaryEntry entry, DetailedDictionaryEntry.Type type) {
        //perform different actions based on type inputed

    }
}
