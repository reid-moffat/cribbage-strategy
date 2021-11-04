package card;

/**
 * Contains the required methods for a mutable playing card. Includes methods to compare cards,
 * modify the card as well as get the card's rank, suit and rank number
 *
 * @author Reid Moffat
 */
public interface PlayingCard extends Comparable<Card> {

    /**
     * Returns this card's rank
     *
     * @return this card's rank
     */
    Rank getRank();

    /**
     * Returns this card's suit
     *
     * @return this card's suit
     */
    Suit getSuit();

    /**
     * Returns the rank place of this card
     *
     * @return the rank place of this card
     */
    int getRankNumber();
}
