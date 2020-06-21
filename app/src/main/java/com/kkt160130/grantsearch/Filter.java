/* Filter.java
 *
 * This file filters the information from the grant object array list based on the user's
 * specified criteria.
 */
package com.kkt160130.grantsearch;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Filter {

    private static final int NUMBER_OF_CATEGORIES = 16;
    private static final int POSITION_501C3_YES = 16;
    private static final int POSITION_501C3_NO = 17;
    private static final int POSITION_GRANT_AMOUNT = 18;
    private static final int POSITION_RACE = 23;
    private static final int NUMBER_OF_RACES = 7;
    private static final int POSITION_RELIGION = 30;
    private static final int NUMBER_OF_RELIGIONS = 8;
    private ArrayList<Grant> filteredGrantListKeywords = new ArrayList<>();
    private ArrayList<Grant> filteredGrantListCategory = new ArrayList<>();
    private ArrayList<Grant> filteredGrantList501c3 = new ArrayList<>();
    private ArrayList<Grant> filteredGrantListGrantAmount = new ArrayList<>();
    private ArrayList<Grant> filteredGrantListGrantDueDate = new ArrayList<>();
    private ArrayList<Grant> filteredGrantListRace = new ArrayList<>();
    private ArrayList<Grant> filteredGrantListReligion = new ArrayList<>();
    private ArrayList<String> filtersSelected = new ArrayList<>();
    private String option501c3Selected = "none";
    private boolean noGrantAmountSelected = true;

    public ArrayList<Grant> filterData (ArrayList<Grant> grantList, ArrayList<FilterItem> filterList, String keywords, int[] date)
    {

        // Keywords
        if(!keywords.equals(""))
        {
            filterByKeywords(grantList, keywords);
        }
        else
        {
            filteredGrantListKeywords = grantList;
        }

        // Category
        filtersSelected = checkIfCategorySelected(filterList);
        if(filtersSelected.size() > 0)
        {
            filterByCategory(filtersSelected);
        }
        else
        {
            filteredGrantListCategory = filteredGrantListKeywords;
        }

        // 501 (c)(3)
        option501c3Selected = checkIf501c3Selected(filterList);
        if((option501c3Selected.equals("yes")) || (option501c3Selected.equals("no")))
        {
            filterBy501c3();
        }
        else
        {
            filteredGrantList501c3 = filteredGrantListCategory;
        }

        // Grant Amount
        noGrantAmountSelected = checkIfGrantAmountSelected(filterList);
        if (!noGrantAmountSelected)
        {
            filterByGrantAmount(filterList);
        }
        else
        {
            filteredGrantListGrantAmount = filteredGrantList501c3;
        }

        // Grant Due Date
        if (date[0] != -1)
        {
            filterByGrantDueDate(date);
        }
        else
        {
            filteredGrantListGrantDueDate = filteredGrantListGrantAmount;
        }

        // Race
        filtersSelected = checkIfRaceSelected(filterList);
        if(filtersSelected.size() > 0)
        {
            filterByRace(filtersSelected);
        }
        else
        {
            filteredGrantListRace = filteredGrantListGrantDueDate;
        }

        // Religion
        filtersSelected = checkIfReligionSelected(filterList);
        if(filtersSelected.size() > 0)
        {
            filterByReligion(filtersSelected);
        }
        else
        {
            filteredGrantListReligion = filteredGrantListRace;
        }

        // sort grant list by how well it scored when passed through the filters
        grantList = sortByScore();

        return grantList;
    }

    // Keywords
    private void filterByKeywords(ArrayList<Grant> grantList, String keywords)
    {
        ArrayList<String> keywordList= new ArrayList<>();
        char characters[] = keywords.toLowerCase().toCharArray();
        String keywordBuilder;
        String keywordSearch;
        String title;
        String description;
        ArrayList<String> eligibleApplicants = new ArrayList<>();
        String additionalEligibleApplicants;
        boolean endOfWord;
        boolean alreadyInserted;

        for(int x = 0; x < characters.length; x++)
        {
            char c = characters[x];
            keywordBuilder = "";
            endOfWord = false;

            if(!Character.toString(characters[x]).equals(" "))
            {
                while (!endOfWord && (x < characters.length))
                {
                    if(Character.toString(characters[x]).equals(" "))
                    {
                        endOfWord = true;
                    }
                    else
                    {
                        keywordBuilder += characters[x];
                        x++;
                    }
                }
            }

            if ((!keywordBuilder.equals("the")) && (!keywordBuilder.equals("and")) && (!keywordBuilder.equals("or")) && (!keywordBuilder.equals("but")) &&
                (!keywordBuilder.equals("yet")) && (!keywordBuilder.equals("for")) && (!keywordBuilder.equals("nor")) && (!keywordBuilder.equals("so")) &&
                    (!keywordBuilder.equals("as")) && (keywordBuilder.length() > 1))
            {
                keywordList.add(keywordBuilder);
            }
        }

        for(int x = 0; x < grantList.size(); x++)
        {
            title = grantList.get(x).getTitle();
            description = grantList.get(x).getDescription();
            eligibleApplicants = grantList.get(x).getEligibleApplicants();
            additionalEligibleApplicants = grantList.get(x).getAdditionalInfoEligibility();
            alreadyInserted = false;

            for(int y = 0; y < keywordList.size(); y++)
            {
                keywordSearch = keywordList.get(y);

                if(title.contains(keywordSearch))
                {
                    grantList.get(x).setScore(grantList.get(x).getScore() + 1);

                    if (!alreadyInserted)
                    {
                        filteredGrantListKeywords.add(grantList.get(x));
                        alreadyInserted = true;
                    }
                }

                if(description.contains(keywordSearch))
                {
                    grantList.get(x).setScore(grantList.get(x).getScore() + 1);

                    if (!alreadyInserted)
                    {
                        filteredGrantListKeywords.add(grantList.get(x));
                        alreadyInserted = true;
                    }
                }

                for (int z = 0; z < eligibleApplicants.size(); z++)
                {
                    if (eligibleApplicants.get(z).contains(keywords))
                    {
                        grantList.get(x).setScore(grantList.get(x).getScore() + 1);

                        if (!alreadyInserted)
                        {
                            filteredGrantListKeywords.add(grantList.get(x));
                            alreadyInserted = true;
                        }
                    }
                }

                if(additionalEligibleApplicants.contains(keywordSearch))
                {
                    grantList.get(x).setScore(grantList.get(x).getScore() + 1);

                    if (!alreadyInserted)
                    {
                        filteredGrantListKeywords.add(grantList.get(x));
                        alreadyInserted = true;
                    }
                }
            }
        }
    }

    // Category
    private void filterByCategory( ArrayList<String> filtersSelected)
    {
        String filterString;
        ArrayList<String> categoryList = new ArrayList<>();
        boolean alreadyInserted;

        for(int x = 0; x < filteredGrantListKeywords.size(); x++)
        {
            categoryList = filteredGrantListKeywords.get(x).getCategory();
            alreadyInserted = false;

            for (int y = 0; y < filtersSelected.size(); y++)
            {
                filterString = filtersSelected.get(y);

                if(filterString.equals("CategoryOther"))
                {
                    filterString = "Other";
                }
                for(int z = 0; z < categoryList.size(); z++)
                {
                    if (categoryList.get(z).equals(filterString))
                    {
                        filteredGrantListKeywords.get(x).setScore(filteredGrantListKeywords.get(x).getScore() + 1);

                        if (!alreadyInserted)
                        {
                            filteredGrantListCategory.add(filteredGrantListKeywords.get(x));
                            alreadyInserted = true;
                        }
                    }
                }
            }
        }
    }

    // 501 (c)(3)
    private void filterBy501c3()
    {
        ArrayList<String> eligibleApplicants = new ArrayList<>();

        if(option501c3Selected.equals("yes"))
        {
            for (int x = 0; x < filteredGrantListCategory.size(); x++)
            {
                eligibleApplicants = filteredGrantListCategory.get(x).getEligibleApplicants();

                for(int y = 0; y < eligibleApplicants.size(); y++)
                {
                    if(eligibleApplicants.get(y).contains("Nonprofits having a 501(c)(3)"))
                    {
                        filteredGrantListCategory.get(x).setScore(filteredGrantListCategory.get(x).getScore() + 1);
                        filteredGrantList501c3.add(filteredGrantListCategory.get(x));
                    }
                }
            }
        }

        else if(option501c3Selected.equals("no"))
        {
            for (int x = 0; x < filteredGrantListCategory.size(); x++)
            {
                eligibleApplicants = filteredGrantListCategory.get(x).getEligibleApplicants();

                for(int y = 0; y < eligibleApplicants.size(); y++)
                {
                    if(eligibleApplicants.get(y).contains("Nonprofits having a 501(c)(3)"))
                    {
                        filteredGrantListCategory.get(x).setScore(filteredGrantListCategory.get(x).getScore() + 1);
                        filteredGrantList501c3.add(filteredGrantListCategory.get(x));

                        if(filteredGrantListCategory.get(x).getEligibleApplicants().indexOf("Nonprofits that do not have a 501(c)(3)") != -1)
                        {
                            filteredGrantListCategory.get(x).setScore(filteredGrantListCategory.get(x).getScore() + 1);
                            filteredGrantList501c3.add(filteredGrantListCategory.get(x));
                        }
                    }
                    else
                    {
                        filteredGrantListCategory.get(x).setScore(filteredGrantListCategory.get(x).getScore() + 1);
                        filteredGrantList501c3.add(filteredGrantListCategory.get(x));
                    }
                }
            }
        }
    }

    // Grant amount
    private void filterByGrantAmount(ArrayList<FilterItem> filterList)
    {
        String stringToInt;
        int floor;
        int ceiling;
        boolean alreadyInserted;

        for (int x = 0; x < filteredGrantList501c3.size(); x++)
        {
            alreadyInserted = false;

            if (filteredGrantList501c3.get(x).getAwardFloor().equals("NONE"))
            {
                floor = 0;
            }
            else
            {
                stringToInt = filteredGrantList501c3.get(x).getAwardFloor().substring(1).replace(",", "");
                floor = Integer.parseInt(stringToInt);
            }
            if (filteredGrantList501c3.get(x).getAwardCeiling().equals("NONE"))
            {
                ceiling = 0;
            }
            else
            {
                stringToInt = filteredGrantList501c3.get(x).getAwardCeiling().substring(1).replace(",", "");
                ceiling = Integer.parseInt(stringToInt);
            }

            // 0 - 9999
            if (filterList.get(POSITION_GRANT_AMOUNT).isClicked())
            {
                if(((floor > 0) && (floor < 1000)) || (ceiling < 1000))
                {
                    filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                    filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                    alreadyInserted = true;
                }
            }

            // 1000 - 9999
            if (filterList.get(POSITION_GRANT_AMOUNT + 1).isClicked())
            {
                if ((floor > 0) && (floor < 1000) && (ceiling >= 1000))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
                else if (((floor >= 1000) && (floor <= 9999)) || ((ceiling >= 1000) && (ceiling <= 9999)))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
            }

            // 10000 - 24999
            if (filterList.get(POSITION_GRANT_AMOUNT + 2).isClicked())
            {
                if ((floor > 0) && (floor < 10000) && (ceiling >= 10000))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
                else if (((floor >= 10000) && (floor <= 24999)) || ((ceiling >= 10000) && (ceiling <= 24999)))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
            }

            // 25000 - 49999
            if (filterList.get(POSITION_GRANT_AMOUNT + 3).isClicked())
            {
                if ((floor > 0) && (floor < 25000) && (ceiling >= 25000))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
                else if (((floor >= 25000) && (floor <= 49999)) || ((ceiling >= 25000) && (ceiling <= 49999)))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
            }

            // 50000+
            if (filterList.get(POSITION_GRANT_AMOUNT + 4).isClicked())
            {
                if ((floor >= 50000) || (ceiling >= 50000))
                {
                    if (!alreadyInserted)
                    {
                        filteredGrantList501c3.get(x).setScore(filteredGrantList501c3.get(x).getScore() + 1);
                        filteredGrantListGrantAmount.add(filteredGrantList501c3.get(x));
                        alreadyInserted = true;
                    }
                }
            }
        }
    }

    // Grant due date
    private void filterByGrantDueDate(int[] date)
    {
        int[] grantDate = new int[] {0, 0, 0};
        String dateToInt;
        boolean validDate = true;

        for (int x = 0; x < filteredGrantListGrantAmount.size(); x++)
        {
            dateToInt = filteredGrantListGrantAmount.get(x).getEndDate();
            validDate = true;

            if (dateToInt.substring(0,3).equals("Jan"))
            {
                grantDate[1] = 1;
            }
            else if (dateToInt.substring(0,3).equals("Feb"))
            {
                grantDate[1] = 2;
            }
            else if (dateToInt.substring(0,3).equals("Mar"))
            {
                grantDate[1] = 3;
            }
            else if (dateToInt.substring(0,3).equals("Apr"))
            {
                grantDate[1] = 4;
            }
            else if (dateToInt.substring(0,3).equals("May"))
            {
                grantDate[1] = 5;
            }
            else if (dateToInt.substring(0,3).equals("Jun"))
            {
                grantDate[1] = 6;
            }
            else if (dateToInt.substring(0,3).equals("Jul"))
            {
                grantDate[1] = 7;
            }
            else if (dateToInt.substring(0,3).equals("Aug"))
            {
                grantDate[1] = 8;
            }
            else if (dateToInt.substring(0,3).equals("Sep"))
            {
                grantDate[1] = 9;
            }
            else if (dateToInt.substring(0,3).equals("Oct"))
            {
                grantDate[1] = 10;
            }
            else if (dateToInt.substring(0,3).equals("Nov"))
            {
                grantDate[1] = 11;
            }
            else if (dateToInt.substring(0,3).equals("Dec"))
            {
                grantDate[1] = 12;
            }
            else
            {
                validDate = false;
            }

            if (validDate)
            {
                grantDate[2] = Integer.parseInt(dateToInt.substring(4, 6));
                grantDate[0] = Integer.parseInt(dateToInt.substring(8, 12));

                if(grantDate[0] <= date[0])
                {
                    if(grantDate[1] <= date[1])
                    {
                        if(grantDate[2] <= grantDate[2])
                        {
                            filteredGrantListGrantAmount.get(x).setScore(filteredGrantListGrantAmount.get(x).getScore() + 1);
                            filteredGrantListGrantDueDate.add(filteredGrantListGrantAmount.get(x));
                        }
                    }
                }
            }
            else
            {
                filteredGrantListGrantAmount.get(x).setScore(filteredGrantListGrantAmount.get(x).getScore() + 1);
                filteredGrantListGrantDueDate.add(filteredGrantListGrantAmount.get(x));
            }
        }
    }

    // Race
    private void filterByRace(ArrayList<String> filtersSelected)
    {
        String filterString;
        String description;
        String additionalEligibleApplicants;
        boolean inserted;

        for(int x = 0; x < filteredGrantListGrantDueDate.size(); x++)
        {
            description = filteredGrantListGrantDueDate.get(x).getDescription();
            additionalEligibleApplicants = filteredGrantListGrantDueDate.get(x).getAdditionalInfoEligibility();
            inserted = false;

            for (int y = 0; y < filtersSelected.size(); y++)
            {
                filterString = filtersSelected.get(y);

                // Asian
                if ((filterString.equals("Asian")) && ((description.contains("Asian")) || (additionalEligibleApplicants.contains("Asian"))))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

                // Black/African American
                if ((filterString.equals("AfricanAmerican")) && ((description.contains("Black")) || (description.contains("African American")) || (additionalEligibleApplicants.contains("Black"))))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

                // Caucasian (RSS Feed currently doesn't have Caucasian information)
                if (filterString.equals("Caucasian"))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

                // Hispanic/Latino
                if ((filterString.equals("Hispanic")) && ((description.contains("Hispanic")) || (description.contains("Latino")) || (additionalEligibleApplicants.contains("Hispanic"))))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

                // Middle Eastern (RSS Feed currently doesn't have Middle Eastern information)
                if (filterString.equals("Middle Eastern"))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

                // Native American
                if ((filterString.equals("Native American")) && ((description.contains("Indian")) || (description.contains("Native American tribal")) || (additionalEligibleApplicants.contains("Indian")) || (additionalEligibleApplicants.contains("Native American tribal"))))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

                // Other (returns all results)
                if(filterString.equals("RaceOther"))
                {
                    filteredGrantListGrantDueDate.get(x).setScore(filteredGrantListGrantDueDate.get(x).getScore() + 1);
                    filteredGrantListRace.add(filteredGrantListGrantDueDate.get(x));
                    inserted = true;
                }

            }
        }
    }

    // Religion
    private void filterByReligion(ArrayList<String> filtersSelected)
    {
        String filterString;
        String description;
        String additionalEligibleApplicants;
        boolean inserted;

        for(int x = 0; x < filteredGrantListRace.size(); x++)
        {
            description = filteredGrantListRace.get(x).getDescription();
            additionalEligibleApplicants = filteredGrantListRace.get(x).getAdditionalInfoEligibility();
            inserted = false;

            for (int y = 0; y < filtersSelected.size(); y++)
            {
                filterString = filtersSelected.get(y);

                // Christian
                if ((filterString.equals("Christian")) && ((description.toLowerCase().contains("christian")) || (additionalEligibleApplicants.toLowerCase().contains("christian"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Catholic
                if ((filterString.equals("Catholic")) && ((description.toLowerCase().contains("catholic")) || (additionalEligibleApplicants.toLowerCase().contains("catholic"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Hindu
                if ((filterString.equals("Hindu")) && ((description.toLowerCase().contains("hindu")) || (additionalEligibleApplicants.toLowerCase().contains("hindu"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Muslim
                if ((filterString.equals("Muslim")) && ((description.toLowerCase().contains("muslim")) || (additionalEligibleApplicants.toLowerCase().contains("muslim"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Buddhist
                if ((filterString.equals("Buddhist")) && ((description.toLowerCase().contains("buddhist")) || (additionalEligibleApplicants.toLowerCase().contains("buddhist"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Sikh
                if ((filterString.equals("Sikh")) && ((description.toLowerCase().contains("sikh")) || (additionalEligibleApplicants.toLowerCase().contains("sikh"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Jewish
                if ((filterString.equals("Jewish")) && ((description.toLowerCase().contains("jewish")) || (additionalEligibleApplicants.toLowerCase().contains("jewish"))))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Other
                if ((filterString.equals("ReligionOther")) && (additionalEligibleApplicants.contains("Faith-based")) && (!inserted))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }

                // Not specified but religious
                if ((additionalEligibleApplicants.contains("Faith-based")) && (!inserted))
                {
                    filteredGrantListRace.get(x).setScore(filteredGrantListRace.get(x).getScore() + 1);
                    filteredGrantListReligion.add(filteredGrantListRace.get(x));
                    inserted = true;
                }
            }
        }
    }

    // check which categories are selected
    private ArrayList<String> checkIfCategorySelected(ArrayList<FilterItem> filterList)
    {
        ArrayList<String> categoriesSelected = new ArrayList<>();

        for (int x = 0; x < NUMBER_OF_CATEGORIES; x++)
        {
            if (filterList.get(x).isClicked())
            {
                categoriesSelected.add(filterList.get(x).getFilterName());
            }
        }

        return categoriesSelected;
    }

    // check if 501(c)(3) is selected
    private String checkIf501c3Selected(ArrayList<FilterItem> filterList)
    {
        if (filterList.get(POSITION_501C3_YES).isClicked())
        {
            return "yes";
        }
        else if (filterList.get(POSITION_501C3_NO).isClicked())
        {
            return "no";
        }

        return "none";
    }

    // check if a grant amount is selected
    private boolean checkIfGrantAmountSelected(ArrayList<FilterItem> filterList)
    {
        for (int x = POSITION_GRANT_AMOUNT; x < POSITION_GRANT_AMOUNT + 5; x++)
        {
            if(filterList.get(x).isClicked())
            {
                return false;
            }
        }

        return true;
    }

    // check which races are selected
    private ArrayList<String> checkIfRaceSelected(ArrayList<FilterItem> filterList)
    {
        ArrayList<String> racesSelected = new ArrayList<>();

        for (int x = POSITION_RACE; x < POSITION_RACE + NUMBER_OF_RACES; x++)
        {
            if (filterList.get(x).isClicked())
            {
                racesSelected.add(filterList.get(x).getFilterName());
            }
        }

        return racesSelected;
    }

    // check which relgiions are selected
    private ArrayList<String> checkIfReligionSelected(ArrayList<FilterItem> filterList)
    {
        ArrayList<String> religionsSelected = new ArrayList<>();

        for (int x = POSITION_RELIGION; x < POSITION_RELIGION + NUMBER_OF_RELIGIONS; x++)
        {
            if (filterList.get(x).isClicked())
            {
                religionsSelected.add(filterList.get(x).getFilterName());
            }
        }

        return religionsSelected;
    }

    // sorts the grant list based on how high of a score it earned through filtering
    private ArrayList<Grant> sortByScore()
    {
        ArrayList<Grant> sortedList = filteredGrantListReligion;
        sortedList.add(sortedList.get(0));

        for(int x = 1; x < sortedList.size(); x++)
        {
            Collections.sort(sortedList, new java.util.Comparator<Grant>() {
                @Override
                public int compare(Grant o1, Grant o2) {
                    return Integer.valueOf(o2.getScore()).compareTo(o1.getScore());
                }
            });
        }

        return sortedList;
    }
}
