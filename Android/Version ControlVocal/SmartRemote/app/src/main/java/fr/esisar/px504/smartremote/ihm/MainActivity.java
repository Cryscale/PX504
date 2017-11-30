package fr.esisar.px504.smartremote.ihm;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import fr.esisar.px504.smartremote.R;
import fr.esisar.px504.smartremote.handler.HttpHandler;
import fr.esisar.px504.smartremote.handler.WebSocketHandler;
import fr.esisar.px504.smartremote.websocket.WebSocketClient;

/**
 * Created by alexia on 19/10/2017.
 */

public class MainActivity extends AppCompatActivity {

    private String user;
    private HttpHandler httpHandler = new HttpHandler(this);
    private final CookieManager cookieManager = new CookieManager();
    private HttpsURLConnection https;
    private EditText logginEditText;
    private EditText pwdEditText;

    private TrustManager[] trustAllCerts = new TrustManager[] {
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





    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //define toolbar **************************************************************************
        //define objet toolbar
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);



        CookieHandler.setDefault(cookieManager);
        HttpCookie cookie = new HttpCookie("lang", "fr");
        cookie.setDomain("SmartRemote");
        cookie.setPath("/");
        cookie.setVersion(0);
        try {
            cookieManager.getCookieStore().add(new URI("http://192.168.10.1:8080/"), cookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        //define connection ***********************************************************************
        //define connection variables
        logginEditText = (EditText) findViewById(R.id.logginEditText);
        logginEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    user = ((EditText) view).getText().toString();
                }
            }
        });
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);



        //define connection button
        Button connexionButton = (Button) findViewById(R.id.connexionButton);
        connexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            SSLContext sc = SSLContext.getInstance("SSL");
                            sc.init(null, trustAllCerts, new java.security.SecureRandom());

                            HostnameVerifier allHostsValid = new HostnameVerifier() {
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            };

                            MessageDigest DMsg = MessageDigest.getInstance("SHA-256");
                            String password = bytesToHex(DMsg.digest(pwdEditText.getText().toString().getBytes(StandardCharsets.UTF_8)));
                            String user = bytesToHex(DMsg.digest(logginEditText.getText().toString().getBytes(StandardCharsets.UTF_8)));
                            //System.out.println(password);

                            URL url = new URL("https://192.168.10.1:8080/login");
                            String urlParameters  = "username="+user+"&password="+password;
                            //String urlParameters  = "username="+logginEditText.getText()+"&password="+pwdEditText.getText();
                            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );

                            https = (HttpsURLConnection) url.openConnection();
                            https.setDoOutput(true);
                            https.setSSLSocketFactory(sc.getSocketFactory());
                            https.setHostnameVerifier(allHostsValid);
                            https.setChunkedStreamingMode(0);

                            OutputStream sender = new BufferedOutputStream(https.getOutputStream());
                            sender.write(postData);
                            sender.flush();

                            InputStream in = new BufferedInputStream(https.getInputStream());
                            readResponse(in);


                        } catch (KeyManagementException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            https.disconnect();
                        }
                    }
                });

                thread.start();


                //Intent intent = new Intent(MainActivity.this,LampControlIHM.class);
                //startActivity(intent);

            }
        });

    }

    public void startActivity(String username) {

        try {
            String cookie = cookieManager.getCookieStore()
                    .get(new URI("https://192.168.10.1:8080/"))
                    .get(0)
                    .getValue();

            if(cookie.contains("s%3A")) {
                cookie = cookie.replace("s%3A", "");
                System.out.println(cookie);
                cookie = cookie.substring(0, cookie.indexOf("."));
                Intent intent = new Intent(this, LampControlIHM.class);
                intent.putExtra("username", username);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
                thread.interrupt();
            } else {
                Toast.makeText(this, "Erreur Cookie", Toast.LENGTH_LONG).show();
            }




        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void readResponse(InputStream in) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String response = "";
        response = reader.readLine();
        Message msg = httpHandler.obtainMessage();


        switch (response) {
            case "OK":
                msg.arg1 = HttpHandler.LOGIN_SUCCESS;
                msg.obj = user;
                httpHandler.sendMessage(msg);
                break;

            case "ERROR PASSWORD":
                msg.arg1 = HttpHandler.LOGIN_FAIL_PASSWORD;
                httpHandler.sendMessage(msg);
                break;

            case "ERROR USERNAME":
                msg.arg1 = HttpHandler.LOGIN_FAIL_USER;
                httpHandler.sendMessage(msg);
                break;

            case "ERROR":
                msg.arg1 = HttpHandler.LOGIN_FAIL;
                httpHandler.sendMessage(msg);
                break;

            default:
                System.out.println("Erreur message");
                break;

        }
    }

    public static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(hash[i] & 0xff).toLowerCase();
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }



}