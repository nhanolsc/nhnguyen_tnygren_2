/**
 * PumpState.java
 *
 * This class is an enum to provide the Pump class with
 * certain states defined in the project.
 */
package pex2;

/**
 * Each pump can only be in one of these four states
 */
public enum PumpState
{
    /**
     * Ready state is used to have the
     * pump wait half a second.
     */
    READY,

    /**
     * Waiting state has the pump send threads to
     * request power from adjacent power supplies.
     */
    WAITING,

    /**
     * Pumping state allows the pump to fill the tank
     * with a randomly determined amount of water and
     * pumping time.
     */
    PUMPING,

    /**
     * Cleaning state has the pump *clean* or wait for
     * the same amount of time it spent pumping water.
     */
    CLEANING
}
