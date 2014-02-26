package pex2;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Pump implements Runnable {

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

	// Has power
	private boolean hasLeftPower = false;
	private boolean hasRightPower = false;

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

	// Constructor
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

			if (waterOverflow()) {
				break;
			}
			// All pump starts with READY state (GREEN). 
			if (pumpState == PumpState.READY) {
				sleep(READY_STATE_WAIT_TIME);               // Wait for half a second
				pumpState = PumpState.WAITING;				//Move to WAITING state
				
				//If the pump is in a WAITING state, pump request power from adjacent power supplies
			} else if (pumpState == PumpState.WAITING) {
				waitForPower();
				
				//If the pump is in the PUMPING state, it pumps water to the tank
			} else if (pumpState == PumpState.PUMPING) {
				// Pumping
				cycles++;	//keep track of how many times a specific pump pumped
				pumpTime = ThreadLocalRandom.current().nextInt(20,51) * 100;    // Get the next random pump time
				pump();
				// Release all power
				releasePower();                 // Resets hasLeftPower and hasRightPower for GUI updates
				leftPower.releasePower(id);
				rightPower.releasePower(id);
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

	// This method get passed in the amount of time that the pump has to wait
	public void sleep(int waitTime) {
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//This is the Waiting State, it is where the pumps request power for adjacent supplies.
	public void waitForPower() {
		PowerRequest left = new PowerRequest(leftPower, id);
		PowerRequest right = new PowerRequest(rightPower, id);

		Thread leftThread = new Thread(left);
		Thread rightThread = new Thread(right);
		
		//Start the powerRequest threads
		leftThread.start();
		rightThread.start();

		// Used to update power supply connections so they can be drawn correctly
		while(leftThread.isAlive() || rightThread.isAlive()) {
			if (id == leftPower.getUserId()) {
				hasLeftPower = true;
			}
			if (id == rightPower.getUserId()) {
				hasRightPower = true;
			}
		}

		// Pump has required power sources
		if (left.getUserId() == id && right.getUserId() == id) {
			hasLeftPower = true;
			hasRightPower = true;
			pumpState = PumpState.PUMPING;
			// Did not get required power, release resources
		} else {
			pumpState = PumpState.READY;
			releasePower();                 // Resets hasLeftPower and hasRightPower for GUI updates
			leftPower.releasePower(id);
			rightPower.releasePower(id);
		}

	}
	// Pumping state
	public void pump() {
		int waterAmountToPump = pumpTime;	//Pump increment the amount of water in the central water tank in 100 equal increments
		int waterPumped = 0;
		try {
			// Pump!
			while (waterPumped < waterAmountToPump) {
				// Check to make sure we aren't over capacity
				if (waterOverflow()) {
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

	// Make sure the water level is not greater than the CAPACITY of the Tank
	public boolean waterOverflow() {
		return currentWaterLevel.get() >= Main.CAPACITY;
	}
	
	public void releasePower() {
		hasLeftPower = false;
		hasRightPower = false;
	}
	
	//Reset the pump and set it back to the READY state (GREEN)
	public void resetPump() {
		hasLeftPower = false;
		hasRightPower = false;
		pumpState = PumpState.READY;
	}
	
	// Color coded the states of the pump, by default all Pumps start with READY state which is GREEN. 
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

	// Color code for the left power wires.  If the wire has power its ORANGE otherwise its LIGHT_GRAY
	public Color getLeftPowerColor() {
		if (hasLeftPower) {
			return Color.ORANGE;
		} else {
			return Color.LIGHT_GRAY;
		}
	}

	// Color code for the left power wires.  If the wire has power its ORANGE otherwise its LIGHT_GRAY
	public Color getRightPowerColor() {
		if (hasRightPower) {
			return Color.ORANGE;
		} else {
			return Color.LIGHT_GRAY;
		}
	}

	//Display how many gallons each pump pumped and how many cycles it ran 
	public void printStatistics() {
		System.out.printf("Pump %d pumped %d gallons in %d cycles\n", id, gallonsPumped, cycles);
	}
}
