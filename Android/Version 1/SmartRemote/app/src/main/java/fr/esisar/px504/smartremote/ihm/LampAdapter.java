package fr.esisar.px504.smartremote.ihm;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import fr.esisar.px504.smartremote.R;

/**
 * Created by valentin on 02/11/2017.
 */


public class LampAdapter extends BaseAdapter {


    private Context context;
    private List<Lamp> list;
    private final Resources resources;


    /**
     *
     * @param context : Activité dans laquelle l'adapter est instancié
     * @param list : Liste des lampes
     */

    public LampAdapter(Context context, List<Lamp> list) {
        this.context = context;
        this.list = list;
        resources = context.getResources();
    }


    /**
     *
     * @return : Taille de la liste de lampe
     */

    @Override
    public int getCount() {
        return list.size();
    }

    /**
     *
     * @param position : Position dans la liste d'un objet Lampe
     * @return Lampe associée à la position
     */

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    /**
     *
     * @param position : Position dans la liste d'un objet Lampe
     * @return : Identifiant associé à l'objet Lampe de cette position (Ici la position en elle même)
     */

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param position : Position dans la liste
     * @param convertView : View précédement chargée (Si existante)
     * @param parent : View Parent
     * @return : View initialisée
     *
     * Gestion de l'affichage de la liste de View
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
            init(view, (Lamp) getItem(position));

        } else {

            view = LayoutInflater.from(context).inflate(R.layout.lamp_view, parent, false);
            init(view, (Lamp) getItem(position));
        }
        return view;
    }

    /**
     *
     * @param view : View à initialiser
     * @param lamp : Lampe associée à la View
     *
     * Initialise la les différents éléments de la View d'une lampe
     */

    public void init(View view, final Lamp lamp) {
        ImageButton lampButton = view.findViewById(R.id.lamp_button);
        TextView brightness = view.findViewById(R.id.brightness_value);
        TextView location = view.findViewById(R.id.lamp_location);
        final SeekBar brightnessBar = view.findViewById(R.id.brightness_bar);

        location.setText(lamp.getLocation());
        if (lamp.getState().equals("off")) {
            brightness.setText("Luminosité : 0%");
            lampButton.setImageDrawable(resources.getDrawable(R.drawable.lamp, null));
            brightnessBar.setProgress(1);

        } else {
            brightness.setText("Luminosité : " + lamp.getbrightness() + "%");
            lampButton.setImageDrawable(resources.getDrawable(R.drawable.lampon, null));
            brightnessBar.setProgress(lamp.getbrightness());


        }

        lampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lamp.getState().equals("off")) {
                    ((LampControlIHM) context).changeState(new Lamp(lamp.getLocation(), "on", lamp.getbrightness()));
                } else {
                    ((LampControlIHM) context).changeState(new Lamp(lamp.getLocation(), "off", lamp.getbrightness()));
                }

            }
        });

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int brightness = lamp.getbrightness();

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (lamp.getState().equals(("off"))) {
                    brightness = 0;
                } else {
                    brightness = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (brightness != 0) {
                    ((LampControlIHM) context).changeState(new Lamp(lamp.getLocation(), "on", brightness));
                } else if(lamp.getState() == "on"){
                    ((LampControlIHM) context).changeState(new Lamp(lamp.getLocation(), "on", 1));
                } else {
                    seekBar.setProgress(1);
                }

            }
        });
    }
}
