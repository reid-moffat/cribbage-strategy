package card;

/**
 * The thirteen card ranks in a standard deck of 52 playing cards, ordered with aces low:
 *
 * <ol>
 * <li><code>ACE</code>
 * <li><code>TWO</code>
 * <li><code>THREE</code>
 * <li><code>FOUR</code>
 * <li><code>FIVE</code>
 * <li><code>SIX</code>
 * <li><code>SEVEN</code>
 * <li><code>EIGHT</code>
 * <li><code>NINE</code>
 * <li><code>TEN</code>
 * <li><code>JACK</code>
 * <li><code>QUEEN</code>
 * <li><code>KING</code>
 * </ol>
 */
public enum Rank {

    ACE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING;

    /**
     * Array of all ranks
     * <p>
     * Use of this over Rank.values() is preferred since Rank.values() requires a .clone() call
     * every time it is invoked
     */
    public static final Rank[] values = Rank.values();

    /**
     * Returns the value of this rank with aces low:
     *
     * <ul>
     * <li><code>ACE</code>: 1
     * <li><code>TWO to TEN</code>: Their respective values
     * <li><code>JACK</code>: 11
     * <li><code>QUEEN</code>: 12
     * <li><code>KING</code>: 13
     * </ul>
     *
     * @return the rank place of the rank
     */
    int getRankValue() {
        return this.ordinal() + 1;
    }

}
