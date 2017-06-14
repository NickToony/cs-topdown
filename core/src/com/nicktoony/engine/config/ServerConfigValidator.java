package com.nicktoony.engine.config;

/**
 * Created by Nick on 14/06/2017.
 */
public class ServerConfigValidator {
    public static String validateName(String name) {
        return validateString(name);
    }

    public static String validateMaxPlayers(String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (Exception e) {
            return "must be a number";
        }
        return validateRange(number, 1, 16);
    }

    public static String validateBots(String input, int maxPlayers) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (Exception e) {
            return "must be a number";
        }
        return validateRange(number, 0, maxPlayers - 1);
    }

    public static String validateMap(String map) {
        return validateString(map);
    }

    public static String validatePlayerMoveSpeed(String input) {
        float number;
        try {
            number = Float.parseFloat(input);
        } catch (Exception e) {
            return "must be a number";
        }
        return validateRange(number, 1, 10);
    }

    public static String validateFreezeTime(String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (Exception e) {
            return "must be a number";
        }
        return validateRange(number, 0, 20);
    }

    public static String validateRoundTime(String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (Exception e) {
            return "must be a number";
        }
        return validateRange(number, 1, 9999);
    }




    private static String validateString(String string) {
        return string.isEmpty() ? "is required." : null;
    }

    private static String validateRange(float number, float min, float max) {
        if (number < min) {
            return "must be greater than " + min;
        }
        if (number > max) {
            return "must be less than " + max;
        }

        return null;
    }


}
