package com.example.serialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HighScoresManager {
    private static HighScoresManager instance;
    private List<Integer> highScores;
    private final Serializer serializer;
    private static final String HIGH_SCORES_FILE = "highscores.json";

    private HighScoresManager() {
        highScores = new ArrayList<>();
        serializer = new Serializer();
        loadHighScores(); // Load scores on initialization
    }

    public static HighScoresManager instance() {
        if (instance == null) {
            instance = new HighScoresManager();
        }
        return instance;
    }

    public List<Integer> getTopHighScores() {
        return Collections.unmodifiableList(highScores.subList(0, Math.min(5, highScores.size()))); // Return top 5 or fewer
    }

    public void addHighScore(int score) {
        highScores.add(score);
        Collections.sort(highScores, Collections.reverseOrder()); // Sort in descending order
        if (highScores.size() > 5) {
            highScores = highScores.subList(0, 5); // Keep only top 5
        }
        saveHighScores(); // Save after adding a new score
    }

    private void loadHighScores() {
        serializer.load(HIGH_SCORES_FILE, new Serializer.LoadCallback() {
            @Override
            public void onDataLoaded(String jsonData) {
                if (jsonData != null && !jsonData.isEmpty()) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Integer>>() {}.getType();
                    highScores = gson.fromJson(jsonData, listType);
                    if (highScores == null) {
                        highScores = new ArrayList<>(); // Ensure not null even if parsing fails
                    }
                } else {
                    highScores = new ArrayList<>(); // Initialize if file is empty or doesn't exist
                }
            }

            @Override
            public void onError(Exception e) {
                System.err.println("Error loading high scores: " + e.getMessage());
                highScores = new ArrayList<>(); // Initialize even on error
            }
        });
    }


    private void saveHighScores() {
        Gson gson = new Gson();
        String jsonData = gson.toJson(highScores);
        serializer.save(HIGH_SCORES_FILE, jsonData);
    }
}