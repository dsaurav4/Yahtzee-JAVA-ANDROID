package com.example.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.Model.Logger;
import com.example.yahtzee.Model.Player;
import com.example.yahtzee.Model.Round;
import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.R;

import java.util.List;


public class DecideTurnActivity extends AppCompatActivity {
    /**
     * Initializes the layout, sets up event handlers for buttons to determine player turn, and logs the action.
     *
     * @param savedInstanceState Bundle containing the saved state of the activity, if any.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        // Reference the "Roll" button
        Button rollButton = findViewById(R.id.rollToChooseTurn);

       //Reference the "Set" button
         Button setButton = findViewById(R.id.setToChooseTurn);

        // Set an OnClickListener for the "Roll" button
        rollButton.setOnClickListener(v -> {
            Logger.log("Rolling to choose turn");
            Intent intent = new Intent(DecideTurnActivity.this, RollToChooseActivity.class);
            startActivity(intent);
            finish();
        });

        setButton.setOnClickListener(v -> showPlayerTypeDialog());
    }

    /**
     * Displays a dialog for the user to select the starting player type (Human or Computer) and initializes the game accordingly.
     */
    private void showPlayerTypeDialog() {
        Logger.log("Set button clicked");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Player Type");

        // Set up the "Human" button
        builder.setPositiveButton("Human", (dialog, which) -> {
            Tournament tournament;
            Logger.log("Human button clicked");
            Logger.log("Tournament started");

            // Initialize the tournament and players
            tournament = new Tournament();
            List<Player> players = tournament.startTournament();
            Player humanPlayer = players.get(0);
            Player computerPlayer = players.get(1);

            Round round = new Round(humanPlayer, computerPlayer);
            Logger.log("Round started with human player starting first.");
            // Start RoundActivity and pass the Round object as an extra
            Intent intent = new Intent(DecideTurnActivity.this, RoundActivity.class);
            intent.putExtra("round", round); // Pass the round object
            startActivity(intent);
            finish();
        });

        // Set up the "Computer" button
        builder.setNegativeButton("Computer", (dialog, which) -> {
            Tournament tournament;
            Logger.log("Computer button clicked");
            Logger.log("Tournament started");
            // Initialize the tournament and players
            tournament = new Tournament();
            List<Player> players = tournament.startTournament();
            Player humanPlayer = players.get(0);
            Player computerPlayer = players.get(1);

            Round round = new Round(computerPlayer, humanPlayer);
            Logger.log("Round started with computer player starting first.");
            // Start RoundActivity and pass the Round object as an extra
            Intent intent = new Intent(DecideTurnActivity.this, RoundActivity.class);
            intent.putExtra("round", round); // Pass the round object
            startActivity(intent);
            finish();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
