package fr.esisar.px504.smartremote.vocalcontrol;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    private Integer[] location_position;
    private Integer[] state_position;
    private Integer[] brightness_position;

    private Lamp lamp;
    private Resources res;
    private Context context;
    private static final String TAG = "vocalControl";


    public VocalControl(Context context){
        this.context = context;
    }
    //Analyse syntaxique

    public List<Lamp> SyntacticAnalysis(ArrayList<String> text) {
        location = new String[10];
        state = new String[10];
        brightness = new Integer[10];

        //Parse la phrase
        Resources res = context.getResources();
        String[] sentence = text.get(0).split(" ");
        sentence[0] = sentence[0].toLowerCase();

        int nb_location = 0;
        int nb_state = 0;
        int nb_brightness = 0;
        List<Lamp> listLamp = new ArrayList<Lamp>();

        for(int i=0; i<sentence.length; i++){

            // Recherche de la localisation ( cuisine, salon)
            String[] arrayLocation = res.getStringArray(R.array.location_word);
            for(int j=0; j < arrayLocation.length; j++){
                if (arrayLocation[j].equals(sentence[i])){
                    location[nb_location] = arrayLocation[j];
                    nb_location++;
                }
            }

            // Recherche de l'action (allumé, éteindre,etc)
            String[] arrayStateOn = res.getStringArray(R.array.action_word_on);
            String[] arrayStateOff = res.getStringArray(R.array.action_word_off);
            if (Arrays.asList(arrayStateOn).contains(sentence[i])) {
                state[nb_state] = "on";
                nb_state++;
            }
            else if (Arrays.asList(arrayStateOff).contains(sentence[i])){
                state[nb_state] = "off";
                nb_state++;
                brightness[nb_brightness]= 0;
                nb_brightness++;
            }

            // Recherche de laluminosité (1, 2, ...,100)
            //  On teste si le mot courant commence par un chiffre
            char c = sentence[i].charAt(0);
            if (c > '0' && c <= '9') {
                int number = Integer.parseInt(sentence[i]);
                // On vérifie que ce nombre possède une valeur cohérente pour la luminosité
                if(number > 0 && number <= 100){
                    brightness[nb_brightness] = number;
                    nb_brightness++;
                }
            }
            else if (nb_brightness == 0 && i == sentence.length -1){
                brightness[nb_brightness] = 0;

            }
        }

        //traitement des différentes requetes
        for (int x=0; x<nb_location; x++){
            if (nb_location == nb_state){
                if(nb_location == nb_brightness){
                    //allume le sallon à 100 et allume la cuisine à 50
                    listLamp.add(new Lamp(location[x],state[x],brightness[x]));
                }   else{
                    //TODO solution
                    //allume le sallon et change la cuisine à 50
                    listLamp.add(new Lamp(location[x],state[x],brightness[0]));
                }
            }else{
                if(nb_location == nb_brightness) {
                    //allume le salon a 50 et la cuisine à 100
                    //allume le salon et la cuisine a 50 et 100
                    listLamp.add(new Lamp(location[x], state[0], brightness[x]));
                }else {
                    //allume le salon et la cuisine à 50
                    listLamp.add(new Lamp(location[x],state[0],brightness[0]));
                }
            }
        }

        return listLamp;
    }

    public List<Lamp> SyntacticAnalysis_version2(ArrayList<String> text) {
        location = new String[100];
        state = new String[100];
        brightness = new Integer[100];
        location_position = new Integer[100];
        state_position = new Integer[100];
        brightness_position = new Integer[100];

        //Parse la phrase
        Resources res = context.getResources();
        String[] sentence = text.get(0).split(" ");
        sentence[0] = sentence[0].toLowerCase();

        int nb_location = 0;
        int nb_state = 0;
        int nb_brightness = 0;
        List<Lamp> listLamp = new ArrayList<Lamp>();

        for(int i=0; i<sentence.length; i++){

            // Recherche de la localisation ( cuisine, salon)
            String[] arrayLocation = res.getStringArray(R.array.location_word);
            for(int j=0; j < arrayLocation.length; j++){
                if (arrayLocation[j].equals(sentence[i])){
                    location[i] = arrayLocation[j];
                    location_position[nb_location] = i;
                    Log.d(TAG, "jai trouvé la location"+ nb_location+" "+ arrayLocation[j]+ " au mot "+ i);
                    nb_location++;
                }
            }

            // Recherche de l'action (allumé, éteindre,etc)
            String[] arrayStateOn = res.getStringArray(R.array.action_word_on);
            String[] arrayStateOff = res.getStringArray(R.array.action_word_off);
            if (Arrays.asList(arrayStateOn).contains(sentence[i])) {
                state[i] = "on";
                state_position[nb_state] = i;
                Log.d(TAG, "jai trouvé une action"+ nb_state +" "+ sentence[i]+ " au mot "+ i);
                nb_state++;
            }
            else if (Arrays.asList(arrayStateOff).contains(sentence[i])){
                state[i] = "off";
                state_position[nb_state] = i;
                Log.d(TAG, "jai trouvé une action"+ nb_state+" "+ sentence[i]+ " au mot "+ i);
                nb_state++;
                //brightness[nb_brightness]= 0;
                //nb_brightness++;
            }

            // Recherche de laluminosité (1, 2, ...,100)
            //  On teste si le mot courant commence par un chiffre
            char c = sentence[i].charAt(0);
            if (c > '0' && c <= '9') {
                int number = Integer.parseInt(sentence[i]);
                // On vérifie que ce nombre possède une valeur cohérente pour la luminosité
                if(number > 0 && number <= 100){
                    brightness[i] = number;
                    brightness_position[nb_brightness] = i;
                    Log.d(TAG, "jai trouvé une luminosité"+ nb_brightness +" "+ number + " au mot "+ i);
                    nb_brightness++;
                }
            }
           /* else if (nb_brightness == 0 && i == sentence.length -1){
                brightness[nb_brightness] = 0;
                nb_brightness++;

            }*/
        }

        //traitement des différentes requetes
        for (int x = 0; x< nb_location ; x++){
            int max =0;
            int min = sentence.length;
            for (int y = 0; y<sentence.length; y++){
                if(y <= nb_state && state_position[y] != null){
                    if (state_position[y]>= max && state_position[y]<location_position[x]){
                        max = state_position[y];
                        Log.d(TAG, "j'anlayse la plus grande action avant la localisation " +
                                "qui est actuellement "+ max);
                    }
                }
                if(y <= nb_brightness && brightness_position[y] != null ){
                    if (brightness_position[y] <= min && brightness_position[y] > location_position[x]) {
                        min = brightness_position[y];
                        Log.d(TAG, "j'analyse la plus petite luminosité " +
                                "après la localisation et avant la prochaine " +
                                "qui est actuellement"+ min);
                    }
                }
            }
            if (state[max] != null ){
                if (brightness[min]!= null){
                    listLamp.add(new Lamp(location[location_position[x]],
                            state[max],brightness[min]));
                    Log.d(TAG, "j'ai une lampe à 3 arguments");
                }else{
                    listLamp.add(new Lamp(location[location_position[x]],
                            state[max],0));
                    Log.d(TAG, "j'ai une lampe à 2 arguments");
                }
            }else{
                Log.d(TAG, "j'ai que " + location[location_position[x]]);
            }
        }

        return listLamp;
    }


    public List<Lamp> SyntacticAnalysis_version3(ArrayList<String> text) {
        location = new String[100];
        state = new String[100];
        brightness = new Integer[100];
        location_position = new Integer[100];
        state_position = new Integer[100];
        brightness_position = new Integer[100];

        //Parse la phrase
        Resources res = context.getResources();
        String[] sentence = text.get(0).split(" ");
        sentence[0] = sentence[0].toLowerCase();
        int k=0;
        int i;
        int l=0;
        int m;
        int tmp;
        List<Lamp> listLamp = new ArrayList<Lamp>();
        String[] arrayLocation = res.getStringArray(R.array.location_word);
        String[] arrayStateOn = res.getStringArray(R.array.action_word_on);
        String[] arrayStateOff = res.getStringArray(R.array.action_word_off);
        NextWord word;

        for(i=0; i<sentence.length; i++) {
            // Tant que l'on n'arrive pas à la prochaine instruction
            if (i < l){
                continue;
            }
            else if(i == l){
                word = NextKeyword(sentence, i, arrayLocation, arrayStateOn, arrayStateOff);
                Log.d(TAG, "i vaut" + i + "Et on a comme mot courant = " + word.getWord());

                switch (word.getState()) {
                    case "VERB":
                        state[k] = word.getWord();
                        Log.d(TAG, "J'ai détecté verbe");
                        word = NextKeyword(sentence, word.getPos() + 1, arrayLocation, arrayStateOn, arrayStateOff);

                        switch (word.getState()) {
                            case "LOCATION":
                                Log.d(TAG, "J'ai détecté un emplacement");
                                location[k] = word.getWord();
                                word = NextKeyword(sentence, word.getPos() + 1, arrayLocation, arrayStateOn, arrayStateOff);

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
                                        tmp=0;
                                        // Tant que l'utilisateur prononce des emplacements
                                        while(word.getState().equals("LOCATION")){
                                            location[k+m] = word.getWord();
                                            m++;
                                            word = NextKeyword(sentence, word.getPos() + 1, arrayLocation, arrayStateOn, arrayStateOff);
                                        }
                                        if(word.getState().equals("LUMINOSITY")){
                                            brightness[k] = Integer.parseInt(word.getWord());
                                        }
                                        else{
                                            brightness[k] = 0;
                                        }
                                        for(int j=0; j<m;j++){
                                            listLamp.add(new Lamp(location[k+j], state[k], brightness[k]));
                                            tmp++;
                                        }
                                        k+=tmp;
                                        Log.d(TAG, "Le mot courant est " + word.getWord());

                                        l= word.getPos();
                                        continue;
                                    case "END":
                                        listLamp.add(new Lamp(location[k], state[k], 0));
                                        k++;
                                }
                                break;

                            case "LUMINOSITY":
                                brightness[k] = Integer.parseInt(word.getWord());
                                word = NextKeyword(sentence, word.getPos() + 1, arrayLocation, arrayStateOn, arrayStateOff);

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
                        word = NextKeyword(sentence, word.getPos() + 1, arrayLocation, arrayStateOn, arrayStateOff);

                        switch (word.getState()) {
                            case "LUMINOSITY":
                                brightness[k] = Integer.parseInt(word.getWord());
                                listLamp.add(new Lamp("on", location[k], brightness[k]));
                                k++;

                            case "VERB":
                                state[k] = word.getWord();
                                word = NextKeyword(sentence, word.getPos() + 1, arrayLocation, arrayStateOn, arrayStateOff);
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



    public NextWord NextKeyword(String[] sentence, int pos, String[] arrayLocation, String[] arrayStateOn, String[] arrayStateOff){
        for(int i=pos; i<sentence.length; i++){
            char c = sentence[i].charAt(0);

            //Détection si le mot est un emplacement
            for(int j=0; j < arrayLocation.length; j++) {
                if (arrayLocation[j].equals(sentence[i])) {
                    NextWord nextWord = new NextWord(arrayLocation[j], i, "LOCATION");
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
            if(i< sentence.length-1){
                if( ((sentence[i].equals("et")) || (sentence[i].equals("est"))) && ((sentence[i+1].equals("un")) || (sentence[i+1].equals("1"))) ){
                    NextWord nextWord = new NextWord("off", i+1, "VERB");
                    return nextWord;
                }
            }

            //Détection si le mot est un nombre entre 1 et 100
            if(c > '0' && c <= '9'){
                int number = Integer.parseInt(sentence[i]);
                // On vérifie que ce nombre possède une valeur cohérente pour la luminosité
                if(number > 0 && number <= 100) {
                    NextWord nextWord = new NextWord(sentence[i], i, "LUMINOSITY");
                    return nextWord;
                }

            }

        }

        NextWord nextWord = new NextWord("NULL", 0,"END");
        return nextWord;
    }
}
