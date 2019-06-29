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
public class CategorySelectionsWithTotalCount implements Cloneable {
    // total count
    private long totalCount = 0;
    // selections
    private ArrayList<Selection> results = null;

    // constructor
    public CategorySelectionsWithTotalCount() {
        this.results = new ArrayList<Selection>();
    }

    // constructor
    public CategorySelectionsWithTotalCount(long totalCount, ArrayList<Selection> results) {
        this.totalCount = totalCount;
        this.results = new ArrayList<Selection>(results.size());
        this.results.addAll(results);//这个是浅拷贝
    }

    // set total count
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    // get total count
    public long getTotalCount() {
        return totalCount;
    }

    public void setResults(ArrayList<Selection> results) {
        this.results.addAll(results);
    }

    // get results
    public ArrayList<Selection> getResults() {
        return results;
    }

    // push selection
    public void pushSelection(Selection selection) {
        this.results.add(selection);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
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

        // set id
        public void setId(int id) {
            this.id = id;
        }

        // get text
        public String getText() {
            return text;
        }

        // set text
        public void setText(String text) {
            this.text = text;
        }
    }
}
