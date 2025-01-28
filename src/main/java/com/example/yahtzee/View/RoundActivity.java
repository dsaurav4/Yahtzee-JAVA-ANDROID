package com.example.yahtzee.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yahtzee.Model.Computer;
import com.example.yahtzee.Model.Dice;
import com.example.yahtzee.Model.Human;
import com.example.yahtzee.Model.Logger;
import com.example.yahtzee.Model.Pair;
import com.example.yahtzee.Model.Player;
import com.example.yahtzee.Model.Round;
import com.example.yahtzee.Model.ScoreCard;
import com.example.yahtzee.Model.Serialization;
import com.example.yahtzee.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundActivity extends AppCompatActivity {

    // Stores TextViews for displaying points for each scoring category.
    // The keys represent category names (e.g., "Aces", "Twos"), and the values are the corresponding TextViews.
    private Map<String, TextView> pointsTextViews = new HashMap<>();

    // Stores TextViews for displaying the winner of each category (e.g., "Human", "Computer").
    // The keys represent category names, and the values are the corresponding TextViews.
    private Map<String, TextView> winnerTextViews = new HashMap<>();

    // Stores TextViews for displaying the round number for each category.
    // The keys represent category names, and the values are the corresponding TextViews.
    private Map<String, TextView> roundTextViews = new HashMap<>();

    // Stores Buttons for each scoring category.
    // The keys represent category names, and the values are the corresponding Buttons.
    private Map<String, Button> scoreButtons = new HashMap<>();

    // Button for initiating the dice roll.
    private Button rollDiceButton;

    // Button for rolling dice again after selecting which dice to keep.
    private Button rollAgainButton;

    // Button for finalizing the dice selection (stand) without rolling again.
    private Button standButton;

    // Button for manually setting dice values instead of rolling.
    private Button setManuallyButton;

    // Button for selecting dice to keep between rolls.
    private Button selectDice;

    // ImageButtons representing the dice on the screen.
    private ImageButton dice1;
    private ImageButton dice2;
    private ImageButton dice3;
    private ImageButton dice4;
    private ImageButton dice5;

    // ImageButton for opening the logs dialog to view game logs.
    private ImageButton logButton;

    // ImageButton for showing hints or advice during the player's turn.
    private ImageButton hintButton;

    // TextViews for game-related information:
    // Displays the name of the current player's turn.
    private TextView turnText;

    // Displays the current round number in the game.
    private TextView roundNumber;

    // Displays the number of rolls completed in the current turn.
    private TextView rollNumber;

    // Displays the total score for the Human player.
    private TextView humanScore;

    // Displays the total score for the Computer player.
    private TextView computerScore;

    // The current player whose turn it is.
    private Player currentPlayer;

    // The current round instance, containing information about players and round state.
    private Round round;

    // Stores the dice values rolled during the current turn.
    private List<Integer> rolledDice = new ArrayList<>();

    // Stores the dice values the player has chosen to keep across rolls.
    private List<Integer> finalDice = new ArrayList<>();

    // Stores the ImageButtons corresponding to the dice the player has chosen to keep.
    private List<ImageButton> finalDiceButtons = new ArrayList<>();

    // Counter for tracking the number of rolls in the current turn (1 to 3).
    private int rollNumberCounter = 1;


    /**
     * Initializes the RoundActivity, setting up UI elements, event listeners, and game logic based on the current round or player state.
     *
     * @param savedInstanceState Contains the saved state of the activity, if any.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_dice);

        round = (Round) getIntent().getSerializableExtra("round");

        pointsTextViews.put("Aces", (TextView) findViewById(R.id.acesPoints));
        winnerTextViews.put("Aces", (TextView) findViewById(R.id.acesWinner));
        pointsTextViews.put("Twos", (TextView) findViewById(R.id.twosPoints));
        winnerTextViews.put("Twos", (TextView) findViewById(R.id.twosWinner));
        pointsTextViews.put("Threes", (TextView) findViewById(R.id.threesPoints));
        winnerTextViews.put("Threes", (TextView) findViewById(R.id.threesWinner));
        pointsTextViews.put("Fours", (TextView) findViewById(R.id.foursPoints));
        winnerTextViews.put("Fours", (TextView) findViewById(R.id.foursWinner));
        pointsTextViews.put("Fives", (TextView) findViewById(R.id.fivesPoints));
        winnerTextViews.put("Fives", (TextView) findViewById(R.id.fivesWinner));
        pointsTextViews.put("Sixes", (TextView) findViewById(R.id.sixesPoints));
        winnerTextViews.put("Sixes", (TextView) findViewById(R.id.sixesWinner));
        pointsTextViews.put("Three of a Kind", (TextView) findViewById(R.id.threeOfAKindPoints));
        winnerTextViews.put("Three of a Kind", (TextView) findViewById(R.id.threeOfAKindWinner));
        pointsTextViews.put("Four of a Kind", (TextView) findViewById(R.id.fourOfAKindPoints));
        winnerTextViews.put("Four of a Kind", (TextView) findViewById(R.id.fourOfAKindWinner));
        pointsTextViews.put("Full House", (TextView) findViewById(R.id.fullHousePoints));
        winnerTextViews.put("Full House", (TextView) findViewById(R.id.fullHouseWinner));
        pointsTextViews.put("Four Straight", (TextView) findViewById(R.id.fourStraightPoints));
        winnerTextViews.put("Four Straight", (TextView) findViewById(R.id.fourStraightWinner));
        pointsTextViews.put("Five Straight", (TextView) findViewById(R.id.fiveStraightPoints));
        winnerTextViews.put("Five Straight", (TextView) findViewById(R.id.fiveStraightWinner));
        pointsTextViews.put("Yahtzee", (TextView) findViewById(R.id.yahtzeePoints));
        winnerTextViews.put("Yahtzee", (TextView) findViewById(R.id.yahtzeeWinner));

        roundTextViews.put("Aces", (TextView) findViewById(R.id.acesRound));
        roundTextViews.put("Twos", (TextView) findViewById(R.id.twosRound));
        roundTextViews.put("Threes", (TextView) findViewById(R.id.threesRound));
        roundTextViews.put("Fours", (TextView) findViewById(R.id.foursRound));
        roundTextViews.put("Fives", (TextView) findViewById(R.id.fivesRound));
        roundTextViews.put("Sixes", (TextView) findViewById(R.id.sixesRound));
        roundTextViews.put("Three of a Kind", (TextView) findViewById(R.id.threeOfAKindRound));
        roundTextViews.put("Four of a Kind", (TextView) findViewById(R.id.fourOfAKindRound));
        roundTextViews.put("Full House", (TextView) findViewById(R.id.fullHouseRound));
        roundTextViews.put("Four Straight", (TextView) findViewById(R.id.fourStraightRound));
        roundTextViews.put("Five Straight", (TextView) findViewById(R.id.fiveStraightRound));
        roundTextViews.put("Yahtzee", (TextView) findViewById(R.id.yahtzeeRound));


        scoreButtons.put("Aces", (Button) findViewById(R.id.scoreAcesButton));
        scoreButtons.put("Twos", (Button) findViewById(R.id.scoreTwosButton));
        scoreButtons.put("Threes", (Button) findViewById(R.id.scoreThreesButton));
        scoreButtons.put("Fours", (Button) findViewById(R.id.scoreFoursButton));
        scoreButtons.put("Fives", (Button) findViewById(R.id.scoreFivesButton));
        scoreButtons.put("Sixes", (Button) findViewById(R.id.scoreSixesButton));
        scoreButtons.put("Three of a Kind", (Button) findViewById(R.id.scoreThreeOfAKindButton));
        scoreButtons.put("Four of a Kind", (Button) findViewById(R.id.scoreFourOfAKindButton));
        scoreButtons.put("Full House", (Button) findViewById(R.id.scoreFullHouseButton));
        scoreButtons.put("Four Straight", (Button) findViewById(R.id.scoreFourStraightButton));
        scoreButtons.put("Five Straight", (Button) findViewById(R.id.scoreFiveStraightButton));
        scoreButtons.put("Yahtzee", (Button) findViewById(R.id.scoreYahtzeeButton));

        for (Button button : scoreButtons.values()) {
            button.setVisibility(View.GONE);
        }


        updateScoreCard();


        rollDiceButton = findViewById(R.id.rollDiceButton);
        rollAgainButton = findViewById(R.id.rollAgainButton);
        standButton = findViewById(R.id.standButton);
        setManuallyButton = findViewById(R.id.setManuallyButton);
        selectDice = findViewById(R.id.selectDice);

        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);
        dice3 = findViewById(R.id.dice3);
        dice4 = findViewById(R.id.dice4);
        dice5 = findViewById(R.id.dice5);

        logButton = findViewById(R.id.logButton);
        hintButton = findViewById(R.id.hintButton);

        turnText = findViewById(R.id.turnText);
        roundNumber = findViewById(R.id.roundNumber);
        rollNumber = findViewById(R.id.rollNumber);
        humanScore = findViewById(R.id.humanScore);
        computerScore = findViewById(R.id.computerScore);

        humanScore.setText("👨🏻: " + ScoreCard.getTotalScore("Human"));
        computerScore.setText("🤖: " + ScoreCard.getTotalScore("Computer"));

        logButton.setOnClickListener(v -> {
            // Inflate the custom layout for the logs dialog
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_logs, null);

            // Retrieve the TextView from the layout
            TextView logsTextView = dialogView.findViewById(R.id.logsTextView);

            // Set the logs text in the TextView
            String logs = Logger.print();
            logsTextView.setText(logs.isEmpty() ? "No logs available." : logs);

            // Build and show the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logs")
                    .setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        if (round != null) {
            currentPlayer = round.getFirstPlayer();
            roundNumber.setText("Round: " + Round.getRoundNumber());
            Logger.log("Round " + Round.getRoundNumber() + " has started");
            turnText.setText(currentPlayer.getName() + "'s turn");
            Logger.log(currentPlayer.getName() + " has started their turn");
            rollNumber.setText("Roll: " + rollNumberCounter);

            rollAgainButton.setVisibility(View.GONE);
            standButton.setVisibility(View.GONE);
            hintButton.setVisibility(View.GONE);
            selectDice.setVisibility(View.GONE);

            rollDiceButton.setOnClickListener(v -> rollDice());
            setManuallyButton.setOnClickListener(v -> setManually());
        } else {
            currentPlayer = (Player) getIntent().getSerializableExtra("player");
            Logger.log(currentPlayer.getName() + " has started their turn.");
            roundNumber.setText("Round: " + Round.getRoundNumber());
            assert currentPlayer != null;
            turnText.setText(currentPlayer.getName() + "'s turn");
            rollNumber.setText("Roll: " + rollNumberCounter);

            rollAgainButton.setVisibility(View.GONE);
            standButton.setVisibility(View.GONE);
            hintButton.setVisibility(View.GONE);
            selectDice.setVisibility(View.GONE);

            rollDiceButton.setOnClickListener(v -> rollDice());
            setManuallyButton.setOnClickListener(v -> setManually());

        }
    }

    /**
     * Updates the scorecard UI elements to reflect the current scores, winners, and round information for each category.
     */
    private void updateScoreCard() {
        // Retrieve the score map from ScoreCard
        Map<String, Map.Entry<String, Map.Entry<Integer, Integer>>> scoreBoard = ScoreCard.getScoreBoard();

        // Iterate over each entry in the scoreBoard map
        for (Map.Entry<String, Map.Entry<String, Map.Entry<Integer, Integer>>> entry : scoreBoard.entrySet()) {
            String category = entry.getKey();
            String winner = entry.getValue().getKey();
            int points = entry.getValue().getValue().getKey();
            int round = entry.getValue().getValue().getValue();

            // Check if the category is filled by checking if the winner is empty
            if (winner.isEmpty()) {
                // Set TextViews to blank if category is not filled
                if (pointsTextViews.containsKey(category)) {
                    pointsTextViews.get(category).setText("");
                }
                if (winnerTextViews.containsKey(category)) {
                    winnerTextViews.get(category).setText("");
                }
                if (roundTextViews.containsKey(category)) {
                    roundTextViews.get(category).setText("");
                }
            } else {
                // Update the TextViews if the category is filled
                if (pointsTextViews.containsKey(category)) {
                    pointsTextViews.get(category).setText(String.valueOf(points));
                }
                if (winnerTextViews.containsKey(category)) {
                    winnerTextViews.get(category).setText(winner);
                }
                if (roundTextViews.containsKey(category)) {
                    roundTextViews.get(category).setText(String.valueOf(round));
                }
            }
        }
    }

    /**
     * Allows the player to select dice to keep or reroll, highlighting the selected dice and updating the game state.
     *
     * @param dice The list of ImageButtons representing dice available for selection.
     */
    private void enableDiceSelection(List<ImageButton> dice) {
        // If finalDice already contains 4 values, skip selection and directly update the roll counter
        if (finalDice.size() == 4) {
            rollNumberCounter++;
            rollNumber.setText("Roll: " + rollNumberCounter);

            rollDiceButton.setVisibility(View.VISIBLE);
            setManuallyButton.setVisibility(View.VISIBLE);
            hintButton.setVisibility(View.GONE);
            return;
        }

        selectDice.setVisibility(View.VISIBLE);

        // Temporary lists to hold the current roll's selections
        List<Integer> tempDice = new ArrayList<>();
        List<ImageButton> tempDiceButtons = new ArrayList<>();

        // Filter the dice list to include only those not in finalDiceButtons
        List<ImageButton> selectableDice = new ArrayList<>();
        for (ImageButton diceButton : dice) {
            if (!finalDiceButtons.contains(diceButton)) {
                selectableDice.add(diceButton);
            }
        }

        for (int i = 0; i < selectableDice.size(); i++) {
            ImageButton diceButton = selectableDice.get(i);
            int dieValue = rolledDice.get(i); // Use the index to get the corresponding dice value

            diceButton.setOnClickListener(v -> {
                if (finalDiceButtons.contains(diceButton)) {
                    // If already selected in a previous roll, show an alert
                    new AlertDialog.Builder(this)
                            .setTitle("Dice Already Selected")
                            .setMessage("This dice was already selected in a previous roll and cannot be changed.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                } else if (tempDiceButtons.contains(diceButton)) {
                    // If already selected in the current roll, deselect it
                    tempDice.remove((Integer) dieValue);
                    tempDiceButtons.remove(diceButton);
                    diceButton.setAlpha(1.0f); // Reset transparency
                } else {
                    // If not selected yet, add to the temporary lists
                    tempDice.add(dieValue);
                    tempDiceButtons.add(diceButton);
                    diceButton.setAlpha(0.5f); // Make it slightly transparent
                }
            });
        }

        selectDice.setOnClickListener(v -> {
            // Confirm the selection by adding the temp selections to final lists
            selectDice.setVisibility(View.GONE);
            finalDice.addAll(tempDice);
            finalDiceButtons.addAll(tempDiceButtons);

            Logger.log("You kept: " + finalDice.toString());

            if(finalDice.size() == 5) {
                boolean hasCategoryToScore = false; // Flag to track if at least one category is available

                hintButton.setOnClickListener(v1 ->{
                    Pair<String, Integer> bestHand = ScoreCard.getBestHands(finalDice);
                    if(!bestHand.getKey().isEmpty()){
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("You should choose " + bestHand.getKey() + " with " + bestHand.getValue() + " points.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("There are no available categories to score.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }

                });

                // Make score buttons visible for categories with points > 0
                for (Map.Entry<String, Button> entry : scoreButtons.entrySet()) {
                    String category = entry.getKey();
                    Button scoreButton = entry.getValue();
                    int points = ScoreCard.calculatePoints(category, finalDice);

                    if (points > 0 && ScoreCard.isCategoryAvailable(category)) {
                        hasCategoryToScore = true; // Set the flag to true if a category is available
                        scoreButton.setVisibility(View.VISIBLE);

                        scoreButton.setOnClickListener(scoreView -> {
                            // Update the scorecard when this button is clicked
                            String winner = "Human";
                            int roundNumber = Round.getRoundNumber();
                            ScoreCard.chooseCategory(category, finalDice, winner, roundNumber);

                            // Hide all score buttons after one is clicked
                            for (Button button : scoreButtons.values()) {
                                button.setVisibility(View.GONE);
                            }

                            // Update the UI to reflect the updated scorecard
                            updateScoreCard();
                            resetCategoryHighlights();

                            handleGameOrRoundEnd();
                        });
                    } else {
                        scoreButton.setVisibility(View.INVISIBLE);
                    }
                }

                // If no category is available, show an alert dialog
                if (!hasCategoryToScore) {
                    new AlertDialog.Builder(this)
                            .setTitle("No Category Available")
                            .setMessage("There are no available categories to score. Please check your options.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            } else {
                resetCategoryHighlights();
                updateScoreCard();

                // Remove the onClick listeners for all dice buttons
                for (ImageButton diceButton : dice) {
                    diceButton.setOnClickListener(null);
                }

                // Clear the temporary lists
                tempDice.clear();
                tempDiceButtons.clear();

                // Update the roll number
                rollNumberCounter++;
                rollNumber.setText("Roll: " + rollNumberCounter);

                // Adjust button visibility for the second roll
                rollDiceButton.setVisibility(View.VISIBLE);
                setManuallyButton.setVisibility(View.VISIBLE);
                rollAgainButton.setVisibility(View.GONE);
                standButton.setVisibility(View.GONE);
                hintButton.setVisibility(View.GONE);

                // Attach functionality to rollDiceButton and setManuallyButton
                rollDiceButton.setOnClickListener(v2 -> rollDice());
                setManuallyButton.setOnClickListener(v2 -> setManually());
            }
        });
    }

    /**
     * Opens a dialog for the user to manually set the values of unselected dice and updates the game state accordingly.
     */
    @SuppressLint("InflateParams")
    private void setManually() {
        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_set_dice, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Find each row and its label in the dialog layout
        List<RadioGroup> diceRows = List.of(
                dialogView.findViewById(R.id.radioGroupRow1),
                dialogView.findViewById(R.id.radioGroupRow2),
                dialogView.findViewById(R.id.radioGroupRow3),
                dialogView.findViewById(R.id.radioGroupRow4),
                dialogView.findViewById(R.id.radioGroupRow5)
        );

        List<TextView> diceLabels = List.of(
                dialogView.findViewById(R.id.diceLabel1),
                dialogView.findViewById(R.id.diceLabel2),
                dialogView.findViewById(R.id.diceLabel3),
                dialogView.findViewById(R.id.diceLabel4),
                dialogView.findViewById(R.id.diceLabel5)
        );

        // Create a list of indices for the dice not already selected
        List<ImageButton> unselectedDice = new ArrayList<>();
        List<ImageButton> dice = List.of(dice1, dice2, dice3, dice4, dice5);
        for (int i = 0; i < dice.size(); i++) {
            if (!finalDiceButtons.contains(dice.get(i))) {
                unselectedDice.add(dice.get(i));
            }
        }

        // Display only the rows and labels needed for unselected dice
        for (int i = 0; i < diceRows.size(); i++) {
            if (i < unselectedDice.size()) {
                diceRows.get(i).setVisibility(View.VISIBLE);
                diceLabels.get(i).setVisibility(View.VISIBLE);
            } else {
                diceRows.get(i).setVisibility(View.GONE);
                diceLabels.get(i).setVisibility(View.GONE);
            }
        }

        // Show the dialog
        AlertDialog dialog = builder.create();

        // OK button to confirm selections
        dialogView.findViewById(R.id.ok_button).setOnClickListener(okButtonView -> {
            List<Integer> selectedValues = new ArrayList<>();
            boolean allSelected = true;

            // Retrieve selected dice values for unselected dice
            for (int i = 0; i < unselectedDice.size(); i++) {
                RadioGroup group = diceRows.get(i);
                int selectedRadioButtonId = group.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    RadioButton selectedButton = group.findViewById(selectedRadioButtonId);
                    selectedValues.add(Integer.parseInt(selectedButton.getText().toString()));
                } else {
                    allSelected = false;
                    break;
                }
            }

            if (allSelected) {
                rolledDice.clear();
                rolledDice.addAll(selectedValues);

                postRoll();

                dialog.dismiss();
            } else {
                // Show an AlertDialog if not all rows have a selection
                new AlertDialog.Builder(this)
                        .setTitle("Incomplete Selection")
                        .setMessage("Please select a dice value for all dice.")
                        .setPositiveButton("OK", (dialogInterface, which) -> dialogInterface.dismiss())
                        .show();
            }
        });

        // Cancel button to dismiss the dialog
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Highlights scoring categories based on the current dice values, using color codes for available or advisable categories.
     * <p>
     * References:
     * <br>- <a href="https://www.geeksforgeeks.org/textview-in-android-with-example/">TextView Example</a>
     * <br>- <a href="https://www.geeksforgeeks.org/text-highlighter-in-android/">Text Highlighter</a>
     * <br>- <a href="https://www.youtube.com/watch?v=NfpP6VFmUNQ">YouTube Example</a>
     * <br>- Help from ChatGPT to highlight particular cells.
     * </p>
     *
     * @param currentDice The list of integers representing the current dice values.
     */
    private void highlightCategoriesWithPoints(List<Integer> currentDice) {
        // Get the list of categories to pursue based on the current dice values
        List<String> categoriesToPursue = ScoreCard.getCategoriesToPursue(currentDice);

        // Iterate over each category to check potential points or pursuit status
        for (String category : pointsTextViews.keySet()) {
            TextView pointsTextView = pointsTextViews.get(category);
            TextView winnerTextView = winnerTextViews.get(category);

            // Check if the category is already filled in the ScoreCard
            boolean isCategoryScored = !ScoreCard.isCategoryAvailable(category);

            // Reset the background and text color for unscored categories
            if (!isCategoryScored) {
                if (pointsTextView != null) {
                    pointsTextView.setBackgroundResource(R.drawable.cell_border); // Reset to default border
                    pointsTextView.setTextColor(Color.BLACK);
                    pointsTextView.setText(""); // Clear points for reset
                }
                if (winnerTextView != null) {
                    winnerTextView.setBackgroundResource(R.drawable.cell_border); // Reset to default border
                    winnerTextView.setTextColor(Color.BLACK);
                }
            }

            // Only proceed if the category is available (not already scored)
            if (ScoreCard.isCategoryAvailable(category)) {
                int points = ScoreCard.calculatePoints(category, currentDice);

                if (points > 0) {
                    // Highlight with green if the category has points > 0
                    if (pointsTextView != null) {
                        pointsTextView.setText(String.valueOf(points));
                        pointsTextView.setBackgroundResource(R.drawable.cell_border_highlighted); // Green highlight
                    }
                    if (winnerTextView != null) {
                        winnerTextView.setBackgroundResource(R.drawable.cell_border_highlighted); // Green highlight
                    }
                } else if (categoriesToPursue.contains(category) && rollNumberCounter != 3) {
                    // Highlight with yellow if the category is in the pursuit list
                    if (pointsTextView != null) {
                        pointsTextView.setBackgroundResource(R.drawable.cell_border_yellow); // Yellow highlight
                    }
                    if (winnerTextView != null) {
                        winnerTextView.setBackgroundResource(R.drawable.cell_border_yellow); // Yellow highlight
                    }
                }
            }
        }
    }

    /**
     * Resets visual highlights for all scoring categories to their default styles, ensuring a clean UI state.
     * Reference: Help from ChatGPT
     */
    private void resetCategoryHighlights() {
        // Reset background and text color for all category text views
        for (String category : pointsTextViews.keySet()) {
            TextView pointsTextView = pointsTextViews.get(category);
            TextView winnerTextView = winnerTextViews.get(category);

            if (pointsTextView != null) {
                pointsTextView.setBackgroundResource(R.drawable.cell_border);
                pointsTextView.setTextColor(Color.BLACK);
            }
            if (winnerTextView != null) {
                winnerTextView.setBackgroundResource(R.drawable.cell_border);
                winnerTextView.setTextColor(Color.BLACK);
            }
        }
    }

    /**
     * Rolls unselected dice, updates the rolled values, and processes post-roll logic to progress the game.
     */
    @SuppressLint("SetTextI18n")
    private void rollDice() {
        resetCategoryHighlights();
        updateScoreCard();

        Dice dice = new Dice();
        // Clear the rolledDice list and roll only the unselected dice
        rolledDice.clear();
        List<Integer> newRolls = dice.rollDice(5-finalDice.size());
        rolledDice.addAll(newRolls); // Add new rolls to rolledDice

        postRoll();
    }

    /**
     * Handles post-roll actions, updating the dice images, highlighting scoring categories, and managing player actions or Computer decisions.
     */
    private void postRoll() {
        Logger.log("Rolling roll number: " + rollNumberCounter);
        Logger.log("Rolled dice: " + rolledDice.toString());

        List<ImageButton> dice = List.of(dice1, dice2, dice3, dice4, dice5);
        List<ImageButton> remainingDiceButtons = new ArrayList<>();

        // Determine which dice were not selected
        for (int i = 0; i < dice.size(); i++) {
            if (!finalDiceButtons.contains(dice.get(i))) {
                remainingDiceButtons.add(dice.get(i));
            }
        }

        // Update the dice images for the newly rolled dice
        for (int i = 0; i < rolledDice.size(); i++) {
            int newDieValue = rolledDice.get(i);
            int drawableId;

            // Map the new dice value to the correct drawable
            switch (newDieValue) {
                case 1:
                    drawableId = R.drawable.one;
                    break;
                case 2:
                    drawableId = R.drawable.two;
                    break;
                case 3:
                    drawableId = R.drawable.three;
                    break;
                case 4:
                    drawableId = R.drawable.four;
                    break;
                case 5:
                    drawableId = R.drawable.five;
                    break;
                case 6:
                    drawableId = R.drawable.six;
                    break;
                default:
                    drawableId = R.drawable.one;
            }

            // Update the dice image for the rolled dice
            ImageButton diceButton = remainingDiceButtons.get(i);
            int finalDrawableId = drawableId;

            // Apply rotation animation
            diceButton.animate()
                    .rotationBy(360)
                    .setDuration(500)
                    .withEndAction(() -> diceButton.setImageResource(finalDrawableId))
                    .start();
        }

        rollDiceButton.setVisibility(View.GONE);
        setManuallyButton.setVisibility(View.GONE);

        // Combine finalDice and rolledDice to get the current state of all dice
        List<Integer> currentDice = new ArrayList<>(finalDice);
        currentDice.addAll(rolledDice);


        // Highlight categories with potential points based on current dice state
        highlightCategoriesWithPoints(currentDice);

        if (currentPlayer instanceof Human) {
            hintButton.setVisibility(View.VISIBLE);

            // Set up hint button if currentPlayer is Human
            Human human = (Human) currentPlayer;
            if(rollNumberCounter == 1){
                hintButton.setOnClickListener(v -> {
                    String advice = human.firstRollHelp(currentDice);
                    new AlertDialog.Builder(this)
                            .setTitle("Hint")
                            .setMessage(advice)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            } else if(rollNumberCounter == 2) {
                hintButton.setOnClickListener(v -> {
                    String advice = human.secondRollHelp(rolledDice, finalDice);
                    new AlertDialog.Builder(this)
                            .setTitle("Hint")
                            .setMessage(advice)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                });
            } else {
                hintButton.setOnClickListener(v1 ->{
                    Pair<String, Integer> bestHand = ScoreCard.getBestHands(currentDice);
                    if(!bestHand.getKey().isEmpty()){
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("You should choose " + bestHand.getKey() + " with " + bestHand.getValue() + " points.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("There are no available categories to score.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }

                });
            }


            if(rollNumberCounter < 3) {
                standButton.setVisibility(View.VISIBLE);
                rollAgainButton.setVisibility(View.VISIBLE);
            }
            else{
                for(ImageButton diceButton : dice) {
                    diceButton.setAlpha(1.0f);
                }

                hintButton.setOnClickListener(v1 ->{
                    Pair<String, Integer> bestHand = ScoreCard.getBestHands(currentDice);
                    if(!bestHand.getKey().isEmpty()){
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("You should choose " + bestHand.getKey() + " with " + bestHand.getValue() + " points.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("There are no available categories to score.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }

                });

                boolean hasCategoryToScore = false; // Flag to track if at least one category is available

                // Make score buttons visible for categories with points > 0
                for (Map.Entry<String, Button> entry : scoreButtons.entrySet()) {
                    String category = entry.getKey();
                    Button scoreButton = entry.getValue();
                    int points = ScoreCard.calculatePoints(category, currentDice);

                    if (points > 0 && ScoreCard.isCategoryAvailable(category)) {
                        hasCategoryToScore = true; // Set the flag to true if a category is available
                        scoreButton.setVisibility(View.VISIBLE);

                        scoreButton.setOnClickListener(scoreView -> {
                            // Update the scorecard when this button is clicked
                            String winner = "Human";
                            int roundNumber = Round.getRoundNumber();
                            ScoreCard.chooseCategory(category, currentDice, winner, roundNumber);

                            // Hide all score buttons after one is clicked
                            for (Button button : scoreButtons.values()) {
                                button.setVisibility(View.GONE);
                            }

                            // Update the UI to reflect the updated scorecard
                            updateScoreCard();
                            resetCategoryHighlights();

                            handleGameOrRoundEnd();
                        });
                    } else {
                        scoreButton.setVisibility(View.INVISIBLE);
                    }
                }

                // If no category is available, show an alert dialog
                if (!hasCategoryToScore) {
                    new AlertDialog.Builder(this)
                            .setTitle("No Category Available")
                            .setMessage("There are no available categories to score. Please check your options.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                handleGameOrRoundEnd();
                                dialog.dismiss();
                            })
                            .show();

                }
            }

            standButton.setOnClickListener(v -> {
                // Hide the stand button after it is pressed
                standButton.setVisibility(View.GONE);
                rollAgainButton.setVisibility(View.GONE);

                hintButton.setOnClickListener(v1 ->{
                    Pair<String, Integer> bestHand = ScoreCard.getBestHands(currentDice);
                    if(!bestHand.getKey().isEmpty()){
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("You should choose " + bestHand.getKey() + " with " + bestHand.getValue() + " points.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Hint")
                                .setMessage("There are no available categories to score.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    }

                });

                boolean hasCategoryToScore = false; // Flag to track if at least one category is available

                for(ImageButton diceButton : dice) {
                    diceButton.setAlpha(1.0f);
                }

                // Make score buttons visible for categories with points > 0
                for (Map.Entry<String, Button> entry : scoreButtons.entrySet()) {
                    String category = entry.getKey();
                    Button scoreButton = entry.getValue();
                    int points = ScoreCard.calculatePoints(category, currentDice);

                    if (points > 0 && ScoreCard.isCategoryAvailable(category)) {
                        hasCategoryToScore = true; // Set the flag to true if a category is available
                        scoreButton.setVisibility(View.VISIBLE);

                        scoreButton.setOnClickListener(scoreView -> {
                            // Update the scorecard when this button is clicked
                            String winner = "Human";
                            int roundNumber = Round.getRoundNumber();
                            ScoreCard.chooseCategory(category, currentDice, winner, roundNumber);

                            // Hide all score buttons after one is clicked
                            for (Button button : scoreButtons.values()) {
                                button.setVisibility(View.GONE);
                            }

                            handleGameOrRoundEnd();

                            // Update the UI to reflect the updated scorecard
                            updateScoreCard();
                            resetCategoryHighlights();
                        });
                    } else {
                        scoreButton.setVisibility(View.INVISIBLE);
                    }
                }

                // If no category is available, show an alert dialog
                if (!hasCategoryToScore) {
                    new AlertDialog.Builder(this)
                            .setTitle("No Category Available")
                            .setMessage("There are no available categories to score. Please check your options.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                handleGameOrRoundEnd();
                                dialog.dismiss();
                            })
                            .show();

                    handleGameOrRoundEnd();
                }
            });


            rollAgainButton.setOnClickListener(v -> {
                rollNumber.setText("Select dice to keep");
                enableDiceSelection(dice); // Enable selection for each dice
                rollAgainButton.setVisibility(View.GONE);
                standButton.setVisibility(View.GONE);
            });
        }
        else {
            Computer computer = (Computer) currentPlayer;

            if (rollNumberCounter == 1) {
                Pair<List<Integer>, List<Integer>> pair = computer.firstRollMove(currentDice);
                List<Integer> computerKeptDice = pair.getKey(); // Dice the computer chooses to keep
                Logger.log("Computer kept: " + computerKeptDice.toString());

                if (computerKeptDice.size() == 5) {
                    Pair<String, Integer> bestHand = ScoreCard.getBestHands(currentDice);
                    if(!bestHand.getKey().isEmpty())
                        Logger.log("Computer chooses to score " + bestHand.getKey() + " with " + bestHand.getValue() + " points.");
                    else
                        Logger.log("Computer has no category to score.");

                    rollDiceButton.postDelayed(() -> {
                        int roundNumber = Round.getRoundNumber();
                        String winner = "Computer";
                        String category = ScoreCard.getBestHands(computerKeptDice).getKey();
                        ScoreCard.chooseCategory(category, currentDice, winner, roundNumber);
                        updateScoreCard();
                        resetCategoryHighlights();

                        handleGameOrRoundEnd();
                    }, 1000);
                } else {
                    Logger.log("Computer is reerolling: " + pair.getValue());
                    // Track which dice values the computer chose to keep
                    List<Integer> remainingKeptDice = new ArrayList<>(computerKeptDice);

                    // Iterate through rolled dice to match and highlight the corresponding buttons
                    for (int i = 0; i < rolledDice.size(); i++) {
                        int diceValue = rolledDice.get(i);
                        ImageButton diceButton = dice.get(i); // Get the button corresponding to this dice

                        if (!remainingKeptDice.isEmpty() && remainingKeptDice.contains(diceValue)) {
                            // Match dice value to the first occurrence in remainingKeptDice
                            remainingKeptDice.remove((Integer) diceValue);
                            diceButton.postDelayed(() -> {
                                diceButton.setAlpha(0.5f);
                                if (!finalDiceButtons.contains(diceButton)) {
                                    finalDiceButtons.add(diceButton); // Add button to finalDiceButtons
                                }
                                finalDice.add(diceValue);
                            }, 1000);
                        } else {
                            diceButton.postDelayed(() -> diceButton.setAlpha(1.0f), 1000);
                        }
                    }

                    rollDiceButton.postDelayed(() -> {
                        rollNumberCounter++;
                        rollDiceButton.setVisibility(View.VISIBLE);
                        setManuallyButton.setVisibility(View.VISIBLE);

                        rollNumber.setText("Roll: " + rollNumberCounter);
                    }, 1000);
                }
            } else if (rollNumberCounter == 2) {
                Pair<List<Integer>, List<Integer>> pair = computer.secondRollMove(rolledDice, finalDice);
                List<Integer> computerKeptDice = pair.getKey(); // Updated list of dice the computer chooses to keep
                Logger.log("Computer kept: " + computerKeptDice.toString());

                if (computerKeptDice.size() == 5) {
                    Pair<String, Integer> bestHand = ScoreCard.getBestHands(currentDice);
                    if(!bestHand.getKey().isEmpty())
                        Logger.log("Computer chooses to score " + bestHand.getKey() + " with " + bestHand.getValue() + " points.");
                    else
                        Logger.log("Computer has no category to score.");

                    rollDiceButton.postDelayed(() -> {
                        int roundNumber = Round.getRoundNumber();
                        String winner = "Computer";
                        String category = ScoreCard.getBestHands(computerKeptDice).getKey();
                        ScoreCard.chooseCategory(category, currentDice, winner, roundNumber);
                        updateScoreCard();
                        resetCategoryHighlights();

                        handleGameOrRoundEnd();
                    }, 1000);
                } else {
                    Logger.log("Computer is reerolling: " + pair.getValue());

                    // Track which dice values the computer chose to keep
                    List<Integer> remainingKeptDice = new ArrayList<>(computerKeptDice);

                    for(int die : finalDice) {
                        remainingKeptDice.remove((Integer) die);
                    }

                    // Iterate through rolled dice to match and highlight the corresponding buttons
                    for (int i = 0; i < rolledDice.size(); i++) {
                        int diceValue = rolledDice.get(i);
                        ImageButton diceButton = remainingDiceButtons.get(i); // Get the button corresponding to this dice

                        if (!remainingKeptDice.isEmpty() && remainingKeptDice.contains(diceValue)) {
                            // Match dice value to the first occurrence in remainingKeptDice
                            remainingKeptDice.remove((Integer) diceValue); // Remove matched value
                            diceButton.postDelayed(() -> {
                                diceButton.setAlpha(0.5f);
                                if (!finalDiceButtons.contains(diceButton)) {
                                    finalDiceButtons.add(diceButton); // Add button to finalDiceButtons
                                }
                                finalDice.add(diceValue);
                            }, 1000);
                        }
                    }

                    rollDiceButton.postDelayed(() -> {
                        rollNumberCounter++;
                        rollDiceButton.setVisibility(View.VISIBLE);
                        setManuallyButton.setVisibility(View.VISIBLE);

                        rollNumber.setText("Roll: " + rollNumberCounter);
                    }, 1000);
                }
            } else {
                Pair<String, Integer> bestHand = ScoreCard.getBestHands(currentDice);
                if(!bestHand.getKey().isEmpty())
                    Logger.log("Computer chooses to score " + bestHand.getKey() + " with " + bestHand.getValue() + " points.");
                else
                    Logger.log("Computer has no category to score.");

                rollDiceButton.setVisibility(View.GONE);
                setManuallyButton.setVisibility(View.GONE);

                finalDice.addAll(rolledDice);
                rollDiceButton.postDelayed(() -> {
                    int roundNumber = Round.getRoundNumber();
                    String winner = "Computer";
                    String category = ScoreCard.getBestHands(finalDice).getKey();
                    if(!category.isEmpty())
                        ScoreCard.chooseCategory(category, currentDice, winner, roundNumber);
                    updateScoreCard();
                    resetCategoryHighlights();

                    handleGameOrRoundEnd();
                }, 1000);
            }

        }
    }

    /**
     * Determines whether to end the game, proceed to the next round, or transition to the next player based on the current game state.
     */
    private void handleGameOrRoundEnd() {
        if (round != null) {
            if (!ScoreCard.isAllCategoriesFilled()) {
                proceedToNextPlayer(round);
            } else {
                showGameOverDialog();
            }
        } else {
            if (!ScoreCard.isAllCategoriesFilled()) {
                showRoundOverDialog();
            } else {
                showGameOverDialog();
            }
        }
    }

    /**
     * Advances to the next player's turn in the current round and starts a new RoundActivity with the next player.
     *
     * @param round The current round instance used to determine the next player.
     */
    private void proceedToNextPlayer(Round round) {
        Intent intent = new Intent(this, RoundActivity.class);
        intent.putExtra("player", round.getSecondPlayer());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    /**
     * Displays a dialog summarizing the game results, including final scores and the winner, and resets the game upon dismissal.
     */
    private void showGameOverDialog() {
        int humanScore = ScoreCard.getTotalScore("Human");
        int computerScore = ScoreCard.getTotalScore("Computer");

        String winnerMessage = determineWinnerMessage(humanScore, computerScore);

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("All categories have been filled. Game over!\n" +
                        "Human Score: " + humanScore + "\n" +
                        "Computer Score: " + computerScore + "\n" +
                        winnerMessage)
                .setPositiveButton("OK", (dialog, which) -> resetGame())
                .show();
    }

    /**
     * Displays a dialog summarizing the round results, including scores and the next round's first player, and prompts to save the game.
     */
    private void showRoundOverDialog() {
        int humanScore = ScoreCard.getTotalScore("Human");
        int computerScore = ScoreCard.getTotalScore("Computer");

        String winnerMessage;
        if (humanScore > computerScore) {
            winnerMessage = "Computer goes first in the next round!";
        } else if (humanScore < computerScore) {
            winnerMessage = "Human goes first in the next round!";
        } else {
            winnerMessage = "It's a tie! Turn will be decided by a dice roll.";
        }

        // Show the Round Over dialog
        new AlertDialog.Builder(this)
                .setTitle("Round Over")
                .setMessage("Round " + Round.getRoundNumber() + " is over!\n" +
                        "Human Score: " + humanScore + "\n" +
                        "Computer Score: " + computerScore + "\n" +
                        winnerMessage)
                .setPositiveButton("OK", (dialog, which) -> {
                    // After acknowledging, prompt to save the game
                    promptForSaveGame(humanScore, computerScore, winnerMessage);
                })
                .show();
    }

    /**
     * Prompts the user to save the game, providing options to save or proceed without saving to the next round.
     *
     * @param humanScore The Human player's total score.
     * @param computerScore The Computer player's total score.
     * @param winnerMessage The message indicating the next round's first player.
     */
    private void promptForSaveGame(int humanScore, int computerScore, String winnerMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Save Game")
                .setMessage("Do you want to save the game before proceeding?")
                .setPositiveButton("Yes", (dialog, which) -> promptForFileNameAndSaveGame(humanScore, computerScore, winnerMessage))
                .setNegativeButton("No", (dialog, which) -> {
                    // If the user skips saving, proceed to the next round
                    proceedToNextRound(humanScore, computerScore);
                })
                .show();
    }

    /**
     * Opens a dialog to enter a file name and saves the game state if a valid name is provided.
     *
     * @param humanScore The Human player's total score.
     * @param computerScore The Computer player's total score.
     * @param winnerMessage The message indicating the next round's first player.
     */
    private void promptForFileNameAndSaveGame(int humanScore, int computerScore, String winnerMessage) {
        AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(this);
        saveDialogBuilder.setTitle("Enter File Name");

        // Add EditText for file name input
        final EditText input = new EditText(this);
        input.setHint("Enter file name");
        saveDialogBuilder.setView(input);

        saveDialogBuilder.setPositiveButton("Save", (dialog, which) -> {
            String fileName = input.getText().toString().trim();

            if (!fileName.isEmpty()) {
                Round.incrementRound();
                Serialization serialization = new Serialization();
                serialization.saveGame(this, fileName); // Save game

                // Show confirmation dialog for successful save
                new AlertDialog.Builder(this)
                        .setTitle("Success")
                        .setMessage("Game saved successfully!")
                        .setPositiveButton("OK", (innerDialog, innerWhich) -> {
                            // Proceed to the next round
                            resetGame();
                        })
                        .show();
            } else {
                // Show an alert dialog if the file name is empty
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("File name cannot be empty. Skipping save.")
                        .setPositiveButton("OK", (innerDialog, innerWhich) -> {
                            // Proceed to the next round without saving
                            proceedToNextRound(humanScore, computerScore);
                        })
                        .show();
            }
        });

        saveDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            // If the user cancels the save, proceed to the next round
            proceedToNextRound(humanScore, computerScore);
        });

        saveDialogBuilder.show();
    }

    /**
     * Proceeds to the next round, determining the first player based on scores and navigating to the appropriate activity.
     *
     * @param humanScore The Human player's total score.
     * @param computerScore The Computer player's total score.
     */
    private void proceedToNextRound(int humanScore, int computerScore) {
        Round.incrementRound(); // Increment the round number

        if(humanScore == computerScore) {
            Intent intent = new Intent(this, DecideTurnActivity.class);
            startActivity(intent);
        } else {
            Round newRound;
            if (humanScore > computerScore) {
                newRound = new Round(new Computer(), new Human());
            } else {
                newRound = new Round(new Human(), new Computer());
            }
            Intent intent = new Intent(this, RoundActivity.class);
            intent.putExtra("round", newRound);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    /**
     * Determines the winner message based on the final scores, declaring a winner or tie.
     *
     * @param humanScore The Human player's total score.
     * @param computerScore The Computer player's total score.
     * @return A message declaring the winner or a tie.
     */
    private String determineWinnerMessage(int humanScore, int computerScore) {
        if (humanScore > computerScore) {
            return "Human wins!";
        } else if (humanScore < computerScore) {
            return "Computer wins!";
        } else {
            return "It's a tie!";
        }
    }

    /**
     * Resets the game to its initial state.
     *
     * - Resets the round number to 1.
     * - Clears the ScoreCard to remove all scores.
     * - Navigates back to the MainActivity to start a new game.
     */
    private void resetGame() {
        Round.setRoundNumber(1);
        ScoreCard.clearScoreBoard();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}