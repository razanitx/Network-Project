package client.controller;

import client.view.MainFrame;
import model.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

public class ClientConnection implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Player player;
    private int roomNumber;
    private boolean connected;

    private final MainFrame mainFrame;

    public ClientConnection(Socket socket, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.connected = false;

        try {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean connect(String name) {
        if (isConnected()) {
            this.player = new Player(name);
            send(1, name);
            return true;
        } else {
            return false;
        }
    }

    public void pairRequest() {
        send(2, "");
    }

    public void leaveRequest() {
        send(5, "");
        close();
    }

    public void sendAnswer(String answer) {
        send(204, answer);
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(int code, String msg) {
        out.println(code + "_" + msg);
    }

    @Override
    public void run() {
        connected = true;
        while (connected) {
            String serverResponse;
            try {
                while ((serverResponse = in.readLine()) != null) {
                    int idx = serverResponse.indexOf("_");
                    int code = Integer.parseInt(serverResponse.substring(0, idx));
                    String msg = serverResponse.substring(idx + 1);

                    switch (code) {
                        case 101: // Connect Success
                            mainFrame.getConnectPanel().showMsg("تم الإتصال بنجاح !");
                            mainFrame.getConnectPanel().connectButton.setEnabled(false);
                            mainFrame.getConnectPanel().serverIPTextField.setEditable(false);
                            mainFrame.getConnectPanel().playButton.setEnabled(true);
                            break;

                        case 102: // Joined Room
                            this.roomNumber = Integer.parseInt(msg);
                            mainFrame.getWaitingPanel().setClient(this);
                            mainFrame.getWaitingPanel().startCountdown(); // ✅ شغّل التايمر
                            mainFrame.showPanel("إنتظار");
                            break;

                        case 3: // Player Joined Message
                            mainFrame.getWaitingPanel().updateStatus(msg);
                            break;

                        case 4: // Start/Next Round
                            int round = Integer.parseInt(msg);

                            if (round > 4) {
                                mainFrame.showGameResult(List.of(player.getUsername())); // ✅ نستخدم showGameResult
                                JOptionPane.showMessageDialog(null, "🎉 مبروك! لقد أنهيت جميع المراحل!", "النتائج", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }

                            if (mainFrame.getGamePanel().getClient() == null) {
                                // الجولة الأولى
                                mainFrame.startGame(this, msg);
                                mainFrame.getGamePanel().updateForNewRound(round);
                                mainFrame.showPanel("game");
                            } else {
                                // باقي الجولات
                                mainFrame.getGamePanel().updateForNewRound(round);
                            }
                            break;

                        case 5: // Player Left
                            if (mainFrame.getGamePanel().getClient() != null)
                                mainFrame.getGamePanel().updateStatus(msg + " left the game.");
                            if (mainFrame.getWaitingPanel() != null)
                                mainFrame.getWaitingPanel().updateStatus(msg + " left the room.");
                            break;

                      case 6: // Game Ended
    if (msg.startsWith("END_WINNERS:")) {
        String winnersString = msg.substring("END_WINNERS:".length());
        List<String> winners = Arrays.asList(winnersString.split(","));
        mainFrame.showGameResult(winners); // ✅ عرض الفائزين مع خلفية النهاية
    } else if (msg.startsWith("END_DRAW:")) {
        String drawString = msg.substring("END_DRAW:".length());
        String[] drawPlayers = drawString.split(",");
        JOptionPane.showMessageDialog(null, "🤝 تعادل بين: " + String.join(" و ", drawPlayers), "النتائج", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.showPanel("إتصال"); // ✅ يرجع مباشرة لواجهة الاتصال بدون عرض صورة نهاية
    } else if (msg.startsWith("END_NO_WINNER:")) {
        String noWinnerMessage = msg.substring("END_NO_WINNER:".length());
        JOptionPane.showMessageDialog(null, noWinnerMessage, "النتائج", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.showPanel("إتصال");
    } else {
        JOptionPane.showMessageDialog(null, msg, "النتائج", JOptionPane.INFORMATION_MESSAGE);
        mainFrame.showPanel("إتصال");
    }
    break;


                        case 201:
                            mainFrame.getConnectPanel().updatePlayersList(msg.split("-"));
                            break;

                        case 202:
                            mainFrame.getWaitingPanel().updatePlayersList(msg.split("-"));
                            break;

                        case 203:
                            mainFrame.getGamePanel().updatePlayersList(msg.split("-"));
                            break;

                        case 205:
                            mainFrame.getGamePanel().appendAnswer(msg);
                            break;

                        case 999: // الكلاينت الفائز يرسل النتيجة للكل
                            send(6, msg);
                            break;

                        case 0:
                            mainFrame.getConnectPanel().showError(msg);
                            break;
                    }
                }
            } catch (Exception e) {
                close();
            }
        }
    }

 

    public void close() {
        connected = false;
        synchronized (socket) {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
