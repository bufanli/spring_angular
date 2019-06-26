package com.example.eurasia.entity.Data;

import java.util.ArrayList;

/**
 * entity class respresents category selections with total count
 * {
 * "results": [
 * {
 * "id": 1,
 * "text": "Option 1"
 * },
 * {
 * "id": 2,
 * "text": "Option 2"
 * }
 * ],
 * "totalCount": 3000
 * }
 */
public class CategorySelectionsWithTotalCount {
    // total count
    private int totalCount = 0;
    // selections
    private ArrayList<Selection> results = null;

    // constructor
    public CategorySelectionsWithTotalCount() {
        this.results = new ArrayList<Selection>();
    }

    // set total count
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    // get total count
    public int getTotalCount() {
        return totalCount;
    }

    // get results
    public ArrayList<Selection> getResults() {
        return results;
    }

    // push selection
    public void pushSelection(Selection selection) {
        this.results.add(selection);
    }

    // inner class of selection
    //      "id": 1,
    //      "text": "Option 1"
    public static class Selection {
        // id field
        private int id = 0;
        // text field
        private String text = null;

        // constructor
        public Selection(int id, String text) {
            this.id = id;
            this.text = text;
        }

        // get id
        public int getId() {
            return id;
        }

        // get text
        public String getText() {
            return text;
        }
    }
}
