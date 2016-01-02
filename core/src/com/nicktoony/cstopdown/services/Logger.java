package com.nicktoony.cstopdown.services;

/**
 * Created by nick on 14/07/15.
 */
/**
 * Allows you to define the server log output in whichever way you desire
 */
public interface Logger {
    public void log(String string);
    public void log(Exception exception);
}
