package com.example.yahtzee.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Computer extends Player implements Serializable {

    /**
     * Constructs a `Computer` object representing the computer-controlled player.
     * Initializes the player's name to "Computer."
     */
    public Computer() {
        name = "Computer";
    }

    // *******************************
    // Utility Methods
    // *******************************

    /**
     * Evaluates the current dice values to decide which dice to keep and which to re-rollbased on the highest-scoring category available.
     *
     * @param currentDiceValues List of integers representing the dice values rolled in the first roll.
     * @return A Pair containing:
     *         - List of integers for dice to keep.
     *         - List of integers for dice to re-roll.
     */
    public Pair<List<Integer>, List<Integer>> firstRollMove(List<Integer> currentDiceValues) {
        List<Integer> diceToRollAgain = new ArrayList<>();
        List<Integer> diceToKeep = new ArrayList<>();

        Pair<String, Integer> bestHands = ScoreCard.getBestHands(currentDiceValues);
        if (bestHands == null) {
            throw new IllegalStateException("ScoreCard.getBestHands() returned null.");
        }
        String bestCategory = bestHands.getKey();
        int currentMaximumPoints = bestHands.getValue();

        Map<Integer, Integer> diceCount = new HashMap<>();
        for (int value : currentDiceValues) {
            diceCount.put(value, diceCount.getOrDefault(value, 0) + 1);
        }

        if (bestCategory != null && !bestCategory.isEmpty()) {
            ScoreCard.displayPossiblePoints(currentDiceValues);
            Logger.log("Computer's best category after this roll is: " + bestCategory + " with " + currentMaximumPoints + " points.");
        } else {
            Logger.log("Computer has no available categories to choose from!");
        }

        if (bestCategory.equals("Yahtzee")) {
            Logger.log("Computer chose to stand with Yahtzee! 50 points for computer!");
            diceToKeep = new ArrayList<>(currentDiceValues);
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Five Straight")) {
            Logger.log("Computer chose to stand with Five straight! 40 points for computer!");
            diceToKeep = new ArrayList<>(currentDiceValues);
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Four Straight")) {
            Set<Integer> temp = new HashSet<>();

            if (!ScoreCard.isCategoryAvailable("Five Straight")) {
                Logger.log("\nComputer will stand on this as Five Straight is not available!");
                diceToKeep = new ArrayList<>(currentDiceValues);
            } else {
                Logger.log("\nComputer wants to pursue Five Straight!");

                for (int value : currentDiceValues) {
                    temp.add(value);
                }

                if (temp.size() == 4) {
                    for (int dice : temp) {
                        if (Collections.frequency(currentDiceValues, dice) == 2) {
                            diceToRollAgain.add(dice);
                        }
                        diceToKeep.add(dice);
                    }
                } else {
                    List<Integer> sortedDice = new ArrayList<>(currentDiceValues);
                    Collections.sort(sortedDice);

                    if (sortedDice.get(0) == 1 && sortedDice.get(1) == 2) {
                        diceToRollAgain.add(sortedDice.get(4));
                        diceToKeep.addAll(sortedDice.subList(0, 4));
                    } else {
                        diceToRollAgain.add(sortedDice.get(0));
                        diceToKeep.addAll(sortedDice.subList(1, 5));
                    }
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Full House")) {
            Logger.log("Computer chose to stand with Full House! 25 points for computer!");
            diceToKeep = new ArrayList<>(currentDiceValues);
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Four of a Kind")) {
            int nonFourKindValue = -1;
            int fourKindValue = -1;
            for (Map.Entry<Integer, Integer> entry : diceCount.entrySet()) {
                if (entry.getValue() == 1) {
                    nonFourKindValue = entry.getKey();
                } else {
                    fourKindValue = entry.getKey();
                }
            }

            if (nonFourKindValue != -1) {
                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                    Logger.log("\nComputer is attempting to go for Yahtzee by rerolling the remaining die!\n");
                    for (int die : currentDiceValues) {
                        if (die == fourKindValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                } else if (nonFourKindValue != 6) {
                    Logger.log("Computer chose to reroll the non-four of a kind value to try to get a higher die: " + nonFourKindValue);
                    diceToRollAgain.add(nonFourKindValue);
                    for (int die : currentDiceValues) {
                        if (die == fourKindValue) {
                            diceToKeep.add(die);
                        }
                    }
                } else {
                    Logger.log("Computer chose to stand on this as the non-four of a kind value is 6 and Yahtzee is not available!");
                    diceToKeep = new ArrayList<>(currentDiceValues);
                }
            } else {
                if (currentDiceValues.get(0) < 4) {
                    Logger.log("\nComputer chose to reroll to try to get a higher die to add to the sum!");
                    diceToKeep.addAll(currentDiceValues.subList(0, 4));
                    diceToRollAgain.add(currentDiceValues.get(4));
                } else {
                    Logger.log("\nComputer chose to stand with Four of a Kind!");
                    diceToKeep = new ArrayList<>(currentDiceValues);
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Three of a Kind")) {
            int threeKindValue = -1;
            for (Map.Entry<Integer, Integer> entry : diceCount.entrySet()) {
                if (entry.getValue() >= 3) {
                    threeKindValue = entry.getKey();
                }
            }

            int fullHousePoints = ScoreCard.calculatePoints("Full House", currentDiceValues);

            if (currentMaximumPoints > fullHousePoints && fullHousePoints == 25) {
                Logger.log("Computer chose to stand with Three of a Kind as it is greater than 25.");
                diceToKeep = new ArrayList<>(currentDiceValues);
                return new Pair<>(diceToKeep, diceToRollAgain);
            }

            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Full House")) {
                if (ScoreCard.isCategoryAvailable("Yahtzee") && ScoreCard.isCategoryAvailable("Full House")) {
                    if (diceCount.get(threeKindValue) == 3) {
                        Logger.log("\nComputer is attempting to go for Yahtzee or Full House!\n");
                    } else {
                        Logger.log("\nComputer is attempting to go for Yahtzee!\n");
                    }

                    for (int die : currentDiceValues) {
                        if (die == threeKindValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                } else if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                    Logger.log("\nComputer is attempting to go for Yahtzee!\n");
                    for (int die : currentDiceValues) {
                        if (die == threeKindValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                } else {
                    Logger.log("\nComputer wants to pursue Full House!");
                    int max = Collections.max(currentDiceValues);

                    if (max == threeKindValue) {
                        for (int die : currentDiceValues) {
                            if (die == threeKindValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    } else {
                        int count = 0;
                        for (int die : currentDiceValues) {
                            if (die == threeKindValue && count < 3) {
                                diceToKeep.add(die);
                                count++;
                            } else if (die == max) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                }
            } else {
                for (int value : currentDiceValues) {
                    if (diceCount.get(value) <= 2) {
                        if (value < 6) {
                            diceToRollAgain.add(value);
                        } else {
                            diceToKeep.add(value);
                        }
                    } else {
                        diceToKeep.add(value);
                    }
                }

                if (diceToRollAgain.size() > 0) {
                    Logger.log("Computer decided to roll again to try and get higher die value!");
                } else {
                    Logger.log("Computer decided to stand with Three of a Kind!");
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Aces") || bestCategory.equals("Twos") || bestCategory.equals("Threes") || bestCategory.equals("Fours") || bestCategory.equals("Fives") || bestCategory.equals("Sixes")) {
            int targetValue = -1;

            switch (bestCategory) {
                case "Aces":
                    targetValue = 1;
                    break;
                case "Twos":
                    targetValue = 2;
                    break;
                case "Threes":
                    targetValue = 3;
                    break;
                case "Fours":
                    targetValue = 4;
                    break;
                case "Fives":
                    targetValue = 5;
                    break;
                case "Sixes":
                    targetValue = 6;
                    break;
            }

            List<Integer> pairs = new ArrayList<>();
            List<Integer> nonPairs = new ArrayList<>();
            boolean isStraightPossible = false;

            for (Map.Entry<Integer, Integer> pair : diceCount.entrySet()) {
                if (pair.getValue() >= 2) {
                    pairs.add(pair.getKey());
                } else if (pair.getValue() == 1) {
                    nonPairs.add(pair.getKey());
                }
            }

            if (pairs.size() == 1) {
                Set<Integer> temp = new HashSet<>(currentDiceValues);

                List<Integer> straight = Arrays.asList(1, 2, 3, 4, 5);
                List<Integer> straight2 = Arrays.asList(2, 3, 4, 5, 6);

                int count = 0;
                for (int die : temp) {
                    if (straight.contains(die)) {
                        count++;
                    }
                }

                if (count == 4) {
                    isStraightPossible = true;
                } else {
                    count = 0;

                    for (int die : temp) {
                        if (straight2.contains(die)) {
                            count++;
                        }
                    }

                    if (count == 4) {
                        isStraightPossible = true;
                    }
                }
            }

            if (pairs.size() == 1) {

                Set<Integer> temp = new HashSet<>(currentDiceValues);
                List<Integer> uniqueDice = new ArrayList<>(temp);
                Collections.sort(uniqueDice);
                if (ScoreCard.isCategoryAvailable("Five Straight") && isStraightPossible) {
                    Logger.log("Computer is re-rolling to pursue Five Straight!");
                    int count = 0;
                    for (int die : currentDiceValues) {
                        if (die == pairs.get(0) && count < 1) {
                            diceToRollAgain.add(die);
                            count++;
                        } else {
                            diceToKeep.add(die);
                        }
                    }
                }
                else if (ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.getSequentialDice(uniqueDice).size() == 3) {
                    diceToKeep = ScoreCard.getSequentialDice(uniqueDice);
                    diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    Logger.log("Computer is pursuing Four Straight!");
                }
                else {
                    if (nonPairs.size() == 3) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                category.append("Three of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                category.append("Full House | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                            for (int die : currentDiceValues) {
                                if (die == targetValue) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }

                    }
                    else if (nonPairs.size() == 2) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                category.append("Full House | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                            for (int die : currentDiceValues) {
                                if (die == targetValue) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                    }
                    else if (nonPairs.size() == 1) {

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else if (ScoreCard.isCategoryAvailable("Full House")) {
                            Logger.log("Computer is pursuing Full House!");
                            int count = 0;
                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0) && count < 3) {
                                    diceToKeep.add(die);
                                    count++;
                                } else if (die == nonPairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                            for (int die : currentDiceValues) {
                                if (die == targetValue) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }

                    }
                    else {
                        Logger.log("Computer chose to keep all dice as they are all the same value.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House")) {
                        Logger.log("Computer is pursuing Full House!");
                        for (int die : currentDiceValues) {
                            if (die == pairs.get(0) || die == pairs.get(1)) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else if((ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")) &&
                            ScoreCard.getSequentialDice(currentDiceValues).size() == 3){
                        Logger.log("Computer is pursuing Straight!");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind"))){
                        StringBuilder category = new StringBuilder();

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            category.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            category.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            category.append("Three of a Kind | ");
                        }

                        Logger.log("Computer is pursuing " + category);

                        if(pairs.get(0) == targetValue){
                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(pairs.get(1) == targetValue){
                            for(int die : currentDiceValues){
                                if(die == pairs.get(1)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            int maxPair = Math.max(pairs.get(0), pairs.get(1));
                            for(int die : currentDiceValues){
                                if(die == maxPair){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                    } else {
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                        for(int die : currentDiceValues){
                            if(die == targetValue){
                                diceToKeep.add(die)   ;
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind")){
                        if(Collections.frequency(currentDiceValues, pairs.get(0)) == 3){
                            StringBuilder category = new StringBuilder();

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }

                            Logger.log("Computer is pursuing " + category);

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                            for(int die : currentDiceValues) {
                                if (die == targetValue) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }

                    }
                    else {
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                        for(int die : currentDiceValues) {
                            if (die == targetValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                }
            }
            else {
                if(ScoreCard.isCategoryAvailable("Four Straight") || ScoreCard.isCategoryAvailable("Five Straight")){
                    Logger.log("Computer is pursuing Straight!");

                    diceToRollAgain.add(1);
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                } else {
                    Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                    for(int die : currentDiceValues) {
                        if (die == targetValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                }
            }

            return new Pair<>(diceToKeep, diceToRollAgain);
        }
        else{
            List<Integer> pairs = new ArrayList<>();
            List<Integer> nonPairs = new ArrayList<>();
            boolean isStraightPossible = false;

            for (Map.Entry<Integer, Integer> pair : diceCount.entrySet()) {
                if (pair.getValue() >= 2) {
                    pairs.add(pair.getKey());
                } else if (pair.getValue() == 1) {
                    nonPairs.add(pair.getKey());
                }
            }

            if (pairs.size() == 1) {
                Set<Integer> temp = new HashSet<>(currentDiceValues);

                List<Integer> straight = Arrays.asList(1, 2, 3, 4, 5);
                List<Integer> straight2 = Arrays.asList(2, 3, 4, 5, 6);

                int count = 0;
                for (int die : temp) {
                    if (straight.contains(die)) {
                        count++;
                    }
                }

                if (count == 4) {
                    isStraightPossible = true;
                } else {
                    count = 0;

                    for (int die : temp) {
                        if (straight2.contains(die)) {
                            count++;
                        }
                    }

                    if (count == 4) {
                        isStraightPossible = true;
                    }
                }
            }

            if (pairs.size() == 1) {

                Set<Integer> temp = new HashSet<>(currentDiceValues);
                List<Integer> uniqueDice = new ArrayList<>(temp);
                Collections.sort(uniqueDice);
                if (ScoreCard.isCategoryAvailable("Five Straight") && isStraightPossible) {
                    Logger.log("Computer is re-rolling to pursue Five Straight!");
                    int count = 0;
                    for (int die : currentDiceValues) {
                        if (die == pairs.get(0) && count < 1) {
                            diceToRollAgain.add(die);
                            count++;
                        } else {
                            diceToKeep.add(die);
                        }
                    }
                }
                else if (ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.getSequentialDice(uniqueDice).size() == 3) {
                    diceToKeep = ScoreCard.getSequentialDice(uniqueDice);
                    diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    Logger.log("Computer is pursuing Four Straight!");
                }
                else {
                    if (nonPairs.size() == 3) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                category.append("Three of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                category.append("Full House | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            Logger.log("Computer is rolling all dice to try to get a better hand!");
                            diceToRollAgain = currentDiceValues;
                        }

                    }
                    else if (nonPairs.size() == 2) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                category.append("Full House | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            Logger.log("Computer is rolling all dice to try to get a better hand!");
                            diceToRollAgain = currentDiceValues;
                        }
                    }
                    else if (nonPairs.size() == 1) {

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else if (ScoreCard.isCategoryAvailable("Full House")) {
                            Logger.log("Computer is pursuing Full House!");
                            int count = 0;
                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0) && count < 3) {
                                    diceToKeep.add(die);
                                    count++;
                                } else if (die == nonPairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            Logger.log("Computer is rolling all dice to try to get a better hand!");
                            diceToRollAgain = currentDiceValues;
                        }

                    }
                    else {
                        Logger.log("Computer chose to keep all dice as they are all the same value.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House")) {
                        Logger.log("Computer is pursuing Full House!");
                        for (int die : currentDiceValues) {
                            if (die == pairs.get(0) || die == pairs.get(1)) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else if((ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")) &&
                            ScoreCard.getSequentialDice(currentDiceValues).size() == 3){
                        Logger.log("Computer is pursuing Straight!");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind"))){
                        StringBuilder category = new StringBuilder();

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            category.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            category.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            category.append("Three of a Kind | ");
                        }

                        Logger.log("Computer is pursuing " + category);


                        int maxPair = Math.max(pairs.get(0), pairs.get(1));
                        for(int die : currentDiceValues){
                            if(die == maxPair){
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                    } else {
                        Logger.log("Computer is rolling all dice to try to get a better hand!");
                        diceToRollAgain = currentDiceValues;
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind")){
                        if(Collections.frequency(currentDiceValues, pairs.get(0)) == 3){
                            StringBuilder category = new StringBuilder();

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }

                            Logger.log("Computer is pursuing " + category);

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            Logger.log("Computer is rolling all dice to try to get a better hand!");
                            diceToRollAgain = currentDiceValues;
                        }

                    }
                    else {
                        Logger.log("Computer is rolling all dice to try to get a better hand!");
                        diceToRollAgain = currentDiceValues;
                    }
                }
            }
            else {
                if(ScoreCard.isCategoryAvailable("Four Straight") || ScoreCard.isCategoryAvailable("Five Straight")){
                    Logger.log("Computer is pursuing Straight!");

                    diceToRollAgain.add(1);
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                } else {
                    Logger.log("Computer is rolling all dice to try to get a better hand!");
                    diceToRollAgain = currentDiceValues;
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        }
    }

    /**
     * Combines the dice kept after the first roll with the newly rolled dice to decide which dice to keep and which to re-roll.
     * @param rolledDice List of integers representing the dice values from the second roll.
     * @param keptDice   List of integers representing the dice values kept after the first roll.
     * @return A Pair containing:
     *         - List of integers for dice to keep.
     *         - List of integers for dice to re-roll.
     */
    public Pair<List<Integer>, List<Integer>> secondRollMove(List<Integer> rolledDice, List<Integer> keptDice) {
        List<Integer> currentDiceValues = new ArrayList<>(keptDice);
        currentDiceValues.addAll(rolledDice);

        List<Integer> diceToRollAgain = new ArrayList<>();
        List<Integer> diceToKeep = new ArrayList<>();

        Pair<String, Integer> bestHands = ScoreCard.getBestHands(currentDiceValues);
        if (bestHands == null) {
            throw new IllegalStateException("ScoreCard.getBestHands() returned null.");
        }
        String bestCategory = bestHands.getKey();
        int currentMaximumPoints = bestHands.getValue();

        Map<Integer, Integer> diceCount = new HashMap<>();
        for (int die : currentDiceValues) {
            diceCount.put(die, diceCount.getOrDefault(die, 0) + 1);
        }

        if (bestCategory != null && !bestCategory.isEmpty()) {
            ScoreCard.displayPossiblePoints(currentDiceValues);
            Logger.log("Computer's best category after this roll is: " + bestCategory + " with " + currentMaximumPoints + " points.");
        } else {
            Logger.log("Computer has no available categories to choose from!");
        }

        if (bestCategory.equals("Yahtzee")) {
            Logger.log("Computer chose to stand with Yahtzee! 50 points for computer!");
            diceToKeep = new ArrayList<>(currentDiceValues);
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Five Straight")) {
            Logger.log("Computer chose to stand with Five Straight! 40 points for computer!");
            diceToKeep = new ArrayList<>(currentDiceValues);
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Four Straight")) {
            Set<Integer> temp = new HashSet<>();

            if (!ScoreCard.isCategoryAvailable("Five Straight")) {
                Logger.log("\nComputer will stand on this as Five Straight is not available!");
                diceToKeep = new ArrayList<>(currentDiceValues);
            } else {
                Logger.log("\nComputer wants to pursue Five Straight!");

                for (int value : currentDiceValues) {
                    temp.add(value);
                }

                if (temp.size() == 4) {
                    int nonStraightValue = -1;
                    for (int dice : currentDiceValues) {
                        if (Collections.frequency(currentDiceValues, dice) == 2) {
                            nonStraightValue = dice;
                        }
                    }

                    if (nonStraightValue != -1 && rolledDice.contains(nonStraightValue)) {
                        Logger.log("Computer is re-rolling: " + nonStraightValue);
                        diceToRollAgain.add(nonStraightValue);
                        for (int dice : temp) {
                            diceToKeep.add(dice);
                        }
                    } else {
                        Logger.log("\nComputer is standing on Four Straight as it cannot reroll " + nonStraightValue + "!");
                        for (int dice : currentDiceValues) {
                            diceToKeep.add(dice);
                        }
                    }
                } else {
                    List<Integer> sortedDice = new ArrayList<>(currentDiceValues);
                    Collections.sort(sortedDice);

                    if (sortedDice.get(0) == 1 && sortedDice.get(1) == 2) {
                        diceToRollAgain.add(sortedDice.get(4));
                        for (int i = 0; i < 4; i++) {
                            diceToKeep.add(sortedDice.get(i));
                        }
                    } else {
                        diceToRollAgain.add(sortedDice.get(0));
                        for (int i = 1; i < 5; i++) {
                            diceToKeep.add(sortedDice.get(i));
                        }
                    }
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Full House")) {
            Logger.log("Computer chose to stand with Full House! 25 points for computer!");
            diceToKeep = new ArrayList<>(currentDiceValues);
            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Four of a Kind")) {
            int nonFourKindValue = -1;
            int fourKindValue = -1;
            for (Map.Entry<Integer, Integer> entry : diceCount.entrySet()) {
                if (entry.getValue() == 1) {
                    nonFourKindValue = entry.getKey();
                } else {
                    fourKindValue = entry.getKey();
                }
            }

            if(nonFourKindValue != -1 && rolledDice.contains(nonFourKindValue)){
                if(ScoreCard.isCategoryAvailable("Yahtzee")){
                    Logger.log("Computer is pursuing Yahtzee!");
                    diceToRollAgain.add(nonFourKindValue);
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                }
                else {
                    if(nonFourKindValue < 4){
                        diceToRollAgain.add(nonFourKindValue);
                    }
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                    if(!diceToRollAgain.isEmpty()){
                        Logger.log("Computer is re-rolling to pursue Four of a Kind with higher die values!");
                    } else {
                        Logger.log("Computer is standing with Four of a Kind!");
                    }
                }
            }
            else if(nonFourKindValue == -1){
                if(fourKindValue < 4){
                    diceToRollAgain.add(fourKindValue);
                }
                diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                if(!diceToRollAgain.isEmpty()){
                    Logger.log("Computer is re-rolling to pursue Four of a Kind with higher die values!");
                } else {
                    Logger.log("Computer is standing with Four of a Kind!");
                }
            }
            else {
                diceToKeep = new ArrayList<>(currentDiceValues);
                Logger.log("Computer is standing with Four of a Kind!");
            }

            return new Pair<>(diceToKeep, diceToRollAgain);
        } else if (bestCategory.equals("Three of a Kind")) {
            int threeKindValue = -1;
            for (Map.Entry<Integer, Integer> entry : diceCount.entrySet()) {
                if (entry.getValue() >= 3) {
                    threeKindValue = entry.getKey();
                }
            }
            
            List<Integer> nonMatching = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : diceCount.entrySet()) {
                if (entry.getValue() == 1) {
                    nonMatching.add(entry.getKey());
                }
            }
            

            int fullHousePoints = ScoreCard.calculatePoints("Full House", currentDiceValues);

            if (currentMaximumPoints > fullHousePoints && fullHousePoints == 25) {
                Logger.log("Computer chose to stand with Three of a Kind as it is greater than Full House points!");
                diceToKeep = new ArrayList<>(currentDiceValues);
                return new Pair<>(diceToKeep, diceToRollAgain);
            }

            if(nonMatching.size() == 2){
                if(rolledDice.contains(nonMatching.get(0)) && rolledDice.contains(nonMatching.get(1))){
                    if(ScoreCard.isCategoryAvailable("Yahtzee")){
                        Logger.log("Computer is pursuing Yahtzee!");
                        for(int die : currentDiceValues){
                            if(die == threeKindValue){
                                diceToKeep.add(die);
                            }else{
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else if(ScoreCard.isCategoryAvailable("Full House")){
                        Logger.log("Computer is pursuing Full House!");
                        int maxNonThreeKindValue = Math.max(nonMatching.get(0), nonMatching.get(1));

                        for (int die : currentDiceValues) {
                            if(die == threeKindValue){
                                diceToKeep.add(die);
                            }else if(die == maxNonThreeKindValue){
                                diceToKeep.add(die);
                            }else{
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else {
                        for(int die : nonMatching){
                            if(die < 4){
                                diceToRollAgain.add(die);
                            }else{
                                diceToKeep.add(die);
                            }
                        }

                        for(int die : currentDiceValues){
                            if(die == threeKindValue){
                                diceToKeep.add(die);
                            }
                        }

                        if(!diceToRollAgain.isEmpty()){
                            Logger.log("Computer is re-rolling to pursue Three of a Kind with higher die values!");
                        } else {
                            Logger.log("Computer is standing with Three of a Kind!");
                        }
                    }
                }
                else if(rolledDice.contains(nonMatching.get(0)) || rolledDice.contains(nonMatching.get(1))){
                    if(ScoreCard.isCategoryAvailable("Full House")){
                        Logger.log("Computer is pursuing Full House!");
                        for(int die : nonMatching){
                            if(rolledDice.contains(die)){
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                    }
                    else{
                        for(int die : nonMatching){
                            if(rolledDice.contains(die) && die < 4){
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        if(!diceToRollAgain.isEmpty()){
                            Logger.log("Computer is re-rolling to pursue Three of a Kind with higher die values!");
                        } else {
                            Logger.log("Computer is standing with Three of a Kind!");
                        }
                    }
                }
                else{
                    diceToKeep = currentDiceValues;
                    Logger.log("Computer is standing with Three of a Kind as it cannot reroll the non-three of a kind values!");
                }
            }
            else if(nonMatching.size() == 1){
                if(rolledDice.contains(nonMatching.get(0))){
                    if(ScoreCard.isCategoryAvailable("Yahtzee")){
                        Logger.log("Computer is pursuing Yahtzee!");
                        diceToRollAgain = nonMatching;
                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                    }
                    else if(ScoreCard.isCategoryAvailable("Full House")){
                        if(rolledDice.contains(threeKindValue)){
                            Logger.log("Computer is pursuing Full House!");
                            diceToRollAgain.add(threeKindValue);
                            diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                        }
                    } else {
                        if(nonMatching.get(0) < 4){
                            diceToRollAgain.add(nonMatching.get(0));
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        if(!diceToRollAgain.isEmpty()){
                            Logger.log("Computer is re-rolling to pursue Three of a Kind with higher die values!");
                        }
                        else {
                            Logger.log("Computer is standing with Three of a Kind!");
                        }
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Full House")){
                        diceToRollAgain.add(threeKindValue);
                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                        Logger.log("Computer is pursuing Full House!");
                    } else {
                        if(threeKindValue < 4){
                            diceToRollAgain.add(threeKindValue);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        if(!diceToRollAgain.isEmpty()){
                            Logger.log("Computer is re-rolling to pursue Three of a Kind with higher die values!");
                        } else {
                            Logger.log("Computer is standing with Three of a Kind!");
                        }
                    }
                }
            }
            else {
                Set<Integer> temp = new HashSet<>(currentDiceValues);
                List<Integer> uniqueDice = new ArrayList<>(temp);

                if(uniqueDice.size() == 1){
                    if(rolledDice.size() >= 2 && ScoreCard.isCategoryAvailable("Full House")){
                        for(int i = 0; i < 2; i++){
                            diceToRollAgain.add(threeKindValue);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                        Logger.log("Computer is pursuing Full House!");
                    }
                    else {
                        Logger.log("Computer is standing with Three of a Kind!");
                        diceToKeep = currentDiceValues;
                    }
                }
                else {
                    List<Integer> nonThreeKind = new ArrayList<>();
                    for(int die : currentDiceValues){
                        if(die != threeKindValue){
                            nonThreeKind.add(die);
                        }
                    }

                    if(Collections.frequency(rolledDice, nonThreeKind.get(0)) == 2){
                        if(ScoreCard.isCategoryAvailable("Yahtzee")){
                            Logger.log("Computer is pursuing Yahtzee!");
                            diceToRollAgain = nonThreeKind;
                            diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                        }
                        else {
                            for(int die : nonThreeKind){
                                if(die < 4)
                                    diceToRollAgain.add(die);
                            }
                            diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                            if(!diceToRollAgain.isEmpty()){
                                Logger.log("Computer is re-rolling to pursue Three of a Kind with higher die values!");
                            }
                            else {
                                Logger.log("Computer is standing with Three of a Kind!");
                            }
                        }
                    } else {
                        diceToKeep = currentDiceValues;
                        Logger.log("Computer is standing with Three of a Kind!");
                    }
                }
            }


            return new Pair<>(diceToKeep, diceToRollAgain);
        }
        else if (bestCategory.equals("Aces") || bestCategory.equals("Twos") || bestCategory.equals("Threes") || bestCategory.equals("Fours") || bestCategory.equals("Fives") || bestCategory.equals("Sixes")) {
            int targetValue = -1;

            switch (bestCategory) {
                case "Aces":
                    targetValue = 1;
                    break;
                case "Twos":
                    targetValue = 2;
                    break;
                case "Threes":
                    targetValue = 3;
                    break;
                case "Fours":
                    targetValue = 4;
                    break;
                case "Fives":
                    targetValue = 5;
                    break;
                case "Sixes":
                    targetValue = 6;
                    break;
            }

            List<Integer> pairs = new ArrayList<>();
            List<Integer> nonPairs = new ArrayList<>();
            boolean isStraightPossible = false;

            for (Map.Entry<Integer, Integer> pair : diceCount.entrySet()) {
                if (pair.getValue() >= 2) {
                    pairs.add(pair.getKey());
                } else if (pair.getValue() == 1) {
                    nonPairs.add(pair.getKey());
                }
            }

            if (pairs.size() == 1) {
                Set<Integer> temp = new HashSet<>(currentDiceValues);

                List<Integer> straight = Arrays.asList(1, 2, 3, 4, 5);
                List<Integer> straight2 = Arrays.asList(2, 3, 4, 5, 6);

                int count = 0;
                for (int die : temp) {
                    if (straight.contains(die)) {
                        count++;
                    }
                }

                if (count == 4) {
                    isStraightPossible = true;
                } else {
                    count = 0;

                    for (int die : temp) {
                        if (straight2.contains(die)) {
                            count++;
                        }
                    }

                    if (count == 4) {
                        isStraightPossible = true;
                    }
                }
            }

            if (pairs.size() == 1) {
                Set<Integer> temp = new HashSet<>(currentDiceValues);
                List<Integer> uniqueDice = new ArrayList<>(temp);
                Collections.sort(uniqueDice);
                if (ScoreCard.isCategoryAvailable("Five Straight") && isStraightPossible && rolledDice.contains(pairs.get(0))) {
                    Logger.log("Computer is re-rolling to pursue Five Straight!");
                    int count = 0;
                    for (int die : currentDiceValues) {
                        if (die == pairs.get(0) && count < 1) {
                            diceToRollAgain.add(die);
                            count++;
                        } else {
                            diceToKeep.add(die);
                        }
                    }
                }
                else if(ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.getSequentialDice(uniqueDice).size() == 3 && rolledDice.contains(ScoreCard.subtractList(currentDiceValues, ScoreCard.getSequentialDice(uniqueDice)).get(0)) && rolledDice.contains(ScoreCard.subtractList(currentDiceValues, ScoreCard.getSequentialDice(uniqueDice)).get(1))){
                    diceToKeep = ScoreCard.getSequentialDice(uniqueDice);
                    diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    Logger.log("Computer is pursuing Four Straight!");
                } else {
                    if(nonPairs.size() == 3){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                StringBuilder category = new StringBuilder();
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    category.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    category.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                    category.append("Three of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    category.append("Full House | ");
                                }
                                Logger.log("Computer is pursuing " + category);

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                for(int die : rolledDice){
                                    if(die == targetValue){
                                        diceToKeep.add(die)   ;
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }

                                diceToKeep.addAll(keptDice);
                            }
                        }
                        else {
                            int count = 0;

                            for(int die : nonPairs){
                                if(rolledDice.contains(die))
                                    count++;
                            }

                            if(count == 2){
                                if (ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                    StringBuilder category = new StringBuilder();
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        category.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                        category.append("Three of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        category.append("Full House | ");
                                    }
                                    Logger.log("Computer is pursuing " + category);

                                    for (int die : currentDiceValues) {
                                        if (die == pairs.get(0)) {
                                            diceToKeep.add(die);
                                        } else if(nonPairs.contains(die) && rolledDice.contains(die)){
                                            diceToRollAgain.add(die);
                                        } else {
                                            diceToKeep.add(die);
                                        }
                                    }
                                } else {
                                    Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                    for(int die : rolledDice){
                                        if(die == targetValue){
                                            diceToKeep.add(die)   ;
                                        } else {
                                            diceToRollAgain.add(die);
                                        }
                                    }

                                    diceToKeep.addAll(keptDice);
                                }
                            }
                            else {
                                Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                for(int die : rolledDice){
                                    if(die == targetValue){
                                        diceToKeep.add(die)   ;
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }

                                diceToKeep.addAll(keptDice);
                            }
                        }
                    }
                    else if(nonPairs.size() == 2){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                StringBuilder category = new StringBuilder();
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    category.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    category.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    category.append("Full House | ");
                                }
                                Logger.log("Computer is pursuing " + category);

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                for(int die : rolledDice){
                                    if(die == targetValue){
                                        diceToKeep.add(die)   ;
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }

                                diceToKeep.addAll(keptDice);
                            }
                        }
                        else {
                            int count = 0;

                            for(int die : nonPairs){
                                if(rolledDice.contains(die))
                                    count++;
                            }

                            if(count == 1){
                                if (ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                    StringBuilder category = new StringBuilder();
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        category.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        category.append("Full House | ");
                                    }
                                    Logger.log("Computer is pursuing " + category);

                                    for (int die : currentDiceValues) {
                                        if (die == pairs.get(0)) {
                                            diceToKeep.add(die);
                                        } else if(nonPairs.contains(die) && rolledDice.contains(die)){
                                            diceToRollAgain.add(die);
                                        } else {
                                            diceToKeep.add(die);
                                        }
                                    }
                                }
                                else {
                                    Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                    for(int die : rolledDice){
                                        if(die == targetValue){
                                            diceToKeep.add(die)   ;
                                        } else {
                                            diceToRollAgain.add(die);
                                        }
                                    }

                                    diceToKeep.addAll(keptDice);
                                }
                            }
                            else {
                                Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                for(int die : rolledDice){
                                    if(die == targetValue){
                                        diceToKeep.add(die)   ;
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }

                                diceToKeep.addAll(keptDice);
                            }
                        }
                    }
                    else if(nonPairs.size() == 1) {
                        if(rolledDice.contains(nonPairs.get(0))){
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                StringBuilder category = new StringBuilder();
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    category.append("Yahtzee | ");
                                }
                                Logger.log("Computer is pursuing " + category);

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                Logger.log("Computer is pursuing Full House!");
                                int count = 0;
                                for(int die : currentDiceValues){
                                    if(die == pairs.get(0) && count < 3){
                                        diceToKeep.add(die);
                                        count++;
                                    } else if(die == nonPairs.get(0)){
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                for(int die : rolledDice){
                                    if(die == targetValue){
                                        diceToKeep.add(die)   ;
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }

                                diceToKeep.addAll(keptDice);
                            }
                        } else {
                            if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                Logger.log("Computer is pursuing Full House!");
                                int count = 0;
                                for(int die : currentDiceValues){
                                    if(die == pairs.get(0) && count < 3){
                                        diceToKeep.add(die);
                                        count++;
                                    } else if(die == nonPairs.get(0)){
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                                for(int die : rolledDice){
                                    if(die == targetValue){
                                        diceToKeep.add(die)   ;
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }

                                diceToKeep.addAll(keptDice);
                            }
                        }
                    } else {
                        Logger.log("Computer chose to keep all dice as they are all the same value.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House") && rolledDice.contains(nonPairs.get(0))) {
                        Logger.log("Computer is pursuing Full House!");
                        for (int die : currentDiceValues) {
                            if (die == pairs.get(0) || die == pairs.get(1)) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else if((ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")) &&
                            ScoreCard.getSequentialDice(currentDiceValues).size() == 3 &&
                            ScoreCard.isSubset(ScoreCard.subtractList(currentDiceValues, ScoreCard.getSequentialDice(currentDiceValues)),rolledDice)){
                        Logger.log("Computer is pursuing Straight!");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind")) &&
                              (Collections.frequency(rolledDice, pairs.get(0)) == 2 || Collections.frequency(rolledDice, pairs.get(1)) == 2) &&
                                rolledDice.contains(nonPairs.get(0))){
                        StringBuilder category = new StringBuilder();

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            category.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            category.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            category.append("Three of a Kind | ");
                        }

                        Logger.log("Computer is pursuing " + category);

                        if(pairs.get(0) == targetValue && Collections.frequency(rolledDice, pairs.get(1)) == 2){
                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(pairs.get(1) == targetValue && Collections.frequency(rolledDice, pairs.get(0)) == 2){
                            for(int die : currentDiceValues){
                                if(die == pairs.get(1)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(rolledDice, pairs.get(1)) == 2) {
                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                    } else {
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");
                        for(int die : rolledDice){
                            if(die == targetValue){
                                diceToKeep.add(die)   ;
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep.addAll(keptDice);
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind")){
                        if(Collections.frequency(currentDiceValues, pairs.get(0)) == 3 && Collections.frequency(rolledDice, pairs.get(1)) == 2){
                            StringBuilder category = new StringBuilder();

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }

                            Logger.log("Computer is pursuing " + category);

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3 && Collections.frequency(rolledDice, pairs.get(0)) == 2) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(0)) == 2 && Collections.frequency(rolledDice, pairs.get(1)) == 3){
                            StringBuilder category = new StringBuilder();

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }

                            Logger.log("Computer is pursuing " + category);

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 2 && Collections.frequency(rolledDice, pairs.get(0)) == 3) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                            for(int die : rolledDice) {
                                if (die == targetValue) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }

                            diceToKeep.addAll(keptDice);
                        }

                    }
                    else {
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                        for(int die : rolledDice) {
                            if (die == targetValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep.addAll(keptDice);
                    }
                }
            }
            else {
                if((ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.isCategoryAvailable("Five Straight")) ||
                        (!ScoreCard.isCategoryAvailable("Five Straight") && ScoreCard.isCategoryAvailable("Four Straight"))){
                    if(rolledDice.contains(1) || rolledDice.contains(6)){
                        if(rolledDice.contains(1)){
                            diceToRollAgain.add(1);
                        } else {
                            diceToRollAgain.add(6);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        Logger.log("Computer is pursuing Straight!");
                    } else if(rolledDice.contains(2)){
                        if(rolledDice.contains(2)){
                            diceToRollAgain.add(2);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        Logger.log("Computer is pursuing Four Straight!");
                    } else{
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                        for(int die : rolledDice) {
                            if (die == targetValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep.addAll(keptDice);
                    }
                }
                else if(ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")){
                    if(rolledDice.contains(1) || rolledDice.contains(6)){
                        if(rolledDice.contains(1)){
                            diceToRollAgain.add(1);
                        } else {
                            diceToRollAgain.add(6);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        Logger.log("Computer is pursuing Straight!");
                    }
                    else{
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                        for(int die : rolledDice) {
                            if (die == targetValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep.addAll(keptDice);
                    }
                }
                else{
                    Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                    for(int die : rolledDice) {
                        if (die == targetValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }

                    diceToKeep.addAll(keptDice);
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        }
        else {
            List<Integer> pairs = new ArrayList<>();
            List<Integer> nonPairs = new ArrayList<>();
            boolean isStraightPossible = false;

            for (Map.Entry<Integer, Integer> pair : diceCount.entrySet()) {
                if (pair.getValue() >= 2) {
                    pairs.add(pair.getKey());
                } else if (pair.getValue() == 1) {
                    nonPairs.add(pair.getKey());
                }
            }

            if (pairs.size() == 1) {
                Set<Integer> temp = new HashSet<>(currentDiceValues);

                List<Integer> straight = Arrays.asList(1, 2, 3, 4, 5);
                List<Integer> straight2 = Arrays.asList(2, 3, 4, 5, 6);

                int count = 0;
                for (int die : temp) {
                    if (straight.contains(die)) {
                        count++;
                    }
                }

                if (count == 4) {
                    isStraightPossible = true;
                } else {
                    count = 0;

                    for (int die : temp) {
                        if (straight2.contains(die)) {
                            count++;
                        }
                    }

                    if (count == 4) {
                        isStraightPossible = true;
                    }
                }
            }

            if (pairs.size() == 1) {
                Set<Integer> temp = new HashSet<>(currentDiceValues);
                List<Integer> uniqueDice = new ArrayList<>(temp);
                Collections.sort(uniqueDice);
                if (ScoreCard.isCategoryAvailable("Five Straight") && isStraightPossible && rolledDice.contains(pairs.get(0))) {
                    Logger.log("Computer is re-rolling to pursue Five Straight!");
                    int count = 0;
                    for (int die : currentDiceValues) {
                        if (die == pairs.get(0) && count < 1) {
                            diceToRollAgain.add(die);
                            count++;
                        } else {
                            diceToKeep.add(die);
                        }
                    }
                }
                else if(ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.getSequentialDice(uniqueDice).size() == 3 && rolledDice.contains(ScoreCard.subtractList(currentDiceValues, ScoreCard.getSequentialDice(uniqueDice)).get(0)) && rolledDice.contains(ScoreCard.subtractList(currentDiceValues, ScoreCard.getSequentialDice(uniqueDice)).get(1))){
                    diceToKeep = ScoreCard.getSequentialDice(uniqueDice);
                    diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    Logger.log("Computer is pursuing Four Straight!");
                } else {
                    if(nonPairs.size() == 3){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                StringBuilder category = new StringBuilder();
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    category.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    category.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                    category.append("Three of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    category.append("Full House | ");
                                }
                                Logger.log("Computer is pursuing " + category);

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling to try to get a better hand!");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                        else {
                            int count = 0;

                            for(int die : nonPairs){
                                if(rolledDice.contains(die))
                                    count++;
                            }

                            if(count == 2){
                                if (ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                    StringBuilder category = new StringBuilder();
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        category.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                        category.append("Three of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        category.append("Full House | ");
                                    }
                                    Logger.log("Computer is pursuing " + category);

                                    for (int die : currentDiceValues) {
                                        if (die == pairs.get(0)) {
                                            diceToKeep.add(die);
                                        } else if(nonPairs.contains(die) && rolledDice.contains(die)){
                                            diceToRollAgain.add(die);
                                        } else {
                                            diceToKeep.add(die);
                                        }
                                    }
                                } else {
                                    Logger.log("Computer is rolling to try to get a better hand!");

                                    diceToKeep.addAll(keptDice);
                                    diceToRollAgain.addAll(rolledDice);
                                }
                            }
                            else {
                                Logger.log("Computer is rolling to try to get a better hand!");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                    }
                    else if(nonPairs.size() == 2){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                StringBuilder category = new StringBuilder();
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    category.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    category.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    category.append("Full House | ");
                                }
                                Logger.log("Computer is pursuing " + category);

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling to try to get a better hand!");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                        else {
                            int count = 0;

                            for(int die : nonPairs){
                                if(rolledDice.contains(die))
                                    count++;
                            }

                            if(count == 1){
                                if (ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                    StringBuilder category = new StringBuilder();
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        category.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        category.append("Full House | ");
                                    }
                                    Logger.log("Computer is pursuing " + category);

                                    for (int die : currentDiceValues) {
                                        if (die == pairs.get(0)) {
                                            diceToKeep.add(die);
                                        } else if(nonPairs.contains(die) && rolledDice.contains(die)){
                                            diceToRollAgain.add(die);
                                        } else {
                                            diceToKeep.add(die);
                                        }
                                    }
                                }
                                else {
                                    Logger.log("Computer is rolling to try to get a better hand!");

                                    diceToKeep.addAll(keptDice);
                                    diceToRollAgain.addAll(rolledDice);
                                }
                            }
                            else {
                                Logger.log("Computer is rolling to try to get a better hand!");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                    }
                    else if(nonPairs.size() == 1) {
                        if(rolledDice.contains(nonPairs.get(0))){
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                StringBuilder category = new StringBuilder();
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    category.append("Yahtzee | ");
                                }
                                Logger.log("Computer is pursuing " + category);

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                Logger.log("Computer is pursuing Full House!");
                                int count = 0;
                                for(int die : currentDiceValues){
                                    if(die == pairs.get(0) && count < 3){
                                        diceToKeep.add(die);
                                        count++;
                                    } else if(die == nonPairs.get(0)){
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling to try to get a better hand!");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        } else {
                            if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                Logger.log("Computer is pursuing Full House!");
                                int count = 0;
                                for(int die : currentDiceValues){
                                    if(die == pairs.get(0) && count < 3){
                                        diceToKeep.add(die);
                                        count++;
                                    } else if(die == nonPairs.get(0)){
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                Logger.log("Computer is rolling to try to get a better hand!");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                    } else {
                        Logger.log("Computer chose to keep all dice as they are all the same value.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House") && rolledDice.contains(nonPairs.get(0))) {
                        Logger.log("Computer is pursuing Full House!");
                        for (int die : currentDiceValues) {
                            if (die == pairs.get(0) || die == pairs.get(1)) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else if((ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")) &&
                            ScoreCard.getSequentialDice(currentDiceValues).size() == 3 &&
                            ScoreCard.isSubset(ScoreCard.subtractList(currentDiceValues, ScoreCard.getSequentialDice(currentDiceValues)),rolledDice)){
                        Logger.log("Computer is pursuing Straight!");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind")) &&
                            (Collections.frequency(rolledDice, pairs.get(0)) == 2 || Collections.frequency(rolledDice, pairs.get(1)) == 2) &&
                            rolledDice.contains(nonPairs.get(0))){
                        StringBuilder category = new StringBuilder();

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            category.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            category.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            category.append("Three of a Kind | ");
                        }

                        Logger.log("Computer is pursuing " + category);

                        if(Collections.frequency(rolledDice, pairs.get(1)) == 2) {
                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                    } else {
                        Logger.log("Computer is rolling to try to get a better hand!");

                        diceToKeep.addAll(keptDice);
                        diceToRollAgain.addAll(rolledDice);
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind")){
                        if(Collections.frequency(currentDiceValues, pairs.get(0)) == 3 && Collections.frequency(rolledDice, pairs.get(1)) == 2){
                            StringBuilder category = new StringBuilder();

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }

                            Logger.log("Computer is pursuing " + category);

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3 && Collections.frequency(rolledDice, pairs.get(0)) == 2) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(0)) == 2 && Collections.frequency(rolledDice, pairs.get(1)) == 3){
                            StringBuilder category = new StringBuilder();

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }

                            Logger.log("Computer is pursuing " + category);

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 2 && Collections.frequency(rolledDice, pairs.get(0)) == 3) {
                            StringBuilder category = new StringBuilder();
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                category.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                category.append("Four of a Kind | ");
                            }
                            Logger.log("Computer is pursuing " + category);

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            Logger.log("Computer is rolling to try to get a better hand!");

                            diceToKeep.addAll(keptDice);
                            diceToRollAgain.addAll(rolledDice);
                        }

                    }
                    else {
                        Logger.log("Computer is rolling the non-target value to try to get a better hand!");

                        Logger.log("Computer is rolling to try to get a better hand!");

                        diceToKeep.addAll(keptDice);
                        diceToRollAgain.addAll(rolledDice);
                    }
                }
            }
            else {
                if((ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.isCategoryAvailable("Five Straight")) ||
                        (!ScoreCard.isCategoryAvailable("Five Straight") && ScoreCard.isCategoryAvailable("Four Straight"))){
                    if(rolledDice.contains(1) || rolledDice.contains(6)){
                        if(rolledDice.contains(1)){
                            diceToRollAgain.add(1);
                        } else {
                            diceToRollAgain.add(6);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        Logger.log("Computer is pursuing Straight!");
                    } else if(rolledDice.contains(2)){
                        if(rolledDice.contains(2)){
                            diceToRollAgain.add(2);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        Logger.log("Computer is pursuing Four Straight!");
                    } else{
                        Logger.log("Computer is rolling to try to get a better hand!");

                        diceToKeep.addAll(keptDice);
                        diceToRollAgain.addAll(rolledDice);
                    }
                }
                else if(ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")){
                    if(rolledDice.contains(1) || rolledDice.contains(6)){
                        if(rolledDice.contains(1)){
                            diceToRollAgain.add(1);
                        } else {
                            diceToRollAgain.add(6);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        Logger.log("Computer is pursuing Straight!");
                    }
                    else{
                        Logger.log("Computer is rolling to try to get a better hand!");

                        diceToKeep.addAll(keptDice);
                        diceToRollAgain.addAll(rolledDice);
                    }
                }
                else{
                    Logger.log("Computer is rolling to try to get a better hand!");

                    diceToKeep.addAll(keptDice);
                    diceToRollAgain.addAll(rolledDice);
                }
            }
            return new Pair<>(diceToKeep, diceToRollAgain);
        }
    }
}
