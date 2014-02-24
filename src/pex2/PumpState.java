package pex2;

import java.awt.*;

/**
 * Created by Samus on 2/24/14.
 */
public enum PumpState {

    READY, WAITING, PUMPING, CLEANING;

    public Color getPumpStateColor (PumpState color) {
        Color pumpStateColor = Color.BLACK;
        switch(color) {
            case READY:
                pumpStateColor = Color.GREEN;
                break;
            case WAITING:
                pumpStateColor = Color.YELLOW;
                break;
            case PUMPING:
                pumpStateColor = Color.BLUE;
                break;
            case CLEANING:
                pumpStateColor = Color.RED;
                break;
            default:
                break;
        }
        return pumpStateColor;
    }
}
