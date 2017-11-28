package fr.esisar.px504.smartremote.ihm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import fr.esisar.px504.smartremote.R;
import fr.esisar.px504.smartremote.handler.WebSocketHandler;
import fr.esisar.px504.smartremote.websocket.WebSocketClient;

/**
 * Created by alexia on 19/10/2017.
 */

public class MainActivity extends AppCompatActivity {

    private String user;
    private WebSocketClient webSocketClient;
    private WebSocketHandler webSocketHandler = new WebSocketHandler(this, null);
    private String pwdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //define toolbar **************************************************************************
        //define objet toolbar
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //define connection ***********************************************************************
        //define connection variables
        final EditText logginEditText = (EditText) findViewById(R.id.logginEditText);
        logginEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    user = ((EditText) view).getText().toString();
                }
            }
        });
        final EditText pwdEditText = (EditText) findViewById(R.id.pwdEditText);

        webSocketClient = new WebSocketClient(webSocketHandler);

        //define connection button
        Button connexionButton = (Button) findViewById(R.id.connexionButton);
        connexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //webSocketClient.login(user, pwdEditText.getText().toString());
                Intent intent = new Intent(MainActivity.this,LampControlIHM.class);
                startActivity(intent);

            }
        });

    }

    public String getUser() {
        return this.user;
    }

    @Override
    public void onStop() {

        super.onStop();
        disconnect();


    }

    public void disconnect() {
        webSocketClient.disconnect();
    }

}