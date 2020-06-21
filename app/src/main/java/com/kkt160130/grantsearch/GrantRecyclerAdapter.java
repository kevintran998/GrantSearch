/* GrantRecyclerAdapter.java
 *
 * This file formats the list of grants by using an adapter.
 */
package com.kkt160130.grantsearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GrantRecyclerAdapter extends RecyclerView.Adapter<GrantRecyclerAdapter.ViewHolder> {

    private ArrayList<Grant> grantList;
    ArrayList<String> bufferList;
    private OnGrantClickListener listener;

    private String extraneousCheck;
    private String deleteExtraneous;
    private int startIndex;
    private int endIndex;

    // creates an on click listen for grants in the recycler view
    public interface OnGrantClickListener
    {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleView;
        public TextView categoryView;
        public TextView startDateView;
        public TextView endDateView;
        OnGrantClickListener listener;

        public ViewHolder(View itemView, OnGrantClickListener l) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleTextView);
            categoryView = itemView.findViewById(R.id.categoryTextView);
            startDateView = itemView.findViewById(R.id.startDateTextView);
            endDateView = itemView.findViewById(R.id.endDateTextView);
            listener = l;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public GrantRecyclerAdapter(ArrayList<Grant> list, OnGrantClickListener l)
    {
        grantList = list;
        listener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Grant grant = grantList.get(position);
        holder.titleView.setText(grant.getTitle());
        holder.categoryView.setText(this.getCategoryText(position));
        holder.startDateView.setText(this.getStartDateText(position));
        holder.endDateView.setText(this.getEndDateText(position));
    }

    @Override
    public int getItemCount() {
        return grantList.size();
    }

    // formats the categories text to be displayed
    private String getCategoryText(int position)
    {
        String categoryList;

        bufferList = new ArrayList<>(grantList.get(position).getCategory());

        if (bufferList.get(0).equals("NONE"))
        {
            categoryList = "Not Specified";
        }
        else
        {
            categoryList = "";
            for (int x = 0; x < grantList.get(position).getCategory().size(); x++)
            {
                if (x != 0)
                {
                    categoryList += ", ";
                }

                categoryList += bufferList.get(x);
            }
        }

        return categoryList;
    }

    // formats the start date text to be displayed
    private String getStartDateText(int position)
    {
        String startDate;

        if (!grantList.get(position).getStartDate().equals("NONE"))
        {
            extraneousCheck = grantList.get(position).getStartDate();
            startIndex = extraneousCheck.indexOf(',');
            endIndex = startIndex + 6;
            if (endIndex == extraneousCheck.length())
            {
                startDate = extraneousCheck;
            }
            else
            {
                deleteExtraneous = extraneousCheck.substring(endIndex, extraneousCheck.length());
                extraneousCheck = extraneousCheck.replace(deleteExtraneous, "");
                startDate = extraneousCheck;
            }
        }
        else
        {
            startDate = "Start Date: Not Specified";
        }

        return startDate;
    }

    // formats the end date text to be displayed
    private String getEndDateText(int position)
    {
        String endDate;

        if (!grantList.get(position).getEndDate().equals("NONE"))
        {
            extraneousCheck = grantList.get(position).getEndDate();
            startIndex = extraneousCheck.indexOf(',');
            endIndex = startIndex + 7;
            if (endIndex == extraneousCheck.length())
            {
                endDate = extraneousCheck;
            }
            else
            {
                deleteExtraneous = extraneousCheck.substring(endIndex, extraneousCheck.length());
                extraneousCheck = extraneousCheck.replace(deleteExtraneous, "");
                endDate = extraneousCheck;
            }
        }
        else
        {
            endDate = "Not Specified";
        }

        return endDate;
    }
}
