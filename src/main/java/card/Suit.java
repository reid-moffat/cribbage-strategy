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
    /**
     * The suit clubs
     */
    CLUBS,

    /**
     * The suit diamonds
     */
    DIAMONDS,

    /**
     * The suit hearts
     */
    HEARTS,

    /**
     * The suit spades
     */
    SPADES;

    /**
     * Array of all suits
     * <p>
     * By storing this as a field, the overhead is decreased since each call of Rank.values()
     * requires a .clone() call every time it is invoked
     */
    public static Suit[] values = Suit.values();

}
