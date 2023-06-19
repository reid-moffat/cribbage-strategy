package card;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private static Set<Card> allCards;

    @BeforeAll
    static void setUpClass() {
        allCards = IntStream.range(0, 52).mapToObj(i -> new Card(Rank.values[i % 13],
                Suit.values[i / 13])).collect(Collectors.toUnmodifiableSet());
        assertEquals(allCards.size(), 52);
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
                assertEquals(c.rank, r);
                assertEquals(c.suit, s);
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

        // Checks random invalid cases of a rank and suit combination
        final int[] invalidSuits = IntStream.range(0, Character.MAX_VALUE + 1)
                .filter(i -> Math.random() < 0.01 && !suitStrings.contains((char) i)).toArray();
        final int[] invalidRanks = IntStream.range(0, Character.MAX_VALUE + 1)
                .filter(i -> Math.random() < 0.01 && !rankStrings.contains(Character.toString(i))).toArray();

        for (int rank : invalidRanks) {
            for (int suit : invalidSuits) {
                assertThrows(IllegalArgumentException.class,
                        () -> Card.stringToCard(Character.toString(rank) + suit),
                        "Card should not be legal: " + Character.toString(rank) + suit);
            }
        }
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
        Card x, y, z;
        for (Rank r : Rank.values) {
            for (Suit s : Suit.values) {
                x = new Card(r, s);
                y = new Card(r, s);
                z = new Card(y.rank, x.suit);
                assertTrue(x.equals(y) && y.equals(x) && x.equals(z) && z.equals(x)
                        && y.equals(z) && z.equals(y), "" + x + " " + y + " " + z);
                assertTrue(x.hashCode() == y.hashCode() && y.hashCode() == z.hashCode(),
                        "" + x + " " + y + " " + z);
            }
        }
    }

    @Test
    void cardEquals() {
        // Equals contract:
        // 1. x.equals(y) is false if y is null (x == null would cause a nullPointerException)
        allCards.forEach(card -> assertNotEquals(null, card));

        // 2. x.equals(y) is false if x and y are not of the same object type
        HashSet<Object> differentTypes = new HashSet<>(List.of(Rank.values));
        differentTypes.addAll(List.of(Suit.values));
        allCards.forEach(card -> differentTypes.forEach(t -> assertNotEquals(card, t)));

        // 3. x.equals(y) is false if x and y are of the same class, but semantically different
        allCards.forEach(card1 -> allCards.forEach(card2 -> {
            if (card1.rank != card2.rank || card1.suit != card2.suit) {
                assertNotEquals(card1, card2);
            }
        }));

        // 4. x.equals(y) is true if x and y are of the same type and semantically equal
        allCards.forEach(card -> assertEquals(card, new Card(card.rank, card.suit)));
        allCards.forEach(card1 -> allCards.forEach(card2 -> {
            if (card1.rank == card2.rank && card1.suit == card2.suit) {
                assertEquals(card1, card2);
            }
        }));
    }

    @Test
    void cardToString() {
        assertEquals(new Card(Rank.ACE, Suit.CLUBS).toString(), "Ace of clubs");
        assertEquals(new Card(Rank.ACE, Suit.SPADES).toString(), "Ace of spades");
        assertEquals(new Card(Rank.TWO, Suit.HEARTS).toString(), "Two of hearts");
        assertEquals(new Card(Rank.TWO, Suit.DIAMONDS).toString(), "Two of diamonds");
        assertEquals(new Card(Rank.THREE, Suit.CLUBS).toString(), "Three of clubs");
        assertEquals(new Card(Rank.FOUR, Suit.HEARTS).toString(), "Four of hearts");
        assertEquals(new Card(Rank.FIVE, Suit.SPADES).toString(), "Five of spades");
        assertEquals(new Card(Rank.SIX, Suit.CLUBS).toString(), "Six of clubs");
        assertEquals(new Card(Rank.SEVEN, Suit.SPADES).toString(), "Seven of spades");
        assertEquals(new Card(Rank.EIGHT, Suit.DIAMONDS).toString(), "Eight of diamonds");
        assertEquals(new Card(Rank.NINE, Suit.HEARTS).toString(), "Nine of hearts");
        assertEquals(new Card(Rank.TEN, Suit.DIAMONDS).toString(), "Ten of diamonds");
        assertEquals(new Card(Rank.JACK, Suit.SPADES).toString(), "Jack of spades");
        assertEquals(new Card(Rank.QUEEN, Suit.CLUBS).toString(), "Queen of clubs");
        assertEquals(new Card(Rank.KING, Suit.HEARTS).toString(), "King of hearts");

        allCards.forEach(card ->
                assertTrue(card.toString().matches("[A-T][a-x]{2,4} of [a-u]{5,8}"), card.toString()));
    }
}
