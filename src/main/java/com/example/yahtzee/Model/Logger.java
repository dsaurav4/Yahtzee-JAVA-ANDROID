package com.example.yahtzee.Model;

import java.util.Date;

public class Logger {

    // *******************************
    // Class Variables
    // *******************************

    /**
     * Singleton instance for storing log messages.
     */
    private static StringBuilder  buffer = new StringBuilder(); ;

    // *******************************
    // Utility Methods
    // *******************************

    /**
     * Logs a message with the current timestamp.
     *
     * @param message The message to log.
     */
    public static void log(String message) {
           String formatMessage = String.format("[%s] %s\n\n", new Date(), message);
           buffer.append(formatMessage);
    }

    /**
     * Retrieves all log messages.
     *
     * @return A string containing all log messages.
     */
    public static String print() {
        return  buffer.toString();
    }
}
