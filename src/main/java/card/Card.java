package card;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents cards in a standard 52-card playing card deck
 *
 * <p>
 * Each card has a {@code Rank} and {@code Suit}
 *
 * @author Reid Moffat
 */
public final class Card implements PlayingCard {

    /**
     * All card ranks in order
     *
     * <p>
     * Ranks are ordered with aces low (ace, two, three, ..., ten, jack, queen, king)
     */
    public static final Rank[] RANKS = Rank.values();

    /**
     * All card suits in alphabetical order
     */
    public static final Suit[] SUITS = Suit.values();

    /**
     * This card's rank enum (ACE, TWO, THREE, ..., QUEEN or KING)
     */
    private final Rank rank;

    /**
     * This card's suit enum (CLUBS, DIAMONDS, HEARTS or SPADES)
     */
    private final Suit suit;

    /**
     * Initializes this card with a rank and suit
     *
     * @param rank the card's {@code Rank} enum
     * @param suit the card's {@code Suit} enum
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Checks if a string represents a playing card and returns it if it does
     *
     * @param card a string that represents a playing card
     * @return a {@code Card} object with the specified rank and suit if the parameter is valid;
     * null otherwise
     */
    public static @Nullable Card validCard(String card) {
        card = card.trim().toUpperCase();
        if (!card.matches("^(10|[1-9JQK])[CDHS]$")) { // Ignore invalid cards
            return null;
        }

        Rank rank;
        switch (card.substring(0, card.length() - 1)) { // Determine the card's rank
            case "J":
                rank = RANKS[10]; // Jack is the 11th value i.e. index 10
                break;
            case "Q":
                rank = RANKS[11];
                break;
            case "K":
                rank = RANKS[12];
                break;
            default:
                int value = Integer.parseInt(card.substring(0, card.length() - 1));
                rank = RANKS[value - 1];
                break;
        }

        switch (card.charAt(card.length() - 1)) { // Determine the card's suit
            case 'C':
                return new Card(rank, Suit.CLUBS);
            case 'D':
                return new Card(rank, Suit.DIAMONDS);
            case 'H':
                return new Card(rank, Suit.HEARTS);
            default:
                return new Card(rank, Suit.SPADES);
        }
    }

    /**
     * Returns this card's rank
     *
     * @return this card's rank
     */
    @Override
    public Rank getRank() {
        return this.rank;
    }

    /**
     * Returns this card's suit
     *
     * @return this card's suit
     */
    @Override
    public Suit getSuit() {
        return this.suit;
    }

    /**
     * Returns the rank number of this card
     *
     * <p>
     * Rank numbers are as follows:
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
     *
     * @return the rank place of this card
     */
    public int getRankNumber() {
        return this.rank.getRankValue();
    }

    /**
     * Returns the number of ranks this card is above the card it is compared to
     *
     * <p>
     * Aces are low and negative values mean the card rank is below
     *
     * <p>
     * Examples:
     *
     * <ul>
     * <li>Comparing a jack to a five returns 6</li>
     * <li>Comparing an ace to a six returns -5</li>
     * <li>Comparing a nine to a king returns -4</li>
     * </ul>
     *
     * @return the number of ranks the current card is above the other card
     */
    @Override
    public int compareTo(@NotNull Card other) {
        return this.getRankNumber() - other.getRankNumber();
    }

    /**
     * Returns a hash code value for this {@code Card}
     */
    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    /**
     * Returns true if both {@code Cards} have the same {@code Rank} and
     * {@code Suit}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Card)) {
            return false;
        }
        Card other = (Card) obj;
        return rank == other.rank && suit == other.suit;
    }

    /**
     * Returns the English description this card
     *
     * <p>
     * The structure of the string is "<i>Rank</i> of <i>suit</i>"
     *
     * <p>
     * Examples:
     * <ul>
     * <li>"Ace of spades"</li>
     * <li>"Three of clubs"</li>
     * <li>"Eight of hearts"</li>
     * <li>"Queen of diamonds"</li>
     * </ul>
     */
    @Override
    public @NotNull String toString() {
        String rankString = rank.toString();
        return rankString.charAt(0) // First letter of the rank (a capital letter)
                + rankString.substring(1).toLowerCase() // Rest of the rank (lower case)
                + " of " + suit.toString().toLowerCase(); // Suit (lowercase)
    }

}
