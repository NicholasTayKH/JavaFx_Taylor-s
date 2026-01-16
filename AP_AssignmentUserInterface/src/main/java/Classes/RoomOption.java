package Classes;

public class RoomOption {
    private final String roomType;
    private final int price;

    public RoomOption(String roomType, int price) {
        this.roomType = roomType;
        this.price = price;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getPrice() {
        return price;
    }
}

