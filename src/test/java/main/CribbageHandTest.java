package main;

import card.Card;
import card.Rank;
import card.Suit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CribbageHandTest {

    /**
     * All possible card ranks
     */
    private final Rank[] ranks = {Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
            Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING};
    /**
     * All possible card suits
     */
    private final Suit[] suits = {Suit.CLUBS, Suit.DIAMONDS, Suit.HEARTS, Suit.SPADES};

    /**
     * Hand for testing
     */
    private final CribbageHand hand = new CribbageHand();

    @BeforeEach
    void setUp() {
        this.hand.clear();
    }

    @Test
    void add() {
        for (Rank r : ranks) {
            for (Suit s : suits) {
                hand.add(new Card(r, s));
                assertFalse(hand.add(new Card(r, s)));
            }
        }
        assertEquals(hand.size(), 52);
    }

    @Test
    void remove() {
        for (Rank r : ranks) {
            for (Suit s : suits) {
                hand.add(new Card(r, s));
                hand.add(new Card(r, s));
            }
        }
        for (Rank r : ranks) {
            for (Suit s : suits) {
                hand.remove(new Card(r, s));
            }
        }
        assertEquals(hand.size(), 0);
    }

    @Test
    void getCards() {
        // Ensure that only a copy is returned
        hand.add(new Card(Rank.ACE, Suit.CLUBS));
        HashSet<Card> handCopy = hand.getCards();
        handCopy.clear();

        assertEquals(hand.size(), 1);
        assertEquals(handCopy.size(), 0);
    }

    @Test
    void totalPoints() {
        /*
         * Quad fives cases
         */
        final String[] fourFives = {"5s", "5c", "5d", "5h"};
        testHand(fourFives, "10c", 28);
        testHand(fourFives, "jc", 28);
        testHand(fourFives, "qc", 28);
        testHand(fourFives, "kc", 28);
        for (int i = 0; i < 9; ++i) {
            if (i != 4) { // Ace to nine, excluding five (already in the hand)
                testHand(fourFives, "" + (i + 1) + "c", 20);
            }
        }
        // Special case for 29 points
        testHand(new String[]{"5c", "5s", "5d", "jh"}, "5h", 29);


        /*
         * Run cases
         */
        final String[] runOfFour = {"9d", "10d", "jd", "qd"};
        // Run of 5: 5 points, flush of 5: 5 points, nobs: 1 point
        testHand(runOfFour, "kd", 11);
        testHand(runOfFour, "8d", 11);

        // Two runs of 4: 8 points, doubles: 2 points, flush (4): 4 points
        testHand(runOfFour, "9h", 14);
        testHand(runOfFour, "10h", 14);
        testHand(runOfFour, "jh", 14);
        testHand(runOfFour, "qh", 14);

        // Four runs of three: 12 points, two doubles: 4 points
        testHand(new String[]{"10c", "10d", "jd", "qd"}, "qh", 16);
        testHand(new String[]{"10c", "10d", "jd", "qd"}, "jh", 16);

        // Three runs of three: 9 points, triples: 6 points
        testHand(new String[]{"10c", "10d", "10s", "jd"}, "qh", 15);
        testHand(new String[]{"10c", "10d", "10s", "9d"}, "8h", 15);

        /*
         * Multiples cases
         */
        final String[] quad = {"1d", "1s", "1c", "1h"};
        for (int i = 1; i < 10; ++i) {
            // Quad: 12 points (no fifteens, flushes, runs of nobs are possible with 4 aces)
            testHand(quad, "" + (i + 1) + "s", 12);
        }

        // Triple: 6 points, double: 2 points
        testHand(new String[]{"10c", "10d", "10s", "9d"}, "9h", 8);

        /*
         * Fifteens
         */
        // 7 fifteens: 12 points, triple: 6 points, double: 2 points
        testHand(new String[]{"10c", "10d", "5s", "5d"}, "5c", 22);

        // 3 fifteens: 6 points, run: 3 points, doubles: 2 points
        testHand(new String[]{"4c", "5d", "6s", "10d"}, "10h", 11);

        // Just one fifteen: 2 points, quads: 12 points
        testHand(new String[]{"7c", "2d", "2s", "2c"}, "2h", 14);

        // No fifteens, since the highest value is 10 (12 points from quads though)
        testHand(new String[]{"10c", "1d", "1s", "1c"}, "1h", 12);
        testHand(new String[]{"jc", "1d", "1s", "1c"}, "1h", 12);
        testHand(new String[]{"qc", "1d", "1s", "1c"}, "1h", 12);
        testHand(new String[]{"kc", "1d", "1s", "1c"}, "1h", 12);
    }

    /**
     * Tests the result of a cribbage hand
     *
     * @param cards    array of the string representation of four cards
     * @param starter  the starter card for this hand (string representation)
     * @param expected the expected amount of points gained from this hand
     */
    void testHand(String @NotNull [] cards, String starter, int expected) {
        if (cards.length != 4 || starter == null) {
            throw new IllegalArgumentException("Must provide four cards and a starter");
        }

        // Refresh hand
        this.hand.clear();
        for (String card : cards) {
            hand.add(Card.stringToCard(card));
        }

        assertEquals(hand.totalPoints(Card.stringToCard(starter)), expected);
    }

}
