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

    // Shared Variables with Main
    private ArrayList<Color> powerColor;

    // Shared Variables with PowerRequest
    private AtomicInteger user = new AtomicInteger(NOT_IN_USE);

    // Power source id
    private int id;


    public Power(ArrayList<Color> powerColor) {
        this.powerColor = powerColor;
        id = powerNumber++;
    }

    public boolean canReceivePower(int requesterId) {
        if (user.get() == NOT_IN_USE) {
            user.set(requesterId);
            return true;
        } else {
            return false;
        }
    }

    public void releasePower() {
        user.set(NOT_IN_USE);
    }
}
