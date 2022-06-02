package main;

import card.Card;
import card.Rank;
import card.Suit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static main.CribbageHandTest.testTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CribbageHandTest {

    private static Set<Card> allCards;
    private CribbageHand hand;

    @BeforeAll
    static void setUpClass() {
        allCards = IntStream.range(0, 52).mapToObj(i -> new Card(Rank.values[i % 13],
                Suit.values[i / 13])).collect(Collectors.toUnmodifiableSet());

        assertEquals(Rank.values().length, 13);
        assertEquals(Suit.values().length, 4);
        assertEquals(allCards.size(), 52);
    }

    @BeforeEach
    void setUp() {
        hand = new CribbageHand();
        hand.clear();
        assertEquals(allCards.size(), 52);
    }

    @AfterEach
    void tearDown() {
        hand.clear();
        assertEquals(allCards.size(), 52);
    }

    @Test
    void add() {
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                hand.add(new Card(r, s));
                assertFalse(hand.add(new Card(r, s)));
            }
        }

        allCards.forEach(c -> assertFalse(hand.add(c)));
    }

    @Test
    void remove() {
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                hand.add(new Card(r, s));
                hand.add(new Card(r, s));
            }
        }
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                hand.remove(new Card(r, s));
            }
        }
    }

    @Test
    void getCards() {
        // Ensure that only a copy is returned
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                hand.add(new Card(r, s));
                HashSet<Card> handCopy = hand.getCards();
                handCopy.clear();

                assertEquals(hand.size(), 1);
                assertEquals(handCopy.size(), 0);

                hand.clear();
            }
        }
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
    private void testHand(String @NotNull [] cards, String starter, int expected) {
        if (cards.length != 4 || starter == null) {
            throw new IllegalArgumentException("Must provide four cards and a starter");
        }

        // Refresh hand
        this.hand.clear();
        for (String card : cards) {
            hand.add(Card.stringToCard(card));
        }

        assertEquals(hand.totalPoints(Card.stringToCard(starter)), expected,
                Arrays.toString(cards) + " " + starter + " " + expected);
    }

    enum testTypes {FIFTEENS, MULTIPLES, RUNS, FLUSHES, NOBS}

    @Test
    void fifteens() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // No fifteens cases
        testPrivateMethod(FIFTEENS, new String[]{}, 0);

        testPrivateMethod(FIFTEENS, new String[]{"1s", "1c", "1d", "1h", "2s"}, 0);
        testPrivateMethod(FIFTEENS, new String[]{"7s", "7c", "7d", "7h", "2d"}, 0);
        testPrivateMethod(FIFTEENS, new String[]{"2s", "4c", "6d", "8h", "10s"}, 0);
        testPrivateMethod(FIFTEENS, new String[]{"2s", "2c", "6d", "6h", "2h"}, 0);
        testPrivateMethod(FIFTEENS, new String[]{"1s", "2c", "3d", "4h", "4c"}, 0);
        testPrivateMethod(FIFTEENS, new String[]{"4s", "6c", "4d", "4h", "4c"}, 0);
        testPrivateMethod(FIFTEENS, new String[]{"7s", "2s", "kd", "jh", "qc"}, 0);

        // One fifteen
        testPrivateMethod(FIFTEENS, new String[]{"1s", "1c", "1d", "5h", "10s"}, 2);
        testPrivateMethod(FIFTEENS, new String[]{"1s", "1c", "1d", "5h", "jd"}, 2);
        testPrivateMethod(FIFTEENS, new String[]{"1s", "1c", "1d", "5h", "qh"}, 2);
        testPrivateMethod(FIFTEENS, new String[]{"1s", "1c", "1d", "5h", "kc"}, 2);

        testPrivateMethod(FIFTEENS, new String[]{"4s", "4c", "5d", "5h", "5c"}, 2);
        testPrivateMethod(FIFTEENS, new String[]{"2s", "4c", "5d", "5h", "5c"}, 2);
        testPrivateMethod(FIFTEENS, new String[]{"7s", "2s", "6d", "jh", "qc"}, 2);

        // Multiple fifteens
        testPrivateMethod(FIFTEENS, new String[]{"5s", "5c", "5d", "5h", "10s"}, 16);
        testPrivateMethod(FIFTEENS, new String[]{"5s", "5c", "5d", "5h", "js"}, 16);
        testPrivateMethod(FIFTEENS, new String[]{"5s", "5c", "5d", "5h", "qs"}, 16);
        testPrivateMethod(FIFTEENS, new String[]{"5s", "5c", "5d", "5h", "ks"}, 16);
        testPrivateMethod(FIFTEENS, new String[]{"7s", "7c", "7d", "7h", "1s"}, 12);
        testPrivateMethod(FIFTEENS, new String[]{"6s", "4c", "5d", "5h", "5c"}, 8);

        testPrivateMethod(FIFTEENS, new String[]{"9s", "5s", "5d", "5h", "5c"}, 8);
        testPrivateMethod(FIFTEENS, new String[]{"4s", "5s", "5d", "5h", "5c"}, 8);
        testPrivateMethod(FIFTEENS, new String[]{"6s", "5s", "5d", "5h", "5c"}, 8);
        testPrivateMethod(FIFTEENS, new String[]{"1s", "5s", "5d", "5h", "5c"}, 8);

        testPrivateMethod(FIFTEENS, new String[]{"7s", "7h", "7d", "8h", "8c"}, 12);
        testPrivateMethod(FIFTEENS, new String[]{"7s", "1s", "7d", "8h", "8c"}, 10);
        testPrivateMethod(FIFTEENS, new String[]{"7s", "2s", "7d", "8h", "8c"}, 8);
    }

    @Test
    void multiples() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // No multiples cases
        testPrivateMethod(MULTIPLES, new String[]{}, 0);
    }

    @Test
    void runs() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // No runs cases
        testPrivateMethod(RUNS, new String[]{}, 0);
    }

    @Test
    void flushes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // No flushes cases
        for (Card c : allCards) {
            testPrivateMethod(FLUSHES, c, 0);
        }
    }

    @Test
    void nobs() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // No nobs cases
        for (Card c : allCards) {
            testPrivateMethod(NOBS, c, 0);
        }
    }

    private void testPrivateMethod(@NotNull testTypes type, Object param, int expected) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        methodCall(type, param, expected);

        // For tests where suits don't matter, run them with the suits cycled through to increase
        // coverage
        if (type == FIFTEENS || type == RUNS || type == MULTIPLES) {
            final Map<Character, Character> suitMaps = Map.of(
                    'c', 'd',
                    'd', 'h',
                    'h', 's',
                    's', 'c'
            );

            String[] values = (String[]) param;
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < values.length; ++j) {
                    String val = values[j];
                    int len = val.length();
                    values[j] = val.substring(0, len - 1) + suitMaps.get(val.charAt(len - 1));
                }
                methodCall(type, param, expected);
            }
        }
    }

    /**
     * Do not call directly
     */
    private void methodCall(testTypes type, Object param, int expected) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        final Method powerSet = hand.getClass().getDeclaredMethod("powerSet", HashSet.class);
        powerSet.setAccessible(true);

        Class<?> paramType;
        switch (type) {
            case FIFTEENS:
                paramType = HashSet.class;
                var cards = Arrays.stream((String[]) param).map(Card::stringToCard)
                        .collect(Collectors.toCollection(HashSet::new));
                if (cards.size() != 5 && cards.size() != 0) throw new IllegalArgumentException(
                        "Duplicate or illegal amount of cards present in input");
                param = powerSet.invoke(hand, cards);
                break;
            case MULTIPLES:
            case RUNS:
                paramType = HashSet.class;
                param = new HashSet<>(List.of((String[]) param));
                break;
            case FLUSHES:
            case NOBS:
                paramType = Card.class;
                break;
            default:
                throw new IllegalStateException("Impossible state");
        }
        final Method m = hand.getClass().getDeclaredMethod(type.name().toLowerCase(), paramType);
        m.setAccessible(true);

        assertEquals(m.invoke(hand, param), expected);
    }

}
