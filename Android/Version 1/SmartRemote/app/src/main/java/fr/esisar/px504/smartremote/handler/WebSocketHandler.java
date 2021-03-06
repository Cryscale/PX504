package fr.esisar.px504.smartremote.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.esisar.px504.smartremote.ihm.Lamp;
import fr.esisar.px504.smartremote.ihm.LampControlIHM;
import fr.esisar.px504.smartremote.ihm.MainActivity;
import fr.esisar.px504.smartremote.websocket.WebSocketClient;

/**
 * Created by valentin on 31/10/2017.
 */

public class WebSocketHandler extends Handler {

    private LampControlIHM lampActivity;


    /**
     * @param lampIHMContext : LampActivity pour le contrôle des lampes
     */

    public WebSocketHandler(Context lampIHMContext) {
        super();


        this.lampActivity = (LampControlIHM) lampIHMContext;
    }

    /**
     * @param msg : Message reçu par le Handler
     *            <p>
     *            Interprétation des message reçus par le Handler
     */

    @Override
    public void handleMessage(Message msg) {

        super.handleMessage(msg);
        JSONObject data = (JSONObject) msg.obj;

        switch (msg.arg1) {

            case WebSocketClient.LIST_LAMP:

                try {
                    JSONArray list = data.getJSONArray("list");
                    lampActivity.initList(list);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;

            case WebSocketClient.STATE_CHANGED:

                try {
                    lampActivity.updateList(new Lamp(data.getString("location"), data.getString("state"), data.getInt("brightness")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;


            case WebSocketClient.ERROR:

                /**
                 * A completer ( Avec Arduino )
                 */

                break;

            case WebSocketClient.DISCONNECTED:

                lampActivity.disconnect();
        }
        //}

    }
}
