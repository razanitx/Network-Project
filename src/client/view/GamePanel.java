package client.view;

import client.controller.ClientConnection;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class GamePanel extends JPanel {

    private MainFrame parent;
    private ClientConnection client;

    private JTextField answerTextField;
    private JTextPane answersTextPane;
    private JLabel statusLabel;
    private JList<String> playersList;

    private Style black, green;
    private String lastSubmittedWord = "";

    private QuestionBackgroundPanel questionBackgroundPanel;
    private JLabel roundTimerLabel;
    private Timer roundTimer;
    private int roundSeconds = 30;

    public GamePanel(MainFrame parent) {
        this.parent = parent;
        initComponents();
    }

    public void setClient(ClientConnection client, String words) {
        this.client = client;
    }

    public ClientConnection getClient() {
        return client;
    }

    private void initComponents() {
        // خلفية ناعمة
        Color backgroundColor = Color.decode("#FFE5B4");

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(backgroundColor);

        // ==== Top Panel with Background Image ====
        JPanel wordsPanel = new JPanel(new BorderLayout());
        wordsPanel.setBorder(BorderFactory.createTitledBorder("بدأت اللعبة!"));
        wordsPanel.setBackground(backgroundColor);

        questionBackgroundPanel = new QuestionBackgroundPanel(1);
        questionBackgroundPanel.setPreferredSize(new Dimension(800, 400));
        questionBackgroundPanel.setLayout(null); // لإضافة التايمر فوق الصورة

        // تايمر الجولة فوق الصورة
        roundTimerLabel = new JLabel("30", SwingConstants.RIGHT);
        roundTimerLabel.setForeground(Color.BLACK);
        roundTimerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        roundTimerLabel.setBounds(6, 20, 100, 30); // يمين فوق
        questionBackgroundPanel.add(roundTimerLabel);

        JPanel centerImagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerImagePanel.setOpaque(false);
        centerImagePanel.add(questionBackgroundPanel);

        wordsPanel.add(centerImagePanel, BorderLayout.CENTER);
        add(wordsPanel, BorderLayout.NORTH);

        // ==== Center Panel ====
        JPanel answerPanel = new JPanel(new BorderLayout(5, 5));
        answerPanel.setBackground(backgroundColor);

        answersTextPane = new JTextPane();
        answersTextPane.setEditable(false);
        answersTextPane.setBackground(Color.WHITE);
        answerPanel.add(new JScrollPane(answersTextPane), BorderLayout.CENTER);

        answerTextField = new JTextField();
        JButton sendButton = new JButton("إرسال");
        JButton leaveButton = new JButton("خروج");

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(answerTextField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(leaveButton, BorderLayout.SOUTH);
        answerPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(answerPanel, BorderLayout.CENTER);

        // ==== Right Panel: Players List ====
        playersList = new JList<>();
        playersList.setBorder(BorderFactory.createTitledBorder("اللاعبين"));
        playersList.setBackground(Color.WHITE);

        JScrollPane playersScrollPane = new JScrollPane(playersList);
        playersScrollPane.setPreferredSize(new Dimension(200, 0));
        playersScrollPane.getViewport().setBackground(backgroundColor);
        add(playersScrollPane, BorderLayout.EAST);

        // ==== Bottom Toolbar ====
        JToolBar toolbar = new JToolBar();
        toolbar.setRollover(true);
        toolbar.setBackground(backgroundColor);
        statusLabel = new JLabel();
        toolbar.add(statusLabel);
        add(toolbar, BorderLayout.SOUTH);

        // ==== Styles ====
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        black = answersTextPane.addStyle("black", def);
        StyleConstants.setForeground(black, Color.BLACK);
        StyleConstants.setFontSize(black, 18);

        green = answersTextPane.addStyle("green", def);
        StyleConstants.setForeground(green, Color.GREEN);
        StyleConstants.setFontSize(green, 18);

        // ==== Actions ====
        sendButton.addActionListener(e -> sendAnswer());
        answerTextField.addActionListener(e -> sendAnswer());
        leaveButton.addActionListener(e -> {
            if (client != null) client.leaveRequest();
            parent.showPanel("إتصال");
        });
    }

    private void sendAnswer() {
        String answer = answerTextField.getText().trim();
        if (!answer.isEmpty() && client != null) {
            lastSubmittedWord = answer;
            client.sendAnswer(answer);
            answerTextField.setText("");
        }
    }

    public void appendAnswer(String result) {
        boolean correct = result.equalsIgnoreCase("true");

        try {
            Document doc = answersTextPane.getDocument();
            doc.insertString(doc.getLength(), lastSubmittedWord + "\n", correct ? green : black);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        lastSubmittedWord = "";
    }

    public void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    public void updateForNewRound(int round) {
        if (questionBackgroundPanel != null) {
            questionBackgroundPanel.setRound(round);
        }

        answerTextField.setText("");
        answersTextPane.setText("");
        startRoundTimer(); // ✅ يبدأ العد التنازلي كل جولة
    }

    public void updatePlayersList(String[] users) {
        playersList.setListData(users);
    }

    // ===== عداد الجولة =====
    public void startRoundTimer() {
        roundSeconds = 30;
        roundTimerLabel.setText(String.valueOf(roundSeconds));

        if (roundTimer != null) roundTimer.stop();

        roundTimer = new Timer(1000, e -> {
            roundSeconds--;
            roundTimerLabel.setText(String.valueOf(roundSeconds));
            if (roundSeconds <= 0) {
                roundTimer.stop();
            }
        });

        roundTimer.start();
    }

    // ===== خلفية المرحلة =====
    private static class QuestionBackgroundPanel extends JPanel {
        private Image bg;

        public QuestionBackgroundPanel(int round) {
            setRound(round);
        }

        public void setRound(int round) {
            try {
                bg = new ImageIcon(getClass().getResource("/client/view/round" + round + ".png")).getImage();
            } catch (Exception e) {
                System.out.println("❌ لم يتم العثور على الصورة: round" + round + ".png");
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
