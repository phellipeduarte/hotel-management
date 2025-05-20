package exceptions;

public class NotAvailable extends Exception {
    @Override
    public String toString()
    {
        return "Room not available.";
    }
}