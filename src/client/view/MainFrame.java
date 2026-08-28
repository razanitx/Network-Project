package client.view;

import client.controller.ClientConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private ConnectPanel connectPanel;
    private WaitingRoomPanel waitingPanel;
    private GamePanel gamePanel;
    private GameResultPanel resultPanel; // ✅ شاشة النتيجة (بتنتهي لاحقاً)

    public MainFrame() {
        setTitle("لعبكة");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ClientConnection client = connectPanel.getClient();
                if (client != null && client.isConnected()) {
                    client.leaveRequest();  // إخطار السيرفر بالمغادرة
                }
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        connectPanel = new ConnectPanel(this);
        waitingPanel = new WaitingRoomPanel(this);
        gamePanel = new GamePanel(this);
        
        resultPanel = new GameResultPanel(List.of()); // ✅ نحط شي فاضي مؤقتًا، راح نحدثه بعدين

        mainPanel.add(connectPanel, "إتصال");
        mainPanel.add(waitingPanel, "إنتظار");
        mainPanel.add(gamePanel, "game");
        mainPanel.add(resultPanel, "النتائج");

        add(mainPanel);
        setVisible(true);
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    public void startWaitingRoom(ClientConnection client) {
        waitingPanel.setClient(client);
        showPanel("إنتظار");
    }

    public void startGame(ClientConnection client, String words) {
        gamePanel.setClient(client, words);
        showPanel("game");
    }

    public ConnectPanel getConnectPanel() {
        setTitle("لعبكة - غرفة الاتصال");
        return connectPanel;
    }

    public WaitingRoomPanel getWaitingPanel() {
        setTitle("لعبكة - غرفة الانتظار");

        return waitingPanel;
    }

    public GamePanel getGamePanel() {
        setTitle("لعبكة - غرفة اللعب");

        return gamePanel;
    }

    public GameResultPanel getResultPanel() {
        setTitle("لعبكة - النتائج");

        
        return resultPanel;
    }

    public void showGameResult(List<String> winners) {
        resultPanel = new GameResultPanel(winners); // نعيد إنشاء شاشة النتيجة بالفائزين
        mainPanel.add(resultPanel, "النتائج"); // نضيفها للبانل الرئيسي
        showPanel("النتائج"); // نعرضها
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }

}