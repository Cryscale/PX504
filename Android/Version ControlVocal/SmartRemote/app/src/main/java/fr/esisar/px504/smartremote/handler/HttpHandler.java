package fr.esisar.px504.smartremote.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import fr.esisar.px504.smartremote.ihm.MainActivity;

/**
 * Created by valentin on 24/11/2017.
 */

public class HttpHandler extends Handler {

    private MainActivity mainActivity;

    public final static int LOGIN_SUCCESS = 1;
    public final static int LOGIN_FAIL_USER = 2;
    public final static int LOGIN_FAIL_PASSWORD = 3;
    public final static int LOGIN_FAIL = 4;

    public HttpHandler(Context context) {
        mainActivity = (MainActivity) context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);


        switch (msg.arg1) {

            case LOGIN_SUCCESS:
                mainActivity.startActivity((String) msg.obj);
                break;

            case LOGIN_FAIL:
                Toast.makeText(mainActivity, "Erreur inconnue lors de l'identification", Toast.LENGTH_LONG).show();
                break;

            case LOGIN_FAIL_USER:
                Toast.makeText(mainActivity, "Erreur : Mauvais identifiant", Toast.LENGTH_LONG).show();
                break;

            case LOGIN_FAIL_PASSWORD:
                Toast.makeText(mainActivity, "Erreur : Mauvais mot de passe", Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(mainActivity, "Erreur Handler", Toast.LENGTH_LONG).show();
                break;

        }

    }

}
