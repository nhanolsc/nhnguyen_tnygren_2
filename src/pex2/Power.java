/**
 * Power.java
 *
 * Represents the Power supplies between pumps.
 * Each power supply created can power only ONE
 * pump at a time.
 */
package pex2;

public class Power
{
    // Denote a power source is not being used
    public static final int NOT_IN_USE = -1;

    // Shared Variables with PowerRequest
    private int user;

    /**
     * Initializes each Power supply with an id
     */
    public Power()
    {
        user = NOT_IN_USE;
    }

    /**
     * Checks the current user to see if the requester
     * can receive power from this power supply. This method
     * is synchronized to keep two Pumps from acquiring the
     * same power supply.
     *
     * @param requesterId           Pump id that is requesting power
     * @return                      True if power is granted, false otherwise
     */
    public synchronized boolean canReceivePower(int requesterId)
    {
        if (user == NOT_IN_USE)
        {
            user = requesterId;
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Releases the power connection between the user Pump
     * and this power supply.
     *
     * @param pumpId            Only the pump using this Power supply can
     *                          set the user to NOT_IN_USE
     */
    public void releasePower(int pumpId)
    {
        if (pumpId == user)
        {
            user = NOT_IN_USE;
        }
    }
}
