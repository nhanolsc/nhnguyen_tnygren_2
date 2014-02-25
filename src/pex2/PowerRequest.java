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


    public PowerRequest(Power requestedPowerNode, int requesterId) {
        System.out.println();
        System.out.println("STARTING POWER REQUEST THREAD FOR REQUESTER " + requesterId + " ON POWER NODE: " + requestedPowerNode.getId());
        System.out.println();
        this.requestedPowerNode = requestedPowerNode;
        this.requesterId = requesterId;
    }

    @Override
    public void run() {
        while(numberOfRequests > 0) {
            if (requestedPowerNode.canReceivePower(requesterId)) {
                requestStatus = REQUEST_SUCCESS;
                break;
            } else {
                sleep();
                numberOfRequests--;
            }
        }
        System.out.println();
        System.out.println("REQUEST FOR POWER FINISHED FOR " + requesterId + " ON POWER NODE: " + requestedPowerNode.getId());
        System.out.println();
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}