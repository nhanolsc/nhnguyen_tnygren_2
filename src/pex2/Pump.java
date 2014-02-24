package pex2;

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

    // Current Water Level
    private AtomicInteger currentWaterLevel;

    // Statistics
    private int gallonsPumped = 0;
    private int cycles = 0;

    // Pump id
    private int id;

    // Starting pump state
    private PumpState pumpState = PumpState.READY;

    // Time allowed to pump
    private int pumpTime;

    public Pump(AtomicInteger currentWaterLevel) {
        this.currentWaterLevel = currentWaterLevel;
        id = pumpNumber++;
    }

    @Override
    public void run() {

        while (true) {

            if (pumpState == PumpState.READY) {

                try {
                    System.out.println("\nREADY STATE: current water level is " + Integer.toString(currentWaterLevel.get()) + "\n");
                    Thread.sleep(500);
                    pumpState = PumpState.PUMPING;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if (pumpState == PumpState.WAITING) {
                // Throw out two threads to request power

            } else if (pumpState == PumpState.PUMPING) {
                pumpTime = ThreadLocalRandom.current().nextInt(2,6) * 1000;
                int waterAmountToPump = ThreadLocalRandom.current().nextInt(20, 51) * 100;
                System.out.println("\nPUMPING STATE: pumptime of: " + Integer.toString(pumpTime) + " and amount to pump: " + Integer.toString(waterAmountToPump) + "\n");
                int waterPumped = 0;
                try {
                    // Pump!
                    while (waterPumped > waterAmountToPump) {
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

                pumpState = PumpState.CLEANING;
            } else if (pumpState == PumpState.CLEANING) {

                try {
                    System.out.println("CLEANING STATE: cleaning for " + Integer.toString(pumpTime));
                    Thread.sleep(pumpTime);
                    pumpState = PumpState.READY;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
