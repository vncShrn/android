package com.utd.vxc152130.breakout.other;

import android.content.Context;
import android.util.Log;

import com.utd.vxc152130.breakout.models.Score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*************************************************************************************************************
 * Utility class for scores file read write
 * @author Vincy Shrine
 * @created 11/27/2017
 **************************************************************************************************************/
public class Helper {

    private static final String TAG = "Helper";
    private static File scoresFile = null;
    private static List<Score> scoresList = new ArrayList<>();
    private static int bestScore = 0;

    public static void setScoresFile(Context context) {
        scoresFile = new File(context.getExternalFilesDir(null) + "temp12.txt");
        readScoresFromFile();
    }

    public static void clearScores() {
        scoresList = new ArrayList<>();
        try {
            scoresFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to get top 10 scores, returns an arraylist of top 10 scores
    private static void readScoresFromFile() {
        String line;

        if (!scoresFile.exists()) {
            try {
                scoresFile.createNewFile();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileReader fr = new FileReader(scoresFile);
            BufferedReader br = new BufferedReader(fr);
            Log.d(TAG, "Inside readFromFile on startup ");
            scoresList = new ArrayList<>();
            bestScore = 0;
            // Read line by line from file
            while ((line = br.readLine()) != null) {
                String input[] = line.split("\t");
                // name, score, date, id
                int score = Integer.parseInt(input[1]);
                if (score > bestScore)
                    bestScore = score;
                scoresList.add(new Score(input[0], score, input[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Write to file
     *
     * @author Vincy Shrine
     */
    public static void writeScoresToFile() {
        FileWriter fileWriter = null;
        StringBuilder sb;
        try {
            fileWriter = new FileWriter(scoresFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter br = new BufferedWriter(fileWriter);
        try {
            for (Score score : scoresList) {
                sb = new StringBuilder();
                sb.append(score.getName() + "\t");
                sb.append(score.getScore() + "\t");
                sb.append(score.getDate() );

                br.write(sb.toString());
                br.newLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Score> getScores() {
        return scoresList;
    }

    public static void addToScoresList(Score score) {
        if (score.getScore() > bestScore)
            bestScore = score.getScore();
        scoresList.add(score);
    }

    public static int getBestScore() {
        return bestScore;
    }
}