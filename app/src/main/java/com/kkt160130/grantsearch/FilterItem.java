/* FilterItem.java
 *
 * FilterItem class
 */
package com.kkt160130.grantsearch;

import java.io.Serializable;

public class FilterItem implements Serializable {

    private String filterName;
    private int filterID;
    private boolean isClicked;

    public FilterItem(String fn, int id)
    {
        filterName = fn;
        filterID = id;
        isClicked = false;
    }
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public int getFilterID() {
        return filterID;
    }

    public void setFilterID(int filterID) {
        this.filterID = filterID;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
