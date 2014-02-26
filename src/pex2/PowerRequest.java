package pex2;

public class PowerRequest implements Runnable{

    public static final int MAX_NUMBER_REQUESTS = 10;

    // ID of power supply to be returned to requester
    private int userId = -1;

    // Requested power node
    private Power requestedPowerNode;

    // Number of tries to attain power
    private int numberOfRequests = MAX_NUMBER_REQUESTS;

    // Requester id
    private int requesterId;


    public PowerRequest(Power requestedPowerNode, int requesterId) {
        this.requestedPowerNode = requestedPowerNode;
        this.requesterId = requesterId;
    }

    @Override
    public void run() {
        while(numberOfRequests > 0) {
            if (requestedPowerNode.canReceivePower(requesterId)) {
                userId = requestedPowerNode.getUserId();
                break;
            } else {
                sleep();
                numberOfRequests--;
            }
        }
    }

    public int getUserId() {
        return userId;
    }

    public void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}