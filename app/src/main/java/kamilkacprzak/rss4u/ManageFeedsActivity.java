package kamilkacprzak.rss4u;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManageFeedsActivity extends AppCompatActivity {

    private SharedPreferences titles_id,
                              url_title;
    private static Menu sMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_feeds);

        LinearLayout parent = new LinearLayout(this);
        parent.setLayoutParams(new LinearLayout
                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout rowOfParent = new LinearLayout(this);
        rowOfParent.setLayoutParams(new LinearLayout
                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        rowOfParent.setOrientation(LinearLayout.HORIZONTAL);

        titles_id = this.getSharedPreferences("mItemsId_strInt",0);
        url_title = this.getSharedPreferences("mUrlForTitle_strStr",0);

        Map<String, ?> allEntries = titles_id.getAll();
        List<Integer> id = new LinkedList<Integer>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
           id.add(Integer.parseInt(entry.getValue().toString()));
        }

        sMenu = MainActivity.getMenu();
        for(Integer ids : id){
            parent.addView(rowOfParent);
            MenuItem mi = sMenu.getItem(ids);
            TextView tv = new TextView(this);
            tv.setText(mi.getTitle());
            rowOfParent.addView(tv);
            Button b1 = new Button(this);
            b1.setText("Modify");
            b1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                }
            });
            rowOfParent.addView(b1);
            Button b2 = new Button(this);
            b2.setText("Delete");
            b2.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });
            rowOfParent.addView(b2);
            rowOfParent = new LinearLayout(this);
            rowOfParent.setLayoutParams(new LinearLayout
                    .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            rowOfParent.setOrientation(LinearLayout.HORIZONTAL);
        }

    }
}
