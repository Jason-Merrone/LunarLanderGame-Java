package com.example;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput {

    /**
     * The type of method to invoke when a keyboard event is invoked
     */
    public interface ICommand {
        void invoke(double elapsedTime);
    }

    public KeyboardInput(long window) {
        this.window = window;
    }

    /**
     * Original registerCommand remains available. It will simply register a null release callback.
     */
    public void registerCommand(int key, boolean keyPressOnly, ICommand pressCallback) {
        registerCommand(key, keyPressOnly, pressCallback, null);
    }

    /**
     * Overloaded registerCommand to support both press and release callbacks.
     */
    public void registerCommand(int key, boolean keyPressOnly, ICommand pressCallback, ICommand releaseCallback) {
        commandEntries.put(key, new CommandEntry(key, keyPressOnly, pressCallback, releaseCallback));
        // Start out by assuming the key isn't currently pressed
        keysPressed.put(key, false);
    }

    /**
     * Go through all the registered commands and invoke the callbacks as appropriate.
     */
    public void update(double elapsedTime) {
        for (var entry : commandEntries.entrySet()) {
            int key = entry.getKey();
            CommandEntry commandEntry = entry.getValue();
            boolean currentPressed = glfwGetKey(window, key) == GLFW_PRESS;
            boolean wasPressed = keysPressed.get(key);

            // Handle key press events as before
            if (commandEntry.keyPressOnly) {
                if (currentPressed && !wasPressed) {
                    commandEntry.pressCallback.invoke(elapsedTime);
                }
            } else {
                if (currentPressed) {
                    commandEntry.pressCallback.invoke(elapsedTime);
                }
            }

            // Handle key release events if a release callback is registered.
            if (commandEntry.releaseCallback != null && !currentPressed && wasPressed) {
                commandEntry.releaseCallback.invoke(elapsedTime);
            }

            // Update the key state for the next cycle.
            keysPressed.put(key, currentPressed);
        }
    }

    private final long window;
    // Table of registered callbacks
    private final HashMap<Integer, CommandEntry> commandEntries = new HashMap<>();
    // Table of registered keys' previous pressed state
    private final HashMap<Integer, Boolean> keysPressed = new HashMap<>();

    /**
     * Keeps track of the details associated with a registered command.
     */
    private record CommandEntry(int key, boolean keyPressOnly, ICommand pressCallback, ICommand releaseCallback) {
    }
}
