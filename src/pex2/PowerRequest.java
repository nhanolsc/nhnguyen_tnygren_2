package pex2;

/**
 * Created by nhhnMAC on 2/24/14.
 */
public class PowerRequest implements Runnable{

    public static final int REQUEST_SUCCESS = 1;
    public static final int REQUEST_DENIED = -1;
    public static final int MAX_NUMBER_REQUESTS = 10;
    // Request status for power
    private int requestStatus = REQUEST_DENIED;

    // Requested power node
    private Power requestedPowerNode;

    // Number of tries to attain power
    private int numberOfRequests = MAX_NUMBER_REQUESTS;

    // Requester's id
    private int requesterId;


    public PowerRequest(Power requestedPowerNode, int requesterId, int canUsePower) {
        this.requestedPowerNode = requestedPowerNode;
        this.requesterId = requesterId;
        this.requestStatus = canUsePower;
    }

    @Override
    public void run() {
        while(numberOfRequests > 0) {
            if (requestedPowerNode.canReceivePower(requesterId)) {
                requestStatus = REQUEST_SUCCESS;
            } else {
                sleep();
                numberOfRequests--;
            }
        }
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}