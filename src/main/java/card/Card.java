package card;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * An immutable playing card in a standard 52-card deck
 *
 * @author Reid Moffat
 */
public final class Card implements Comparable<Card> {

    /**
     * This card's rank enum (ACE, TWO, THREE, ..., QUEEN or KING)
     */
    public final Rank rank;

    /**
     * This card's suit enum (CLUBS, DIAMONDS, HEARTS or SPADES)
     */
    public final Suit suit;

    /**
     * Initializes this card with a rank and suit
     */
    public Card(@NotNull Rank rank, @NotNull Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Checks if a String represents a valid card, and returns the {@code Card} object that it
     * represents if it does
     *
     * <p> A valid card string is the rank (1-10, j, q or k) of the card followed by the first
     * letter of the suit (neither are case-sensitive). Examples:
     *
     * <ul>
     * <li>"3d": Three of diamonds</li>
     * <li>"jS": Jack of spades</li>
     * <li>"10c": Ten of clubs</li>
     * <li>"1H": Ace of hearts</li>
     * </ul>
     *
     * @param card a string that represents a playing card
     * @return a {@code Card} object with the specified rank and suit if the parameter is valid
     * @throws IllegalArgumentException if the string does not represent a valid card
     */
    public static @NotNull Card stringToCard(String card) {
        card = card.trim().toUpperCase();
        if (!card.matches("^(10|[1-9JQK])[CDHS]$")) {
            throw new IllegalArgumentException("Card string '" + card + "' is invalid, it must " +
                    "match (10|[1-9JQK])[CDHS]");
        }

        Rank rank;
        String rankString = card.substring(0, card.length() - 1);
        switch (rankString) { // Translate the card's rank
            case "J":
                rank = Rank.JACK;
                break;
            case "Q":
                rank = Rank.QUEEN;
                break;
            case "K":
                rank = Rank.KING;
                break;
            default:
                rank = Rank.values[Integer.parseInt(rankString) - 1];
                break;
        }

        switch (card.charAt(card.length() - 1)) { // Translate the card's suit
            case 'C':
                return new Card(rank, Suit.CLUBS);
            case 'D':
                return new Card(rank, Suit.DIAMONDS);
            case 'H':
                return new Card(rank, Suit.HEARTS);
            case 'S':
                return new Card(rank, Suit.SPADES);
            default:
                throw new IllegalStateException("Program is in an impossible state: " + card);
        }
    }

    /**
     * Returns the rank number of this card with aces low
     *
     * <p> Rank numbers are as follows:
     *
     * <ul>
     * <li><code>ACE: 1</code>
     * <li><code>TWO to TEN: Their respective number</code>
     * <li><code>JACK: 11</code>
     * <li><code>QUEEN: 12</code>
     * <li><code>KING: 13</code>
     * </ul>
     *
     * @return the rank place of this card
     */
    public int getRankNumber() {
        return this.rank.ordinal() + 1;
    }

    /**
     * Returns the number of ranks this card is above the card it is compared to
     *
     * <p>Aces are low and negative values mean the card rank is below
     *
     * <p> Examples:
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
     * Returns true if both {@code Cards} have the same {@code Rank} and {@code Suit}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Card)) {
            return false;
        }
        final Card other = (Card) obj;
        return rank == other.rank && suit == other.suit;
    }

    /**
     * Returns the English description this card
     *
     * <p> The structure of the string is "<i>Rank</i> of <i>suit</i>"
     *
     * <p> Examples:
     * <ul>
     * <li>"Ace of spades"</li>
     * <li>"Three of clubs"</li>
     * <li>"Eight of hearts"</li>
     * <li>"Queen of diamonds"</li>
     * </ul>
     */
    @Override
    public @NotNull String toString() {
        final String rankString = rank.toString();
        return rankString.charAt(0) // First letter of the rank (a capital letter)
                + rankString.substring(1).toLowerCase() // Rest of the rank (lower case)
                + " of " + suit.toString().toLowerCase(); // Suit (lowercase)
    }

}
