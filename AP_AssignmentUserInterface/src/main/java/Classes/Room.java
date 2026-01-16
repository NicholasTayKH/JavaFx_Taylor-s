package Classes;

public class Room {
    private String roomNumber;
    private String roomType;
    private String status;
    private String image;
    private String price;  // ✅ New field

    // ✅ Updated Constructor with price
    public Room(String roomNumber, String roomType, String status, String image, String price) {
        this.roomNumber = roomNumber.trim();
        this.roomType = roomType.trim();
        this.status = status.trim();
        this.image = image.trim();
        this.price = price.trim();
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {  // ✅ New getter
        return price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(String price) {  // ✅ Optional setter
        this.price = price;
    }
}
