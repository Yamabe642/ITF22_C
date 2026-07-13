package Customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class FoodOrderView extends JFrame {

    private JComboBox<String> menuCombo;
    private JTextField quantityField;
    private JButton orderButton;
    private JButton clearButton;

    public FoodOrderView() {
        setTitle("飲食注文システム");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("飲食のご注文", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0x22, 0x22, 0x28));
        titleLabel.setBorder(new EmptyBorder(16, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(10, 30, 20, 30));

        menuCombo = new JComboBox<>(new String[]{
            "フライドポテト (450円)",
            "唐揚げ盛り合わせ (780円)",
            "ミックスピザ (900円)",
            "生ビール (600円)",
            "ウーロン茶 (350円)"
        });

        quantityField = new JTextField("1");
        quantityField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        quantityField.setBorder(new CompoundBorder(new LineBorder(new Color(0xE2, 0xE2, 0xE8), 1, true), new EmptyBorder(6, 10, 6, 10)));

        // グループ化
        centerPanel.add(new JLabel("メニュー"));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(menuCombo);
        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(new JLabel("数量"));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(quantityField);
        centerPanel.add(Box.createVerticalStrut(20));

        orderButton = new JButton("注文を確定する");
        orderButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        orderButton.setForeground(Color.WHITE);
        orderButton.setBackground(new Color(0xE4, 0x57, 0x2E));
        orderButton.setFocusPainted(false);
        orderButton.setBorder(new EmptyBorder(10, 0, 10, 0));
        orderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        clearButton = new JButton("クリア");

        // 注文確定イベント（他メンバのサービスに蓄積）
        orderButton.addActionListener(e -> {
            try {
                String selection = (String) menuCombo.getSelectedItem();
                int qty = getQuantity();
                if(qty <= 0) throw new NumberFormatException();

                String name = selection.split(" \\(")[0];
                int price = Integer.parseInt(selection.replaceAll("[^0-9]", ""));

                Food f = new Food(0, name, price, "GENERAL");
                FoodOrder order = new FoodOrder(f, qty);

                // 共有データストアに追加
                SharedOrderData.getInstance().addOrder(order);

                JOptionPane.showMessageDialog(this, name + " を " + qty + " 個注文しました！");
                dispose();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "数量を正しく入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> quantityField.setText("1"));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(orderButton);
        btnPanel.add(clearButton);
        centerPanel.add(btnPanel);

        add(centerPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public int getQuantity() {
        return Integer.parseInt(quantityField.getText().trim());
    }
}