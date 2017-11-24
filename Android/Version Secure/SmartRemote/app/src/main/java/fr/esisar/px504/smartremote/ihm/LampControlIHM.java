package fr.esisar.px504.smartremote.ihm;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.esisar.px504.smartremote.R;
import fr.esisar.px504.smartremote.handler.WebSocketHandler;
import fr.esisar.px504.smartremote.websocket.WebSocketClient;

/**
 * Created by alexia on 19/10/2017.
 */

public class LampControlIHM extends AppCompatActivity {

    public final static String ACTIVITY_NAME = "com.ltm.ltmactionbar.MESSAGE";

    private TextView titleTextView;
    private String user;
    private String cookie;
    private WebSocketClient webSocketClient;
    private WebSocketHandler webSocketHandler = new WebSocketHandler(this);
    private List<Lamp> list;
    private LampAdapter adapter;


    //Test menu
    private ListView menuElementsList; //Menu
    private DrawerLayout menuDrawerLayout;
    private NavigationView menuNavigView;
    private ActionBarDrawerToggle menuDrawerToggle; //Gère l'ouverture et la fermeture du menu
    private String[] list_menu_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp);

        Intent previousActivity = getIntent();
        this.user = previousActivity.getStringExtra("username");
        this.cookie = previousActivity.getStringExtra("cookie");
        System.out.println("ZADZADAZD");
        System.out.println(cookie);

        this.webSocketClient = new WebSocketClient(this.webSocketHandler, this.cookie);
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

        //active le menu
        //recupere les layout
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_lamp);
        menuElementsList = (ListView) findViewById(R.id.list_menu);
        menuNavigView = (NavigationView) findViewById(R.id.menu_navigView);
        menuDrawerToggle = new ActionBarDrawerToggle(this,menuDrawerLayout,0,0);
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
     *
     * @param list : Liste obtenue à la réception du message WebSocket "listLamp"
     *
     * Initialise la liste de lampe de l'application a partir de la liste obtenue dans le message WebSocket
     */

    public void initList(JSONArray list) {
        try {
            for(int i =0; i< list.length(); i++) {
                JSONObject lamp = (JSONObject) list.get(i);
                this.list.add(new Lamp(lamp.getString("location"), lamp.getString("state"), lamp.getInt("brightness")));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param lamp : Lampe qui a changé d'état
     *
     * Actualise la liste de lampe en modifiant celle qui a changé d'état
     */

    public void updateList(Lamp lamp) {

        for(Lamp previousLamp : list) {

            if(previousLamp.getLocation().equals(lamp.getLocation())) {
                list.set(list.indexOf(previousLamp), lamp);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     *
     * @param lamp : Lampe destinée au changement d'état
     *
     * Utilise le client WebSocket pour envoyer une demande de changement d'état d'une lampe
     */

    public void changeState(Lamp lamp) {
        webSocketClient.changeState(lamp);
    }


}
