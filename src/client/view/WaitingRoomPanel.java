package client.view;

import client.controller.ClientConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class WaitingRoomPanel extends JPanel {
    private MainFrame parent;
    private ClientConnection client;

    private JList<String> waitingPlayersList;
    private JLabel statusLabel;
    private Image backgroundImage;

    private JLabel timerLabel;
    private Timer countdownTimer;
    private int remainingSeconds = 30;

    public WaitingRoomPanel(MainFrame parent) {
        this.parent = parent;

        URL imageURL = getClass().getResource("/images/waiting_background.png");
        if (imageURL != null) {
            backgroundImage = new ImageIcon(imageURL).getImage();
        } else {
            backgroundImage = new ImageIcon("src/client/view/waiting_background.png").getImage();
        }

        initComponents();
    }

    public void setClient(ClientConnection client) {
        this.client = client;
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setOpaque(false);

        JPanel centerPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        waitingPlayersList = new JList<>();
        waitingPlayersList.setOpaque(false);
        waitingPlayersList.setBackground(new Color(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(waitingPlayersList);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBounds(20, 60, 200, 200);
        centerPanel.add(scrollPane);

        // تايمر يظهر فوق يمين الشاشة
        timerLabel = new JLabel("30", SwingConstants.RIGHT);
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        timerLabel.setBounds(650, 20, 100, 30);
        centerPanel.add(timerLabel);

        JButton leaveButton = new JButton("خروج");
        leaveButton.setOpaque(false);
        leaveButton.setBounds(20, 270, 100, 30);
        leaveButton.addActionListener(e -> {
            if (client != null) client.leaveRequest();
            parent.showPanel("إتصال");
        });
        centerPanel.add(leaveButton);

        add(centerPanel, BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.setRollover(true);
        toolbar.setOpaque(false);
        statusLabel = new JLabel();
        toolbar.add(statusLabel);
        add(toolbar, BorderLayout.SOUTH);
    }

    public void updatePlayersList(String[] players) {
        waitingPlayersList.setListData(players);
        restartCountdown(); // ✅ إعادة التايمر كل مرة لاعب جديد يدخل
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public void startCountdown() {
        remainingSeconds = 30;
        timerLabel.setText(String.valueOf(remainingSeconds)); // ✅ تحديث الليبل مباشرة

        if (countdownTimer != null) countdownTimer.stop();

        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingSeconds--;
                timerLabel.setText(String.valueOf(remainingSeconds));
                if (remainingSeconds <= 0) {
                    countdownTimer.stop();
                }
            }
        });

        countdownTimer.start();
    }

    private void restartCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        startCountdown();
    }
}