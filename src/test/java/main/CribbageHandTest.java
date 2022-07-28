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

        testPrivateMethod(MULTIPLES, new String[]{"2s", "3d", "4c", "5h", "9c"}, 0);
        testPrivateMethod(MULTIPLES, new String[]{"1s", "3d", "4c", "jh", "2s"}, 0);
        testPrivateMethod(MULTIPLES, new String[]{"2s", "3d", "jc", "5h", "6d"}, 0);
        testPrivateMethod(MULTIPLES, new String[]{"2s", "10d", "4c", "qh", "3s"}, 0);

        // Double cases
        testPrivateMethod(MULTIPLES, new String[]{"2s", "6d", "4c", "5h", "6c"}, 2);
        testPrivateMethod(MULTIPLES, new String[]{"3s", "3d", "4c", "5h", "6h"}, 2);
        testPrivateMethod(MULTIPLES, new String[]{"5s", "3d", "4c", "5h", "6d"}, 2);
        testPrivateMethod(MULTIPLES, new String[]{"js", "3d", "4c", "jh", "1s"}, 2);
        testPrivateMethod(MULTIPLES, new String[]{"qs", "2d", "qc", "5h", "6d"}, 2);

        testPrivateMethod(MULTIPLES, new String[]{"qs", "2d", "qc", "5h", "2s"}, 4);
        testPrivateMethod(MULTIPLES, new String[]{"10s", "10d", "qc", "jh", "jd"}, 4);
        testPrivateMethod(MULTIPLES, new String[]{"1s", "2d", "2c", "3h", "3d"}, 4);
        testPrivateMethod(MULTIPLES, new String[]{"7s", "2d", "7c", "2h", "6d"}, 4);

        // Triple cases
        testPrivateMethod(MULTIPLES, new String[]{"2s", "2d", "7c", "2h", "6d"}, 6);
        testPrivateMethod(MULTIPLES, new String[]{"7s", "7d", "7c", "2h", "6d"}, 6);
        testPrivateMethod(MULTIPLES, new String[]{"1s", "4d", "3c", "1d", "1h"}, 6);

        testPrivateMethod(MULTIPLES, new String[]{"2s", "jd", "2c", "js", "2d"}, 8);
        testPrivateMethod(MULTIPLES, new String[]{"1s", "3d", "3c", "1d", "1h"}, 8);

        // Quad cases
        testPrivateMethod(MULTIPLES, new String[]{"1s", "1d", "1c", "js", "1h"}, 12);
        testPrivateMethod(MULTIPLES, new String[]{"7s", "7d", "7h", "4d", "7c"}, 12);
        testPrivateMethod(MULTIPLES, new String[]{"ks", "10s", "kd", "kh", "kc"}, 12);
    }

    @Test
    void runs() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // No runs cases
        testPrivateMethod(RUNS, new String[]{}, 0);
        testPrivateMethod(RUNS, new String[]{"ks", "10s", "kd", "kh", "kc"}, 0);
        testPrivateMethod(RUNS, new String[]{"2s", "3s", "6d", "5h", "5c"}, 0);
        testPrivateMethod(RUNS, new String[]{"7d", "3s", "6d", "9h", "6c"}, 0);
        testPrivateMethod(RUNS, new String[]{"js", "3d", "kd", "10h", "8c"}, 0);
        testPrivateMethod(RUNS, new String[]{"qs", "3d", "qd", "10h", "8c"}, 0);
        testPrivateMethod(RUNS, new String[]{"ks", "3d", "kd", "10h", "8c"}, 0);

        // Run of three cases
        testPrivateMethod(RUNS, new String[]{"2s", "3d", "4c", "7h", "6h"}, 3);
        testPrivateMethod(RUNS, new String[]{"3s", "3h", "7h", "5d", "6h"}, 3);
        testPrivateMethod(RUNS, new String[]{"qs", "8d", "4c", "jh", "10d"}, 3);
        testPrivateMethod(RUNS, new String[]{"jd", "3c", "4c", "jh", "2s"}, 3);
        testPrivateMethod(RUNS, new String[]{"qs", "4d", "qd", "5s", "6d"}, 3);

        // Run of four cases
        testPrivateMethod(RUNS, new String[]{"2s", "3d", "4c", "7h", "5d"}, 4);
        testPrivateMethod(RUNS, new String[]{"2s", "7c", "5h", "6h", "4d"}, 4);
        testPrivateMethod(RUNS, new String[]{"qs", "kd", "4c", "jh", "10d"}, 4);
        testPrivateMethod(RUNS, new String[]{"6s", "3d", "4d", "5h", "1s"}, 4);
        testPrivateMethod(RUNS, new String[]{"6s", "8d", "qc", "5h", "7d"}, 4);

        // Run of five cases
        testPrivateMethod(RUNS, new String[]{"2s", "3d", "4c", "5h", "6c"}, 5);
        testPrivateMethod(RUNS, new String[]{"qs", "10d", "9c", "kh", "jc"}, 5);
        testPrivateMethod(RUNS, new String[]{"qs", "10d", "9c", "jc", "8d"}, 5);

        // Multiple runs cases
        testPrivateMethod(RUNS, new String[]{"2s", "3d", "4c", "4h", "6c"}, 6);
        testPrivateMethod(RUNS, new String[]{"qs", "10d", "9c", "jc", "9d"}, 8);
        testPrivateMethod(RUNS, new String[]{"10s", "10d", "9c", "10c", "8d"}, 9);
        testPrivateMethod(RUNS, new String[]{"2s", "3d", "3c", "4c", "4d"}, 12);
        testPrivateMethod(RUNS, new String[]{"js", "qd", "kc", "qc", "qh"}, 9);
        testPrivateMethod(RUNS, new String[]{"qs", "10d", "jc", "10c", "qd"}, 12);
    }

    @Test
    void flushes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Note: Last card is the starter

        // No flushes cases
        testPrivateMethod(FLUSHES, new String[]{"5s", "6s", "7s", "8d", "9s"}, 0);
    }

    @Test
    void nobs() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Note: Last card is the starter

        // No nobs cases
        testPrivateMethod(NOBS, new String[]{"5s", "6s", "7s", "8d", "9s"}, 0);
        testPrivateMethod(NOBS, new String[]{"5s", "6s", "7s", "8d", "9s"}, 0);
        testPrivateMethod(NOBS, new String[]{"5s", "6s", "7s", "8d", "9s"}, 0);
        testPrivateMethod(NOBS, new String[]{"5s", "6s", "7s", "8d", "9s"}, 0);
        testPrivateMethod(NOBS, new String[]{"5s", "6s", "7s", "8d", "9s"}, 0);

        // Nobs cases
        final String[] ranks = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k"};
        final char[] suits = {'s', 'c', 'd', 'h'};

        String card, jack;
        for (String rank : ranks) {
            for (char suit : suits) {
                if (rank.equalsIgnoreCase("j")) continue;
                card = rank + suit;
                jack = "j" + suit;

                testPrivateMethod(NOBS, new String[]{jack, "2s", "8s", "jd", card}, 1);
                testPrivateMethod(NOBS, new String[]{"5s", jack, "7s", "qd", card}, 1);
                testPrivateMethod(NOBS, new String[]{"5s", "6s", jack, "jd", card}, 1);
                testPrivateMethod(NOBS, new String[]{"5s", "6s", "7s", jack, card}, 1);
            }
        }
    }

    // See testPrivateMethod
    final Map<Character, Character> suitMaps = Map.of(
            'c', 'd',
            'd', 'h',
            'h', 's',
            's', 'c'
    );

    private void testPrivateMethod(@NotNull testTypes type, Object param, int expected) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        methodCall(type, param, expected);

        // For tests where suits shouldn't matter, run them with all suites to confirm
        if (type == FIFTEENS || type == RUNS || type == MULTIPLES) {
            final String[] values = (String[]) param;
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
    private void methodCall(@NotNull testTypes type, Object param, int expected) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        Class<?> paramType;
        switch (type) {
            case FIFTEENS:
            case RUNS:
            case MULTIPLES:
                paramType = HashSet.class;

                var cards = Arrays.stream((String[]) param).map(Card::stringToCard)
                        .collect(Collectors.toCollection(HashSet::new));
                if (cards.size() != 5 && cards.size() != 0) throw new IllegalArgumentException(
                        "Duplicate or illegal amount of cards present in input: must be 5 or 0");

                if (type == MULTIPLES) {
                    param = cards;
                } else {
                    final Method powerSet = hand.getClass().getDeclaredMethod("powerSet", HashSet.class);
                    powerSet.setAccessible(true);
                    param = powerSet.invoke(hand, cards);
                }

                break;
            case FLUSHES:
            case NOBS:
                paramType = Card.class;

                final var cardStrings = (String[]) param;
                if (cardStrings.length != 5) throw new IllegalArgumentException(
                        "Duplicate or illegal amount of cards present in input: must be 5");

                hand.clear();
                Arrays.stream(Arrays.copyOfRange(cardStrings, 0, 4)).forEach(c -> hand.add(Card.stringToCard(c)));

                param = Card.stringToCard(cardStrings[4]);
                break;
            default:
                throw new IllegalStateException("Impossible state");
        }
        final Method m = hand.getClass().getDeclaredMethod(type.name().toLowerCase(), paramType);
        m.setAccessible(true);

        assertEquals(expected, m.invoke(hand, param));
    }

}
