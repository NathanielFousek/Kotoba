package ndf333.nathaniel.kotoba;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Nathaniel on 3/9/2018.
 */

public class Deck implements Parcelable {

    //a class that models a deck of flash cards.

    public String deckName;
    public int numCards;
    public String description;

    public ArrayList<Card> cards;


    //create a new deck with this name
    public Deck(String name, String description) {
        this.deckName = name;
        this.description = description;
        cards = new ArrayList<Card>();
    }

    public Deck() {
        cards = new ArrayList<Card>();
    }

    public void add(Card c) {
        cards.add(c);
    }

    //functions to get cards from a deck

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deckName);
        dest.writeInt(this.numCards);
        dest.writeString(this.description);
        dest.writeList(this.cards);
    }

    protected Deck(Parcel in) {
        this.deckName = in.readString();
        this.numCards = in.readInt();
        this.description = in.readString();
        this.cards = new ArrayList<Card>();
        in.readList(this.cards, Card.class.getClassLoader());
    }

    public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
        @Override
        public Deck createFromParcel(Parcel source) {
            return new Deck(source);
        }

        @Override
        public Deck[] newArray(int size) {
            return new Deck[size];
        }
    };
}
