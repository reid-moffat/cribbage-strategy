package main;

import card.Card;
import card.Rank;
import card.Suit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The class {@code CribbageHand} represents a player's hand in the game of
 * cribbage
 *
 * <p> Initializing this class requires a {@code HashSet} of {@code Card} objects.
 * The hand's {@code Cards} can be modified using the methods {@code setHand},
 * {@code add}, {@code clear} and {@code remove}
 *
 * <p> The total points obtained from this cribbage hand (in the counting phase)
 * can be calculated by passing a starter {@code Card} object through
 * {@code totalPoints} if this hand has four {@code Card} objects
 *
 * @author Reid Moffat
 */
final class CribbageHand implements CribbageCombinations {

    /**
     * A set of unique playing {@code Cards}. Must include 4 {@code Cards} for
     * points to be calculated
     *
     * <p> Does not include the starter {@code Card}
     */
    private HashSet<Card> hand;

    /**
     * The starter {@code Card}
     *
     * <p> Kept separate from the {@code Card} hand due to points from runs and nobs
     */
    private Card starter;

    /**
     * A set that includes the hand of four {@code Card} objects and the starter
     * {@code Card}
     */
    private HashSet<Card> handWithStarter;

    /**
     * Every combination of {@code Card} objects in the hand and starter
     * {@code Card}
     *
     * <p> More formally, the power set of {@code handWithStarter}
     */
    private HashSet<HashSet<Card>> cardCombinations;

    /**
     * Initializes this {@code CribbageHand} with a set of {@code Cards}
     *
     * @param hand a {@code Set} of {@code Card} objects (not including the starter
     *             card)
     */
    public CribbageHand(HashSet<Card> hand) {
        this.hand = hand;
    }

    /**
     * Returns the power set of a given {@code HashSet}
     *
     * <p> Adapted from <a href=
     * "https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java"> stack overflow</a>
     *
     * @param originalSet a {@code HashSet} of objects
     * @param <T>         the type of objects in {@code originalSet}
     * @return a {@code HashSet} containing all subsets of {@code originalSet}
     */
    private static <T> HashSet<HashSet<T>> powerSet(HashSet<T> originalSet) {
        HashSet<HashSet<T>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<T> list = new ArrayList<>(originalSet);
        T head = list.get(0);
        HashSet<T> rest = new HashSet<>(list.subList(1, list.size()));
        for (HashSet<T> set : powerSet(rest)) {
            HashSet<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    /**
     * Sets the cribbage hand to a copy of the specified hand
     *
     * @param hand a {@code Set} of {@code Card} objects
     */
    @Override
    public void setHand(HashSet<Card> hand) {
        this.hand = new HashSet<>(hand);
    }

    /**
     * Removes all {@code Card} objects from this hand
     */
    @Override
    public void clearHand() {
        this.hand.clear();
    }

    /**
     * Adds a {@code Card} object to this hand
     *
     * @param card a {@code Card} object
     */
    @Override
    public void add(Card card) {
        this.hand.add(card);
    }

    /**
     * Removes a {@code Card} object from this hand
     *
     * @param card a {@code Card} object
     * @throws IllegalArgumentException if the {@code Card} is not in the hand
     */
    @Override
    public void remove(Card card) {
        if (!this.hand.remove(card)) {
            throw new IllegalArgumentException(card.toString() + "is not present in this hand");
        }
    }

    /**
     * Returns the number of {@code Card} objects in this hand
     *
     * @return the number of {@code Card} objects in this hand
     */
    @Override
    public int size() {
        return this.hand.size();
    }

    /**
     * Returns a copy of this hand
     *
     * @return a copy of this hand
     */
    @Override
    public HashSet<Card> getCards() {
        return new HashSet<>(hand);
    }

    /**
     * Some fields needs to be refreshed if the hand changes before performing a
     * total point calculation
     *
     * <p> This method must always be called in totalPoints()
     *
     * @param starter the starter {@code Card} object
     */
    private void refreshHand(Card starter) {
        //@formatter:off
        /*
         * Different point methods require different input types:
         * -Fifteens accepts any combination of the hand with starter card
         * -Multiples counts duplicates in the hand with starter card
         * -Runs uses any combination of the hand plus starter card
         * -Flushes and nobs need to differentiate between the starter and hand
         */
        //@formatter:on
        this.starter = starter;
        this.handWithStarter = new HashSet<>(hand);
        this.handWithStarter.add(this.starter);

        /*
         * Although calculating a power set is O(2^n), a hand and starter card is
         * guaranteed to only have five Card objects
         *
         * Using a power set significantly reduces the number of test cases, allowing
         * methods to be much more concise
         */
        this.cardCombinations = powerSet(handWithStarter);
    }

    /**
     * Calculates the sum of point combinations for this hand (if it includes 4
     * {@code Card} objects with a starter {@code Card} object)
     *
     * @param starter the starter {@code Card}
     * @return the total number of points in this cribbage hand with the given
     * starter {@code Card}
     * @throws IllegalArgumentException if {@code hand} does not contain exactly
     *                                  four {@code Card} objects or {@code starter}
     *                                  is {@code null}
     */
    @Override
    public int totalPoints(Card starter) {
        if (hand == null || starter == null || hand.size() != 4) {
            throw new IllegalArgumentException("illegal hand and/or starter card");
        }

        /* Updates all fields then calculates the total points*/
        this.refreshHand(starter);
        return fifteens() + multiples() + runs() + flushes() + nobs();
    }

    /**
     * Returns the number of points obtained from fifteens
     *
     * <p> Each unique combination of cards that add up to 15 is worth two points. Any
     * number of cards (between 2 and 5) can be used for each combination, and cards
     * may be used for multiple fifteens. All face cards have a value of 10 when
     * calculating fifteens
     *
     * <p> For example, a hand with four 5s and a three has 8 (2 points from each
     * fifteen, 4 choose 3 = 4 fifteens from the fives) points from fifteens
     *
     * @return the number of points obtained from fifteens
     */
    private int fifteens() {
        return this.cardCombinations.stream() // Every card combination is tested
                .mapToInt(this::isFifteen) // 2 points for each fifteen, 0 for not
                .sum(); // Return the total points from fifteens
    }

    /**
     * Checks if all the rank values of the {@code Card} objects add up to 15
     *
     * @param cards a {@code HashSet} of {@code Card} objects
     * @return 2 if the card values add up to 15, 0 if not
     */
    private int isFifteen(HashSet<Card> cards) {
        return cards.stream().mapToInt(this::cribbageValue) // Maps each card to it's cribbage value (10 for face cards)
                .sum() == 15 ? 2 : 0;
    }

    /**
     * Returns the cribbage value of a card. Used to calculate fifteens
     *
     * <p> Ranks are as follows:
     * <ul>
     * <li>Ace: 1</li>
     * <li>Two to ten: Their respective values</li>
     * <li>Jack, queen and king: 10</li>
     * </ul>
     *
     * @param card a {@code Card} object
     * @return the cribbage value of the {@code Card} object
     */
    private int cribbageValue(Card card) {
        return Math.min(card.getRankNumber(), 10); // All face cards are worth 10
    }

    /**
     * Returns the number of points obtained from multiples in the given cribbage
     * hand (with the starter card included)
     *
     * <p> Multiples are a double (2 points), triple (6 points) or quadruple (12 points)
     * of one rank of card. Face cards are not considered the same for multiples; a
     * ten and a queen both have a rank value of 10 but they would not give points
     * for a double
     *
     * @return the number of points obtained from multiples
     */
    private int multiples() {
        // @formatter:off
        /*
         * Counting points from each multiple is simple because a multiple of n cards is
         * worth n*n - n points:
         *
         * Single: 1*1-1 = 0 points
         * Double: 2*2-2 = 2 points
         * Triple: 3*3 - 3 = 3 points
         * Quadruple: 4*4 - 4 = 12 points
         */
        // @formatter:on
        return countDuplicates(this.handWithStarter).values() // Count of each rank present in the hand + starter
                .stream().mapToInt(v -> v * v - v).sum(); // Total points from multiples
    }

    /**
     * Returns the number of points obtained from runs
     *
     * <p> A run is a sequence of three (3 points), four (4 points) or five (5 points)
     * cards with consecutive ranks. Suit does not matter, and cards can be part of
     * multiple runs (but only the highest run is counted; a run of four is only four
     * points, NOT two runs of three)
     *
     * @return the number of points obtained from runs
     */
    private int runs() {
        // @formatter:off
        /*
         * Each card can be part of multiple runs of the same length, but not not
         * multiple runs of different lengths. The longest run always trumps lower length runs
         *
         * Take a hand containing cards with the ranks 2-3-3-4-5 for example:
         * -There are eight points from runs: 2-3-4-5 and 2-3-4-5 (with the other 3)
         * -The combination 2-3-4 is not counted as a run because 2-3-4-5 trumps it
         */
        // @formatter:on
        int score = this.cardCombinations.stream().filter(c -> c.size() == 5).mapToInt(this::isRun).sum();

        /* It is only possible to have one length of run possible in a hand */
        if (score == 0) {
            score += this.cardCombinations.stream().filter(c -> c.size() == 4).mapToInt(this::isRun).sum();
            if (score == 0) {
                score += this.cardCombinations.stream().filter(c -> c.size() == 3).mapToInt(this::isRun).sum();
            }
        }
        return score;
    }

    /**
     * Checks if the supplied cards are consecutive to form a run
     *
     * @param cards a {@code HashSet} of {@code Card} objects
     * @return 0 if the cards don't form a run; the length of the run (3, 4 or 5) if
     * the cards do form a run
     */
    private int isRun(HashSet<Card> cards) {
        /* Creates a sorted list of card rank numbers (ex: [2, 5, 5, 11, 13]) */
        ArrayList<Integer> values = cards.stream()
                .mapToInt(Card::getRankNumber).sorted().boxed().collect(Collectors.toCollection(ArrayList::new));

        /* If any card is 'out of order', no points are given for runs */
        return IntStream.range(0, values.size() - 1).anyMatch(i -> values.get(i) + 1 != values.get(i + 1)) ? 0
                : values.size();
    }

    /**
     * Returns the number of points obtained from flushes in a given cribbage hand
     * and starter card
     *
     * <p> To obtain a flush, the player's hand must have all four cards of the same
     * suit (4 points). If the starter card is also the same suit, 5 points are
     * obtained. Note that if only three cards in the player's hand plus the starter
     * card have the same suit, this is not a flush
     *
     * @return the number of points obtained from flushes
     */
    private int flushes() {
        /* All the suits in this hand */
        HashSet<Suit> suits = this.hand.stream().map(Card::getSuit).collect(Collectors.toCollection(HashSet::new));

        /* If all the suits are the same, 'suits' will only have one object */
        return suits.size() == 1 ? 4 + (suits.add(this.starter.getSuit()) ? 0 : 1) : 0;
    }

    /**
     * Returns the number of points obtained from nobs in a given cribbage hand and
     * starter card
     *
     * <p> One point is obtained from nobs if the player's hand has a jack of the same
     * suit as the starter card
     *
     * @return the number of points obtained from nobs
     */
    private int nobs() {
        return this.hand.stream().filter(c -> c.getRank() == Rank.JACK) // Filter out non jacks
                .map(Card::getSuit) // All suits of jacks in this hand
                .anyMatch(this.starter.getSuit()::equals) ? 1 : 0;
    }

    /**
     * Maps each card rank number in cards to the number of occurrences of that rank
     * number
     *
     * <p> For example, three kings, an ace and four would have the key-value pairs:
     *
     * <ul>
     * <li><code>1: 1</code></li>
     * <li><code>4: 1</code></li>
     * <li><code>13: 3</code></li>
     * </ul>
     *
     * @param cards an array of {@code Card} objects
     * @return a {@code Map} that maps each rank number to the number of occurrences
     */
    private Map<Integer, Integer> countDuplicates(HashSet<Card> cards) {
        return cards.stream().collect(Collectors.groupingBy(Card::getRankNumber, Collectors.summingInt(x -> 1)));
    }

}
