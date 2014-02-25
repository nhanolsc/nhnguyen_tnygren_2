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

    // This pump's left and right power supplies
    private Power leftPower;
    private Power rightPower;

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

    public Pump(AtomicInteger currentWaterLevel, Power leftPower, Power rightPower) {
        this.currentWaterLevel = currentWaterLevel;
        this.leftPower = leftPower;
        this.rightPower = rightPower;
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
                System.out.println("PUMP: " + id + " IS RELEASING POWER");
                leftPower.releasePower();
                rightPower.releasePower();
                pumpState = PumpState.CLEANING;
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
            pumpState = PumpState.WAITING;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForPower() {
        System.out.println();
        System.out.println("PUMP: " + id + " IS WAITING FOR POWER FROM " + leftPower.getId() + " AND " + rightPower.getId());
        PowerRequest left = new PowerRequest(leftPower, id);
        PowerRequest right = new PowerRequest(rightPower, id);

        Thread leftThread = new Thread(left);
        Thread rightThread = new Thread(right);
        leftThread.start();
        rightThread.start();

        try {
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("PUMP: " + id);
        System.out.println("PUMP : " + id + " LEFT REQUEST: " + left.getRequestStatus());
        System.out.println("PUMP : " + id + " RIGHT REQUEST: " + right.getRequestStatus());
        System.out.println();

        if (left.getRequestStatus() == PowerRequest.REQUEST_SUCCESS && right.getRequestStatus() == PowerRequest.REQUEST_SUCCESS) {
            pumpState = PumpState.PUMPING;
        } else {
            pumpState = PumpState.READY;
            System.out.println("RELEASING POWER FROM PUMP: " + id);
            leftPower.releasePower();
            rightPower.releasePower();
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Color getPumpColor() {
        switch(pumpState) {
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

    public void printPowerSupplies() {
        System.out.println("PUMP: " + id);
        System.out.println("LEFT POWER ID: " + leftPower.getId());
        System.out.println("RIGHT POWER ID: " + rightPower.getId());
        System.out.println();
    }

    public void printStatistics() {
        System.out.printf("Pump %d pumped %d gallons in %d cycles\n", id, gallonsPumped, cycles);
    }
}
