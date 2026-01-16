package Classes;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomManager {

    private static List<Room> roomList = new ArrayList<>();

    // ✅ Load rooms from available_rooms.txt with 5 fields (RoomNumber, RoomType, Status, Image, Price)
    public static void loadRooms() {
        roomList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("available_rooms.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);  // ⬅ Fixed to split into 5 parts
                if (parts.length == 5) {
                    String roomNumber = parts[0].trim();
                    String roomType = parts[1].trim();
                    String status = parts[2].trim();
                    String image = parts[3].trim();
                    String price = parts[4].trim();

                    Room room = new Room(roomNumber, roomType, status, image, price);
                    roomList.add(room);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Room> getAvailableRoomsByType(String roomType) {
        return roomList.stream()
                .filter(r -> r.getRoomType().equalsIgnoreCase(roomType.trim())
                        && r.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());
    }

    public static List<Room> getAllAvailableRooms() {
        return roomList.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase("Available"))
                .collect(Collectors.toList());
    }

    public static List<Room> getAllRooms() {
        return new ArrayList<>(roomList);
    }

    public static void markRoomsAsUnavailable(List<Room> roomsToBook) {
        for (Room room : roomsToBook) {
            room.setStatus("Unavailable");
        }
        saveRooms();
    }

    public static void markRoomsAsAvailable(List<Room> roomsToFree) {
        for (Room room : roomsToFree) {
            room.setStatus("Available");
        }
        saveRooms();
    }

    // ✅ Save rooms with all 5 fields including price
    public static void saveRooms() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("available_rooms.txt"))) {
            for (Room room : roomList) {
                writer.write(room.getRoomNumber() + "," +
                        room.getRoomType() + "," +
                        room.getStatus() + "," +
                        room.getImage() + "," +
                        room.getPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Room getRoomByNumber(String roomNumber) {
        for (Room room : roomList) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public static long countBookedRooms(String name, LocalDate startDate, LocalDate returnDate) {
        long count = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader("BookingInfo.txt"))) {
            String line;
            String currentRoomType = null;
            LocalDate currentStartDate = null;
            LocalDate currentReturnDate = null;
            int currentRoomCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("Room Type:")) {
                    currentRoomType = line.substring("Room Type:".length()).trim();
                } else if (line.startsWith("Start Date:")) {
                    String dateStr = line.substring("Start Date:".length()).trim();
                    currentStartDate = LocalDate.parse(dateStr, formatter);
                } else if (line.startsWith("Return Date:")) {
                    String dateStr = line.substring("Return Date:".length()).trim();
                    currentReturnDate = LocalDate.parse(dateStr, formatter);
                } else if (line.startsWith("Number of Rooms:")) {
                    String roomNumStr = line.substring("Number of Rooms:".length()).trim();
                    try {
                        currentRoomCount = Integer.parseInt(roomNumStr);
                    } catch (NumberFormatException e) {
                        currentRoomCount = 1;
                    }
                } else if (line.startsWith("--------------------------------------------------")) {
                    if (currentRoomType != null && currentStartDate != null && currentReturnDate != null) {
                        boolean roomMatches = currentRoomType.equalsIgnoreCase(name);
                        boolean dateOverlaps = !returnDate.isBefore(currentStartDate) && !startDate.isAfter(currentReturnDate);

                        if (roomMatches && dateOverlaps) {
                            count += currentRoomCount;
                        }
                    }
                    currentRoomType = null;
                    currentStartDate = null;
                    currentReturnDate = null;
                    currentRoomCount = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}
