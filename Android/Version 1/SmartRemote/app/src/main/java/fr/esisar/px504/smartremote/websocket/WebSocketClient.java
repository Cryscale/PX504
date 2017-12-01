package fr.esisar.px504.smartremote.websocket;

/**
 * Created by valentin on 31/10/2017.
 */


import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fr.esisar.px504.smartremote.handler.WebSocketHandler;
import fr.esisar.px504.smartremote.ihm.Lamp;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import okhttp3.OkHttpClient;

/**
 * Created by valentin on 31/10/2017.
 */

public class WebSocketClient {

    public static final int LIST_LAMP = 1;
    public static final int STATE_CHANGED = 2;
    public static final int DISCONNECTED = 3;
    public static final int ERROR = 4;
    public static final String WS_SERVER = "http://192.168.10.1:8080";
    final private WebSocketHandler webSocketHandler;

    /**
     * Création du client WebSocket
     */


    private Socket socket;


    public WebSocketClient(final WebSocketHandler handler, final String cookie) {

        this.webSocketHandler = handler;

        try {

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());


            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(allHostsValid)
                    .sslSocketFactory(sc.getSocketFactory(), new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    })
                    .build();

            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            this.socket = IO.socket("https://192.168.10.1:8080", opts);

            socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport)args[0];

                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Map<String, List<String>> headers = (Map<String, List<String>>)args[0];
                            // modify request headers
                            headers.put("Cookie", Arrays.asList(cookie));
                        }
                    });
                }
            });

            this.socket.connect();

            /**
             * Gestion de réception des différents messages WebSocket
             * Envoi de ceux-ci au Handler
             */

            this.socket.on("disconnect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Message msg = webSocketHandler.obtainMessage();
                    msg.arg1 = DISCONNECTED;
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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
            if(lamp.getbrightness() !=0) {
                msg.put("brightness", lamp.getbrightness());
            }

            socket.emit("setLamp", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}