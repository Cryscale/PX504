package px504.smartremote;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by alexia on 19/10/2017.
 *
 */

public class MainActivity extends AppCompatActivity {

    private String logginText;
    private String pwdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //define toolbar **************************************************************************
        //define objet toolbar
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //define text in toolbar
        TextView titleTextView = (TextView) findViewById(R.id.titreTextView);
        titleTextView.setText(R.string.activity_connexion);
        //define image button menu in toolbar
        ImageButton menuImageButton = (ImageButton) findViewById(R.id.menuButton);
        menuImageButton.setVisibility(View.INVISIBLE);

        //define connection ***********************************************************************
        //define connection variables
        final EditText logginEditText = (EditText) findViewById(R.id.logginEditText);
        final EditText pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        //define connection button
        Button connexionButton = (Button) findViewById(R.id.connexionButton);
        connexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logginText = logginEditText.getText().toString();
                pwdText = pwdEditText.getText().toString();
                //TODO sécurity connection
                Intent intent = new Intent(MainActivity.this,LampControlIHM.class);
                startActivity(intent);

            }
        });

    }

}
