package com.utd.vxc152130.breakout.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.utd.vxc152130.breakout.R;
import com.utd.vxc152130.breakout.models.Score;
import com.utd.vxc152130.breakout.other.Helper;
import com.utd.vxc152130.breakout.other.ScoreAdapter;

import java.util.Collections;
import java.util.List;

/*************************************************************************************************************
 * Activity that displays all scores after user enters score, also accessible from main game view
 * @author Vincy Shrine
 * @created 11/27/2017
 *************************************************************************************************************/

public class DisplayScoresActivity extends AppCompatActivity {
    ListView scoreListView = null;

    /**
     * On create of activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_scores);
        scoreListView = (ListView) findViewById(R.id.scoreList);
        ImageView clearScoresImg = (ImageView) findViewById(R.id.clearScoresImg);
        clearScoresImg.setOnClickListener(clearScoresButton());
        List<Score> scoresList = Helper.getScores();
        Collections.sort(scoresList);
        Log.d("SCORES ACTIVITY", "Scores List: " + scoresList);
        ScoreAdapter scoreAdapter = new ScoreAdapter(this, scoresList);
        //link this adapter with the layout ListView
        scoreListView.setAdapter(scoreAdapter);
    }

    /**
     * CLEAR button action
     * @return
     */
    private View.OnClickListener clearScoresButton() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.clearScores();
                scoreListView.setAdapter(null);Toast toast = Toast.makeText(getApplicationContext(), "Scores cleared",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 500, 500);
                toast.show();
            }
        };
    }

    /**
     * On Resumer of activty
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
