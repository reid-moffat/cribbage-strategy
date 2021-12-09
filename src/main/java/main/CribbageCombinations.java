package main;

import card.Card;

import java.util.HashSet;

/**
 * Includes the required methods for a class that can:
 *
 * <ul>
 * <li>Store a cribbage hand of cards</li>
 * <li>Add or remove cards from the hand</li>
 * <li>Get the size of the hand</li>
 * <li>Calculate the number of cribbage points in the hand with a starter card</li>
 * </ul>
 *
 * @author Reid Moffat
 */
interface CribbageCombinations {

    /**
     * Adds a {@code Card} object to this hand
     *
     * @param card a {@code Card} object
     * @return if the card was successfully added (the card is not currently in the hand)
     */
    boolean add(Card card);

    /**
     * Removes a {@code Card} object from this hand
     *
     * @param card a {@code Card} object
     */
    void remove(Card card);

    /**
     * Returns the number of {@code Card} objects in this hand
     *
     * @return the number of {@code Card} objects in this hand
     */
    int size();

    /**
     * Returns a copy of this hand
     *
     * @return a copy of this hand
     */
    HashSet<Card> getCards();

    /**
     * Calculates the sum of point combinations for a valid cribbage hand plus
     * starter {@code Card}
     *
     * @param starter the starter {@code Card}
     * @return the total number of points in this cribbage hand with the given
     * starter {@code Card}
     */
    int totalPoints(Card starter);

}
