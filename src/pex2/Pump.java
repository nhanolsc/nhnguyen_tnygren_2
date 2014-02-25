package pex2;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nhhnMAC on 2/24/14.
 */
public class Pump implements Runnable {

    // Pumping increments
    public static final int PUMPING_INCREMENTS = 100;

    // Used for pump ID
    private static int pumpNumber = 0;

    // Shared Variables with Main
    private AtomicInteger currentWaterLevel;
    private ArrayList<Color> pumpColor;

    // This pump's left and right power supplies
    private Power leftPower;
    private Power rightPower;

    // Used for the return code of both power request threads
    private int canUseLeft = PowerRequest.REQUEST_DENIED;
    private int canUseRight = PowerRequest.REQUEST_DENIED;

    // Statistics
    private int gallonsPumped = 0;
    private int cycles = 0;

    // Pump id
    private int id;

    // Starting pump state
    private PumpState pumpState = PumpState.READY;

    // Time allowed to pump
    private int pumpTime;

    // Stopping variable
    private boolean pumping = true;

    public Pump(AtomicInteger currentWaterLevel, ArrayList<Color> pumpColor, Power leftPump, Power rightPump) {
        this.currentWaterLevel = currentWaterLevel;
        this.pumpColor = pumpColor;
        this.leftPower = leftPump;
        this.rightPower = rightPump;
        id = pumpNumber++;
    }

    @Override
    public void run() {
        System.out.printf("Starting pump %d...\n", id);
        while (pumping) {

            if (pumpState == PumpState.READY) {
                ready();
            } else if (pumpState == PumpState.WAITING) {
                waitForPower();
            } else if (pumpState == PumpState.PUMPING) {
                cycles++;
                pumpTime = ThreadLocalRandom.current().nextInt(2,6) * 1000;
                pump();
//                leftPower.releasePower();
//                rightPower.releasePower();
                pumpState = PumpState.CLEANING;
                pumpColor.add(id, Color.RED);
            } else if (pumpState == PumpState.CLEANING) {
                clean();
            }
        }

        printStatistics();
    }

    public void ready() {
        try {
//                    System.out.println("\nREADY STATE: current water level is " + Integer.toString(currentWaterLevel.get()) + "\n");
            Thread.sleep(500);
            // TODO: change this
            pumpState = PumpState.PUMPING;
            pumpColor.add(id, Color.BLUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForPower() {
        Thread left = new Thread(new PowerRequest(leftPower, id, canUseLeft));
        Thread right = new Thread(new PowerRequest(rightPower, id, canUseRight));

        left.start();
        right.start();

        try {
            left.join();
            right.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void pump() {
        int waterAmountToPump = ThreadLocalRandom.current().nextInt(20, 51) * 100;
//                System.out.println("\nPUMPING STATE: pumptime of: " + Integer.toString(pumpTime) + " and amount to pump: " + Integer.toString(waterAmountToPump) + "\n");
        int waterPumped = 0;
        try {
            // Pump!
            while (waterPumped < waterAmountToPump) {
                // Check to make sure we aren't over capacity
                if (currentWaterLevel.get() >= Main.CAPACITY) {
                    pumping = false;
                    break;
                }
                // Pump water
                currentWaterLevel.getAndAdd(waterAmountToPump / PUMPING_INCREMENTS);
                waterPumped += waterAmountToPump / PUMPING_INCREMENTS;
                gallonsPumped += waterAmountToPump / PUMPING_INCREMENTS;
                // Wait
                Thread.sleep( pumpTime / PUMPING_INCREMENTS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        try {
//                    System.out.println("CLEANING STATE: cleaning for " + Integer.toString(pumpTime));
            Thread.sleep(pumpTime);
            pumpState = PumpState.READY;
            pumpColor.add(id, Color.GREEN);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printStatistics() {
        System.out.printf("Pump %d pumped %d gallons in %d cycles\n", id, gallonsPumped, cycles);
    }
}
