package com.example.yahtzee.Model;

import java.util.*;

public class ScoreCard {

    // *******************************
    // Class Constants
    // *******************************

    /**
     * The scoring categories available in the game.
     */
    private static final String[] rows = { "Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", 
                                           "Three of a Kind", "Four of a Kind", "Full House", 
                                           "Four Straight", "Five Straight", "Yahtzee" };

    // *******************************
    // Class Variables
    // *******************************

    /**
     * The scores for each category, represented as a map.
     * The map stores the category, player name, points, and the round in which it was scored.
     */
    private static Map<String, Map.Entry<String, Map.Entry<Integer, Integer>>> scores = new HashMap<>();

    static {
        for (String row : rows) {
            scores.put(row, Map.entry("", Map.entry(0, 0)));
        }
    }

    // *******************************
    // Utility Methods
    // *******************************

    /**
     * Checks if all categories in the scorecard are filled.
     *
     * @return True if all categories are filled, false otherwise.
     */
    public static boolean isAllCategoriesFilled() {
        for (Map.Entry<String, Map.Entry<String, Map.Entry<Integer, Integer>>> score : scores.entrySet()) {
            if (score.getValue().getKey().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Displays potential points for available categories based on the given dice values.
     *
     * @param diceValues A list of integers representing the dice values.
     */
    public static void displayPossiblePoints(List<Integer> diceValues) {
        System.out.println("\nPotential points in available categories:");
        for (String category : rows) {
            if (scores.get(category).getKey().isEmpty()) {
                int points = calculatePoints(category, diceValues);
                if (points != 0) {
                    System.out.println(category + ": " + points);
                }
            }
        }
        System.out.println();
    }

    /**
     * Checks if a given category is available for scoring.
     *
     * @param category The category name to check.
     * @return True if the category is available, false otherwise.
     */
    public static boolean isCategoryAvailable(String category) {
        Map.Entry<String, Map.Entry<Integer, Integer>> scoreData = scores.get(category);
        return scoreData != null && scoreData.getKey().isEmpty();
    }

    /**
     * Retrieves a list of categories to choose for scoring based on the dice values.
     *
     * @param diceValues A list of integers representing the dice values.
     * @return A list of available categories that have non-zero potential points.
     */
    public static List<String> getCategoryToChoose(List<Integer> diceValues) {
        List<String> availableCategories = new ArrayList<>();
        for (String category : rows) {
            if (scores.get(category).getKey().isEmpty() && calculatePoints(category, diceValues) != 0) {
                availableCategories.add(category);
            }
        }
        return availableCategories;
    }

    /**
     * Chooses a category for scoring and updates the scorecard.
     *
     * @param category   The category to choose.
     * @param diceValues The dice values used for scoring.
     * @param winner     The name of the player.
     * @param round      The current round number.
     */
    public static void chooseCategory(String category, List<Integer> diceValues, String winner, int round) {
        if (scores.get(category).getKey().isEmpty()) {
            int points = calculatePoints(category, diceValues);
            scores.put(category, Map.entry(winner, Map.entry(points, round)));
        } else {
            System.out.println("Category already filled!");
        }
    }

    /**
     * Identifies the best scoring category based on the current dice values.
     *
     * @param diceValues A list of integers representing the current dice values.
     * @return A pair containing:
     *         - The category name as a String.
     *         - The points as an Integer.
     *         Returns a default pair ("", 0) if no points can be scored.
     */

    public static Pair<String, Integer> getBestHands(List<Integer> diceValues) {
        Pair<String, Integer> bestHand = new Pair<>("", 0);
        boolean pointsEarned = false;

        for (String category : rows) {
            if (scores.get(category).getKey().isEmpty()) {
                int points = calculatePoints(category, diceValues);
                if (points > 0) {
                    pointsEarned = true;
                    if (points >= bestHand.getValue()) {
                        bestHand = new Pair<>(category, points);
                    }
                }
            }
        }
        return pointsEarned ? bestHand : new Pair<>("", 0); // Return the best hand or default empty pair
    }

    /**
     * Calculates the total score for a specific player by summing up their scores across all categories.
     *
     * @param name The name of the player.
     * @return The total score of the player.
     */

    public static int getTotalScore(String name) {
        int totalScore = 0;
        for (Map.Entry<String, Map.Entry<String, Map.Entry<Integer, Integer>>> score : scores.entrySet()) {
            if (score.getValue().getKey().equals(name)) {
                totalScore += score.getValue().getValue().getKey();
            }
        }
        return totalScore;
    }

    /**
     * Calculates the potential points for a given category based on the dice values.
     *
     * @param category   The name of the scoring category.
     * @param diceValues A list of integers representing the dice values.
     * @return The calculated score for the category. Returns 0 if the criteria for the category are not met.
     */
    public static int calculatePoints(String category, List<Integer> diceValues) {
        int points = 0;

        if (category.equals("Aces")) {
            for (int value : diceValues) {
                if (value == 1) {
                    points += 1;
                }
            }
        } else if (category.equals("Twos")) {
            for (int value : diceValues) {
                if (value == 2) {
                    points += 2;
                }
            }
        } else if (category.equals("Threes")) {
            for (int value : diceValues) {
                if (value == 3) {
                    points += 3;
                }
            }
        } else if (category.equals("Fours")) {
            for (int value : diceValues) {
                if (value == 4) {
                    points += 4;
                }
            }
        } else if (category.equals("Fives")) {
            for (int value : diceValues) {
                if (value == 5) {
                    points += 5;
                }
            }
        } else if (category.equals("Sixes")) {
            for (int value : diceValues) {
                if (value == 6) {
                    points += 6;
                }
            }
        } else if (category.equals("Three of a Kind")) {
            Map<Integer, Integer> valueCount = new HashMap<>();
            for (int value : diceValues) {
                valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
            }
            for (int count : valueCount.values()) {
                if (count >= 3) {
                    for (int value : diceValues) {
                        points += value;
                    }
                    break;
                }
            }
        } else if (category.equals("Four of a Kind")) {
            Map<Integer, Integer> valueCount = new HashMap<>();
            for (int value : diceValues) {
                valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
            }
            for (int count : valueCount.values()) {
                if (count >= 4) {
                    for (int value : diceValues) {
                        points += value;
                    }
                    break;
                }
            }
        } else if (category.equals("Full House")) {
            Map<Integer, Integer> valueCount = new HashMap<>();
            for (int value : diceValues) {
                valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
            }
            if (valueCount.containsValue(3) && valueCount.containsValue(2)) {
                points = 25;
            }
        } else if (category.equals("Four Straight")) {
            Set<Integer> uniqueValues = new TreeSet<>(diceValues);
            List<Integer> temp = new ArrayList<>(uniqueValues);
            if (temp.size() >= 4) {
                boolean hasFourStraight = false;
                for (int i = 0; i <= temp.size() - 4; i++) {
                    if (temp.get(i + 1) == temp.get(i) + 1 &&
                        temp.get(i + 2) == temp.get(i) + 2 &&
                        temp.get(i + 3) == temp.get(i) + 3) {
                        hasFourStraight = true;
                        break;
                    }
                }
                points = hasFourStraight ? 30 : 0;
            }
        } else if (category.equals("Five Straight")) {
            List<Integer> temp = new ArrayList<>(diceValues);
            Collections.sort(temp);
            if ((temp.equals(Arrays.asList(1, 2, 3, 4, 5)) ||
                 temp.equals(Arrays.asList(2, 3, 4, 5, 6)))) {
                points = 40;
            }
        } else if (category.equals("Yahtzee")) {
            Map<Integer, Integer> valueCount = new HashMap<>();
            for (int value : diceValues) {
                valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
                if (valueCount.get(value) == 5) {
                    points = 50;
                    break;
                }
            }
        } else {
            System.out.println("Invalid category!");
        }

        return points;
    }

    /**
     * Suggests categories that players can pursue based on the current dice values.
     *
     * @param diceValues A list of integers representing the dice values.
     * @return A list of category names that can be pursued.
     */
    public static List<String> getCategoriesToPursue(List<Integer> diceValues) {
        List<String> categoriesToPursue = new ArrayList<>();

        List<Integer> uniqueValues = new ArrayList<>(new TreeSet<>(diceValues));
        List<Integer> pairValues = new ArrayList<>();
        for (int value : uniqueValues) {
            if (Collections.frequency(diceValues, value) >= 2) {
                pairValues.add(value);
            }
        }
        List<Integer> nonPairValues = new ArrayList<>();
        for (int value : uniqueValues) {
            if (!pairValues.contains(value)) {
                nonPairValues.add(value);
            }
        }

        if(uniqueValues.size() == 5 && (isCategoryAvailable("Five Straight") || isCategoryAvailable("Four Straight"))) {
            if(isCategoryAvailable("Five Straight") && !isCategoryAvailable("Four Straight"))
                categoriesToPursue.add("Five Straight");
            else if(!isCategoryAvailable("Five Straight") && isCategoryAvailable("Four Straight"))
                categoriesToPursue.add("Four Straight");
            else{
                categoriesToPursue.add("Five Straight");
                categoriesToPursue.add("Four Straight");
            }
        } else if (ScoreCard.isCategoryAvailable("Five Straight") && uniqueValues.size() == 4 && (isSubset(diceValues, List.of(1, 2, 3, 4, 5)) || isSubset(diceValues, List.of(2, 3, 4, 5, 6)))) {
            categoriesToPursue.add("Five Straight");
        } else if (ScoreCard.isCategoryAvailable("Four Straight") && !ScoreCard.isCategoryAvailable("Five Straight") && uniqueValues.size() == 4 && (isSubset(diceValues, List.of(1, 2, 3, 4, 5)) || isSubset(diceValues, List.of(2, 3, 4, 5, 6)))) {
            categoriesToPursue.add("Four Straight");
        }

        List<Integer> sequentialDice = getSequentialDice(uniqueValues);
        if (ScoreCard.isCategoryAvailable("Four Straight") && sequentialDice.size() == 3 && uniqueValues.size() >= 3 && (isSubset(sequentialDice, List.of(1, 2, 3, 4)) || isSubset(sequentialDice, List.of(2, 3, 4, 5)) || isSubset(sequentialDice, List.of(3, 4, 5, 6)))) {
            categoriesToPursue.add("Four Straight");
        }

        if (pairValues.size() >= 1) {
            if (ScoreCard.isCategoryAvailable("Yahtzee"))
                categoriesToPursue.add("Yahtzee");
            if (ScoreCard.isCategoryAvailable("Full House"))
                categoriesToPursue.add("Full House");
            if (ScoreCard.isCategoryAvailable("Four of a Kind"))
                categoriesToPursue.add("Four of a Kind");
            if (ScoreCard.isCategoryAvailable("Three of a Kind"))
                categoriesToPursue.add("Three of a Kind");
        }

        List<Integer> upperSectionScores = new ArrayList<>();
        upperSectionScores.add(ScoreCard.isCategoryAvailable("Aces") ? calculatePoints("Aces", diceValues) : 0);
        upperSectionScores.add(ScoreCard.isCategoryAvailable("Twos") ? calculatePoints("Twos", diceValues) : 0);
        upperSectionScores.add(ScoreCard.isCategoryAvailable("Threes") ? calculatePoints("Threes", diceValues) : 0);
        upperSectionScores.add(ScoreCard.isCategoryAvailable("Fours") ? calculatePoints("Fours", diceValues) : 0);
        upperSectionScores.add(ScoreCard.isCategoryAvailable("Fives") ? calculatePoints("Fives", diceValues) : 0);
        upperSectionScores.add(ScoreCard.isCategoryAvailable("Sixes") ? calculatePoints("Sixes", diceValues) : 0);

        Integer maxScore = Collections.max(upperSectionScores); // Using Collections.max()

        if (maxScore != null) {
            if (maxScore == calculatePoints("Aces", diceValues) && ScoreCard.isCategoryAvailable("Aces")) {
                categoriesToPursue.add("Aces");
            } else if (maxScore == calculatePoints("Twos", diceValues) && ScoreCard.isCategoryAvailable("Twos")) {
                categoriesToPursue.add("Twos");
            } else if (maxScore == calculatePoints("Threes", diceValues) && ScoreCard.isCategoryAvailable("Threes")) {
                categoriesToPursue.add("Threes");
            } else if (maxScore == calculatePoints("Fours", diceValues) && ScoreCard.isCategoryAvailable("Fours")) {
                categoriesToPursue.add("Fours");
            } else if (maxScore == calculatePoints("Fives", diceValues) && ScoreCard.isCategoryAvailable("Fives")) {
                categoriesToPursue.add("Fives");
            } else if (maxScore == calculatePoints("Sixes", diceValues) && ScoreCard.isCategoryAvailable("Sixes")) {
                categoriesToPursue.add("Sixes");
            }
        } else {
            if (calculatePoints("Aces", diceValues) > 0 && ScoreCard.isCategoryAvailable("Aces")) {
                categoriesToPursue.add("Aces");
            } else if (calculatePoints("Twos", diceValues) > 0 && ScoreCard.isCategoryAvailable("Twos")) {
                categoriesToPursue.add("Twos");
            } else if (calculatePoints("Threes", diceValues) > 0 && ScoreCard.isCategoryAvailable("Threes")) {
                categoriesToPursue.add("Threes");
            } else if (calculatePoints("Fours", diceValues) > 0 && ScoreCard.isCategoryAvailable("Fours")) {
                categoriesToPursue.add("Fours");
            } else if (calculatePoints("Fives", diceValues) > 0 && ScoreCard.isCategoryAvailable("Fives")) {
                categoriesToPursue.add("Fives");
            } else if (calculatePoints("Sixes", diceValues) > 0 && ScoreCard.isCategoryAvailable("Sixes")) {
                categoriesToPursue.add("Sixes");
            }
        }

        return categoriesToPursue;
    }

    /**
     * Finds the longest sequence of consecutive dice values from the given list.
     *
     * @param diceValues A list of integers representing the dice values.
     * @return A list containing the longest sequence of consecutive values.
     */
    public static List<Integer> getSequentialDice(List<Integer> diceValues) {
        if (diceValues == null || diceValues.isEmpty()) {
            return new ArrayList<>();
        }

        Collections.sort(diceValues);

        List<Integer> longestSequence = new ArrayList<>();
        List<Integer> currentSequence = new ArrayList<>();
        currentSequence.add(diceValues.get(0));

        for (int i = 1; i < diceValues.size(); i++) {
            if (diceValues.get(i) == diceValues.get(i - 1) + 1) {
                currentSequence.add(diceValues.get(i));
            } else {
                if (currentSequence.size() > longestSequence.size()) {
                    longestSequence = new ArrayList<>(currentSequence);
                }
                currentSequence.clear();
                currentSequence.add(diceValues.get(i));
            }
        }


        if (currentSequence.size() > longestSequence.size()) {
            longestSequence = new ArrayList<>(currentSequence);
        }

        return longestSequence;
    }

    /**
     * Checks if one list of integers is a subset of another list.
     *
     * @param list1 The potential subset.
     * @param list2 The list to check against.
     * @return True if list1 is a subset of list2; false otherwise.
     */
    public static boolean isSubset(List<Integer> list1, List<Integer> list2) {
        if (list1 == null || list1.isEmpty() || list2 == null || list2.isEmpty()) {
            return false;
        }

        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);

        return set2.containsAll(set1);
    }

    /**
     * Subtracts elements of one list from another.
     *
     * @param list1 The list to subtract from.
     * @param list2 The list of elements to subtract.
     * @return A new list with elements of list2 removed from list1.
     */
    public static List<Integer> subtractList(List<Integer> list1, List<Integer> list2) {
        List<Integer> result = new ArrayList<>(list1);
        for (Integer element : list2) {
            result.remove(element);
        }
        return result;
    }

    /**
     * Retrieves the current state of the scorecard, showing scores and their associated categories.
     *
     * @return A map representing the scorecard.
     */
    public static Map<String, Map.Entry<String, Map.Entry<Integer, Integer>>> getScoreBoard() {
        return scores;
    }

    /**
     * Returns all available scoring categories in the game.
     *
     * @return A list of category names.
     */
    public static List<String> getAllCategories() {
        return Arrays.asList(rows);
    }

    /**
     * Updates the current state of the scorecard with a new scoreboard.
     *
     * @param newScoreBoard A map representing the new scorecard.
     */
    public void setScoreBoard(Map<String, Map.Entry<String, Map.Entry<Integer, Integer>>> newScoreBoard) {
        scores = newScoreBoard;
    }

    /**
     * Resets the scorecard to its initial state, clearing all scores and categories.
     */

    public static void clearScoreBoard() {
        for (String category : rows) {
            scores.put(category, Map.entry("", Map.entry(0, 0)));
        }
    }
}
