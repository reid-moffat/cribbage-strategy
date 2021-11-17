package card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RankTest {

    private final Rank[] ranks = {Rank.ACE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX,
            Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING};

    @Test
    void testToString() {
        final String[] names = {"ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT",
                "NINE", "TEN", "JACK", "QUEEN", "KING"};
        for (int i = 0; i < ranks.length; ++i) {
            assertEquals(ranks[i].toString(), names[i]);
        }
    }

    @Test
    void testEquals() {
        Rank r1 = Rank.ACE;
        Rank r2 = Rank.ACE;
        Rank r3 = Rank.TWO;

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertNotEquals(r2, r3);
        r3 = Rank.ACE;

        for (int i = 1; i < ranks.length; ++i) {
            r1 = ranks[i];
            r2 = ranks[i];

            assertEquals(r1, r2);
            assertNotEquals(r1, r3);
            assertNotEquals(r2, r3);
        }
    }

    @Test
    void testHashCode() {
        // Test that the hashCode value for a specific rank is constant
        // no matter when it is calculated or what variable it is from
        int hashCode1, hashCode2;
        Rank r2;

        for (Rank r : ranks) {
            hashCode1 = r.hashCode();
            r2 = r;
            hashCode2 = r.hashCode();

            assertEquals(hashCode1, hashCode2);
            assertEquals(hashCode1, r2.hashCode());
        }
    }

    @Test
    void getRankValue() {
        for (int i = 0; i < ranks.length; ++i) {
            assertEquals(ranks[i].getRankValue(), i + 1);
        }
    }

}
