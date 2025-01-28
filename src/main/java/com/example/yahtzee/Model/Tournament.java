package com.example.yahtzee.Model;

import java.util.ArrayList;
import java.util.List;

public class Tournament {

    // Class Variables
    private final List<Player> players;

    // Constructor
    public Tournament() {
        players = new ArrayList<>();
        players.add(new Human());
        players.add(new Computer());
    }

    /**
     * Starts the tournament by returning the list of players.
     *
     * @return A list of Player objects representing the participants.
     */

    public List<Player> startTournament() {
        return players;
    }

}
