package com.example.yahtzee.Model;

import java.util.Objects;

/**
 * A generic class representing a pair of two objects.
 * Provides methods to get, set, and compare the pair's key and value.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class Pair<K, V> {
    // *******************************
    // Class Variables
    // *******************************

    private K key;
    private V value;

    // *******************************
    // Constructor
    // *******************************

    /**
     * Constructs a Pair with the specified key and value.
     *
     * @param key   The key of the pair.
     * @param value The value of the pair.
     */

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // *******************************
    // Selectors
    // *******************************

    /**
     * Retrieves the key of the pair.
     *
     * @return The key of the pair.
     */
    public K getKey() {
        return key;
    }

    // *******************************
    // Mutators
    // *******************************

    /**
     * Sets the key of the pair.
     *
     * @param key The new key to set.
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Retrieves the value of the pair.
     *
     * @return The value of the pair.
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets the value of the pair.
     *
     * @param value The new value to set.
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the pair.
     *
     * @return A string representing the pair in the format "Pair{key=key, value=value}".
     */
    @Override
    public String toString() {
        return "Pair{" + "key=" + key + ", value=" + value + '}';
    }

    /**
     * Checks if this pair is equal to another object.
     * Two pairs are equal if both their key and value are equal.
     *
     * @param o The object to compare this pair to.
     * @return True if the pairs are equal; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }

    /**
     * Returns the hash code of the pair.
     * The hash code is computed based on the key and value.
     *
     * @return The hash code of the pair.
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
