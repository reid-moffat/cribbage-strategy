package main;

import card.Card;
import card.Rank;
import card.Suit;

import java.util.*;

/**
 * UI for a cribbage calculator
 *
 * <p>
 * Used to get user input from the console for a cribbage hand, and use the
 * class {@code CribbageCombinations} to determine the optimal strategies for
 * dropping cards
 *
 * @author Reid Moffat
 */
final class UserInterface {

    /**
     * A standard 52-deck of cards
     */
    private static final HashSet<Card> cardPile = initializeDeck();
    /**
     * A list of valid card ranks used to check if a user input is valid
     *
     * <ul>
     * <li><code>1</code></li>
     * <li><code>2</code></li>
     * <li><code>3</code></li>
     * <li><code>4</code></li>
     * <li><code>5</code></li>
     * <li><code>6</code></li>
     * <li><code>7</code></li>
     * <li><code>8</code></li>
     * <li><code>9</code></li>
     * <li><code>10</code></li>
     * <li><code>J</code></li>
     * <li><code>Q</code></li>
     * <li><code>K</code></li>
     * </ul>
     */
    private static final ArrayList<String> VALID_RANKS = new ArrayList<>(
            Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"));
    /**
     * A list of valid card suits that the user can input
     *
     * <p>
     * These are not part of the suits in Card.Suit
     */
    private static final ArrayList<Character> VALID_SUITS = new ArrayList<>(Arrays.asList('C', 'D', 'H', 'S'));
    /**
     * A string that gives a short introduction to the program and asks the user to
     * input the number of players in the cribbage game (for determining the number
     * of starting cards)
     */
    private static final String ENTER_PLAYERS = "Cribbage Calculator\n"
            + "Created by Reid Moffat\n\n"
            + "How many players (2-4)? ";
    /**
     * A string that explains how to properly enter card values, gives some examples
     * and prompts the user to enter their cards into the console
     */
    private static final String ENTER_CARDS = "\nEach card must be its value (1-10, J, Q or K) plus the suit (case insensitive)\n"
            + "Examples:\n"
            + "'1D': Ace of diamonds\n"
            + "'4S': Four of spades\n"
            + "'10C': Ten of clubs\n"
            + "'KH': King of hearts\n"
            + "Enter each of the cards in your hand one by one below and press enter:\n";

    // @formatter:off (for string readability)
    /**
     * A set of 5 (3 players) or 6 (2 players) cards the player is dealt at the beginning of the round
     */
    private final HashSet<Card> dealtHand;
    /**
     * Used to get user input from the console
     */
    private final Scanner input;
    // @formatter:on

    /**
     * Initializes a cribbage calculator {@code UserInterface} object
     *
     * <p>
     * Use the method {@code .run()} to run the UI
     */
    public UserInterface() {
        this.dealtHand = new HashSet<>();
        this.input = new Scanner(System.in);
    }

    /**
     * Returns a {@code HashSet} that includes each of the cards (as a {@code Card}
     * object) in a standard 52-card deck
     *
     * @return a {@code HashSet} with all standard playing cards
     */
    private static HashSet<Card> initializeDeck() {
        HashSet<Card> deck = new HashSet<>();
        Arrays.stream(Card.RANKS)
                .forEach(rank -> Arrays.stream(Card.SUITS).forEach(suit -> deck.add(new Card(rank, suit))));
        return deck;
    }

    /**
     * Checks if a String represents a valid card, and returns the {@code Card}
     * object that it represents if it does
     *
     * <p>
     * A valid card string is the rank (1-10, j, q or k) of the card followed by the
     * first letter of the suit (neither are case-sensitive). Examples:
     *
     * <ul>
     * <li>"3d": Three of diamonds</li>
     * <li>"jS": Jack of spades</li>
     * <li>"10c": Ten of clubs</li>
     * <li>"1H": Ace of hearts</li>
     * </ul>
     *
     * @param card a string that represents a playing card
     * @return a {@code Card} object with the specified rank and suit if the
     * parameter is valid; null otherwise
     */
    private static Card checkValidCard(String card) {
        card = card.trim().toUpperCase();
        if (card.matches("^(10|[1-9JQK])[SDCH]$")) {
            Rank rank = Card.RANKS[VALID_RANKS.indexOf(card.substring(0, card.length() - 1))];
            Suit suit = Card.SUITS[VALID_SUITS.indexOf(card.charAt(card.length() - 1))];
            return new Card(rank, suit);
        }
        return null;
    }

    /**
     * Generates a {@code HashSet} of all 2-{@code Card} combinations in
     * {@code cards}
     *
     * @param cards a {@code HashSet} of {@code Card} objects
     * @return a {@code HashSet} of 2-element subsets (stored as {@code Card[]})
     */
    private static HashSet<Card[]> subset2(HashSet<Card> cards) {
        HashSet<Card[]> subsets = new HashSet<>();
        HashSet<Card> remaining = new HashSet<>(cards);
        cards.forEach(card1 -> {
            remaining.remove(card1);
            remaining.forEach(card2 -> subsets.add(new Card[]{card1, card2}));
        });
        return subsets;
    }

    public static void main(String[] args) {
        new UserInterface().run();

        int i = 0; // google style check
        switch (i) {
            case 1 :
                break;
        }
    }

    /**
     * Runs the program's user interface, calling the required methods in the
     * correct order
     */
    public void run() {
        this.getCards(this.getNumCards());
        this.printAveragePoints();
    }

    /**
     * Introduces the program and prompts the user to enter the number of cribbage
     * players in the console
     *
     * <p>
     * Loops until a valid number of players is inputted (2-4), then calculates and
     * returns the number of cards each player starts the game with:
     *
     * <ul>
     * <li>2 Players: 6 cards</li>
     * <li>3 Players: 5 cards</li>
     * <li>4 Players: 5 cards</li>
     * </ul>
     *
     * @return the number of starting cards for the given number of players
     */
    private int getNumCards() {
        System.out.print(UserInterface.ENTER_PLAYERS);

        /* Loops until a valid number of players is inputted */
        String numPlayers = input.nextLine();
        while (!(numPlayers.equals("2") || numPlayers.equals("3") || numPlayers.equals("4"))) {
            System.out.println("Invalid input. Try again: ");
            numPlayers = input.nextLine();
        }

        return numPlayers.equals("2") ? 6 : 5;
    }

    /**
     * Prompts the user to enter the playing cards in their hand and stores their
     * values
     *
     * @param numCards the number of cards to be inputted
     */
    private void getCards(int numCards) {
        System.out.println(numCards + " cards to start");
        System.out.println(UserInterface.ENTER_CARDS);

        /* Gets and stores each valid card the user inputs */
        for (int i = 1; i <= numCards; i++) {
            System.out.print("Card " + i + ": ");
            Card card = checkValidCard(input.nextLine());
            while (card == null || !notInHand(card)) {
                System.out.print("Invalid or duplicate card, input again: ");
                card = checkValidCard(input.nextLine());
            }
            this.dealtHand.add(card);
            System.out.println(card + "\n");
        }
        this.input.close();
    }



    /**
     * Calculates the average cribbage points obtained for each combination of cards
     * to be dropped and prints out the value
     *
     * <p>
     * The average number of points takes into account the number of points gained
     * from each possible starter card to be flipped up
     */

    private void printAveragePoints() {
        CribbageHand hand = new CribbageHand(new HashSet<>(this.dealtHand));
        ArrayList<String> hands = new ArrayList<>(); // Highest to the lowest points for combinations

        /*
         * The player has seen either 5 or 6 cards so far (from their hand), implying
         * that the remaining 47 or 48 cards respectively could all possibly be the
         * starter
         */
        final int unknownCards = 52 - hand.size();

        System.out.println("---Drop combinations by average points---");
        if (hand.size() == 6) { // With 6 cards, 2 must be dropped
            // Each hand possibility (removing two cards) is calculated for average points
            for (Card[] combination : subset2(hand.getCards())) {
                hand.remove(combination[0]);
                hand.remove(combination[1]);

                // Calculate the total number of points (all starters) for this combination
                double totalPoints = cardPile.stream().filter(this::notInHand).mapToInt(hand::totalPoints).sum();

                // The combination and its average number of points to 2 decimals
                double avgPoints = Math.round(100 * (totalPoints / unknownCards)) / 100.0;
                hands.add(String.format("%4.0f%s and %s: %3.2f", 100 * avgPoints, combination[0], combination[1], avgPoints));

                hand.add(combination[0]);
                hand.add(combination[1]);
            }
        } else { // With 5 cards, only one needs to be dropped
            // Each hand possibility (removing a card) is calculated for average points
            for (Card droppedCard : hand.getCards()) {
                hand.remove(droppedCard);

                // Calculate the total number of points (all starters) for this combination
                double totalPoints = cardPile.stream().filter(this::notInHand).mapToInt(hand::totalPoints).sum();

                // The combination and its average number of points to 2 decimals
                double avgPoints = Math.round(100 * (totalPoints / unknownCards)) / 100.0;
                hands.add(String.format("%4.0f%s: %3.2f", 100 * avgPoints, droppedCard, avgPoints));

                hand.add(droppedCard);
            }
        }

        // Sorts the combinations from highest to lowest points and outputs them
        hands.sort(Collections.reverseOrder());
        int counter = 1;
        boolean fives = false; // Fives and aces are apical cases; you might not want to drop them
        boolean aces = false;
        for (int i = 0; i < hands.size(); ++i) {
            // The first four characters of the string is the average points (multiplied by 100 to remove the decimal)
            // So it can be sorted
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
        if (fives) System.out.println("(*) Consider keeping fives if you don't have the crib");
        if (aces )
            System.out.println("(**) Aces are good for the play round, consider keeping it is the points are close");
    }

    /**
     * Checks if the player's hand (before dropping cards) contains the specified
     * card
     *
     * @param card the card to check
     * @return true if the card is not in the player's hand, false otherwise
     */
    private boolean notInHand(Card card) {
        return !this.dealtHand.contains(card);
    }

}
