package fr.esisar.px504.smartremote.ihm;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.esisar.px504.smartremote.R;
import fr.esisar.px504.smartremote.handler.WebSocketHandler;
import fr.esisar.px504.smartremote.vocalcontrol.VocalControl;
import fr.esisar.px504.smartremote.websocket.WebSocketClient;

/**
 * Created by alexia on 19/10/2017.
 */

public class LampControlIHM extends AppCompatActivity {

    public final static String ACTIVITY_NAME = "com.ltm.ltmactionbar.MESSAGE";

    private TextView titleTextView;
    private String user;
    private WebSocketClient webSocketClient;
    private WebSocketHandler webSocketHandler = new WebSocketHandler(null, this);
    private List<Lamp> list;
    private LampAdapter adapter;


    //Test menu
    private ListView menuElementsList; //Menu
    private DrawerLayout menuDrawerLayout;
    private NavigationView menuNavigView;
    private ActionBarDrawerToggle menuDrawerToggle; //Gère l'ouverture et la fermeture du menu
    private String[] list_menu_item;

    // Test vocal
    private VocalControl vocalControl = new VocalControl(this);
    private TextView textInput;
    private TextView analyse;
    private ImageButton buttonMic;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);

        Intent previousActivity = getIntent();
        this.user = previousActivity.getStringExtra("user");
        this.webSocketClient = new WebSocketClient(this.webSocketHandler);
        ListView listView = (ListView) findViewById(R.id.list_lamp);
        list = new ArrayList<Lamp>();
        adapter = new LampAdapter(this, list);
        listView.setAdapter(adapter);
        webSocketClient.getListLamp(user);

        Log.i("my_app", "begin");

        //define objet toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.activity_lamp);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Test vocal
        textInput = (TextView) findViewById(R.id.textInput);
        analyse = (TextView) findViewById(R.id.analyse);
        buttonMic = (ImageButton) findViewById(R.id.buttonMic);

        // Gestion de l'evenement du clic sur l'icone du micro
        buttonMic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        //active le menu
        //recupere les layout
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_lamp);
        menuElementsList = (ListView) findViewById(R.id.list_menu);
        menuNavigView = (NavigationView) findViewById(R.id.menu_navigView);
        menuDrawerToggle = new ActionBarDrawerToggle(this, menuDrawerLayout, 0, 0);
        menuDrawerLayout.addDrawerListener(menuDrawerToggle);

        menuNavigView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //TODO navigation de view en view

                }
                menuDrawerLayout.closeDrawers();  // CLOSE DRAWER
                return true;
            }
        });

        //TODO list dans le file string.xml avec array
        String[] menuItem = new String[]{
                "Lampes", "Musique", "Déconnexion"
        };
        //creartion de l'adapteur qui rempli le menu
        final ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,
                R.layout.element_menu, menuItem);
        menuElementsList.setAdapter(menuAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // THIS IS YOUR DRAWER/HAMBURGER BUTTON
            case android.R.id.home:
                menuDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // synchroniser le drawerToggle après la restauration via onRestoreInstanceState
        menuDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        menuDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        webSocketClient.disconnect();


    }

    /**
     * @param list : Liste obtenue à la réception du message WebSocket "listLamp"
     *             <p>
     *             Initialise la liste de lampe de l'application a partir de la liste obtenue dans le message WebSocket
     */

    public void initList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject lamp = (JSONObject) list.get(i);
                this.list.add(new Lamp(lamp.getString("location"), lamp.getString("state"), lamp.getInt("brightness")));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param lamp : Lampe qui a changé d'état
     *             <p>
     *             Actualise la liste de lampe en modifiant celle qui a changé d'état
     */

    public void updateList(Lamp lamp) {

        for (Lamp previousLamp : list) {

            if (previousLamp.getLocation().equals(lamp.getLocation())) {
                list.set(list.indexOf(previousLamp), lamp);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * @param lamp : Lampe destinée au changement d'état
     *             <p>
     *             Utilise le client WebSocket pour envoyer une demande de changement d'état d'une lampe
     */

    public void changeState(Lamp lamp) {
        webSocketClient.changeState(lamp);
    }


    // Test vocal

    private void promptSpeechInput() {
        // Creation d'un intent pour démarrer l'activite d'ecoute
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Choix du modele de language :
        // LANGUAGE_MODEL_FREE_FORM en general
        // LANGUAGE_MODEL_WEB_SEARCH pour les requetes plus courtes
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Langue par defaut du systeme
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        // Lancement de l'activite avec les paramètres choisis ci-dessus
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //String [] sentence = result.get(0).split(" ");

                    textInput.setText(result.get(0));
                    String text = "";
                    List<Lamp> listLamp = vocalControl.NewSyntacticAnalysis(result);

                    for (Lamp lamp : listLamp) {
                        text = text + lamp.getLocation() + lamp.getState() + lamp.getbrightness();
                        //webSocketClient.changeState(lamp);
                    }

                    analyse.setText(text);
                    break;
                }

            }
        }
    }
}
