package com.example.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.Model.Logger;
import com.example.yahtzee.Model.Player;
import com.example.yahtzee.Model.Round;
import com.example.yahtzee.Model.Tournament;
import com.example.yahtzee.R;

import java.util.List;

public class RollToChooseActivity extends AppCompatActivity {

    private Player humanPlayer;
    private Player computerPlayer;

    /**
     * Initializes the activity, sets the content view, initializes players, and initiates the dice roll to determine the turn.
     *
     * @param savedInstanceState Contains the previously saved state of the activity, if any.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_turn);

        Tournament tournament;

        // Initialize the tournament and players
        tournament = new Tournament();
        List<Player> players = tournament.startTournament();
        humanPlayer = players.get(0);
        computerPlayer = players.get(1);

        // Show the dice roll images and handle ties
        showRoll();
    }

    /**
     * Handles dice rolls for human and computer players, animates the roll, and determines the starting player or handles ties.
     */

    private void showRoll() {
        int humanRoll = humanPlayer.turnChooseRoll();
        Logger.log("Human Rolled: " + humanRoll);
        int computerRoll = computerPlayer.turnChooseRoll();
        Logger.log("Computer Rolled: " + computerRoll);

        ImageView humanDiceImage = findViewById(R.id.humanDiceImageView);
        ImageView computerDiceImage = findViewById(R.id.computerDiceImageView);
        TextView turnRollWinnerTextView = findViewById(R.id.turnRollWinnerTextView);

        int[] diceImages = {
                R.drawable.one, R.drawable.two, R.drawable.three,
                R.drawable.four, R.drawable.five, R.drawable.six
        };

        animateDiceRoll(humanDiceImage, diceImages[humanRoll - 1]);
        animateDiceRoll(computerDiceImage, diceImages[computerRoll - 1]);

        if (humanRoll == computerRoll) {
            new Handler().postDelayed(this::showRollAgainDialog, 500);
            Logger.log("It's a tie! Rolling again to choose turn.");
        } else {
            // Determine the winner and set the TextView with the result

            if (humanRoll > computerRoll) {
                turnRollWinnerTextView.setText("Human Goes First!");
                Logger.log("Human Goes First!");
                startRoundActivity(humanPlayer, computerPlayer);

            } else {
                turnRollWinnerTextView.setText("Computer Goes First!");
                Logger.log("Computer Goes First!");
                startRoundActivity(computerPlayer, humanPlayer);
            }
        }

    }

    /**
     * Displays a dialog prompting the user to roll the dice again in the event of a tie.
     */
    private void showRollAgainDialog() {
        // Inflate the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dice_roll_again, null);
        builder.setView(customView);

        // Prevent dialog from closing when touching outside
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        // Find the roll again button and set its click listener
        Button rollAgainButton = customView.findViewById(R.id.rollAgainButton);
        rollAgainButton.setOnClickListener(v -> {
            showRoll();
            dialog.dismiss();
        });

        dialog.show();

    }

    /**
     * Animates a dice roll by rotating the image and setting the final dice image after the animation ends.
     * <br>
     * Reference:
     * <br>
     * Help from ChatGPT
     * @param diceImageView The ImageView to animate.
     * @param finalImageResource The resource ID of the final dice image to display.
     */
    private void animateDiceRoll(ImageView diceImageView, int finalImageResource) {
        // Rotate animation
        diceImageView.animate()
                .rotationBy(360) // Rotate 360 degrees
                .setDuration(300) // Animation duration in milliseconds
                .withEndAction(() -> {
                    // Set the final image after the animation ends
                    diceImageView.setImageResource(finalImageResource);
                })
                .start();
    }

    /**
     * Starts the `RoundActivity` with the specified players and logs the start of the round.
     *
     * @param firstPlayer The player who takes the first turn.
     * @param secondPlayer The other player in the round.
     */
    private void startRoundActivity(Player firstPlayer, Player secondPlayer) {
        Button startRoundButton = findViewById(R.id.startRoundButton);

        startRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.log("Starting round...");
                Round round = new Round(firstPlayer, secondPlayer);
                Intent intent = new Intent(RollToChooseActivity.this, RoundActivity.class);
                intent.putExtra("round", round);
                startActivity(intent);
                finish();
            }
        });
    }
}
