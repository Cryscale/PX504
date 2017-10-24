package voicecontrol.valentin.testwebsocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {



    private EditText msgPerso;
    private TextView response;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://localhost:8080");
        } catch (URISyntaxException e) {
            System.out.println("ERREUR");
        }
    }

    JSONObject msg = new JSONObject();

    public void sendMessage(View V) {

        try {
            msg.put("user", "valentin");
            msg.put("password", 1111);
            mSocket.emit("getListLamp", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msgPerso = (EditText) findViewById(R.id.message);
        response = (TextView) findViewById(R.id.response);

        mSocket.connect();

        mSocket.on("listLamp", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject msg = (JSONObject)args[0];

                        try {
                            if(msg.getBoolean("succes")) {
                                response.setText("Connexion réussie");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }


}
