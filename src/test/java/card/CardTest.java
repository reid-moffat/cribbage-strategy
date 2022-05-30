package card;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private static Set<Card> allCards;

    @BeforeAll
    static void setUpClass() {
        allCards = IntStream.range(0, 52).mapToObj(i -> new Card(Rank.values[i % 13],
                Suit.values[i / 13])).collect(Collectors.toUnmodifiableSet());
    }

    @Test
    void rankToString() {
        final String[] names = {"ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT",
                "NINE", "TEN", "JACK", "QUEEN", "KING"};
        for (int i = 0; i < Rank.values.length; ++i) {
            assertEquals(Rank.values[i].toString(), names[i]);
        }
    }

    @Test
    void suitToString() {
        final String[] names = {"CLUBS", "DIAMONDS", "HEARTS", "SPADES"};
        for (int i = 0; i < Suit.values.length; ++i) {
            assertEquals(Suit.values[i].toString(), names[i]);
        }
    }

    @Test
    void getData() {
        Card c;
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                c = new Card(r, s);
                assertEquals(c.getRank(), r);
                assertEquals(c.getSuit(), s);
            }
        }
    }

    @Test
    void testStringToCard() {
        // Valid ranks and suits
        final List<String> rankStrings = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9",
                "10", "J", "Q", "K", "j", "q", "k");
        final List<Character> suitStrings = Arrays.asList('c', 'd', 'h', 's', 'C', 'D', 'H', 'S');

        // Valid cases
        for (int i = 0; i < rankStrings.size(); ++i) {
            for (int j = 0; j < suitStrings.size(); ++j) {
                // stringToCard has parameter checking included
                Card fromString = Card.stringToCard(rankStrings.get(i) + suitStrings.get(j));
                Card fromEnum = new Card(Rank.values[i < 13 ? i : i - 3], Suit.values[j < 4 ? j :
                        j - 4]);

                assertEquals(fromString, fromEnum);
            }
        }

        // Checks ~107k (~(2^16/200)^2) random invalid cases of a rank and suit combination
        int[] invalidSuits = IntStream.range(0, Character.MAX_VALUE + 1)
                .filter(i -> Math.random() < 0.005 && !suitStrings.contains((char) i)).toArray();
        IntStream.range(0, Character.MAX_VALUE + 1)
                .filter(i -> Math.random() < 0.005 && !rankStrings.contains(Character.toString(i)))
                .forEach(rank -> {
                    for (int suit : invalidSuits) {
                        assertThrows(IllegalArgumentException.class,
                                () -> Card.stringToCard(Character.toString(rank) + suit),
                                "Card should not be legal: " + Character.toString(rank) + suit);
                    }
                });
    }

    @Test
    void rankNumber() {
        Card c;
        for (int i = 0; i < Rank.values.length; ++i) {
            for (Suit s : Suit.values) {
                c = new Card(Rank.values[i], s);
                assertEquals(i + 1, c.getRankNumber());
            }
        }
    }

    @Test
    void compareTo() {
        allCards.forEach(card1 -> allCards.forEach(card2 -> assertEquals(card1.compareTo(card2),
                card1.getRankNumber() - card2.getRankNumber())));
    }

    @Test
    void testHashCode() {
        // Test that the hashCode value for a specific suit is constant
        // no matter when it is calculated or what variable it is from
        int hashCode1, hashCode2;
        Card c1, c2;

        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                c1 = new Card(r, s);
                hashCode1 = c1.hashCode();
                c2 = c1;
                hashCode2 = c1.hashCode();

                assertEquals(hashCode1, hashCode2);
                assertEquals(hashCode1, c2.hashCode());
            }
        }
    }

    @Test
    void cardEquals() {
        // Equals contract:
        // 1. x.equals(y) is false if y is null (x == null would cause a nullPointerException)
        allCards.forEach(card -> assertNotEquals(null, card));

        // 2. x.equals(y) is false if x and y are not of the same object type
        HashSet<Object> differentTypes = new HashSet<>();
        differentTypes.add(Rank.values);
        differentTypes.add(Suit.values);
        allCards.forEach(card -> differentTypes.forEach(t -> assertNotEquals(card, t)));

        // 3. x.equals(y) is false if x and y are of the same class, but semantically different
        allCards.forEach(card1 -> allCards.forEach(card2 -> {
            if (!(card1 == card2)) {
                assertNotEquals(card1, card2);
            }
        }));

        // 4. x.equals(y) is true if x and y are of the same type and semantically equal
        allCards.forEach(card -> assertEquals(card, new Card(card.getRank(), card.getSuit())));
    }

    @Test
    void cardToString() {
        assertEquals(new Card(Rank.ACE, Suit.CLUBS).toString(), "Ace of clubs");
        assertEquals(new Card(Rank.ACE, Suit.SPADES).toString(), "Ace of spades");
        assertEquals(new Card(Rank.TWO, Suit.SPADES).toString(), "Two of spades");
        assertEquals(new Card(Rank.TWO, Suit.DIAMONDS).toString(), "Two of diamonds");
        assertEquals(new Card(Rank.FOUR, Suit.HEARTS).toString(), "Four of hearts");
        assertEquals(new Card(Rank.EIGHT, Suit.DIAMONDS).toString(), "Eight of diamonds");
        assertEquals(new Card(Rank.QUEEN, Suit.CLUBS).toString(), "Queen of clubs");
        assertEquals(new Card(Rank.KING, Suit.HEARTS).toString(), "King of hearts");

        Card c;
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                c = new Card(r, s);
                assertTrue(c.toString().matches("[A-T][a-x]{2,4} of [a-u]{5,8}"), c.toString());
            }
        }
    }
}
