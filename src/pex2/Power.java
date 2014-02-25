package pex2;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nhhnMAC on 2/24/14.
 */
public class Power {

    // Denote a power source is not being used
    public static final int NOT_IN_USE = -1;

    // Used for each instance of Power
    private static int powerNumber = 0;

    // Shared Variables with PowerRequest
    private int user = NOT_IN_USE;

    // User id to return correct color
    private int requesterId = -1;

    // Power source id
    private int id;

    // Left and Right colors
    private Color leftColor = Color.LIGHT_GRAY;
    private Color rightColor = Color.LIGHT_GRAY;


    public Power() {
        id = powerNumber++;
    }

    public synchronized boolean canReceivePower(int requesterId) {
        System.out.println();
        System.out.println("REQUESTING PUMP: " + requesterId + " FROM POWER SUPPLY " + id);
        System.out.println("CURRENT USER: " + user + " REQUESTER " + requesterId);
        if (user == NOT_IN_USE) {
            user = requesterId;
            this.requesterId = requesterId;
            System.out.println("RECEIVED ACCESS: " + requesterId + " FROM POWER SUPPLY " + id);
            System.out.println();
            if (requesterId == id) {
                rightColor = Color.ORANGE;
            } else {
                leftColor = Color.ORANGE;
            }
            return true;
        } else {
            System.out.println("POWER: " + id + "-----ACCESS DENIED: " + requesterId);
            System.out.println();
            return false;
        }
    }

    public void resetColors() {
        rightColor = Color.LIGHT_GRAY;
        leftColor = Color.LIGHT_GRAY;
    }

    public synchronized void releasePower() {
        System.out.println();
        System.out.println("POWER: " + id + "---------PUMP : " + requesterId + " HAS RELEASED POWER");
        System.out.println();
        user = NOT_IN_USE;
        requesterId = -1;
        resetColors();
    }

    public Color getPowerColor(String side) {
        if (side.equals("left")) {
            return leftColor;
        } else {
            return rightColor;
        }
    }

    public int getId() {
        return id;
    }
}
