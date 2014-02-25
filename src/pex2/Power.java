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
    private AtomicInteger user = new AtomicInteger(NOT_IN_USE);

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

    public boolean canReceivePower(int requesterId) {
        if (user.get() == NOT_IN_USE) {
            user.set(requesterId);
            if (requesterId == id) {
                rightColor = Color.ORANGE;
            } else {
                leftColor = Color.ORANGE;
            }
            return true;
        } else {
            return false;
        }
    }

    public void resetColors() {
        rightColor = Color.LIGHT_GRAY;
        leftColor = Color.LIGHT_GRAY;
    }

    public void releasePower() {
        user.set(NOT_IN_USE);
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
}
