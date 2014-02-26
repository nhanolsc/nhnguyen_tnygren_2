package pex2;

public class PowerRequest implements Runnable{
	
	// The max number of that a pump can request for power.
    public static final int MAX_NUMBER_REQUESTS = 10;

    // ID of power supply to be returned to requester
    private int userId = -1;

    // Requested power node
    private Power requestedPowerNode;

    // Number of tries to attain power
    private int numberOfRequests = MAX_NUMBER_REQUESTS;

    // Requester id
    private int requesterId;

    //Constructor
    public PowerRequest(Power requestedPowerNode, int requesterId) {
        this.requestedPowerNode = requestedPowerNode;
        this.requesterId = requesterId;
    }

    
    /*
    *Thread can ask for a max of 10 request and has to wait one-tenth of a second between 
    *each request.
    *If request is granted, it will break this loop 
    */
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
    // Thread wait one-tenth of a second before making another request
    public void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    /*---------------------------Getters-----------------------------*/

    public int getUserId() {
        return userId;
    }
    

}