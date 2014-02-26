/**
 * PowerRequest.java
 *
 * Pumps request power from the adjacent power supplies
 * by issuing two power request threads. Each instance requests
 * power from a single power supply
 */
package pex2;

public class PowerRequest implements Runnable
{
    // Max number of requests a Power Request thread can attempt
    public static final int MAX_NUMBER_REQUESTS = 10;

    // ID of Pump using requested power supply to be returned to requester
    private int userId = -1;

    // Requested power node
    private Power requestedPowerNode;

    // Requesting Pump's id
    private int requesterId;

    /**
     * Initializes the power request thread
     *
     * @param requestedPowerNode        Node that is being asked for power
     * @param requesterId               Pump that is requesting power
     */
    public PowerRequest(Power requestedPowerNode, int requesterId)
    {
        this.requestedPowerNode = requestedPowerNode;
        this.requesterId = requesterId;
    }

    /**
     * Thread can ask for a max of 10 request and has to wait one-tenth of a second between
     * each request. If request is granted, the pump id is returned from the power supply to
     * be accessed by the pump after the thread terminates.
     */
    @Override
    public void run()
    {
        int numberOfRequests = MAX_NUMBER_REQUESTS;
        while(numberOfRequests > 0)
        {
            if (requestedPowerNode.canReceivePower(requesterId))
            {
                userId = requesterId;
                break;
            } else
            {
                sleep();        // Wait one-tenth a second before next request
                numberOfRequests--;
            }
        }
    }

    /**
     * Power Requests must wait one-tenth of a second before
     * making another request.
     */
    public void sleep()
    {
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Used to return the id of the pump that is using the requested
     * power supply. This is the only communication between Pumps and
     * Power supplies.
     *
     * @return              Pump id using the requested power supply
     */
    public int getUserId()
    {
        return userId;
    }
}