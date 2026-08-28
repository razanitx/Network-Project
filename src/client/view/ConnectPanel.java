package client.view;

import client.controller.ClientConnection;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics;

public class ConnectPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("src/client/view/background.png").getImage();

    private MainFrame parent;
    private ClientConnection client;

    public JTextField serverIPTextField, serverPortTextField, usernameTextField;
    public JButton connectButton, playButton;
    private JList<String> playersList;

    private final int serverPort = 9090;

    public ConnectPanel(MainFrame parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(" ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false); // ✨ خلفية شفافة
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel userPanel = new JPanel();
        userPanel.setOpaque(false); // ✨ خلفية شفافة
        userPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#ffdfdf"), 3));

        serverIPTextField = new JTextField("127.0.0.1", 15);
        serverPortTextField = new JTextField("9090", 5);
        serverPortTextField.setEditable(false);
        usernameTextField = new JTextField(15);
        connectButton = new JButton("إتصال");

        // ✨ إنشاء المتغيرات المرتبطة بالليبلز
        JLabel labelIP = new JLabel("IP Address:");
        JLabel labelPort = new JLabel("Port:");

        // ✨ إضافتها للواجهة
        userPanel.add(labelIP);
        userPanel.add(serverIPTextField);
        userPanel.add(labelPort);
        userPanel.add(serverPortTextField);

        // ✨ إخفاؤها فقط (ما تنحذف من الذاكرة)
        labelIP.setVisible(false);
        serverIPTextField.setVisible(false);
        labelPort.setVisible(false);
        serverPortTextField.setVisible(false);

        userPanel.add(new JLabel("اللاعب:"));
        userPanel.add(usernameTextField);
        userPanel.add(connectButton);

        connectButton.addActionListener(e -> connect());
        add(userPanel, BorderLayout.CENTER);

        JPanel playersPanel = new JPanel(new BorderLayout());
        playersPanel.setOpaque(false); // ✨ خلفية شفافة
playersPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createLineBorder(Color.decode("#ffdfdf"), 3),
    "قائمة المتصلين:"
));

        playersList = new JList<>();
        playersPanel.add(new JScrollPane(playersList), BorderLayout.CENTER);

        playButton = new JButton("إبدأ اللعب");
        playButton.setEnabled(false);
        playersPanel.add(playButton, BorderLayout.SOUTH);

        playButton.addActionListener(e -> {
            client.pairRequest();
            parent.showPanel("إنتظار");
        });

        add(playersPanel, BorderLayout.SOUTH);
    }

    private void connect() {
        String username = usernameTextField.getText().trim();
        String serverIP = serverIPTextField.getText().trim();

        if (username.isEmpty() || serverIP.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        try {
            Socket socket = new Socket(InetAddress.getByName(serverIP), serverPort);
            client = new ClientConnection(socket, parent);
            if (client.connect(username)) {
                new Thread(client).start();
                connectButton.setEnabled(false);
                serverIPTextField.setEditable(false);
                playButton.setEnabled(true);
            }
        } catch (Exception e) {
            showError("Failed to connect to server.");
            if (client != null) client.close();
        }
    }

    public void showMsg(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public void showError(String error) {
        JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void updatePlayersList(String[] users) {
        playersList.setListData(users);
    }

    public ClientConnection getClient() {
        return client;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JTextField getServerIPTextField() {
        return serverIPTextField;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
