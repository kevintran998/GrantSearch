/* ConvertRSSFeedData.java
 *
 * This file processes the data obtained by ConvertRSSFeedData. The program grabs select information
 * from the document and generates grant objects.
 */
package com.kkt160130.grantsearch;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProcessRSSFeedData {

    private ArrayList<Grant> grantList = new ArrayList<>();
    private Filter filter = new Filter();

    // constructor
    public ProcessRSSFeedData ()
    {

    }

    // processes data from converted rss feed and inserts data into grant objects
    public ArrayList<Grant> processData (Document doc, ArrayList<FilterItem> filterList, String keywords, int[] date)
    {
        if(doc != null)
        {
            Element root = doc.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList nodeList = channel.getChildNodes();

            // process through node list of xml tags
            for(int x = 0; x < nodeList.getLength(); x++)
            {
                Node currentChild = nodeList.item(x);

                // if the tag is an item
                if (currentChild.getNodeName().equalsIgnoreCase("item"))
                {
                    Grant grant = new Grant();
                    NodeList nodeListChildren = currentChild.getChildNodes();

                    // retrieve data from inside the item and store them into a grant object
                    for (int y = 0; y < nodeListChildren.getLength(); y++)
                    {
                        Node current = nodeListChildren.item(y);
                        if(current.getNodeName().equalsIgnoreCase("title"))
                        {
                            grant.setTitle(current.getTextContent());
                        }
                        else if(current.getNodeName().equalsIgnoreCase("link"))
                        {
                            grant.setLink(current.getTextContent());
                        }
                        else if(current.getNodeName().equalsIgnoreCase("category"))
                        {
                            grant.addCategory(current.getTextContent());
                        }
                        else if(current.getNodeName().equalsIgnoreCase("content:encoded"))
                        {
                            grant.setCDATA(current.getTextContent());
                        }
                    }

                    // add the object to the array list
                    grantList.add(grant);
                }
            }
        }

        this.processCDATA(); // retrieves data from CDATA values in XML file
        this.processCategory(); // changes the category descriptions
        grantList = filter.filterData(grantList, filterList, keywords, date); // filter the grant list
        return grantList; // send grant list back to display
    }

    // changes category text to a more readable format
    private void processCategory()
    {
        ArrayList<String> data = new ArrayList<>();
        String modifiedData;

        // cycle through grant list
        for (int x = 0; x < grantList.size(); x++)
        {
            data = grantList.get(x).getCategory();

            // if the grant contains at least one category, modify the category
            if (!data.get(0).equals("NONE"))
            {
                for(int y = 0; y < data.size(); y++)
                {
                    modifiedData = categoryChecker(data.get(y));
                    data.set(y, modifiedData);
                }
            }

            // set new category to grant
            grantList.get(x).setCategory(data);
        }
    }

    // change categories based on grants.gov category format
    private String categoryChecker(String data)
    {
        if (data.substring(0,3).equals("ACA"))
        {
            data = "Affordable Care Act";
        }
        else if (data.substring(0,2).equals("AG"))
        {
            data = "Agriculture";
        }
        else if (data.substring(0,2).equals("AR"))
        {
            data = "Arts";
        }
        else if (data.substring(0,2).equals("BC"))
        {
            data = "Business";
        }
        else if (data.substring(0,2).equals("CD"))
        {
            data = "Community";
        }
        else if (data.substring(0,2).equals("CP"))
        {
            data = "Consumer Protection";
        }
        else if (data.substring(0,3).equals("DPR"))
        {
            data = "Disaster Protection";
        }
        else if (data.substring(0,2).equals("ED"))
        {
            data = "Education";
        }
        else if (data.substring(0,3).equals("ELT"))
        {
            data = "Employment";
        }
        else if (data.substring(0,3).equals("ENV"))
        {
            data = "Environment";
        }
        else if (data.substring(0,2).equals("EN"))
        {
            data = "Energy";
        }
        else if (data.substring(0,2).equals("FN"))
        {
            data = "Nutrition";
        }
        else if (data.substring(0,2).equals("HL"))
        {
            data = "Health";
        }
        else if (data.substring(0,2).equals("HO"))
        {
            data = "Housing";
        }
        else if (data.substring(0,2).equals("HU"))
        {
            data = "Humanities";
        }
        else if (data.substring(0,3).equals("ISS"))
        {
            data = "Social Services";
        }
        else if (data.substring(0,2).equals("IS"))
        {
            data = "Information and Statistics";
        }
        else if (data.substring(0,3).equals("LJL"))
        {
            data = "Law";
        }
        else if (data.substring(0,2).equals("NR"))
        {
            data = "Natural Resources";
        }
        else if (data.substring(0,2).equals("O|"))
        {
            data = "Other";
        }
        else if (data.substring(0,2).equals("OZ"))
        {
            data = "Opportunity Zone Benefits";
        }
        else if (data.substring(0,2).equals("RD"))
        {
            data = "Regional Development";
        }
        else if (data.substring(0,2).equals("ST"))
        {
            data = "Technology";
        }
        else if (data.substring(0,2).equals("T|"))
        {
            data = "Transportation";
        }
        return data;
    }

    // retrieves additional information from CDATA tags in the rss feed and adds them to the grant objects
    private void processCDATA()
    {
        int startIndex;
        int endIndex;
        String data;

        for (int x = 0; x < grantList.size(); x++)
        {
            data = grantList.get(x).getCDATA();

            // DESCRIPTION
            if ((startIndex = data.indexOf("<td>Description:</td>")) != -1)
            {
                startIndex += 25;
                endIndex = data.indexOf("</td>", startIndex);
                if (startIndex != endIndex)
                {
                    grantList.get(x).setDescription(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setDescription("NONE");
                }
            } else {grantList.get(x).setDescription("NONE");}

            // START DATE
            if ((startIndex = data.indexOf("<td>Estimated Synopsis Post Date:</td>")) != -1)
            {
                startIndex += 42;
                endIndex = data.indexOf("</td>", startIndex);
                if (startIndex != endIndex)
                {
                    grantList.get(x).setStartDate(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setStartDate("NONE");
                }
            }
            else if ((startIndex = data.indexOf("<td>Posted Date:</td>")) != -1)
            {
                startIndex += 25;
                endIndex = data.indexOf("</td>", startIndex);
                if (startIndex != endIndex)
                {
                    grantList.get(x).setStartDate(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setStartDate("NONE");
                }
            } else {grantList.get(x).setStartDate("NONE");}

            // END DATE
            if ((startIndex = data.indexOf("<td>Close Date:</td>")) != -1)
            {
                startIndex += 24;
                endIndex = data.indexOf("</td>", startIndex);
                if (startIndex != endIndex)
                {
                    grantList.get(x).setEndDate(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setEndDate("NONE");
                }
            } else {grantList.get(x).setEndDate("NONE");}

            // AWARD CEILING
            if ((startIndex = data.indexOf("<td>Award Ceiling:</td>")) != -1)
            {
                startIndex += 27;
                endIndex = data.indexOf("</td>", startIndex);
                if ((startIndex != endIndex) && (data.substring(startIndex, endIndex) != "$0"))
                {
                    grantList.get(x).setAwardCeiling(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setAwardCeiling("NONE");
                }
            } else {grantList.get(x).setAwardCeiling("NONE");}


            // AWARD FLOOR
            if ((startIndex = data.indexOf("<td>Award Floor:</td>")) != -1)
            {
                startIndex += 25;
                endIndex = data.indexOf("</td>", startIndex);
                if ((startIndex != endIndex) && (data.substring(startIndex, endIndex) != "$0"))
                {
                    grantList.get(x).setAwardFloor(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setAwardFloor("NONE");
                }
            } else {grantList.get(x).setAwardFloor("NONE");}


            // ADDITIONAL INFO ELIGIBILITY
            if ((startIndex = data.indexOf("<td valign='top'>Additional Information on Eligibility:</td>")) != -1)
            {
                startIndex += 64;
                endIndex = data.indexOf("</td>", startIndex);
                if (startIndex != endIndex)
                {
                    grantList.get(x).setAdditionalInfoEligibility(data.substring(startIndex, endIndex));
                }
                else
                {
                    grantList.get(x).setAdditionalInfoEligibility("NONE");
                }
            } else {grantList.get(x).setAdditionalInfoEligibility("NONE");}


            int breakPointer;
            boolean finished = false;

            // ELIGIBLE APPLICANTS
            if ((startIndex = data.indexOf("<td valign='top'>Eligible Applicants:</td>")) != -1)
            {
                startIndex += 46;
                endIndex = data.indexOf("</td>", startIndex);
                if (startIndex != endIndex)
                {
                    while (!finished)
                    {
                        breakPointer = data.indexOf("<br>", startIndex);
                        if((breakPointer > endIndex) || (breakPointer == -1))
                        {
                            grantList.get(x).setEligibleApplicants(data.substring(startIndex, endIndex));
                            finished = true;
                        }
                        else
                        {
                            grantList.get(x).setEligibleApplicants(data.substring(startIndex, breakPointer));
                            startIndex = breakPointer + 4;
                        }
                    }
                }
                else
                {
                    grantList.get(x).setEligibleApplicants("NONE");
                }

            }else {grantList.get(x).setEligibleApplicants("NONE");}
        }
    }
}
