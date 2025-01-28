package com.example.yahtzee.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dice {
    // List to store the results of the dice rolls
    private List<Integer> diceResult;

    // Random number generator
    private Random random;

    // Constructor
    public Dice() {
        // Initialize the random number generator and the results list
        random = new Random();
        diceResult = new ArrayList<>();
    }

    /**
     * Rolls a specified number of dice and returns the results as a list.
     * @param numDice The number of dice to roll.
     * @return A list of integers, representing the results of the dice rolls.
     */
    public List<Integer> rollDice(int numDice) {
        // Clear the list to store the results of the dice rolls
        diceResult.clear();

        // Roll the dice numDice times
        for (int i = 0; i < numDice; ++i) {
            // Add the result of the die roll to the list
            diceResult.add(random.nextInt(6) + 1);
        }

        // Return the list of dice rolls
        return diceResult;
    }

    /**
     * Rolls a single die and returns the result.
     * @return An integer representing the result of the die roll.
     */
    public int rollSingleDice() {
        // Return a random number between 1 and 6 for the die roll
        return random.nextInt(6) + 1;
    }
}
