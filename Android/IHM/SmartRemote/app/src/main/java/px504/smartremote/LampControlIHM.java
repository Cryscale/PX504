package px504.smartremote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by alexia on 19/10/2017.
 */

public class LampControlIHM extends AppCompatActivity {

    public final static String ACTIVITY_NAME = "com.ltm.ltmactionbar.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);

        Log.i("my_app", "begin");
        //define toolbar **************************************************************************
        //define objet toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //change text in toolbar
        TextView titleTextView = (TextView) findViewById(R.id.titreTextView);
        titleTextView.setText(R.string.activity_lamp);
        //define image button menu in toolbar
        final ImageButton menuImageButton = (ImageButton) findViewById(R.id.menuButton);
        menuImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LampControlIHM.this,MenuContollIHM.class);
                intent.putExtra(ACTIVITY_NAME, R.string.activity_lamp);
                startActivity(intent);
            }
        });

        //change picture of lamp button
        final ImageButton lampLoc1ImageButton = (ImageButton) findViewById(R.id.lamp_loc1_imageButton);
        changeColorLampIHM(lampLoc1ImageButton);
        final ImageButton lampLoc2ImageButton = (ImageButton) findViewById(R.id.lamp_loc2_imageButton);
        changeColorLampIHM(lampLoc2ImageButton);
        final ImageButton lampLoc3ImageButton = (ImageButton) findViewById(R.id.lamp_loc3_imageButton);
        changeColorLampIHM(lampLoc3ImageButton);
        final ImageButton lampLoc4ImageButton = (ImageButton) findViewById(R.id.lamp_loc4_imageButton);
        changeColorLampIHM(lampLoc4ImageButton);
        final ImageButton lampLoc5ImageButton = (ImageButton) findViewById(R.id.lamp_loc5_imageButton);
        changeColorLampIHM(lampLoc5ImageButton);

        //Display seekbar value changing
        final SeekBar seekBarLoc1 = (SeekBar) findViewById(R.id.seekBar_loc1);
        final TextView seekBarValueLoc1TextView = (TextView) findViewById(R.id.luminosite_valeur_loc1_textView);
        displaySeekBarValue(seekBarLoc1, seekBarValueLoc1TextView);

        final SeekBar seekBarLoc2 = (SeekBar) findViewById(R.id.seekBar_loc2);
        final TextView seekBarValueLoc2TextView = (TextView) findViewById(R.id.luminosite_valeur_loc2_textView);
        displaySeekBarValue(seekBarLoc2, seekBarValueLoc2TextView);

        final SeekBar seekBarLoc3 = (SeekBar) findViewById(R.id.seekBar_loc3);
        final TextView seekBarValueLoc3TextView = (TextView) findViewById(R.id.luminosite_valeur_loc3_textView);
        displaySeekBarValue(seekBarLoc3, seekBarValueLoc3TextView);

        final SeekBar seekBarLoc4= (SeekBar) findViewById(R.id.seekBar_loc4);
        final TextView seekBarValueLoc4TextView = (TextView) findViewById(R.id.luminosite_valeur_loc4_textView);
        displaySeekBarValue(seekBarLoc4, seekBarValueLoc4TextView);

        final SeekBar seekBarLoc5 = (SeekBar) findViewById(R.id.seekBar_loc5);
        final TextView seekBarValueLoc5TextView = (TextView) findViewById(R.id.luminosite_valeur_loc5_textView);
        displaySeekBarValue(seekBarLoc5, seekBarValueLoc5TextView);

    }

    public void displaySeekBarValue(final SeekBar seekBar, final TextView seekBarValueTextView){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                seekBarValueTextView.setText(String.valueOf(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    public void changeColorLampIHM(final ImageButton imageButton){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageButton.getDrawable().getConstantState() == getDrawable(R.drawable.lamp).getConstantState()) {
                    //TODO send message to light on lamp
                    imageButton.setImageDrawable(getDrawable(R.drawable.lampon));
                } else {
                    //TODO send message to light off lamp
                    imageButton.setImageDrawable(getDrawable(R.drawable.lamp));
                }
            }
        });
    }


}
