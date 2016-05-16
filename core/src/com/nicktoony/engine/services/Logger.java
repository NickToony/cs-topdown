package com.nicktoony.engine.services;

/**
 * Created by nick on 14/07/15.
 */
/**
 * Allows you to define the server log output in whichever way you desire
 */
public interface Logger {
    void log(String string);
    void log(Exception exception);
}
