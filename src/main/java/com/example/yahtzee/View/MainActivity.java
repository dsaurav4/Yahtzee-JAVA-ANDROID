package com.example.yahtzee.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yahtzee.Model.Computer;
import com.example.yahtzee.Model.Human;
import com.example.yahtzee.Model.Logger;
import com.example.yahtzee.Model.Round;
import com.example.yahtzee.Model.ScoreCard;
import com.example.yahtzee.Model.Serialization;
import com.example.yahtzee.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /**
     * Sets up the main activity layout, initializes UI components, and configures event handlers for game actions.
     *
     * @param savedInstanceState Bundle containing the saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button startGameButton = findViewById(R.id.start_game);
        Button loadGameButton = findViewById(R.id.load_game);

        // Set up OnClickListener for "Start Game" button
        startGameButton.setOnClickListener(view -> {
            Logger.log("Starting new game");
            // Start DecideTurnActivity
            Intent intent = new Intent(MainActivity.this, DecideTurnActivity.class);
            startActivity(intent);
        });

        loadGameButton.setOnClickListener(view -> showLoadGameDialog());
    }

    /**
     * Displays a dialog for the user to select a saved game file to load.
     */
    private void showLoadGameDialog() {
        File directory = getFilesDir(); // Internal storage directory for the app
        String[] fileList = directory.list((dir, name) -> name.endsWith(".txt")); // Filter for .txt files

        if (fileList == null || fileList.length == 0) {
            // Show a dialog if no files are available
            new AlertDialog.Builder(this)
                    .setTitle("No Saved Games")
                    .setMessage("No saved game files found.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            return;
        }

        // Create an AlertDialog to show the list of files
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a File to Load");
        Logger.log("Displaying list of saved game files.");

        builder.setItems(fileList, (dialog, which) -> {
            String selectedFile = fileList[which]; // Get the selected file name
            Serialization serialization = new Serialization();
            serialization.loadGame(this, selectedFile.replace(".txt", "")); // Load the game without .txt extension

            int humanScore = ScoreCard.getTotalScore("Human");
            int computerScore = ScoreCard.getTotalScore("Computer");

            if (humanScore == computerScore) {
                Intent intent = new Intent(this, DecideTurnActivity.class);
                startActivity(intent);
                Logger.log("Starting DecideTurnActivity because scores are equal.");
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
                Logger.log("Starting RoundActivity.");
                finish();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.show();
    }

}

