package fr.esisar.px504.smartremote.websocket;

/**
 * Created by valentin on 31/10/2017.
 */


import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import fr.esisar.px504.smartremote.handler.WebSocketHandler;
import fr.esisar.px504.smartremote.ihm.Lamp;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by valentin on 31/10/2017.
 */

public class WebSocketClient {

    public static final int LIST_LAMP = 1;
    public static final int STATE_CHANGED = 2;
    public static final int LOGIN_RESULT = 3;
    public static final int ERROR = 4;
    public static final String WS_SERVER = "http://192.168.10.1:8080";
    final private WebSocketHandler webSocketHandler;

    /**
     * Création du client WebSocket
     */

    private Socket socket;
    {
        try {
            socket = IO.socket(WS_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public WebSocketClient(final WebSocketHandler handler) {

        this.webSocketHandler = handler;
        this.socket.connect();

        /**
         * Gestion de réception des différents messages WebSocket
         * Envoi de ceux-ci au Handler
         */

        this.socket.on("loginResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Message msg = webSocketHandler.obtainMessage();
                msg.arg1 = LOGIN_RESULT;
                msg.obj = (JSONObject) args[0];
                webSocketHandler.sendMessage(msg);
            }
        });

        this.socket.on("getListLampResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Message msg = webSocketHandler.obtainMessage();
                msg.arg1 = LIST_LAMP;
                msg.obj = (JSONObject) args[0];
                webSocketHandler.sendMessage(msg);

            }
        });

        this.socket.on("zigbeeFail", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Message msg = webSocketHandler.obtainMessage();
                msg.arg1 = ERROR;
                msg.obj = (JSONObject) args[0];
                webSocketHandler.sendMessage(msg);
            }
        });

        this.socket.on("setLampResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Message msg = webSocketHandler.obtainMessage();
                msg.arg1 = STATE_CHANGED;
                msg.obj = (JSONObject) args[0];
                webSocketHandler.sendMessage(msg);
            }
        });
    }

    /**
     * Méthode de déconnexion
     */

    public void disconnect() {
        socket.disconnect();
        socket.off();
    }


    /**
     * @param user     : Identifiant
     * @param password : Mot de passe
     *                 <p>
     *                 Envoi d'un message WebSocket de login
     */

    public void login(String user, String password) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("user", user);
            msg.put("password", password);
            socket.emit("login", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param user : Utilisateur associé aux lampes
     *             <p>
     *             Envoi d'un message WebSocket d'obtention de la liste de lampes
     */

    public void getListLamp(String user) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("user", user);
            socket.emit("getListLamp", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param lamp : Lampe destinée au changement d'état
     *             <p>
     *             Envoi d'un message WebSocket de changment d'état d'une lampe
     */

    public void changeState(Lamp lamp) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("location", lamp.getLocation());
            msg.put("state", lamp.getState());
            msg.put("brightness", lamp.getbrightness());
            socket.emit("setLamp", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}