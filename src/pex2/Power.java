package pex2;

public class Power {

    // Denote a power source is not being used
    public static final int NOT_IN_USE = -1;

    // Shared Variables with PowerRequest
    private int user = NOT_IN_USE;

    public synchronized boolean canReceivePower(int requesterId) {
        if (user == NOT_IN_USE) {
            user = requesterId;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void releasePower(int pumpId) {
        if (pumpId == user) {
            user = NOT_IN_USE;
        }
    }

    public synchronized int getUserId() {
        return user;
    }
}
