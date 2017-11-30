package fr.esisar.px504.smartremote.vocalcontrol;

/**
 * Created by arthurlandon on 28/11/2017.
 */

public class NextWord {

    private String word;
    private int pos;
    private String state;

    NextWord(String word, int pos, String state){
        this.word = word;
        this.pos = pos;
        this.state = state;
    }

    public String getWord() {
        return this.word;
    }

    public int getPos() {
        return this.pos;
    }

    public String getState() {
        return this.state;
    }

}
