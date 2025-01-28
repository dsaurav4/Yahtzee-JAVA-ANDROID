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

public class Human extends Player implements Serializable {

    // Constructor for the human player
    public Human() {
        name = "Human";
    }

    // *******************************
    // Utility Methods
    // *******************************

    /**
     * The function evaluates the current dice values and suggests the best action based on available categories.
     *
     * @param currentDiceValues A list of integers representing the dice values from the first roll.
     * @return A string containing advice on which dice to keep, which to reroll, and the recommended category.
     */
    public String firstRollHelp(List<Integer> currentDiceValues) {
        List<Integer> diceToKeep = new ArrayList<>();
        List<Integer> diceToRollAgain = new ArrayList<>();
        StringBuilder advice = new StringBuilder();

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
            advice.append("Your best category after this roll is: ").append(bestCategory).append(" with ").append(currentMaximumPoints).append(" points.\n");
        } else {
            advice.append("You have no available categories to choose from!\n");
        }

        if (bestCategory.equals("Yahtzee")) {
            advice.append("You have a Yahtzee! You should stand with Yahtzee and score 50 points!\n");
            diceToKeep = new ArrayList<>(currentDiceValues);
        } else if (bestCategory.equals("Five Straight")) {
            advice.append("You have a Five Straight! You should stand with Five Straight and score 40 points!\n");
            diceToKeep = new ArrayList<>(currentDiceValues);
        } else if (bestCategory.equals("Four Straight")) {
            Set<Integer> temp = new HashSet<>();

            if (!ScoreCard.isCategoryAvailable("Five Straight")) {
                advice.append("\nYou should stand with Four Straight as Five Straight is not available!\n");
                diceToKeep = new ArrayList<>(currentDiceValues);
            } else {
                advice.append("\nYou might want to pursue Five Straight!\n");

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
        } else if (bestCategory.equals("Full House")) {
            advice.append("You have a Full House! You should stand with Full House and score 25 points!\n");
            diceToKeep = new ArrayList<>(currentDiceValues);
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
                    advice.append("\nYou might attempt to go for Yahtzee by rerolling the remaining die!\n");
                    for (int die : currentDiceValues) {
                        if (die == fourKindValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                } else if (nonFourKindValue != 6) {
                    advice.append("You might reroll the non-four of a kind value to try to get a higher die: ").append(nonFourKindValue).append("\n");
                    diceToRollAgain.add(nonFourKindValue);
                    for (int die : currentDiceValues) {
                        if (die == fourKindValue) {
                            diceToKeep.add(die);
                        }
                    }
                } else {
                    advice.append("You should stand as the non-four of a kind value is 6 and Yahtzee is not available!\n");
                    diceToKeep = new ArrayList<>(currentDiceValues);
                }
            } else {
                if (currentDiceValues.get(0) < 4) {
                    advice.append("\nYou might reroll to try to get a higher die to add to the sum!\n");
                    diceToKeep.addAll(currentDiceValues.subList(0, 4));
                    diceToRollAgain.add(currentDiceValues.get(4));
                } else {
                    advice.append("\nYou should stand with Four of a Kind!\n");
                    diceToKeep = new ArrayList<>(currentDiceValues);
                }
            }
        } else if (bestCategory.equals("Three of a Kind")) {
            int threeKindValue = -1;
            for (Map.Entry<Integer, Integer> entry : diceCount.entrySet()) {
                if (entry.getValue() >= 3) {
                    threeKindValue = entry.getKey();
                }
            }

            int fullHousePoints = ScoreCard.calculatePoints("Full House", currentDiceValues);

            if (currentMaximumPoints > fullHousePoints && fullHousePoints == 25) {
                advice.append("You might stand with Three of a Kind as it scores higher than a Full House.\n");
                diceToKeep = new ArrayList<>(currentDiceValues);
            }

            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Full House")) {
                if (ScoreCard.isCategoryAvailable("Yahtzee") && ScoreCard.isCategoryAvailable("Full House")) {
                    if (diceCount.get(threeKindValue) == 3) {
                        advice.append("\nYou might attempt to go for Yahtzee or Full House!\n");
                    } else {
                        advice.append("\nYou might attempt to go for Yahtzee!\n");
                    }

                    for (int die : currentDiceValues) {
                        if (die == threeKindValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                } else if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                    advice.append("\nYou might attempt to go for Yahtzee!\n");
                    for (int die : currentDiceValues) {
                        if (die == threeKindValue) {
                            diceToKeep.add(die);
                        } else {
                            diceToRollAgain.add(die);
                        }
                    }
                } else {
                    advice.append("\nYou might want to pursue a Full House!\n");
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
                    advice.append("You might decide to roll again to try and get higher die values!\n");
                } else {
                    advice.append("You might decide to stand with Three of a Kind!\n");
                }
            }
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
                    advice.append("You might pursue a Five Straight!\n");
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
                else if (ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.getSequentialDice(uniqueDice).size() == 3 ) {
                    diceToKeep = ScoreCard.getSequentialDice(uniqueDice);
                    diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    advice.append("You might pursue a Four Straight!\n");
                }
                else {
                    if (nonPairs.size() == 3) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                advice.append("Three of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("Full House | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("Full House | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else if (ScoreCard.isCategoryAvailable("Full House")) {
                            advice.append("You might pursue Full House!\n");
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
                            advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                        advice.append("You have all dice with the same value. You should keep all dice.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House")) {
                        advice.append("You might pursue a Full House!\n");
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
                        advice.append("You might pursue a Straight!\n");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind"))){
                        advice.append("You might pursue ");

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            advice.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            advice.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            advice.append("Three of a Kind | ");
                        }

                        advice.append("\n");

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
                        advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                            advice.append("You might pursue ");

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }

                            advice.append("\n");

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            advice.append("You might reroll the non-target value to try to get a better hand!\n");

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
                        advice.append("You might reroll the non-target value to try to get a better hand!\n");

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
                    advice.append("You might pursue a Straight!\n");

                    diceToRollAgain.add(1);
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                } else {
                    advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                    advice.append("You might pursue a Five Straight!\n");
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
                    advice.append("You might pursue a Four Straight!\n");
                }
                else {
                    if (nonPairs.size() == 3) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                advice.append("Three of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("Full House | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            advice.append("You might reroll all dice to try to get a better hand!\n");
                            diceToRollAgain = currentDiceValues;
                        }

                    }
                    else if (nonPairs.size() == 2) {
                        if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            if (ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("Full House | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else {
                            advice.append("You might reroll all dice to try to get a better hand!\n");
                            diceToRollAgain = currentDiceValues;
                        }
                    }
                    else if (nonPairs.size() == 1) {

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(0)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        } else if (ScoreCard.isCategoryAvailable("Full House")) {
                            advice.append("You might pursue Full House!\n");
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
                            advice.append("You might reroll all dice to try to get a better hand!\n");
                            diceToRollAgain = currentDiceValues;
                        }

                    }
                    else {
                        advice.append("You have all dice with the same value. You should keep all dice.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House")) {
                        advice.append("You might pursue a Full House!\n");
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
                        advice.append("You might pursue a Straight!\n");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind"))){
                        advice.append("You might pursue ");

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            advice.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            advice.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            advice.append("Three of a Kind | ");
                        }

                        advice.append("\n");


                        int maxPair = Math.max(pairs.get(0), pairs.get(1));
                        for(int die : currentDiceValues){
                            if(die == maxPair){
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                    } else {
                        advice.append("You might reroll all dice to try to get a better hand!\n");
                        diceToRollAgain = currentDiceValues;
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind")){
                        if(Collections.frequency(currentDiceValues, pairs.get(0)) == 3){
                            advice.append("You might pursue ");

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }

                            advice.append("\n");

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            advice.append("You might reroll all dice to try to get a better hand!\n");
                            diceToRollAgain = currentDiceValues;
                        }

                    }
                    else {
                        advice.append("You might reroll all dice to try to get a better hand!\n");
                        diceToRollAgain = currentDiceValues;
                    }
                }
            }
            else {
                if(ScoreCard.isCategoryAvailable("Four Straight") || ScoreCard.isCategoryAvailable("Five Straight")){
                    advice.append("You might pursue a Straight!\n");

                    diceToRollAgain.add(1);
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                } else {
                    advice.append("You might reroll all dice to try to get a better hand!\n");
                    diceToRollAgain = currentDiceValues;
                }
            }
        }

        advice.append("\nYou should keep these dice: ");
        if (diceToKeep.isEmpty()) {
            advice.append("None");
        } else {
            for (int die : diceToKeep) {
                advice.append(die).append(" ");
            }
        }

        advice.append("\n\nYou should reroll these dice: ");
        if (diceToRollAgain.isEmpty()) {
            advice.append("None");
        } else {
            for (int die : diceToRollAgain) {
                advice.append(die).append(" ");
            }
        }
        advice.append("\n");

        return advice.toString();
    }

    /**
     * Combines kept dice from the first roll with newly rolled dice to recommend the best action.
     *
     * @param rolledDice A list of integers representing the dice values from the second roll.
     * @param keptDice   A list of integers representing the dice values kept from the first roll.
     * @return A string containing advice on which dice to keep, which to reroll, and the recommended category.
     */
    public String secondRollHelp(List<Integer> rolledDice, List<Integer> keptDice) {
        List<Integer> currentDiceValues = new ArrayList<>(keptDice);
        currentDiceValues.addAll(rolledDice);

        List<Integer> diceToKeep = new ArrayList<>();
        List<Integer> diceToRollAgain = new ArrayList<>();
        StringBuilder advice = new StringBuilder();

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
            advice.append("Your best category after this roll is: ").append(bestCategory).append(" with ").append(currentMaximumPoints).append(" points.\n");
        } else {
            advice.append("You have no available categories to choose from!\n");
        }

        if (bestCategory.equals("Yahtzee")) {
            advice.append("You have a Yahtzee! You should stand with Yahtzee and score 50 points!\n");
            diceToKeep = new ArrayList<>(currentDiceValues);
        } else if (bestCategory.equals("Five Straight")) {
            advice.append("You have a Five Straight! You should stand with Five Straight and score 40 points!\n");
            diceToKeep = new ArrayList<>(currentDiceValues);
        } else if (bestCategory.equals("Four Straight")) {
            Set<Integer> temp = new HashSet<>();

            if (!ScoreCard.isCategoryAvailable("Five Straight")) {
                advice.append("\nYou should stand with Four Straight as Five Straight is not available!\n");
                diceToKeep = new ArrayList<>(currentDiceValues);
            } else {
                advice.append("\nYou might want to pursue Five Straight!\n");

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
                        advice.append("You might re-roll: ").append(nonStraightValue).append("\n");
                        diceToRollAgain.add(nonStraightValue);
                        for (int dice : temp) {
                            diceToKeep.add(dice);
                        }
                    } else {
                        advice.append("\nYou might stand on Four Straight as you cannot re-roll ").append(nonStraightValue).append("!\n");
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
        } else if (bestCategory.equals("Full House")) {
            advice.append("You have a Full House! You should stand with Full House and score 25 points!\n");
            diceToKeep = new ArrayList<>(currentDiceValues);
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
                    advice.append("You might pursue Yahtzee!\n");
                    diceToRollAgain.add(nonFourKindValue);
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                }
                else {
                    if(nonFourKindValue < 4){
                        diceToRollAgain.add(nonFourKindValue);
                    }
                    diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                    if(!diceToRollAgain.isEmpty()){
                        advice.append("You might re-roll to pursue Four of a Kind with higher die values!\n");
                    } else {
                        advice.append("You might stand with Four of a Kind!\n");
                    }
                }
            }
            else if(nonFourKindValue == -1){
                if(fourKindValue < 4){
                    diceToRollAgain.add(fourKindValue);
                }
                diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                if(!diceToRollAgain.isEmpty()){
                    advice.append("You might re-roll to pursue Four of a Kind with higher die values!\n");
                } else {
                    advice.append("You might stand with Four of a Kind!\n");
                }
            }
            else {
                diceToKeep = new ArrayList<>(currentDiceValues);
                advice.append("You might stand with Four of a Kind!\n");
            }

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
                advice.append("You might stand with Three of a Kind as it scores higher than a Full House.\n");
                diceToKeep = new ArrayList<>(currentDiceValues);
            }

            if(nonMatching.size() == 2){
                if(rolledDice.contains(nonMatching.get(0)) && rolledDice.contains(nonMatching.get(1))){
                    if(ScoreCard.isCategoryAvailable("Yahtzee")){
                        advice.append("You might pursue Yahtzee!\n");
                        for(int die : currentDiceValues){
                            if(die == threeKindValue){
                                diceToKeep.add(die);
                            }else{
                                diceToRollAgain.add(die);
                            }
                        }
                    }
                    else if(ScoreCard.isCategoryAvailable("Full House")){
                        advice.append("You might pursue Full House!\n");
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
                            advice.append("You might re-roll to pursue Three of a Kind with higher die values!\n");
                        } else {
                            advice.append("You might stand with Three of a Kind!\n");
                        }
                    }
                }
                else if(rolledDice.contains(nonMatching.get(0)) || rolledDice.contains(nonMatching.get(1))){
                    if(ScoreCard.isCategoryAvailable("Full House")){
                        advice.append("You might pursue Full House!\n");
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
                            advice.append("You might re-roll to pursue Three of a Kind with higher die values!\n");
                        } else {
                            advice.append("You might stand with Three of a Kind!\n");
                        }
                    }
                }
                else{
                    diceToKeep = currentDiceValues;
                    advice.append("You might stand with Three of a Kind as it cannot reroll the non-three of a kind values!\n");
                }
            }
            else if(nonMatching.size() == 1){
                if(rolledDice.contains(nonMatching.get(0))){
                    if(ScoreCard.isCategoryAvailable("Yahtzee")){
                        advice.append("You might pursue Yahtzee!\n");
                        diceToRollAgain = nonMatching;
                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                    }
                    else if(ScoreCard.isCategoryAvailable("Full House")){
                        if(rolledDice.contains(threeKindValue)){
                            advice.append("You might pursue Full House!\n");
                            diceToRollAgain.add(threeKindValue);
                            diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                        }
                    } else {
                        if(nonMatching.get(0) < 4){
                            diceToRollAgain.add(nonMatching.get(0));
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        if(!diceToRollAgain.isEmpty()){
                            advice.append("You might re-roll to pursue Three of a Kind with higher die values!\n");
                        }
                        else {
                            advice.append("You might stand with Three of a Kind!\n");
                        }
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Full House")){
                        diceToRollAgain.add(threeKindValue);
                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);
                        advice.append("You might pursue Full House!\n");
                    } else {
                        if(threeKindValue < 4){
                            diceToRollAgain.add(threeKindValue);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        if(!diceToRollAgain.isEmpty()){
                            advice.append("You might re-roll to pursue Three of a Kind with higher die values!\n");
                        } else {
                            advice.append("You might stand with Three of a Kind!\n");
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
                        advice.append("You might pursue Full House!\n");
                    }
                    else {
                        advice.append("You might stand with Three of a Kind!\n");
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
                            advice.append("You might pursue Yahtzee!\n");
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
                                advice.append("You might re-roll to pursue Three of a Kind with higher die values!\n");
                            }
                            else {
                                advice.append("You might stand with Three of a Kind!\n");
                            }
                        }
                    } else {
                        diceToKeep = currentDiceValues;
                        advice.append("You might stand with Three of a Kind!\n");
                    }
                }
            }


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
                    advice.append("You might re-roll to pursue Five Straight!\n");
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
                    advice.append("You might pursue Four Straight!\n");
                }
                else {
                    if(nonPairs.size() == 3){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("You might pursue ");
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    advice.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    advice.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                    advice.append("Three of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    advice.append("Full House | ");
                                }
                                advice.append("\n");

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                    advice.append("You might pursue ");
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        advice.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                        advice.append("Three of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        advice.append("Full House | ");
                                    }
                                    advice.append("\n");

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
                                    advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                advice.append("You might pursue ");
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    advice.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    advice.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    advice.append("Full House | ");
                                }
                                advice.append("\n");

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                    advice.append("You might pursue ");
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        advice.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        advice.append("Full House | ");
                                    }
                                    advice.append("\n");

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
                                    advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                advice.append("You might pursue ");
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    advice.append("Yahtzee | ");
                                }
                                advice.append("\n");

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                advice.append("You might pursue Full House!\n");
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
                                advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                                advice.append("You might pursue Full House!\n");
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
                                advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                    else {
                        advice.append("You have all dice with the same value. You should keep all dice.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House") && rolledDice.contains(nonPairs.get(0))) {
                        advice.append("You might pursue a Full House!\n");
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
                        advice.append("You might pursue a Straight!\n");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind")) &&
                            (Collections.frequency(rolledDice, pairs.get(0)) == 2 || Collections.frequency(rolledDice, pairs.get(1)) == 2) &&
                            rolledDice.contains(nonPairs.get(0))){
                        advice.append("You might pursue ");

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            advice.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            advice.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            advice.append("Three of a Kind | ");
                        }

                        advice.append("\n");

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
                        advice.append("You might reroll the non-target value to try to get a better hand!\n");
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
                            advice.append("You might pursue ");

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }

                            advice.append("\n");

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3 && Collections.frequency(rolledDice, pairs.get(0)) == 2) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(0)) == 2 && Collections.frequency(rolledDice, pairs.get(1)) == 3){
                            advice.append("You might pursue ");

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }

                            advice.append("\n");

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 2 && Collections.frequency(rolledDice, pairs.get(0)) == 3) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            advice.append("You might reroll the non-target value to try to get a better hand!\n");

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
                        advice.append("You might reroll the non-target value to try to get a better hand!\n");

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
                if ((ScoreCard.isCategoryAvailable("Four Straight") && ScoreCard.isCategoryAvailable("Five Straight")) ||
                        (!ScoreCard.isCategoryAvailable("Five Straight") && ScoreCard.isCategoryAvailable("Four Straight"))) {
                    if (rolledDice.contains(1) || rolledDice.contains(6)) {
                        if (rolledDice.contains(1)) {
                            diceToRollAgain.add(1);
                        } else {
                            diceToRollAgain.add(6);
                        }


                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        advice.append("You might pursue a Straight!\n");
                    } else if (rolledDice.contains(2)) {
                        if (rolledDice.contains(2)) {
                            diceToRollAgain.add(2);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        advice.append("You might pursue Four Straight!\n");
                    } else {
                        advice.append("You might reroll the non-target value to try to get a better hand!\n");

                        for (int die : rolledDice) {
                            if (die == targetValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep.addAll(keptDice);
                    }
                } else if (ScoreCard.isCategoryAvailable("Five Straight") || ScoreCard.isCategoryAvailable("Four Straight")) {
                    if (rolledDice.contains(1) || rolledDice.contains(6)) {
                        if (rolledDice.contains(1)) {
                            diceToRollAgain.add(1);
                        } else {
                            diceToRollAgain.add(6);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        advice.append("You might pursue a Straight!\n");
                    } else {
                        advice.append("You might reroll the non-target value to try to get a better hand!\n");

                        for (int die : rolledDice) {
                            if (die == targetValue) {
                                diceToKeep.add(die);
                            } else {
                                diceToRollAgain.add(die);
                            }
                        }

                        diceToKeep.addAll(keptDice);
                    }
                } else {
                    advice.append("You might reroll the non-target value to try to get a better hand!\n");

                    for (int die : rolledDice) {
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
                    advice.append("You might re-roll to pursue Five Straight!\n");
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
                    advice.append("You might pursue Four Straight!\n");
                } else {
                    if(nonPairs.size() == 3){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("You might pursue ");
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    advice.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    advice.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                    advice.append("Three of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    advice.append("Full House | ");
                                }
                                advice.append("\n");

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                advice.append("You might reroll to try to get a better hand!\n");

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
                                    advice.append("You might pursue ");
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        advice.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                                        advice.append("Three of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        advice.append("Full House | ");
                                    }
                                    advice.append("\n");

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
                                    advice.append("You might reroll to try to get a better hand!\n");

                                    diceToKeep.addAll(keptDice);
                                    diceToRollAgain.addAll(rolledDice);
                                }
                            }
                            else {
                                advice.append("You might reroll to try to get a better hand!\n");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                    }
                    else if(nonPairs.size() == 2){
                        if(ScoreCard.isSubset(nonPairs, rolledDice)){
                            if (ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Full House")) {
                                advice.append("You might pursue ");
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    advice.append("Yahtzee | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                    advice.append("Four of a Kind | ");
                                }
                                if (ScoreCard.isCategoryAvailable("Full House")) {
                                    advice.append("Full House | ");
                                }
                                advice.append("\n");

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else {
                                advice.append("You might reroll to try to get a better hand!\n");

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
                                    advice.append("You might pursue ");
                                    if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                        advice.append("Four of a Kind | ");
                                    }
                                    if (ScoreCard.isCategoryAvailable("Full House")) {
                                        advice.append("Full House | ");
                                    }
                                    advice.append("\n");

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
                                    advice.append("You might reroll to try to get a better hand!\n");

                                    diceToKeep.addAll(keptDice);
                                    diceToRollAgain.addAll(rolledDice);
                                }
                            }
                            else {
                                advice.append("You might reroll to try to get a better hand!\n");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                    }
                    else if(nonPairs.size() == 1) {
                        if(rolledDice.contains(nonPairs.get(0))){
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("You might pursue ");
                                if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                    advice.append("Yahtzee | ");
                                }
                                advice.append("\n");

                                for (int die : currentDiceValues) {
                                    if (die == pairs.get(0)) {
                                        diceToKeep.add(die);
                                    } else {
                                        diceToRollAgain.add(die);
                                    }
                                }
                            }
                            else if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                advice.append("You might pursue Full House!\n");
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
                                advice.append("You might reroll to try to get a better hand!\n");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        } else {
                            if(Collections.frequency(rolledDice, pairs.get(0)) >= 1 && ScoreCard.isCategoryAvailable("Full House")){
                                advice.append("You might pursue Full House!\n");
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
                                advice.append("You might reroll to try to get a better hand!\n");

                                diceToKeep.addAll(keptDice);
                                diceToRollAgain.addAll(rolledDice);
                            }
                        }
                    } else {
                        advice.append("You have all dice with the same value. You should keep all dice.\n");
                        diceToKeep = new ArrayList<>(currentDiceValues);
                    }
                }
            }
            else if (pairs.size() == 2){
                if(nonPairs.size() == 1){
                    if(ScoreCard.isCategoryAvailable("Full House") && rolledDice.contains(nonPairs.get(0))) {
                        advice.append("You might pursue a Full House!\n");
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
                        advice.append("You might pursue a Straight!\n");
                        diceToKeep = ScoreCard.getSequentialDice(currentDiceValues);
                        diceToRollAgain = ScoreCard.subtractList(currentDiceValues, diceToKeep);
                    }
                    else if((ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind") || ScoreCard.isCategoryAvailable("Three of a Kind")) &&
                            (Collections.frequency(rolledDice, pairs.get(0)) == 2 || Collections.frequency(rolledDice, pairs.get(1)) == 2) &&
                            rolledDice.contains(nonPairs.get(0))){
                        advice.append("You might pursue ");

                        if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                            advice.append("Yahtzee | ");
                        } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                            advice.append("Four of a Kind | ");
                        } else if (ScoreCard.isCategoryAvailable("Three of a Kind")) {
                            advice.append("Three of a Kind | ");
                        }

                        advice.append("\n");

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
                        advice.append("You might reroll to try to get a better hand!\n");

                        diceToKeep.addAll(keptDice);
                        diceToRollAgain.addAll(rolledDice);
                    }
                }
                else{
                    if(ScoreCard.isCategoryAvailable("Yahtzee") || ScoreCard.isCategoryAvailable("Four of a Kind")){
                        if(Collections.frequency(currentDiceValues, pairs.get(0)) == 3 && Collections.frequency(rolledDice, pairs.get(1)) == 2){
                            advice.append("You might pursue ");

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }

                            advice.append("\n");

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 3 && Collections.frequency(rolledDice, pairs.get(0)) == 2) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(0)) == 2 && Collections.frequency(rolledDice, pairs.get(1)) == 3){
                            advice.append("You might pursue ");

                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }

                            advice.append("\n");

                            for(int die : currentDiceValues){
                                if(die == pairs.get(0)){
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else if(Collections.frequency(currentDiceValues, pairs.get(1)) == 2 && Collections.frequency(rolledDice, pairs.get(0)) == 3) {
                            advice.append("You might pursue ");
                            if (ScoreCard.isCategoryAvailable("Yahtzee")) {
                                advice.append("Yahtzee | ");
                            } else if (ScoreCard.isCategoryAvailable("Four of a Kind")) {
                                advice.append("Four of a Kind | ");
                            }
                            advice.append("\n");

                            for (int die : currentDiceValues) {
                                if (die == pairs.get(1)) {
                                    diceToKeep.add(die);
                                } else {
                                    diceToRollAgain.add(die);
                                }
                            }
                        }
                        else {
                            advice.append("You might reroll to try to get a better hand!\n");

                            diceToKeep.addAll(keptDice);
                            diceToRollAgain.addAll(rolledDice);
                        }

                    }
                    else {
                        advice.append("You might reroll to try to get a better hand!\n");

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

                        advice.append("You might pursue a Straight!\n");
                    } else if(rolledDice.contains(2)){
                        if(rolledDice.contains(2)){
                            diceToRollAgain.add(2);
                        }

                        diceToKeep = ScoreCard.subtractList(currentDiceValues, diceToRollAgain);

                        advice.append("You might pursue Four Straight!\n");
                    } else{
                        advice.append("You might reroll to try to get a better hand!\n");

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

                        advice.append("You might pursue a Straight!\n");
                    }
                    else{
                        advice.append("You might reroll to try to get a better hand!\n");

                        diceToKeep.addAll(keptDice);
                        diceToRollAgain.addAll(rolledDice);
                    }
                }
                else{
                    advice.append("You might reroll to try to get a better hand!\n");

                    diceToKeep.addAll(keptDice);
                    diceToRollAgain.addAll(rolledDice);
                }
            }
        }

        advice.append("\nYou should keep these dice: ");
        if (diceToKeep.isEmpty()) {
            advice.append("None");
        } else {
            for (int die : diceToKeep) {
                advice.append(die).append(" ");
            }
        }

        advice.append("\n\nYou should reroll these dice: ");
        if (diceToRollAgain.isEmpty()) {
            advice.append("None");
        } else {
            for (int die : diceToRollAgain) {
                advice.append(die).append(" ");
            }
        }
        advice.append("\n");

        return advice.toString();
    }

}