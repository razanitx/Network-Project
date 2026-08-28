package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class GameResultPanel extends JPanel {

    private List<String> winners; // قائمة أسماء الفائزين
    private JLabel winner1Label;
    private JLabel winner2Label;
    private JLabel winner3Label;
    private Image backgroundImage;

    public GameResultPanel(List<String> winners) {
        this.winners = winners;
        setLayout(null); // استخدام null layout لتحديد المواقع يدويًا

        // تحميل صورة الخلفية
        backgroundImage = new ImageIcon(getClass().getResource("starr.png")).getImage();

        // إعداد لابلات الفائزين
        winner1Label = createWinnerLabel();
        winner2Label = createWinnerLabel();
        winner3Label = createWinnerLabel();

        add(winner1Label);
        add(winner2Label);
        add(winner3Label);

        updateWinners();

        // 🔥 نضيف Listener عشان نحدث مواقع اللابلات إذا كبرت الشاشة
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustLabels();
            }
        });
    }

    private JLabel createWinnerLabel() {
        JLabel label = new JLabel("", SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.BLACK);
        label.setOpaque(false);
        return label;
    }

    private void updateWinners() {
        if (winners != null) {
            if (winners.size() >= 1) {
                winner1Label.setText(" " + winners.get(0));
            }
            if (winners.size() >= 2) {
                winner2Label.setText(" " + winners.get(1));
            }
            if (winners.size() >= 3) {
                winner3Label.setText(" " + winners.get(2));
            }
        }
    }

   private void adjustLabels() {
    int w = getWidth();
    int h = getHeight();

    int labelWidth = (int)(w * 0.3); // خلي عرض اللابل 30% من عرض الصورة
    int labelHeight = (int)(h * 0.08); // خلي ارتفاع اللابل 8% تقريبا من ارتفاع الصورة

    int centerX = (int)(w * 0.58); // ✨ أكثر يمين


    // 📌 مواقع الفائزين كنسبة من ارتفاع الصورة:
   winner1Label.setBounds(centerX, (int)(h * 0.32), labelWidth, labelHeight);
    winner2Label.setBounds(centerX, (int)(h * 0.49), labelWidth, labelHeight);
    winner3Label.setBounds(centerX, (int)(h * 0.66), labelWidth, labelHeight);
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
