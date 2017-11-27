package fr.esisar.px504.smartremote.vocalcontrol;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
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

    private Lamp lamp;
    private Resources res;
    private Context context;

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

            // Recherche de l'action (luminosité)
            /*String[] arrayBrightness = res.getStringArray(R.array.brightness_number);
            for(int j=0; j < arrayBrightness.length; j++){
                if (arrayBrightness[j].equals(sentence[i])){
                    brightness[nb_brightness] = Integer.parseInt(arrayBrightness[j]);
                    nb_brightness++;
                }else if(j == arrayBrightness.length -1 & nb_brightness == 0){
                    brightness[nb_brightness] = 0;
                }
            }*/

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
        for (int x=0; x<nb_location;x++){
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

        // TODO : Envoyer message vers bd

        return listLamp;
    }


}
