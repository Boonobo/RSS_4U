package kamilkacprzak.rss4u;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.text.Html.fromHtml;
import static android.view.Menu.NONE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String  mTitle = "",
                    mUrlValue = "";
    private int     mId = 100;
    private static Menu    sMenu;

    private SharedPreferences   mItemsId_strInt,
                                mUrlForTitle_strStr;
    private SharedPreferences.Editor    mEditItems,
                                        mEditUrl;

    private RecyclerView       mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private String             mUrlLink,
                               mTitleMainAct;
    private List<RssFeedModel> mFeedModelList;

    public class RssFeedModel {

        public String title;
        public String link;
        public String description;

        public RssFeedModel(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = fromHtml(description).toString();
        }
    }


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

        //Assigning view for feeds and refreshing
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });

        //Creating drawer menu based on shared preferences
        mItemsId_strInt = this.getSharedPreferences("mItemsId_strInt",0);
        mEditItems = mItemsId_strInt.edit();
        mUrlForTitle_strStr = this.getSharedPreferences("mUrlForTitle_strStr",0);
        mEditUrl = mUrlForTitle_strStr.edit();

        sMenu = navigationView.getMenu();
        sMenu.add(10, mId, NONE, "Add a Feed");
        mId++;
        sMenu.add(10,mId,NONE,"Manage Your Feeds");
        mId++;
        sMenu.add("Your Feeds").setEnabled(false);

        Map<String, ?> allEntries = mItemsId_strInt.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                mId = Integer.parseInt(entry.getValue().toString());
                // (10000 - mId) - making order of menu items from highest ID's to lowest
                sMenu.add(11, mId,10000- mId, entry.getKey());

        }
        mId++;

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = item.getTitle().toString();
        // Add Feed - Showing alert dialog to enter data and clear fields afterwards
        // else if - fetching feed for selected item from menu
        if (id == 100) {
            showAddFeedDialog(MainActivity.this);
            mTitle = "";
            mUrlValue = "";
        }else if(id == 101){
            Intent intent = new Intent(this,ManageFeedsActivity.class);
            startActivity(intent);
        }else if (mUrlForTitle_strStr.getString(title,"") != ""){
            mUrlLink =  mUrlForTitle_strStr.getString(title,"");
            mTitleMainAct = item.getTitle().toString();
            new FetchFeedTask().execute((Void) null);
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
        feedEditText.setHint("Enter Title");
        layout.addView(feedEditText);
        urlEditText.setHint("Enter URL");
        layout.addView(urlEditText);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add a new feed")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTitle = String.valueOf(feedEditText.getText());
                        mUrlValue = String.valueOf(urlEditText.getText());
                        if(mTitle.isEmpty() || mUrlValue.isEmpty()){
                            AlertDialog ad = new AlertDialog.Builder(inC)
                                                .setMessage("You have to enter a title and url!")
                                                .setPositiveButton("Ok",null)
                                                .create();
                            ad.show();
                        }else if ( mItemsId_strInt.getInt(mTitle,0) != 0){
                            AlertDialog ad = new AlertDialog.Builder(inC)
                                                .setMessage("This title already exists, pick another one!")
                                                .setPositiveButton("Ok",null)
                                                .create();
                            ad.show();
                        }else{
                            addNewFeedToMenu(mTitle, mUrlValue);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private boolean addNewFeedToMenu(String title, String url){
            mEditItems.putInt(title, mId);
            sMenu.add(11, mId,10000 - mId,title);
            mEditUrl.putString(title,url);
            mId++;

            mEditItems.commit();
            mEditUrl.commit();
            return true;
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(mUrlLink))
                return false;

            try {
                if(!mUrlLink.startsWith("http://") && !mUrlLink.startsWith("https://")){
                    mUrlLink = "http://" + mUrlLink;
                }

                URL url = new URL(mUrlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
                MainActivity.this.setTitle(mTitleMainAct);
            } else {
                Toast.makeText(MainActivity.this,"Enter a valid Rss feed url or check your connection",
                               Toast.LENGTH_LONG).show();
            }
        }
    }

    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);
            xmlPullParser.nextTag();

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();
                String name = xmlPullParser.getName();

                if(name == null) continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) isItem = false;
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }else if(name.equalsIgnoreCase("channel")){
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";

                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        RssFeedModel item = new RssFeedModel(title, link, description);
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }

    public static Menu getMenu(){
        return sMenu;
    }
    //TODO: Manage your feeds activity
    //TODO: Settings Activity
    //TODO: About us activity
    // TODO: Paypal/reklamy????

}
