package server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import model.Room;

public class GameServer {

    public static final int NUMBER_OF_ROOMS = 5;
    public static final int SERVER_PORT = 9090;
    public static final int NUMBER_OF_PLAYERS_PER_ROOM = 4;

    private static final ArrayList<ServerConnection> clients = new ArrayList<>();
    private static final ArrayList<GameRoomController> rooms = new ArrayList<>();

    public static void initServer() {
        for (int i = 1; i <= NUMBER_OF_ROOMS; i++) {
            rooms.add(new GameRoomController(new Room(i, NUMBER_OF_PLAYERS_PER_ROOM)));
        }
    }

    public static void main(String[] args) throws IOException {
        initServer();                
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

        while (true) {
            System.out.println("Waiting for client connection");
            Socket client = serverSocket.accept();
            System.out.println("Connected to client");
            ServerConnection clientThread = new ServerConnection(client, clients, rooms); 
            new Thread(clientThread).start();
        }
    }
}
