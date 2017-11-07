package fr.esisar.px504.smartremote.ihm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
    private WebSocketClient webSocketClient;
    private WebSocketHandler webSocketHandler = new WebSocketHandler(null, this);
    private List<Lamp> list;
    private LampAdapter adapter;


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
        //define toolbar **************************************************************************
        //define objet toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //change text in toolbar
        //TextView titleTextView = (TextView) findViewById(R.id.titreTextView);
        this.titleTextView = (TextView) findViewById(R.id.titreTextView);
        titleTextView.setText(R.string.activity_lamp);


        //define image button menu in toolbar
        final ImageButton menuImageButton = (ImageButton) findViewById(R.id.menuButton);
        menuImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LampControlIHM.this, MenuContollIHM.class);
                intent.putExtra(ACTIVITY_NAME, R.string.activity_lamp);
                startActivity(intent);
            }
        });


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
