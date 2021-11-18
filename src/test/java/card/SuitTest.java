package card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SuitTest {

    static final Suit[] suits = {Suit.CLUBS, Suit.DIAMONDS, Suit.HEARTS, Suit.SPADES};

    @Test
    void testToString() {
        final String[] names = {"CLUBS", "DIAMONDS", "HEARTS", "SPADES"};
        for (int i = 0; i < suits.length; ++i) {
            assertEquals(suits[i].toString(), names[i]);
        }
    }

    @Test
    void testEquals() {
        Suit s1 = Suit.CLUBS;
        Suit s2 = Suit.CLUBS;
        Suit s3 = Suit.DIAMONDS;

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertNotEquals(s2, s3);
        s3 = Suit.CLUBS;

        for (int i = 1; i < suits.length; ++i) {
            s1 = suits[i];
            s2 = suits[i];

            assertEquals(s1, s2);
            assertNotEquals(s1, s3);
            assertNotEquals(s2, s3);
        }
    }

    @Test
    void testHashCode() {
        // Test that the hashCode value for a specific suit is constant
        // no matter when it is calculated or what variable it is from
        int hashCode1, hashCode2;
        Suit s2;

        for (Suit s : suits) {
            hashCode1 = s.hashCode();
            s2 = s;
            hashCode2 = s.hashCode();

            assertEquals(hashCode1, hashCode2);
            assertEquals(hashCode1, s2.hashCode());
        }
    }
}
