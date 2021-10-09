package card;

/**
 * The thirteen card ranks in a standard deck of 52 playing cards
 *
 * <p> Card ranks are ordered as aces low:
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

    /**
     * The rank ace (lowest in this case)
     */
    ACE,

    /**
     * The rank two
     */
    TWO,

    /**
     * The rank three
     */
    THREE,

    /**
     * The rank four
     */
    FOUR,

    /**
     * The rank five
     */
    FIVE,

    /**
     * The rank six
     */
    SIX,

    /**
     * The rank seven
     */
    SEVEN,

    /**
     * The rank eight
     */
    EIGHT,

    /**
     * The rank nine
     */
    NINE,

    /**
     * The rank ten
     */
    TEN,

    /**
     * The rank jack
     */
    JACK,

    /**
     * The rank queen
     */
    QUEEN,

    /**
     * The rank king (highest)
     */
    KING;

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