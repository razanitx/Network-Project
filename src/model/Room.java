package model;

import java.util.ArrayList;

public class Room {
    private int roomNumber;
    private final ArrayList<Player> players;
    private boolean available;
    
    public Room(int roomNumber, int maxNumOfPlayers) {
        this.roomNumber = roomNumber;
        this.players = new ArrayList<>(maxNumOfPlayers);
        this.available = true;
    }
    
    public int getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }
    
    public void removePlayer(Player player) {
        players.remove(player);
    }
    
}
