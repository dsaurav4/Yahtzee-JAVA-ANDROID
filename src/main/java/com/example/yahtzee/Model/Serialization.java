package com.example.yahtzee.Model;

import android.content.Context;

import java.io.*;
import java.util.*;

public class Serialization {

    /**
     * Save the current game state to internal storage in the specified file.
     *
     * @param context  The Android context for file operations.
     * @param fileName The name of the file to save the game data to (without extension).
     */
    public void saveGame(Context context, String fileName) {
        try (FileOutputStream fos = context.openFileOutput(fileName + ".txt", Context.MODE_PRIVATE)) {
            // Prepare game data for saving
            ScoreCard scoreCard = new ScoreCard();
            List<String> categoryList = scoreCard.getAllCategories();
            Map<String, Map.Entry<String, Map.Entry<Integer, Integer>>> scores = scoreCard.getScoreBoard();

            StringBuilder data = new StringBuilder();
            data.append("Round: ").append(Round.getRoundNumber()).append("\n\n");
            data.append("Scorecard:\n");

            for (int i = 0; i < 12; ++i) {
                String category = categoryList.get(i);
                Map.Entry<String, Map.Entry<Integer, Integer>> scoreData = scores.get(category);

                if (scoreData != null && scoreData.getKey() != null && !scoreData.getKey().isEmpty()) {
                    int points = scoreData.getValue().getKey();
                    String winner = scoreData.getKey();
                    int roundScored = scoreData.getValue().getValue();
                    data.append(points).append(" ").append(winner).append(" ").append(roundScored).append("\n");
                } else {
                    data.append("0\n");
                }
            }

            // Write data to the file
            fos.write(data.toString().getBytes());
            Logger.log("Game saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Failed to save game.");
        }
    }

    /**
     * Load the game state from internal storage from the specified file.
     *
     * @param context  The Android context for file operations.
     * @param fileName The name of the file to load the game data from (without extension).
     */
    public void loadGame(Context context, String fileName) {
        try (FileInputStream fis = context.openFileInput(fileName + ".txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            ScoreCard scoreCard = new ScoreCard();
            List<String> categoryList = scoreCard.getAllCategories();
            Map<String, Map.Entry<String, Map.Entry<Integer, Integer>>> scores = new HashMap<>();

            // Read the round number
            String line = reader.readLine();
            if (line != null && line.startsWith("Round:")) {
                String[] parts = line.split(" ");
                int roundNumber = Integer.parseInt(parts[1]);
                Round.setRoundNumber(roundNumber);
            } else {
                Logger.log("Invalid save file format: Missing 'Round:' header.");
                return;
            }

            reader.readLine(); // Skip the empty line
            line = reader.readLine();
            if (!"Scorecard:".equals(line)) {
                Logger.log("Invalid save file format: Missing 'Scorecard:' header.");
                return;
            }

            // Read the scores for each category
            for (int i = 0; i < 12; ++i) {
                line = reader.readLine();
                if ("0".equals(line)) {
                    scores.put(categoryList.get(i), Map.entry("", Map.entry(0, 0)));
                } else {
                    String[] scoreParts = line.split(" ");
                    int points = Integer.parseInt(scoreParts[0]);
                    String winner = scoreParts[1];
                    int roundScored = Integer.parseInt(scoreParts[2]);
                    scores.put(categoryList.get(i), Map.entry(winner, Map.entry(points, roundScored)));
                }
            }

            // Set the loaded scores into the ScoreCard
            scoreCard.setScoreBoard(scores);
            Logger.log("Game loaded successfully!");
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            Logger.log("Failed to load game.");
        }
    }
}
