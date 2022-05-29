package main;

import card.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserInterfaceTest {

    private UserInterface ui;

    @BeforeEach
    void setUp() {
        ui = new UserInterface();
    }

    @Test
    @SuppressWarnings("unchecked")
    void dataIntegrity() throws NoSuchFieldException, IllegalAccessException {
        final Field cardPileField = ui.getClass().getDeclaredField("cardPile");
        cardPileField.setAccessible(true);
        final var cardPile = (HashSet<Card>) cardPileField.get(ui);

        assertEquals(cardPile.size(), 52);
    }
}
