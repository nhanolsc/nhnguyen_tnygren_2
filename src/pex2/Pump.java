/**
 * Pump.java
 *
 * This class controls the pumps that will fill
 * the tank with water.
 */
package pex2;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;


public class Pump implements Runnable
{
    // Pumping increments
    public static final int PUMPING_INCREMENTS = 100;
    public static final int READY_STATE_WAIT_TIME = 500;

    // Used for pump ID
    private static int pumpNumber = 0;

    // Shared Variables with Main
    private AtomicInteger currentWaterLevel;

    // This pump's left and right power supplies
    private Power leftPower;
    private Power rightPower;

    // Used to return correct power supply chord color
    private boolean hasLeftPower = false;
    private boolean hasRightPower = false;

    // Statistics to be printed
    private int gallonsPumped = 0;
    private int cycles = 0;

    // Pump id
    private int id;

    // Starting pump state
    private PumpState pumpState = PumpState.READY;

    // Time allowed to pump
    private int pumpTime;

    // Stopping variable when the pump fills
    private boolean pumping = true;

    /**
     * Initializes the current water level and both left and right power supplies
     *
     * @param currentWaterLevel     The current water level throughout the simulation
     *                              This is shared between all pumps created and Main
     * @param leftPower             The left power supply that corresponds with a single
     *                              pump.
     * @param rightPower            The right power supply that corresponds with a single
     *                              pump.
     */
    public Pump(AtomicInteger currentWaterLevel, Power leftPower, Power rightPower)
    {
        this.currentWaterLevel = currentWaterLevel;
        this.leftPower = leftPower;
        this.rightPower = rightPower;
        id = pumpNumber++;
    }

    /**
     * The pump cycles through four states
     * while filling a tank to a maximum
     * capacity denoted in Main. Once the
     * water reaches the max level the
     * pumps stop, reset back to default
     * and print simple statistics: Number
     * of cycles the pump had and the total
     * amount of gallons pumped by this pump.
     */
    @Override
    public void run()
    {
        System.out.printf("Starting pump %d...\n", id);

        while (pumping)
        {

            if (waterOverflow())
            {
                break;
            }
            // All pump starts with READY state (GREEN).
            if (pumpState == PumpState.READY)
            {
                sleep(READY_STATE_WAIT_TIME);               // Wait for half a second
                pumpState = PumpState.WAITING;				//Move to WAITING state

                //If the pump is in a WAITING state, pump request power from adjacent power supplies
            } else if (pumpState == PumpState.WAITING)
            {
                requestPower();

                //If the pump is in the PUMPING state, it pumps water to the tank
            } else if (pumpState == PumpState.PUMPING)
            {
                // Pumping
                cycles++;	//keep track of how many times a specific pump pumped
                pumpTime = ThreadLocalRandom.current().nextInt(20,51) * 100;    // Get the next random pump time
                pump();

                // Release all power
                releasePower();
                pumpState = PumpState.CLEANING;

                // Cleaning
                sleep(pumpTime);               // Sleep for as long as the pump time for cleaning
                pumpState = PumpState.READY;
            }
        }
        // Tank is filled
        resetPump();
        printStatistics();
    }

    /**
     * Performs a sleep on the current Pump that is
     * either the wait time for the READY state (0.5 seconds)
     * or the time to cool down in the CLEANING state
     * (the number generated for pumpTime
     *
     * @param waitTime              Amount of time to wait, either 0.5 seconds or 2-5 seconds
     */
    public void sleep(int waitTime)
    {
        try
        {
            Thread.sleep(waitTime);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * During the waiting state, a pump must request power from both
     * adjacent power supplies using two separate threads for the left
     * power supply and the right power supply.
     */
    public void requestPower()
    {
        PowerRequest leftRequest = new PowerRequest(leftPower, id);
        PowerRequest rightRequest = new PowerRequest(rightPower, id);

        Thread leftThread = new Thread(leftRequest);
        Thread rightThread = new Thread(rightRequest);

        //Start the powerRequest threads
        leftThread.start();
        rightThread.start();

        /**
         * Used to update power supply connections so they can be drawn correctly
         * Using a join on both threads could not predict which one finishes
         * first, therefore not updating the power supply chords until both
         * threads finish.
         */
        while(leftThread.isAlive() || rightThread.isAlive())
        {
            if (id == leftRequest.getUserId())
            {
                hasLeftPower = true;
            }
            if (id == rightRequest.getUserId())
            {
                hasRightPower = true;
            }
        }

        // Pump has required power sources
        if (leftRequest.getUserId() == id && rightRequest.getUserId() == id)
        {
            // If power is granted by both power supplies, both right and left have power
            // This makes sure both left and right are represented as on because a thread
            // can die some time after the setting of these variables, causing the GUI
            // to look incorrect.
            hasLeftPower = true;
            hasRightPower = true;
            pumpState = PumpState.PUMPING;
            // Did not get required power, release resources
        } else
        {
            pumpState = PumpState.READY;
            releasePower();                 // Resets hasLeftPower and hasRightPower for GUI updates
        }

    }

    /**
     * Controls the amount of water to pump into the tank.
     */
    public void pump()
    {
        int waterAmountToPump = pumpTime;	// Pump increment the amount of water in the central water tank
        int waterPumped = 0;                // Current amount of water pumped
        try
        {
            // Pump!
            while (waterPumped < waterAmountToPump)
            {
                // Check to make sure we aren't over capacity
                if (waterOverflow())
                {
                    pumping = false;
                    break;
                }
                // Pump water
                currentWaterLevel.getAndAdd(waterAmountToPump / PUMPING_INCREMENTS);
                waterPumped += waterAmountToPump / PUMPING_INCREMENTS;
                gallonsPumped += waterAmountToPump / PUMPING_INCREMENTS;
                // Wait to provide smooth filling effect of the tank
                Thread.sleep( pumpTime / PUMPING_INCREMENTS);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Check that the water level is not greater than the CAPACITY of the Tan
     *
     * @return          True if the current water level exceeds the capacity,
     *                  otherwise false
     */
    public boolean waterOverflow()
    {
        return currentWaterLevel.get() >= Main.CAPACITY;
    }

    /**
     * Release the power chord colors from powered (ORANGE)
     * to not powered(LIGHT_GRAY) by setting both hasLeftPower
     * and hasRightPower to false.
     */
    public void releasePower()
    {
        hasLeftPower = false;
        hasRightPower = false;
        leftPower.releasePower(id);
        rightPower.releasePower(id);
    }

    /**
     * Display how many gallons each pump pumped and how many cycles it ran
     */
    public void printStatistics()
    {
        System.out.printf("Pump %d pumped %d gallons in %d cycles\n", id, gallonsPumped, cycles);
    }

    /**
     * Reset the pump and set it back to the READY state (GREEN)
     * Used to reset the GUI after the simulation is complete.
     */
    public void resetPump()
    {
        hasLeftPower = false;
        hasRightPower = false;
        pumpState = PumpState.READY;
    }

    /**
     * Color coded the states of the pump, by default all Pumps start with READY state which is GREEN
     *
     * @return          Corresponding color of the pump is returned base on its current states
     */
    public Color getPumpColor()
    {
        switch(pumpState)
        {
            case WAITING:
                return Color.YELLOW;
            case PUMPING:
                return Color.BLUE;
            case CLEANING:
                return Color.RED;
            default:
                return Color.GREEN;
        }
    }

    /**
     * Color code for the left power chord.
     *
     * @return              If the left chord has power it returns ORANGE otherwise it returns LIGHT_GRAY
     */
    public Color getLeftPowerColor()
    {
        if (hasLeftPower)
        {
            return Color.ORANGE;
        } else
        {
            return Color.LIGHT_GRAY;
        }
    }

    /**
     * Color code for the right power chord.
     *
     * @return              If the right chord has power it returns ORANGE otherwise it returns LIGHT_GRAY
     */
    public Color getRightPowerColor()
    {
        if (hasRightPower)
        {
            return Color.ORANGE;
        } else
        {
            return Color.LIGHT_GRAY;
        }
    }
}
