package kamilkacprzak.rss4u;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageFeedsActivity extends AppCompatActivity {
    private SharedPreferences titles_id;
    private static Menu sMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_feeds);
        LinearLayout lr = (LinearLayout) findViewById(R.id.manage_layout);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        titles_id = this.getSharedPreferences("mItemsId_strInt",0);
        Map<String, ?> allEntries = titles_id.getAll();
        List<Integer> id = new ArrayList<Integer>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
           id.add(Integer.parseInt(entry.getValue().toString()));
        }
        sMenu = MainActivity.getMenu();
        //while(id)

    }
}
