/* GrantActivity.java
 * Layout: activity_grant.xml
 *
 * This activity displays detailed information on the selected grant.
 */
package com.kkt160130.grantsearch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class GrantActivity extends AppCompatActivity {

    private Grant grant;
    private ArrayList<String> bufferList;
    private SpannableStringBuilder stringBuilder;
    private final StyleSpan boldText = new StyleSpan(Typeface.BOLD);
    private TextView titleTV;
    private TextView startDateTV;
    private TextView endDateTV;
    private TextView categoryTV;
    private TextView fundingTV;
    private TextView descriptionTV;
    private TextView eligibleApplicantTV;
    private TextView additionalEligibilityTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grant);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        grant = (Grant) bundle.getSerializable("grant");

        displayInfo();
    }

    // displays grant information
    public void displayInfo()
    {
        titleTV = findViewById(R.id.titleInfoTextView);
        startDateTV = findViewById(R.id.startDateInfoTextView);
        endDateTV = findViewById(R.id.endDateInfoTextView);
        categoryTV = findViewById(R.id.categoryInfoTextView);
        fundingTV = findViewById(R.id.fundingInfoTextView);
        descriptionTV = findViewById(R.id.descriptionInfoTextView);
        eligibleApplicantTV = findViewById(R.id.eligibleApplicantInfoTextView);
        additionalEligibilityTV = findViewById(R.id.additionalEligibilityInfoTextView);

        titleTV.setText(grant.getTitle());
        startDateTV.setText(this.getStartDateText());
        endDateTV.setText(this.getEndDateText());
        categoryTV.setText(this.getCategoryText());
        fundingTV.setText(this.getFundingText());
        descriptionTV.setText(this.getDescriptionText());
        eligibleApplicantTV.setText(this.getEligibleApplicantsText());
        additionalEligibilityTV.setText(this.getAdditionalInfoEligibilityText());
    }

    // onClick listener for link
    // sends user to the web page for the grant on grants.gov
    public void onClickLink (View view)
    {
        Uri uri = Uri.parse(grant.getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // formats text for start date
    private SpannableStringBuilder getStartDateText()
    {
        String startDate;

        if (!grant.getStartDate().equals("NONE"))
        {
            startDate = "Start Date: " + grant.getStartDate();
        }
        else
        {
            startDate = "Start Date: Not Specified";
        }

        stringBuilder = new SpannableStringBuilder(startDate);
        stringBuilder.setSpan(boldText, 0, 11, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    // formats text for end date
    private SpannableStringBuilder getEndDateText()
    {
        String endDate;

        if (!grant.getEndDate().equals("NONE"))
        {
            endDate = "End Date: " + grant.getEndDate();
        }
        else
        {
            endDate = "End Date: Not Specified";
        }

        stringBuilder = new SpannableStringBuilder(endDate);
        stringBuilder.setSpan(boldText, 0, 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    // formats text for category
    private SpannableStringBuilder getCategoryText()
    {
        String categoryList;
        bufferList = new ArrayList<>(grant.getCategory());

        if (bufferList.get(0).equals("NONE"))
        {
            categoryList = "Categories: Not Specified";
        }
        else
        {
            categoryList = "Categories: ";
            for (int x = 0; x < grant.getCategory().size(); x++)
            {
                if (x != 0)
                {
                    categoryList += ", ";
                }
                categoryList += bufferList.get(x);
            }
        }

        stringBuilder = new SpannableStringBuilder(categoryList);
        stringBuilder.setSpan(boldText, 0, 11, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    // formats text for grant award
    private SpannableStringBuilder getFundingText()
    {
        String funding;
        boolean isZeroFloor = false;
        boolean isZeroCeiling = false;

        if ((grant.getAwardFloor().equals("$0")) || (grant.getAwardFloor().equals("NONE")))
        {
            isZeroFloor = true;
        }
        if ((grant.getAwardCeiling().equals("$0")) || (grant.getAwardCeiling().equals("NONE")))
        {
            isZeroCeiling = true;
        }

        if(!isZeroFloor && !isZeroCeiling)
        {
            if (!grant.getAwardFloor().equals(grant.getAwardCeiling()))
            {
                funding = "Estimated Award: " + grant.getAwardFloor() + " - " + grant.getAwardCeiling();
            }
            else
            {
                funding = "Estimated Award: " + grant.getAwardFloor();
            }
        }
        else if (!isZeroFloor && isZeroCeiling)
        {
            funding = "Estimated Award: " + grant.getAwardFloor();
        }
        else if (isZeroFloor && !isZeroCeiling)
        {
            funding = "Estimated Award: " + grant.getAwardCeiling();
        }
        else
        {
            funding = "Estimated Award: Not Specified";
        }

        stringBuilder = new SpannableStringBuilder(funding);
        stringBuilder.setSpan(boldText, 0, 16, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    // formats text for description
    private SpannableStringBuilder getDescriptionText()
    {
        String description;

        if (!grant.getDescription().equals("NONE"))
        {
            description = "Description: " + grant.getDescription();
        }
        else
        {
            description = "Description: No description available.";
        }

        stringBuilder = new SpannableStringBuilder(description);
        stringBuilder.setSpan(boldText, 0, 12, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    // formats text for eligible applicants
    private SpannableStringBuilder getEligibleApplicantsText()
    {
        String applicantList;
        bufferList = new ArrayList<>(grant.getEligibleApplicants());

        if (bufferList.get(0).equals("NONE"))
        {
            applicantList = "Eligible Applicants: Not Specified";
        }
        else
        {
            applicantList = "Eligible Applicants: ";
            for (int x = 0; x < grant.getEligibleApplicants().size(); x++)
            {
                if (x != 0)
                {
                    applicantList += ", ";
                }
                applicantList += bufferList.get(x);
            }
        }

        stringBuilder = new SpannableStringBuilder(applicantList);
        stringBuilder.setSpan(boldText, 0, 20, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    // formats text for additional info on eligibility
    private SpannableStringBuilder getAdditionalInfoEligibilityText()
    {
        String additionalEligibility;

        if (!grant.getAdditionalInfoEligibility().equals("NONE"))
        {
            additionalEligibility = "Additional Information on Eligibility: " + grant.getAdditionalInfoEligibility();
        }
        else
        {
            additionalEligibility = "Additional Information on Eligibility: No additional information available.";
        }

        stringBuilder = new SpannableStringBuilder(additionalEligibility);
        stringBuilder.setSpan(boldText, 0, 38, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return false;
    }
}
