package main;

import card.Card;
import card.Rank;
import card.Suit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * CLI UI for a cribbage calculator
 *
 * <p>Used to get user input from the console for a cribbage hand, and use the class {@code
 * CribbageCombinations} to determine the optimal strategies for dropping cards
 *
 * @author Reid Moffat
 */
final class UserInterface {

    /**
     * A standard 52-card deck to compare with
     */
    private static final HashSet<Card> cardPile =
            new HashSet<>(IntStream.range(0, 52).mapToObj(i -> new Card(Rank.values[i % 13],
                    Suit.values[i / 13])).collect(Collectors.toSet()));

    /**
     * A set of 5 cards (for 3 players) or 6 cards (for 2 players) the player is dealt at the
     * beginning of the round
     */
    private final HashSet<Card> dealtHand = new HashSet<>();

    /**
     * Runs the cribbage calculator
     */
    public UserInterface() {
        getUserInput();
        var averagePoints = getAveragePoints();
        printPoints(averagePoints);
    }

    /**
     * Generates a {@code HashSet} of all 2-{@code Card} combinations in {@code cards}
     *
     * @param cards a {@code HashSet} of {@code Card} objects
     * @return a {@code HashSet} of 2-element subsets (stored as {@code Card[]})
     */
    private static @NotNull HashSet<Card[]> subset2(HashSet<Card> cards) {
        HashSet<Card[]> subsets = new HashSet<>();
        HashSet<Card> remaining = new HashSet<>(cards);
        cards.forEach(card1 -> {
            remaining.remove(card1);
            remaining.forEach(card2 -> subsets.add(new Card[]{card1, card2}));
        });
        return subsets;
    }

    /**
     * Prints instructions to the console and gets user input
     */
    private void getUserInput() {
        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Cribbage Calculator\nCreated by Reid Moffat\n\nHow many players (2-4)? ");

            // Loops until a valid number of players is inputted
            final List<String> validNumPlayers = Arrays.asList("2", "3", "4");
            String numPlayers = input.nextLine().trim();
            while (!validNumPlayers.contains(numPlayers)) {
                System.out.print("Invalid input; must be between 2 and 4 (inclusive): ");
                numPlayers = input.nextLine().trim();
            }
            int numCards = numPlayers.equals("2") ? 6 : 5;

            System.out.println(numCards + " cards to start");
            System.out.println("\nEach cards is represented as their value (1-10, J, Q or K) and suit\n"
                    + "Examples:\n"
                    + "'1D': Ace of diamonds\n"
                    + "'10c': Ten of clubs\n"
                    + "'KH': King of hearts\n"
                    + "Enter each of the cards in your hand one by one below and press enter:\n");

            // Gets and stores each valid card the user inputs
            for (int i = 1; i <= numCards; ++i) {
                System.out.print("Card " + i + ": ");

                Card card;
                while (true) {
                    try {
                        card = Card.stringToCard(input.nextLine());
                        if (notInHand(card)) {
                            break;
                        }
                        System.out.print("Card is already in your hand, try another one: ");
                    } catch (IllegalArgumentException e) {
                        System.out.print("Invalid card, input again: ");
                    }
                }
                this.dealtHand.add(card);
                System.out.println(card + "\n");
            }
        }
    }

    /**
     * Calculates and outputs the average cribbage points obtained for each combination of cards to
     * be dropped and prints out the value
     *
     * <p> The average number of points takes into account the number of points gained from each
     * possible starter card to be flipped up
     */
    private @NotNull ArrayList<String> getAveragePoints() {
        final CribbageHand hand = new CribbageHand(new HashSet<>(this.dealtHand)); // Current cards
        final ArrayList<String> hands = new ArrayList<>(); // Highest to the lowest point combinations

        // Any of the remaining cards (cards not in the user's hand) could be the starter
        final int unknownCards = 52 - hand.size();

        System.out.println("---Drop combinations by average points---");
        double totalPoints, avgPoints;
        if (hand.size() == 6) { // With 6 cards, 2 must be dropped
            // Each hand possibility (removing two cards) is calculated for average points
            for (Card[] combination : subset2(hand.getCards())) {
                hand.remove(combination[0]);
                hand.remove(combination[1]);

                // Calculate the total number of points (all starters) for this combination
                totalPoints = cardPile.stream().filter(this::notInHand)
                        .mapToInt(hand::totalPoints).sum();

                // The combination and its average number of points to 2 decimals
                avgPoints = 100 * (totalPoints / unknownCards) / 100.0;
                hands.add(String.format("%4.0f%s and %s: %3.2f", 100 * avgPoints, combination[0],
                        combination[1], avgPoints));

                hand.add(combination[0]);
                hand.add(combination[1]);
            }
        } else { // With 5 cards, only one needs to be dropped
            // Each hand possibility (removing a card) is calculated for average points
            for (Card droppedCard : hand.getCards()) {
                hand.remove(droppedCard);

                // Calculate the total number of points (all starters) for this combination
                totalPoints = cardPile.stream().filter(this::notInHand)
                        .mapToInt(hand::totalPoints).sum();

                // The combination and its average number of points to 2 decimals
                avgPoints = 100 * (totalPoints / unknownCards) / 100.0;
                hands.add(String.format("%4.0f%s: %3.2f", 100 * avgPoints, droppedCard, avgPoints));

                hand.add(droppedCard);
            }
        }
        return hands;
    }

    /**
     * Prints to the console the average points for each hand with suggestions for cards to keep
     */
    private void printPoints(@NotNull ArrayList<String> hands) { // TODO: clean this up
        // Sorts the combinations from highest to lowest points and outputs them
        hands.sort(Collections.reverseOrder());
        int counter = 1; // Current rank (multiple combinations may have the same amount of points)
        boolean fives = false, aces = false; // Fives and aces are special cases; you might not want to drop them

        for (int i = 0; i < hands.size(); ++i) {
            // The first four characters of the string is the average points (multiplied by 100
            // to remove the decimal) so it can be sorted
            if (i > 0 && hands.get(i).substring(0, 3).compareTo(hands.get(i - 1).substring(0, 3)) != 0) {
                counter = i + 1;
            }
            System.out.printf("#%d: %s", counter, hands.get(i).substring(4));

            // A couple special cases (the average points doesn't take into account the crib or the playing round)
            if (hands.get(i).contains("Five")) {
                System.out.print(" (*)");
                fives = true;
            }
            if (hands.get(i).contains("Ace")) {
                System.out.print(" (**)");
                aces = true;
            }
            System.out.println();
        }

        System.out.println();
        if (fives) {
            System.out.println("(*) Consider keeping fives (if the points are close), especially if you don't have the crib");
        }
        if (aces) {
            System.out.println("(**) Aces are good for the play round, consider keeping them " +
                    "(if the points are very close)");
        }
    }

    /**
     * Checks if the player's hand (before dropping cards) contains the specified card
     *
     * @param card the card to check
     * @return true if the card is not in the player's hand, false otherwise
     */
    private boolean notInHand(Card card) {
        return !this.dealtHand.contains(card);
    }

}
