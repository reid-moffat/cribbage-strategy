package main;

import card.Card;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserInterfaceTest {

    private final UserInterface ui = new UserInterface();

    @Test
    void dataIntegrity() throws NoSuchFieldException, IllegalAccessException {
        final Field cardPile = ui.getClass().getDeclaredField("cardPile");
        cardPile.setAccessible(true);
        assertEquals(((HashSet<Card>) cardPile.get(ui)).size(), 52);
    }

    @Test
    @SuppressWarnings({"UnusedDeclaration"})
    void main() {
        UserInterface ui = new UserInterface();
    }
}
