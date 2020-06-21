/* GrantSearchResultsActivity.java
 * Layout: activity_grant_search_results.xml
 *
 * This activity displays the grants in a scrollable list. The user is able to click on each
 * grant for more information.
 */
package com.kkt160130.grantsearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class GrantSearchResultsActivity extends AppCompatActivity implements GrantRecyclerAdapter.OnGrantClickListener {

    private ArrayList<Grant> grantList = new ArrayList<>();
    private ArrayList<FilterItem> filterList = new ArrayList<>();
    private String keywords;
    private int date[];
    Document document;
    private RecyclerView recyclerView;
    private GrantRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant_search_results);

        // retrieve intent
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        filterList = (ArrayList<FilterItem>) bundle.getSerializable("filterList");
        keywords = bundle.getString("keywords");
        date = bundle.getIntArray("date");

        // custom action bar settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // converts the rss feed into a string document by using an Async task to perform in the background
        ConvertRSSFeedData convertFeed = (ConvertRSSFeedData) new ConvertRSSFeedData(new ConvertRSSFeedData.AsyncOutput() {
            @Override
            public void retrieveGrantList(Document doc) {
                document = doc; // set the document to the converted rss feed file
                ProcessRSSFeedData processFeed = new ProcessRSSFeedData(); // create a new ProcessRSSFeedData object
                grantList = processFeed.processData(document, filterList, keywords, date); // process the document

                // if there is at least one grant in the list, display it to the screen
                if(grantList.size() > 0)
                {
                    refreshView();
                }

                // else show no results found
                else
                {
                    displayNoResults();
                }

            }
        }).execute(); // execute Async task
    }

    // click listener for grants; if a grant is clicked, start GrantActivity and display grant information
    @Override
    public void onItemClick(int position)
    {
        Intent intent = new Intent(GrantSearchResultsActivity.this, GrantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("grant", grantList.get(position));
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    // refreshes the recycler view with grants after processing
    private void refreshView()
    {
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(GrantSearchResultsActivity.this);
        adapter = new GrantRecyclerAdapter(grantList, GrantSearchResultsActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Toast.makeText(this, Integer.toString(grantList.size()) + " results found", Toast.LENGTH_SHORT).show();
    }

    // if there are no grants found, display a message saying no results found
    private void displayNoResults()
    {
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.noResults);

        recyclerView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
    }

    // back button click listener
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return false;
    }
}
