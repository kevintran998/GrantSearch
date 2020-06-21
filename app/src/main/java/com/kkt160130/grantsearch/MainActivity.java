/* Grant Search
* Written By: Kevin Tran
*
* Grant Search utilizes grants.gov RSS feed data to provide a list of grants to the user
* based on the user's search criteria. The user is given a set of filtering options. Once the
* user inputs their desired filters, the app will connect to the internet and gather
* information from the grants.gov RSS feed in the form of an XML file. The application will
* parse the XML file into a string document, pull out information on each grant, and place
* the info into objects. The grant objects are held within an array list which is
* then filtered based on the user's initial inputs. The app displays the filtered list onto the
* screen when finished. The user is given the option to scroll through the list and click on each
* grant for more information.s
 */
package com.kkt160130.grantsearch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ArrayList<FilterItem> filterList;
    private String filterStrings[];
    private int date[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // custom action bar settings
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);

        // retrieve filter strings from strings file
        filterStrings = getResources().getStringArray(R.array.FilterItemStringList);
        date = new int[] {-1, -1, -1};

        // initialize filterList
        filterList = new ArrayList<>();
        initializeFilterList();

        // on click listener for grant due date
        Button buttonStart = findViewById(R.id.dateSelect);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerStart = new DueDateFragment();
                datePickerStart.show(getSupportFragmentManager(), "date picker start");
            }
        });
    }

    // sets the date of the calendar when clicked
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        Calendar calendar = Calendar.getInstance();

        // set date
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String date = DateFormat.getDateInstance().format(calendar.getTime());

        // update date text view on the screen
        TextView textView = findViewById(R.id.dateText);
        textView.setText(date);
        textView.setVisibility(View.VISIBLE);

        // set values of date int array to be used for filtering
        this.date[0] = year;
        this.date[1] = month;
        this.date[2] = dayOfMonth;
    }

    // onClick listener to reset values of calendar when user hits clear
    public void onClickClearDate(View view)
    {
        Calendar calendar = Calendar.getInstance();

        // set date to 0
        calendar.set(Calendar.YEAR, 0);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        String date = DateFormat.getDateInstance().format(calendar.getTime());

        // update date text view on the screen
        TextView textView = findViewById(R.id.dateText);
        textView.setText(date);
        textView.setVisibility(View.INVISIBLE);

        // set values of date int array to -1
        this.date[0] = -1;
        this.date[1] = -1;
        this.date[2] = -1;
    }

    // onClick listener to search for grants
    public void onClickSearch(View view)
    {
        EditText input = findViewById(R.id.keywordSearch);
        String keywords = input.getText().toString();

        // pass filter list, keywords, and grant due date through intent and start GrantSearchResultsActivity
        Intent intent = new Intent(MainActivity.this, GrantSearchResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("filterList", filterList);
        bundle.putString("keywords", keywords);
        bundle.putIntArray("date", date);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    // handles filter clicks; sets a filter as clicked if the user selects it, and clears the filter if
    // the user selects the filter a second time
    public void onClickFilter(View view)
    {
        if (view.getId() == R.id.yes501c3)
        {
            if ((filterList.get(16).isClicked() == false) && (filterList.get(17).isClicked() == false))
            {
                filterList.get(16).setClicked(true);
                view.setSelected(true);
            }
            else if (filterList.get(16).isClicked() == true)
            {
                filterList.get(16).setClicked(false);
                view.setSelected(false);
            }
        }
        else if (view.getId() == R.id.no501c3)
        {
            if ((filterList.get(17).isClicked() == false) && (filterList.get(16).isClicked() == false))
            {
                filterList.get(17).setClicked(true);
                view.setSelected(true);
            }
            else if (filterList.get(17).isClicked() == true)
            {
                filterList.get(17).setClicked(false);
                view.setSelected(false);
            }
        }
        else
        {
            for (int x = 0; x < filterList.size(); x++)
            {
                if (view.getId() == filterList.get(x).getFilterID())
                {
                    if (filterList.get(x).isClicked() == false)
                    {
                        filterList.get(x).setClicked(true);
                        view.setSelected(true);
                    }
                    else if (filterList.get(x).isClicked() == true)
                    {
                        filterList.get(x).setClicked(false);
                        view.setSelected(false);
                    }
                }
            }
        }
    }

    // initializes filter list with pre-determined filters from the strings file
    public void initializeFilterList()
    {
        filterList.add(this.addFilter(filterStrings[0], R.id.categoryAgriculture));
        filterList.add(this.addFilter(filterStrings[1], R.id.categoryArts));
        filterList.add(this.addFilter(filterStrings[2], R.id.categoryBusiness));
        filterList.add(this.addFilter(filterStrings[3], R.id.categoryCommunity));
        filterList.add(this.addFilter(filterStrings[4], R.id.categoryDisasterPrevention));
        filterList.add(this.addFilter(filterStrings[5], R.id.categoryEducation));
        filterList.add(this.addFilter(filterStrings[6], R.id.categoryEmployment));
        filterList.add(this.addFilter(filterStrings[7], R.id.categoryEnvironment));
        filterList.add(this.addFilter(filterStrings[8], R.id.categoryNutrition));
        filterList.add(this.addFilter(filterStrings[9], R.id.categoryHealth));
        filterList.add(this.addFilter(filterStrings[10], R.id.categoryHumanities));
        filterList.add(this.addFilter(filterStrings[11], R.id.categorySocialServices));
        filterList.add(this.addFilter(filterStrings[12], R.id.categoryLaw));
        filterList.add(this.addFilter(filterStrings[13], R.id.categoryNaturalResources));
        filterList.add(this.addFilter(filterStrings[14], R.id.categoryOther));
        filterList.add(this.addFilter(filterStrings[15], R.id.categoryTechnology));
        filterList.add(this.addFilter(filterStrings[16], R.id.yes501c3));
        filterList.add(this.addFilter(filterStrings[17], R.id.no501c3));
        filterList.add(this.addFilter(filterStrings[18], R.id.priceRangeLess1000));
        filterList.add(this.addFilter(filterStrings[19], R.id.priceRange1000To10000));
        filterList.add(this.addFilter(filterStrings[20], R.id.priceRange10000To25000));
        filterList.add(this.addFilter(filterStrings[21], R.id.priceRange25000To50000));
        filterList.add(this.addFilter(filterStrings[22], R.id.priceRangeMore50000));
        filterList.add(this.addFilter(filterStrings[23], R.id.raceAsian));
        filterList.add(this.addFilter(filterStrings[24], R.id.raceAfricanAmerican));
        filterList.add(this.addFilter(filterStrings[25], R.id.raceCaucasian));
        filterList.add(this.addFilter(filterStrings[26], R.id.raceHispanic));
        filterList.add(this.addFilter(filterStrings[27], R.id.raceMiddleEastern));
        filterList.add(this.addFilter(filterStrings[28], R.id.raceNativeAmerican));
        filterList.add(this.addFilter(filterStrings[29], R.id.raceOther));
        filterList.add(this.addFilter(filterStrings[30], R.id.religionChristian));
        filterList.add(this.addFilter(filterStrings[31], R.id.religionCatholic));
        filterList.add(this.addFilter(filterStrings[32], R.id.religionHindu));
        filterList.add(this.addFilter(filterStrings[33], R.id.religionMuslim));
        filterList.add(this.addFilter(filterStrings[34], R.id.religionBuddhist));
        filterList.add(this.addFilter(filterStrings[35], R.id.religionSikh));
        filterList.add(this.addFilter(filterStrings[36], R.id.religionJewish));
        filterList.add(this.addFilter(filterStrings[37], R.id.religionOther));
    }

    // adds a filter to the filterList array list
    public FilterItem addFilter(String s, int x)
    {
        FilterItem filter = new FilterItem(s, x);
        return filter;
    }
}
