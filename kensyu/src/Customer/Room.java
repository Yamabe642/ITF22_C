package Customer;

public class Room {

    private int roomId;
    private int capacity;
    private String machine;
    private boolean available;

    public Room() {}

    public Room(int roomId, int capacity, String machine, boolean available) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.machine = machine;
        this.available = available;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getMachine() {
        return machine;
    }

    public boolean isAvailable() {
        return available;
    }
}