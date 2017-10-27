package px504.smartremote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alexia on 19/10/2017.
 */

public class MenuContollIHM extends AppCompatActivity {


    private DrawerLayout menuLayout; //Layout Principal
    private ListView menuElementsList; //Menu
    private ActionBarDrawerToggle menuToggle; //Gère l'ouverture et la fermeture du menu


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        //define toolbar **************************************************************************
        //define objet toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //define text in toolbar
        TextView titleTextView = (TextView) findViewById(R.id.titreTextView);
        Intent intent = getIntent();
        //titleTextView.setText(intent.getStringExtra(LampControlIHM.ACTIVITY_NAME));
        //define image button menu in toolbar
        ImageButton menuImageButton = (ImageButton) findViewById(R.id.menuButton);
        menuImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Menu list
        //ListView menuListView = (ListView) findViewById(R.id.menuListView);
        menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
        ListView menuElementsList = (ListView) findViewById(R.id.menu_elements);

        menuLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        //TODO get from string.xml
        String lampes = getString(R.string.activity_lamp);
        String musique = getString(R.string.activity_musique);
        String deconnexion = getString(R.string.activity_deconnexion);

        String[] menuListActivity = new String[]{lampes,musique,deconnexion};

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuContollIHM.this,
                android.R.layout.simple_expandable_list_item_1, menuListActivity);
        //menuListView.setAdapter(adapter);
        menuElementsList.setAdapter(adapter);


        menuElementsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,long id) {
                //Faites ce que vous désirez suite au clic sur l’élément ayant comme index "position"...
                Log.i("may app", String.valueOf(position));
            }
        });

       /** menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ItemClicked item = adapter.getItemAtPosition(position);

                Intent intent = new Intent(MenuContollIHM.this,destinationActivity.class);
                startActivity(intent);
            }
        });**/
    }
}
