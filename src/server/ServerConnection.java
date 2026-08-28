package server;

import model.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ArrayList<ServerConnection> clients;
    private static ArrayList<GameRoomController> rooms;

    private Player player;
    private Room room;

    private boolean connected = false;

    public ServerConnection(Socket socket, ArrayList<ServerConnection> clients, ArrayList<GameRoomController> rooms) {
        try {
            this.clients = clients;
            this.rooms = rooms;
            this.socket = socket;

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void send(int code, String msg) {
        out.println(code + "_" + msg);
    }

    @Override
    public void run() {
        connected = true;
        while (connected) {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    int idx = serverResponse.indexOf("_");
                    int code = Integer.parseInt(serverResponse.substring(0, idx));
                    String msg = serverResponse.substring(idx + 1);

                    if (code >= 0) {
                        switch (code) {
                            case 1:
                                player = new Player(msg);
                                if (!connect(this)) {
                                    this.close();
                                }
                                break;

                            case 2:
                                pairRequest(this);
                                break;

                            case 5:
                                leaveRequest(this);
                                break;

                            case 204:
                                for (GameRoomController controller : rooms) {
                                    if (controller.getRoom().getRoomNumber() == room.getRoomNumber()) {
                                        controller.handleWordSubmission(this, msg);
                                        break;
                                    }
                                }
                                break;

                            case 201:
                                send(201, getOnlinePlayers());
                                break;

                            case 202:
                                send(202, getRoomPlayers(room.getRoomNumber()));
                                break;

                            case 0:
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                close();
            }
        }
    }

    public boolean connect(ServerConnection client) {
        for (ServerConnection c : clients) {
            if (c.getPlayer().getUsername().equals(client.getPlayer().getUsername())) {
                client.send(0, "Username already exists");
                return false;
            }
        }
        client.send(101, "");
        clients.add(client);
        sendToPlayers(201, getOnlinePlayers());
        return true;
    }

    public void pairRequest(ServerConnection client) {
        GameRoomController room = null;
        for (GameRoomController r : rooms) {
            if (r.isRoomAvailable()) {
                room = r;
                break;
            }
        }

        if (room != null) {
            room.addPlayer(client);
        } else {
            client.send(0, "No available room found.");
        }
    }

    public void leaveRequest(ServerConnection client) {
        clients.remove(client);
        sendToPlayers(5, client.getPlayer().getUsername());
        if (client.getRoom() != null) {
            removePlayerFromRoom(client);
        }
        sendToPlayers(201, getOnlinePlayers());
    }

    public String getOnlinePlayers() {
        StringBuilder msg = new StringBuilder();
        for (ServerConnection client : clients) {
            msg.append(client.getPlayer().getUsername()).append("-");
        }
        return msg.toString();
    }

    public String getRoomPlayers(int roomNumber) {
        for (GameRoomController room : rooms) {
            if (room.getRoom().getRoomNumber() == roomNumber) {
                return room.getRoomPlayers();
            }
        }
        return "";
    }

    public void sendToPlayers(int code, String msg) {
        for (ServerConnection client : clients) {
            client.send(code, msg);
        }
    }

    public void removePlayerFromRoom(ServerConnection client) {
        for (GameRoomController r : rooms) {
            if (r.getRoom().getRoomNumber() == client.getRoom().getRoomNumber()) {
                r.removePlayer(client);
                break;
            }
        }
    }

    public void close() {
        connected = false;
        synchronized (socket) {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
