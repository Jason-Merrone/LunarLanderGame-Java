package com.example.serialization;

import com.google.gson.Gson;

import java.io.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Serializer implements Runnable {

    private enum Activity {
        Nothing,
        Save,
        Load
    }

    public interface LoadCallback {
        void onDataLoaded(String jsonData);
        void onError(Exception e);
    }


    private final Lock lockSignal = new ReentrantLock();
    private final Condition doSomething = lockSignal.newCondition();
    private volatile boolean done = false;
    private volatile Activity doThis = Activity.Nothing;
    private String filePath;
    private String jsonDataToSave;
    private String loadedJsonData;
    private LoadCallback loadCallback;


    public Serializer() {
        Thread tInternal = new Thread(this);
        tInternal.start();
    }

    public void shutdown() {
        done = true;
        signalDoSomething();
    }

    public void save(String filePath, String jsonData) {
        lockSignal.lock();
        try {
            this.filePath = filePath;
            this.jsonDataToSave = jsonData;
            this.doThis = Activity.Save;
            signalDoSomething();
        } finally {
            lockSignal.unlock();
        }
    }

    public void load(String filePath, LoadCallback callback) {
        lockSignal.lock();
        try {
            this.filePath = filePath;
            this.loadCallback = callback;
            this.doThis = Activity.Load;
            signalDoSomething();
        } finally {
            lockSignal.unlock();
        }
    }


    private void signalDoSomething() {
        lockSignal.lock();
        try {
            doSomething.signal();
        } finally {
            lockSignal.unlock();
        }
    }


    private void saveSomething() {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(jsonDataToSave);
        } catch (Exception ex) {
            System.err.println("Error saving data to " + filePath + ": " + ex.getMessage());
        }
    }

    private void loadSomething() {
        StringBuilder sb = new StringBuilder();
        try (FileReader reader = new FileReader(filePath)) {
            int character;
            while ((character = reader.read()) != -1) {
                sb.append((char) character);
            }
            loadedJsonData = sb.toString();
            if (loadCallback != null) {
                loadCallback.onDataLoaded(loadedJsonData);
            }

        } catch (FileNotFoundException fnfEx) {
            if (loadCallback != null) {
                loadCallback.onDataLoaded(null); // File not found is not an error for loading, just no data
            }
        }
        catch (Exception ex) {
            System.err.println("Error loading data from " + filePath + ": " + ex.getMessage());
            if (loadCallback != null) {
                loadCallback.onError(ex);
            }
        } finally {
            // Reset callback after execution to prevent multiple calls in loop if something goes wrong.
            loadCallback = null;
        }
    }


    @Override
    public void run() {
        try {
            while (!done) {
                lockSignal.lock();
                try {
                    doSomething.await();
                } finally {
                    lockSignal.unlock();
                }


                switch (doThis) {
                    case Nothing -> {}
                    case Save -> saveSomething();
                    case Load -> loadSomething();
                }
                doThis = Activity.Nothing; // Reset activity
            }
        } catch (InterruptedException ex) {
            System.err.println("Serializer thread interrupted: " + ex.getMessage());
        }
    }
}