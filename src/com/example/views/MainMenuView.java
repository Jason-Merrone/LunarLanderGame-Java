package com.example.views;

import com.example.GameStateEnum;
import com.example.KeyboardInput;
import edu.usu.graphics.*;

import static org.lwjgl.glfw.GLFW.*;

public class MainMenuView extends GameStateView {
    private Texture backgroundTexture;
    private Rectangle backgroundRect = new Rectangle(-1.0f, -1.0f, 2.0f, 2.0f, -1);

    private enum MenuState {
        NewGame,
        HighScores,
        Help,
        About,
        Quit;

        public MenuState next() {
            int nextOrdinal = (this.ordinal() + 1) % MenuState.values().length;
            return MenuState.values()[nextOrdinal];
        }

        public MenuState previous() {
            int previousOrdinal = (this.ordinal() - 1) % MenuState.values().length;
            if (previousOrdinal < 0) {
                previousOrdinal = Quit.ordinal();
            }
            return MenuState.values()[previousOrdinal];
        }
    }

    private MenuState currentSelection = MenuState.NewGame;
    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.MainMenu;
    private Font fontMenu;
    private Font fontSelected;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        backgroundTexture = new Texture("resources/images/background.jpg");

        fontMenu = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);
        fontSelected = new Font("resources/fonts/Roboto-Bold.ttf", 48, false);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // Arrow keys to navigate the menu
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            currentSelection = currentSelection.previous();
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            currentSelection = currentSelection.next();
        });
        // When Enter is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            nextGameState = switch (currentSelection) {
                case MenuState.NewGame -> GameStateEnum.GamePlay;
                case MenuState.HighScores -> GameStateEnum.HighScores;
                case MenuState.Help -> GameStateEnum.Help;
                case MenuState.About -> GameStateEnum.About;
                case MenuState.Quit -> GameStateEnum.Quit;
            };
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.MainMenu;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void render(double elapsedTime) {
        graphics.draw(backgroundTexture,backgroundRect,Color.WHITE);
        final float HEIGHT_MENU_ITEM = 0.075f;
        float top = -0.25f;
        top = renderMenuItem(currentSelection == MenuState.NewGame ? fontSelected : fontMenu, "New Game", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.NewGame ? Color.YELLOW : Color.BLUE);
        top = renderMenuItem(currentSelection == MenuState.HighScores ? fontSelected : fontMenu, "High Scores", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.HighScores ? Color.YELLOW : Color.BLUE);
        top = renderMenuItem(currentSelection == MenuState.Help ? fontSelected : fontMenu, "Help", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Help ? Color.YELLOW : Color.BLUE);
        top = renderMenuItem(currentSelection == MenuState.About ? fontSelected : fontMenu, "Credits", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.About ? Color.YELLOW : Color.BLUE);
        renderMenuItem(currentSelection == MenuState.Quit ? fontSelected : fontMenu, "Quit", top, HEIGHT_MENU_ITEM, currentSelection == MenuState.Quit ? Color.YELLOW : Color.BLUE);
    }

    /**
     * Centers the text horizontally, at the specified top position.
     * It also returns the vertical position to draw the next menu item
     */
    private float renderMenuItem(Font font, String text, float top, float height, Color color) {
        float width = font.measureTextWidth(text, height);
        graphics.drawTextByHeight(font, text, 0.0f - width / 2, top, height, color);

        return top + height;
    }
}
