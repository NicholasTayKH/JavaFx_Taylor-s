package Classes;

public class BookingData
{
    private String UserID;
    private String Username;
    private String startDate;
    private String returnDate;
    private String roomType;
    private int TotalNight;
    private int roomAmount;
    private String travelPackage;
    private String name;
    private String icOrPassport;
    private double paymentAmount;
    private String paymentMethod;
    private String phone_number;


    // Getters and Setters
    public String getUserID() {
        return UserID;
    }
    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getUsername() {
        return Username;
    }
    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public int getTotalNight(){return TotalNight;}
    public void setTotalNight(int totalNight){this.TotalNight = totalNight;}

    public String getRoomType() {
        return roomType;
    }
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getTravelPackage() {
        return travelPackage;
    }
    public void setTravelPackage(String travelPackage) {
        this.travelPackage = travelPackage;
    }

    public int getRoomAmount() {
        return roomAmount;
    }

    public void setRoomAmount(int roomAmount) {
        this.roomAmount = roomAmount;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIcOrPassport() {
        return icOrPassport;
    }
    public void setIcOrPassport(String icOrPassport) {
        this.icOrPassport = icOrPassport;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPhone_number(){return phone_number;}
    public  void setPhone_number(String phone_number){this.phone_number = phone_number;}

    @Override
    public String toString() {
        return "BookingData{" +
                "UserID='" + UserID + '\'' +
                "Username='" + Username + '\'' +
                "startDate='" + startDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", Total night='" + TotalNight + '\'' +
                ", roomType='" + roomType + '\'' +
                ", Total_room=  " + roomAmount + '\''+
                ", travelPackage='" + travelPackage + '\'' +
                ", name='" + name + '\'' +
                ", icOrPassport='" + icOrPassport + '\'' +
                ", Phone number='" + phone_number + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }



}
