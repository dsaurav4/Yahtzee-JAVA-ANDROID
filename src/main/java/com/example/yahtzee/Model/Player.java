package com.example.yahtzee.Model;

import java.io.Serializable;
import java.util.*;

public abstract class Player implements Serializable {
    // *******************************
    // Class Variables
    // *******************************
    protected String name;

    // *******************************
    // Selectors
    // *******************************

    /**
     * Retrieves the name of the player.
     *
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Calculates and retrieves the total score of the player.
     *
     * @return The total score of the player.
     */
    public int getTotalScore() {
        return ScoreCard.getTotalScore(this.name);
    }

    // *******************************
    // Utility Methods
    // *******************************

    /**
     * Simulates a dice roll to decide the player's turn order.
     *
     * @return The value of a single dice roll.
     */
    public int turnChooseRoll() {
        Dice dice = new Dice();
        return dice.rollSingleDice();
    }

    /**
     * Rolls the dice for the player.
     * Can roll the specified number of dice randomly or set the values manually.
     *
     * @param diceCount The number of dice to roll.
     * @return A list of integers representing the rolled dice values.
     */
    public List<Integer> roll(int diceCount) {
        Dice dice = new Dice();
        return dice.rollDice(diceCount);
    }
}



