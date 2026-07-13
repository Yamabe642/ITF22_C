package Customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ReservationView extends JFrame {

    static final Color BG_WHITE     = Color.WHITE;
    static final Color PANEL_GRAY   = new Color(0xF7, 0xF7, 0xF9);
    static final Color BORDER_GRAY  = new Color(0xE2, 0xE2, 0xE8);
    static final Color TEXT_DARK    = new Color(0x22, 0x22, 0x28);
    static final Color TEXT_MUTED   = new Color(0x8A, 0x8A, 0x96);
    static final Color ACCENT       = new Color(0xE4, 0x57, 0x2E);
    static final Color ACCENT_HOVER = new Color(0xF0, 0x6A, 0x3D);

    JTextField nameField, phoneField, dateField;
    JComboBox<String> timeBox, durationBox, drinkBox, machineBox;
    JComboBox<Integer> peopleBox;
    JLabel totalPriceLabel;

    public ReservationView() {
        setTitle("カラオケ予約画面");
        setSize(520, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_WHITE);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("新規予約情報の入力");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        outer.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_WHITE);

        nameField = createStyledField("例: 山田 太郎");
        phoneField = createStyledField("例: 09012345678");
        dateField = createStyledField("例: 2026-07-20");

        formPanel.add(createFormGroup("お名前", nameField));
        formPanel.add(createFormGroup("電話番号", phoneField));
        formPanel.add(createFormGroup("利用日 (yyyy-mm-dd)", dateField));

        timeBox = new JComboBox<>(new String[]{"10:00", "12:00", "14:00", "16:00", "18:00", "20:00"});
        durationBox = new JComboBox<>(new String[]{"1時間", "2時間", "3時間", "4時間", "5時間"});
        peopleBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        machineBox = new JComboBox<>(new String[]{"DAM", "JOYSOUND"});
        drinkBox = new JComboBox<>(new String[]{"あり", "なし"});

        durationBox.addActionListener(e -> updateCalculatedPrice());
        peopleBox.addActionListener(e -> updateCalculatedPrice());

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(BG_WHITE);
        row1.add(createFormGroup("開始時間", timeBox));
        row1.add(createFormGroup("利用時間", durationBox));

        JPanel row2 = new JPanel(new GridLayout(1, 3, 12, 0));
        row2.setBackground(BG_WHITE);
        row2.add(createFormGroup("人数", peopleBox));
        row2.add(createFormGroup("機種", machineBox));
        row2.add(createFormGroup("ドリンクバー", drinkBox));

        formPanel.add(row1);
        formPanel.add(row2);
        formPanel.add(Box.createVerticalStrut(14));

        JPanel priceSummary = new JPanel(new BorderLayout());
        priceSummary.setBackground(PANEL_GRAY);
        priceSummary.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(12, 14, 12, 14)));
        JLabel pLabel = new JLabel("人数・時間連動 室料合計（概算）");
        pLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pLabel.setForeground(TEXT_MUTED);
        totalPriceLabel = new JLabel("0円");
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalPriceLabel.setForeground(ACCENT);
        priceSummary.add(pLabel, BorderLayout.WEST);
        priceSummary.add(totalPriceLabel, BorderLayout.EAST);
        formPanel.add(priceSummary);
        formPanel.add(Box.createVerticalStrut(20));

        JButton registerButton = new JButton("予約を確定する");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(ACCENT);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(new EmptyBorder(12, 0, 12, 0));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { registerButton.setBackground(ACCENT_HOVER); }
            public void mouseExited(MouseEvent e)  { registerButton.setBackground(ACCENT); }
        });

        registerButton.addActionListener(e -> {
            if (nameField.getText().equals("例: 山田 太郎") || phoneField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "名前と電話番号は必須入力です。", "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String message = "予約を登録しました！\n\n"
                    + "名前：" + nameField.getText()
                    + "\n時間：" + durationBox.getSelectedItem()
                    + "\n人数：" + peopleBox.getSelectedItem() + "名"
                    + "\n室料合計：" + totalPriceLabel.getText();
            JOptionPane.showMessageDialog(this, message);
            dispose();
        });
        formPanel.add(registerButton);

        updateCalculatedPrice();

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);
        outer.add(scroll, BorderLayout.CENTER);

        add(outer);
        setVisible(true);
    }

    private void updateCalculatedPrice() {
        try {
            int hours = Integer.parseInt(((String) durationBox.getSelectedItem()).replace("時間", ""));
            int people = (Integer) peopleBox.getSelectedItem();
            int total = 400 * people * hours;
            totalPriceLabel.setText(NumberFormat.getNumberInstance(Locale.JAPAN).format(total) + "円");
        } catch (Exception ignored) {}
    }

    private JPanel createFormGroup(String text, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false); // ← 背景を透明（非不透明）にする正しい書き方に修正
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(TEXT_MUTED);
        l.setBorder(new EmptyBorder(4, 2, 4, 0));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        comp.setPreferredSize(new Dimension(200, 38));
        p.add(l);
        p.add(comp);
        p.add(Box.createVerticalStrut(8));
        return p;
    }

    private JTextField createStyledField(String hint) {
        JTextField f = new JTextField(hint);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setForeground(TEXT_MUTED);
        f.setBackground(BG_WHITE);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(6, 10, 6, 10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(TEXT_DARK); }
                f.setBorder(new CompoundBorder(new LineBorder(ACCENT, 1, true), new EmptyBorder(6, 10, 6, 10)));
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(hint); f.setForeground(TEXT_MUTED); }
                f.setBorder(new CompoundBorder(new LineBorder(BORDER_GRAY, 1, true), new EmptyBorder(6, 10, 6, 10)));
            }
        });
        return f;
    }
}