/* Grant.java
 *
 * Grant Class
 */
package com.kkt160130.grantsearch;

import java.io.Serializable;
import java.util.ArrayList;

public class Grant implements Serializable {

    private String title;
    private String link;
    private String description;
    private ArrayList<String> category = new ArrayList<String>();
    private String startDate;
    private String endDate;
    private String awardCeiling;
    private String awardFloor;
    private String CDATA;
    private ArrayList<String> eligibleApplicants = new ArrayList<String>();
    private String additionalInfoEligibility;
    private ArrayList<String> keywords = new ArrayList<String>();
    private int score = 0;

    public Grant()
    {

    }
    public Grant(String t, String l, String d)
    {
        title = t;
        link = l;
        description = d;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public void addCategory(String category)
    {
        this.category.add(category);
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAwardCeiling() {
        return awardCeiling;
    }

    public void setAwardCeiling(String awardCeiling) {
        this.awardCeiling = awardCeiling;
    }

    public String getAwardFloor() {
        return awardFloor;
    }

    public void setAwardFloor(String awardFloor) {
        this.awardFloor = awardFloor;
    }

    public String getCDATA() {
        return CDATA;
    }

    public void setCDATA(String CDATA) {
        this.CDATA = CDATA;
    }

    public ArrayList<String> getEligibleApplicants() {
        return eligibleApplicants;
    }

    public void setEligibleApplicants(String applicant) {
        this.eligibleApplicants.add(applicant);
    }

    public String getAdditionalInfoEligibility() {
        return additionalInfoEligibility;
    }

    public void setAdditionalInfoEligibility(String additionalInfoEligibility) {
        this.additionalInfoEligibility = additionalInfoEligibility;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
