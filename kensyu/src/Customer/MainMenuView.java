package Customer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainMenuView extends JFrame {

    static final Color BG_WHITE     = Color.WHITE;
    static final Color BORDER_GRAY  = new Color(0xE2, 0xE2, 0xE8);
    static final Color TEXT_DARK    = new Color(0x22, 0x22, 0x28);
    static final Color TEXT_MUTED   = new Color(0x8A, 0x8A, 0x96);
    static final Color ACCENT       = new Color(0xE4, 0x57, 0x2E);
    static final Color ACCENT_HOVER = new Color(0xF0, 0x6A, 0x3D);

    public MainMenuView() {
        setTitle("カラオケ総合システム - メインメニュー");
        setSize(450, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WHITE);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG_WHITE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(32, 32, 32, 32)));
        card.setPreferredSize(new Dimension(380, 440));

        JLabel logo = new JLabel("♪ カラオケ予約・注文システム");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("ご利用になりたいメニューを選択してください");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setBorder(new EmptyBorder(6, 0, 24, 0));

        JButton reserveBtn = createModernButton("部屋の新規予約をする");
        JButton foodBtn = createModernButton("🍽 フード・ドリンクの注文");
        JButton historyBtn = createModernButton("📜 注文履歴の確認");
        JButton exitBtn = new JButton("システムを終了する");
        
        exitBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        exitBtn.setForeground(TEXT_DARK);
        exitBtn.setBackground(BG_WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(8, 14, 8, 14)));
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        reserveBtn.addActionListener(e -> new ReservationView());
        foodBtn.addActionListener(e -> new FoodOrderView());
        historyBtn.addActionListener(e -> new OrderHistoryView());
        exitBtn.addActionListener(e -> System.exit(0));

        card.add(logo);
        card.add(sub);
        card.add(reserveBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(foodBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(historyBtn);
        card.add(Box.createVerticalStrut(24));
        card.add(exitBtn);

        wrap.add(card);
        add(wrap);
        setVisible(true);
    }

    private JButton createModernButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(ACCENT);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 20, 12, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT_HOVER); }
            public void mouseExited(MouseEvent e)  { b.setBackground(ACCENT); }
        });
        return b;
    }
}