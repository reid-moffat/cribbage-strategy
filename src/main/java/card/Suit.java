package card;

/**
 * The four suits in a standard deck of 52 playing cards ordered alphabetically:
 *
 * <ol>
 * <li><code>CLUBS</code></li>
 * <li><code>DIAMONDS</code></li>
 * <li><code>HEARTS</code></li>
 * <li><code>SPADES</code></li>
 * </ol>
 */
public enum Suit {

    CLUBS,
    DIAMONDS,
    HEARTS,
    SPADES;

    /**
     * Array of all suits
     * <p>
     * Use of this over Suit.values() is preferred since Suit.values() requires a .clone() call
     * every time it is invoked
     */
    public static final Suit[] values = Suit.values();

}
