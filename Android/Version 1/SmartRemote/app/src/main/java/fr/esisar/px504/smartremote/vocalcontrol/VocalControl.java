package fr.esisar.px504.smartremote.vocalcontrol;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.esisar.px504.smartremote.R;
import fr.esisar.px504.smartremote.ihm.Lamp;

/**
 * Created by arthurlandon on 19/11/2017.
 */

public class VocalControl {
    // private List<String> locations;
    private String[] state;
    private Integer[] brightness;
    private String[] location;


    private Lamp lamp;
    private Resources res;
    private Context context;
    private static final String TAG = "vocalControl";
    private List<String> listLocation = new ArrayList<>();


    public VocalControl(Context context , List<Lamp> list) {
        this.context = context;
        for(Lamp lamp: list) {
            listLocation.add(lamp.getLocation());
        }
    }
    //Analyse syntaxique

    /**
     * @param text : Chaîne de caractères reconnus par le SpeechRecognizer
     * @return : Liste d'objets Lamp signifiants les commandes à effectuer
     */


    public List<Lamp> SyntacticAnalysis(ArrayList<String> text) {
        location = new String[100];
        state = new String[100];
        brightness = new Integer[100];

        //Parse la phrase
        Resources res = context.getResources();
        String[] sentence = text.get(0).split(" ");
        sentence[0] = sentence[0].toLowerCase();
        int k = 0;
        int i;
        int l = 0;
        int m;
        int tmp;
        List<Lamp> listLamp = new ArrayList<Lamp>();
        //String[] arrayLocation = res.getStringArray(R.array.location_word);
        String[] arrayStateOn = res.getStringArray(R.array.action_word_on);
        String[] arrayStateOff = res.getStringArray(R.array.action_word_off);
        NextWord word;

        for (i = 0; i < sentence.length; i++) {
            // Tant que l'on n'arrive pas à la prochaine instruction
            if (i < l) {
                continue;
            } else if (i == l) {
                word = NextKeyword(sentence, i, listLocation, arrayStateOn, arrayStateOff);
                Log.d(TAG, "i vaut" + i + "Et on a comme mot courant = " + word.getWord());

                switch (word.getState()) {
                    case "VERB":
                        state[k] = word.getWord();
                        Log.d(TAG, "J'ai détecté verbe");
                        word = NextKeyword(sentence, word.getPos() + 1, listLocation, arrayStateOn, arrayStateOff);

                        switch (word.getState()) {
                            case "LOCATION":
                                Log.d(TAG, "J'ai détecté un emplacement");
                                location[k] = word.getWord();
                                word = NextKeyword(sentence, word.getPos() + 1, listLocation, arrayStateOn, arrayStateOff);

                                switch (word.getState()) {
                                    case "VERB":
                                        listLamp.add(new Lamp(location[k], state[k], 0));
                                        k++;
                                        l = word.getPos();
                                        continue;

                                    case "LUMINOSITY":
                                        brightness[k] = Integer.parseInt(word.getWord());
                                        listLamp.add(new Lamp(location[k], state[k], brightness[k]));
                                        k++;
                                        l = word.getPos() + 1;
                                        Log.d(TAG, "J'ai détecté luminosité" + k + l);
                                        continue;

                                    case "LOCATION":
                                        m = 1;
                                        tmp = 0;
                                        // Tant que l'utilisateur prononce des emplacements
                                        while (word.getState().equals("LOCATION")) {
                                            location[k + m] = word.getWord();
                                            m++;
                                            word = NextKeyword(sentence, word.getPos() + 1, listLocation, arrayStateOn, arrayStateOff);
                                        }
                                        if (word.getState().equals("LUMINOSITY")) {
                                            brightness[k] = Integer.parseInt(word.getWord());
                                        } else {
                                            brightness[k] = 0;
                                        }
                                        for (int j = 0; j < m; j++) {
                                            listLamp.add(new Lamp(location[k + j], state[k], brightness[k]));
                                            tmp++;
                                        }
                                        k += tmp;
                                        Log.d(TAG, "Le mot courant est " + word.getWord());

                                        l = word.getPos();
                                        continue;
                                    case "END":
                                        listLamp.add(new Lamp(location[k], state[k], 0));
                                        k++;
                                }
                                break;

                            case "LUMINOSITY":
                                brightness[k] = Integer.parseInt(word.getWord());
                                word = NextKeyword(sentence, word.getPos() + 1, listLocation, arrayStateOn, arrayStateOff);

                                switch (word.getState()) {
                                    case "LOCATION":
                                        location[k] = word.getWord();
                                        listLamp.add(new Lamp(location[k], state[k], brightness[k]));
                                        k++;
                                        l = word.getPos() + 1;
                                        continue;

                                }

                        }
                        break;

                    case "LOCATION":
                        location[k] = word.getWord();
                        word = NextKeyword(sentence, word.getPos() + 1, listLocation, arrayStateOn, arrayStateOff);

                        switch (word.getState()) {
                            case "LUMINOSITY":
                                brightness[k] = Integer.parseInt(word.getWord());
                                listLamp.add(new Lamp(location[k], "on", brightness[k]));
                                k++;

                            case "VERB":
                                state[k] = word.getWord();
                                word = NextKeyword(sentence, word.getPos() + 1, listLocation, arrayStateOn, arrayStateOff);
                                switch (word.getState()) {
                                    case "LUMINOSITY":
                                        brightness[k] = Integer.parseInt(word.getWord());
                                        listLamp.add(new Lamp(location[k], state[k], brightness[k]));
                                        k++;
                                }
                        }
                    case "LUMINOSITY":
                }
            }
        }
        return listLamp;
    }


    private NextWord NextKeyword(String[] sentence, int pos, List<String> arrayLocation, String[] arrayStateOn, String[] arrayStateOff) {
        for (int i = pos; i < sentence.length; i++) {
            char c = sentence[i].charAt(0);

            //Détection si le mot est un emplacement
            for (int j = 0; j < arrayLocation.size(); j++) {
                if (arrayLocation.get(j).equals(sentence[i])) {
                    NextWord nextWord = new NextWord(arrayLocation.get(j), i, "LOCATION");
                    return nextWord;
                }
            }

            //Détection si le mot est une action positive
            if (Arrays.asList(arrayStateOn).contains(sentence[i])) {
                NextWord nextWord = new NextWord("on", i, "VERB");
                return nextWord;
            }

            //Détection si le mot est une action négative
            if (Arrays.asList(arrayStateOff).contains(sentence[i])) {
                NextWord nextWord = new NextWord("off", i, "VERB");
                return nextWord;
            }
            if (i < sentence.length - 1) {
                if (((sentence[i].equals("et")) || (sentence[i].equals("est"))) && ((sentence[i + 1].equals("un")) || (sentence[i + 1].equals("1")) || (sentence[i + 1].equals("dans")))) {
                    NextWord nextWord = new NextWord("off", i + 1, "VERB");
                    return nextWord;
                }
            }

            //Détection si le mot est un nombre entre 1 et 100
            if (c > '0' && c <= '9') {
                int number = Integer.parseInt(sentence[i]);
                // On vérifie que ce nombre possède une valeur cohérente pour la luminosité
                if (number > 0 && number <= 100) {
                    NextWord nextWord = new NextWord(sentence[i], i, "LUMINOSITY");
                    return nextWord;
                }

            }

        }

        NextWord nextWord = new NextWord("NULL", 0, "END");
        return nextWord;
    }
}
