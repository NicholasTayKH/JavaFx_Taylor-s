package org.example.assignment;

public class DashboardRoomItem {
    private final String id;          // Could be bookingId or roomId
    private final String name;        // Display name
    private final String status;      // "Booked" or "Available"
    private final String imagePath;

    // Booking detail fields (optional)
    private String roomType;
    private String startDate;
    private String endDate;
    private String customerName;
    private String packageType;

    // Base constructor
    public DashboardRoomItem(String id, String name, String status, String imagePath) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.imagePath = imagePath;
    }

    // Extended constructor for booking detail use
    public DashboardRoomItem(String id, String name, String status, String imagePath,
                             String roomType, String startDate, String endDate,
                             String customerName, String packageType) {
        this(id, name, status, imagePath);
        this.roomType = roomType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerName = customerName;
        this.packageType = packageType;
    }

    public String getId() {
        return id;
    }

    public String getAll() {
        return name + " (" + id + ") - " + status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }

    // New getters
    public String getRoomType() {
        return roomType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPackageType() {
        return packageType;
    }
}
