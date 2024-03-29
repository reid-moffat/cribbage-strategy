package main;

import card.Card;
import card.Rank;
import card.Suit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The class {@code CribbageHand} represents a player's hand in the game of cribbage
 *
 * <p> Initializing this class requires a {@code HashSet} of {@code Card} objects. The hand's
 * {@code Cards} can be modified using the methods {@code add} and {@code remove}
 *
 * <p> The total points obtained from this cribbage hand (in the counting phase) can be
 * calculated by passing a starter {@code Card} object through {@code totalPoints} if this hand
 * has four {@code Card} objects
 *
 * @author Reid Moffat
 */
final class CribbageHand {

    /**
     * A set of unique playing {@code Cards}. Must include 4 {@code Cards} for points to be
     * calculated
     *
     * <p> Does not include the starter {@code Card}
     */
    private final HashSet<Card> hand;

    /**
     * Initializes this {@code CribbageHand} with a set of {@code Cards}
     *
     * @param hand a {@code Set} of {@code Card} objects (not including the starter card)
     * @throws IllegalArgumentException if any card in the parameter is null
     */
    CribbageHand(@NotNull HashSet<Card> hand) {
        if (hand.contains(null)) {
            throw new IllegalArgumentException("Cannot have a null card in the hand");
        }
        this.hand = new HashSet<>(hand);
    }

    /**
     * Returns the power set of a given {@code HashSet}
     *
     * @param originalSet a {@code HashSet} of objects
     * @return a {@code HashSet} containing all subsets of {@code originalSet}
     */
    private static @NotNull
    HashSet<HashSet<Card>> powerSet(@NotNull HashSet<Card> originalSet) {
        final HashSet<HashSet<Card>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }

        final List<Card> list = new ArrayList<>(originalSet);
        final HashSet<Card> rest = new HashSet<>(list.subList(1, list.size()));
        for (HashSet<Card> set : powerSet(rest)) {
            HashSet<Card> newSet = new HashSet<>();
            newSet.add(list.get(0));
            newSet.addAll(set);

            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    /**
     * Adds a {@code Card} object to this hand
     *
     * @param card a {@code Card} object
     * @return if the card was successfully added (the card is not currently in the hand)
     * @throws IllegalArgumentException if the card parameter is null
     */
    public boolean add(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add a null card to the hand");
        }
        return this.hand.add(card);
    }

    /**
     * Removes a {@code Card} object from this hand
     *
     * @param card a {@code Card} object
     * @throws IllegalArgumentException if the {@code Card} is not in the hand
     */
    public void remove(Card card) {
        if (!this.hand.remove(card)) {
            throw new IllegalArgumentException(card.toString() + "is not present in this hand");
        }
    }

    /**
     * Removes all the {@code Card} objects from this cribbage hand
     */
    public void clear() {
        this.hand.clear();
    }

    /**
     * Returns the number of {@code Card} objects in this hand
     *
     * @return the number of {@code Card} objects in this hand
     */
    int size() {
        return this.hand.size();
    }

    /**
     * Returns a copy of this hand
     *
     * @return a copy of this hand
     */
    @Contract(" -> new")
    public @NotNull HashSet<Card> getCards() {
        return new HashSet<>(hand);
    }

    /**
     * Calculates the sum of point combinations for this hand (if it includes 4 {@code Card}
     * objects with a starter {@code Card} object)
     *
     * @param starter the starter {@code Card}
     * @return the total number of points in this cribbage hand with the given
     * starter {@code Card}
     * @throws IllegalArgumentException if {@code hand} does not contain exactly four {@code Card}
     *                                  objects or {@code starter} is {@code null}
     */
    public int totalPoints(Card starter) {
        if (this.hand == null || this.hand.size() != 4) {
            throw new IllegalArgumentException("Illegal hand: the hand must have four unique " +
                    "cards in it");
        }
        if (starter == null || this.hand.contains(starter)) {
            throw new IllegalArgumentException("Illegal starter card: the start card must not be " +
                    "in the hand");
        }

        // Various representations of the hand (the hand by itself, the hand with the starter card
        // and every possible combination of cards) help make calculating the total score easier
        final HashSet<Card> handWithStarter = new HashSet<>(this.hand);
        handWithStarter.add(starter);
        final HashSet<HashSet<Card>> cardCombinations = powerSet(handWithStarter);

        return fifteens(cardCombinations) + multiples(handWithStarter) + runs(cardCombinations) +
                flushes(starter) + nobs(starter);
    }

    /**
     * Returns the number of points obtained from fifteens
     *
     * <p> Each unique combination of cards that add up to 15 is worth two points. 'Any' number of
     * cards (i.e. between 2 and 5) can be used for each combination, and cards may be used for
     * multiple fifteens. All face cards have a value of 10 when calculating fifteens
     *
     * <p> For example, a hand with four 5s and a three has 8 (2 points from each fifteen, 4
     * choose 3 = 4 fifteens from the fives) points from fifteens
     *
     * @return the number of points obtained from fifteens
     */
    private int fifteens(@NotNull HashSet<HashSet<Card>> cardCombinations) {
        return cardCombinations.stream().mapToInt(cards -> cards.stream().mapToInt(card ->
                Math.min(card.getRankNumber(), 10)).sum() == 15 ? 2 : 0).sum();
    }

    /**
     * Returns the number of points obtained from multiples in the given cribbage hand (with the
     * starter card included)
     *
     * <p> Multiples are a double (2 points), triple (6 points) or quadruple (12 points) of one
     * rank of card. Face cards are not considered the same for multiples; a ten and a queen both
     * have a rank value of 10, but they would not give points for a double
     *
     * @return the number of points obtained from multiples
     */
    private int multiples(@NotNull HashSet<Card> handWithStarter) {
        // Maps each card rank to the number of occurrences of that rank then counts multiples
        // A multiple of n cards is n*n - n points (single: 0, double: 2, triple: 6, quadruple: 12)
        return handWithStarter.stream()
                .collect(Collectors.groupingBy(Card::getRankNumber, Collectors.summingInt(x -> 1)))
                .values().stream().mapToInt(v -> v * v - v).sum();
    }

    /**
     * Returns the number of points obtained from runs
     *
     * <p> A run is a sequence of three (3 points), four (4 points) or five (5 points) cards with
     * consecutive ranks. Suit does not matter, and cards can be part of multiple unique runs (but
     * only the highest run is counted; a run of four is only four points, NOT two runs of three)
     *
     * @return the number of points obtained from runs
     */
    private int runs(@NotNull HashSet<HashSet<Card>> cardCombinations) {
        // Total points obtained from each length of run, offset by 3 (runScores[0] is for a run of 3)
        final int[] runScores = {0, 0, 0};

        cardCombinations.forEach(cards -> {
            // If this could be a run (>2 cards), sort the list of card rank numbers with duplicates
            // (ex: [2, 5, 5, 11, 13])
            if (cards.size() < 3) return;
            final int[] values = cards.stream().mapToInt(Card::getRankNumber).sorted().toArray();

            // If any card is 'out of order', no points are given for runs
            runScores[cards.size() - 3] += IntStream.range(0, values.length - 1)
                    .anyMatch(i -> values[i] + 1 != values[i + 1]) ? 0 : values.length;
        });

        // Only one length of run is possible in a hand (a run of 5 is NOT two runs of 4)
        return runScores[2] != 0 ? runScores[2] : runScores[1] != 0 ? runScores[1] : runScores[0];
    }

    /**
     * Returns the number of points obtained from flushes in a given cribbage hand and starter card
     *
     * <p> To obtain a flush, the player's hand must have all four cards of the same suit (4
     * points). If the starter card is also the same suit, 5 total points are awarded. Note that if
     * only three cards in the player's hand plus the starter card have the same suit, this is
     * not a flush
     *
     * @return the number of points obtained from flushes
     */
    private int flushes(@NotNull Card starter) {
        // All the suits in this hand
        final HashSet<Suit> suits = this.hand.stream().map(c -> c.suit).collect(Collectors
                .toCollection(HashSet::new));

        // If all the suits are the same, the 'suits' set will only have one suit object
        return suits.size() == 1 ? 4 + (suits.add(starter.suit) ? 0 : 1) : 0;
    }

    /**
     * Returns the number of points obtained from nobs in a given cribbage hand and starter card
     *
     * <p> One point is obtained from nobs if the player's hand has a jack of the same suit as
     * the starter card
     *
     * @return the number of points obtained from nobs
     */
    private int nobs(@NotNull Card starter) {
        return this.hand.stream().filter(c -> c.rank == Rank.JACK).map(c -> c.suit)
                .anyMatch(starter.suit::equals) ? 1 : 0;
    }
}
