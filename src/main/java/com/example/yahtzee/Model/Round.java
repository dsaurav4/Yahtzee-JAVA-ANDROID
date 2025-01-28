package com.example.yahtzee.Model;

import java.io.Serializable;
import java.util.List;

public class Round implements Serializable {

    // *******************************
    // Class Variables
    // *******************************

    // Stores the current round number, shared across all instances
    private static int roundNumber = 1;

    // *******************************
    // Instance Variables
    // *******************************

    // List of players participating in the round
    private final Player firstPlayer;
    private final Player secondPlayer;



    // *******************************
    // Constructor
    // *******************************

    /**
     * Initializes a new round with two players.
     *
     * @param firstPlayer  The first player in the round.
     * @param secondPlayer The second player in the round.
     */
    public Round(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    // *******************************
    // Selectors Methods
    // *******************************

    /**
     * Increments the current round number by one.
     */
    public static void incrementRound() {
        roundNumber++;
    }

    /**
     * Retrieves the current round number.
     *
     * @return The current round number.
     */
    public static int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Sets the round number to a specified value.
     *
     * @param newRoundNumber The new value for the round number.
     */
    public static void setRoundNumber(int newRoundNumber) {
        roundNumber = newRoundNumber;
    }

    /**
     * Retrieves the first player of the round.
     *
     * @return The first player.
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Retrieves the second player of the round.
     *
     * @return The second player.
     */
    public Player getSecondPlayer() {
        return secondPlayer;
    }
}
