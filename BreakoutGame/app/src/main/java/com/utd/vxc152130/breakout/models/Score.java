package com.utd.vxc152130.breakout.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/*************************************************************************************************************
 * Bean class for score data
 * @author Vincy Shrine
 * @created 11/27/2017
 **************************************************************************************************************/
public class Score implements Comparable, Serializable {
    /**
     * Instantiate a score with no values intialized
     */
    public Score() {
    }

    /**
     * Instantiate a score
     *
     * @param dateString
     * @param score
     */
    public Score(String dateString, Integer score) {
        this.score = score;
        this.dateString = dateString;
    }

    /**
     *
     * @param name
     * @param score
     * @param dateString
     */
    public Score(String name, Integer score, String dateString) {
        this.name = name;
        this.score = score;
        this.dateString = dateString;
    }

    private String name ="";
    private Integer score =0;
    private String dateString="";

    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return dateString;
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        Score other = (Score) obj;
        // First sort by scores
        int testResult = this.getScore().compareTo(other.getScore());
        if (testResult == 0) {
            //Next sort by date
            int dateResult = this.getDate().compareTo(other.getDate());
            if (dateResult == 0) {
                // Last sort by Name
                return this.getName().compareTo(other.getName());
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Score{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", dateString='" + dateString + '\'' +
                '}';
    }
}
