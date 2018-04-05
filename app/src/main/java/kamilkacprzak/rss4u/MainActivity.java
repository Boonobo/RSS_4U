package kamilkacprzak.rss4u;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import static android.view.Menu.NONE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String title = "",
            url = "";
    private Menu menu;
    private SharedPreferences itemsId_strInt ;
    private SharedPreferences urlForTitle_strStr;
    private SharedPreferences.Editor editItems, editUrl;
    private int id = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        itemsId_strInt = this.getSharedPreferences("itemsId_strInt",0);
        editItems = itemsId_strInt.edit();
        urlForTitle_strStr = this.getSharedPreferences("urlForTitle_strStr",0);
        editUrl = urlForTitle_strStr.edit();

        menu = navigationView.getMenu();
        menu.add(NONE, 100, NONE, "Add a Feed");
        id += 1;
        menu.add("Your Feeds");

        Map<String, ?> allEntries = itemsId_strInt.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                id = Integer.parseInt(entry.getValue().toString());
                menu.add(11, id,10000- id, entry.getKey());

        }
        id += 1;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == 100) {
            showAddFeedDialog(MainActivity.this);

            // TODO: here adding feeds

            title = "";
            url = "";
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAddFeedDialog(Context c) {
        final EditText feedEditText = new EditText(c);
        final EditText urlEditText = new EditText(c);
        final Context inC = c;
        LinearLayout layout = new LinearLayout(c);

        layout.setOrientation(LinearLayout.VERTICAL);
        feedEditText.setHint("Enter title");
        layout.addView(feedEditText);
        urlEditText.setHint("Enter URL");
        layout.addView(urlEditText);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new feed")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = String.valueOf(feedEditText.getText());
                        url = String.valueOf(urlEditText.getText());
                        if(title.isEmpty() || url.isEmpty()){
                            AlertDialog ad = new AlertDialog.Builder(inC).setMessage("You have to enter a title and url!").setPositiveButton("Ok",null).create();
                            ad.show();
                        }else if ( itemsId_strInt.getInt(title,0) != 0){
                            AlertDialog ad = new AlertDialog.Builder(inC).setMessage("This title already exists, pick another one!").setPositiveButton("Ok",null).create();
                            ad.show();
                        }else{
                            addNewTitle(title,url);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void addNewTitle(String title, String url){
            editItems.putInt(title,id);
            menu.add(11,id,10000 - id,title);
            editUrl.putString(title,url);
            id +=1;
            editItems.commit();
            editUrl.commit();
    }
}
