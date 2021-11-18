package card;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    private final Rank[] ranks = RankTest.ranks;
    private final Suit[] suits = SuitTest.suits;

    @Test
    void getRank() {
        Card c;
        for (Rank r : ranks) {
            for (Suit s : suits) {
                c = new Card(r, s);
                assertEquals(c.getRank(), r);
            }
        }
    }

    @Test
    void getSuit() {
        Card c;
        for (Rank r : ranks) {
            for (Suit s : suits) {
                c = new Card(r, s);
                assertEquals(c.getSuit(), s);
            }
        }
    }

    @Test
    void compareTo() {
        Random r = new Random();
        for (int i = 0; i < 1000; ++i) {
            int[] randomRanks = r.ints(2, 0, ranks.length - 1).toArray();
            int rank1 = randomRanks[0];
            int rank2 = randomRanks[1];
            Card c1 = new Card(ranks[rank1], Suit.CLUBS);
            Card c2 = new Card(ranks[rank2], Suit.CLUBS);

            assertEquals(c1.compareTo(c2), rank1 - rank2);
        }
    }

    @Test
    void testHashCode() {
        // Test that the hashCode value for a specific suit is constant
        // no matter when it is calculated or what variable it is from
        int hashCode1, hashCode2;
        Card c1, c2;

        for (Rank r : ranks) {
            for (Suit s : suits) {
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
    void testEquals() {

    }

    @Test
    void testToString() {
        assertEquals(new Card(Rank.ACE, Suit.CLUBS).toString(), "Ace of clubs");
        assertEquals(new Card(Rank.ACE, Suit.SPADES).toString(), "Ace of spades");
        assertEquals(new Card(Rank.TWO, Suit.SPADES).toString(), "Two of spades");
        assertEquals(new Card(Rank.TWO, Suit.DIAMONDS).toString(), "Two of diamonds");
        assertEquals(new Card(Rank.FOUR, Suit.HEARTS).toString(), "Four of hearts");
        assertEquals(new Card(Rank.EIGHT, Suit.DIAMONDS).toString(), "Eight of diamonds");
        assertEquals(new Card(Rank.QUEEN, Suit.CLUBS).toString(), "Queen of clubs");
        assertEquals(new Card(Rank.KING, Suit.HEARTS).toString(), "King of hearts");
    }
}
