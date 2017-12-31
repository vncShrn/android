package com.utd.vxc152130.breakout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.utd.vxc152130.breakout.R;
import com.utd.vxc152130.breakout.models.Score;
import com.utd.vxc152130.breakout.other.Helper;

/*************************************************************************************************************
 * Activity that displays your current score at the end of the game, and asks player to enter their name
 * @author Vincy Shrine
 * @created 11 /27/2017
 *************************************************************************************************************/

public class SubmitScoreActivity extends AppCompatActivity {

    TextView isWonView, scoreView;
    EditText nameView;
    Button submitButton, cancelButton;
    Score receivedScore;

    /**
     * On create of SubmitScoreActivity activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_score);
        this.setFinishOnTouchOutside(false);
        // Get view elements
        nameView = (EditText) findViewById(R.id.entryName);
        scoreView = (TextView) findViewById(R.id.entryScore);
        isWonView = (TextView) findViewById(R.id.isWonOrLost);
        submitButton = (Button) findViewById(R.id.buttonSubmit);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        // Read intent details
        Intent intent = getIntent();
        receivedScore = (Score) intent.getSerializableExtra("gameScoreObject");
        boolean isWon = intent.getBooleanExtra("isWon", false);
        if (isWon)
            isWonView.setText("YOU WON!");
        else
            isWonView.setText("OOPS YOU LOST!");
        scoreView.setText("SCORE: "+receivedScore.getScore() + "");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameView.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "please enter your name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                receivedScore.setName(nameView.getText().toString());
                Helper.addToScoresList(receivedScore);
                finish();
                startActivity(new Intent(getApplicationContext(), DisplayScoresActivity.class));
            }
        });
    }
}
