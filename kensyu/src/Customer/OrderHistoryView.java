package Customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class OrderHistoryView extends JFrame {

    public OrderHistoryView() {
        setTitle("フード・ドリンク注文履歴");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("現在の注文履歴一覧");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(0x22, 0x22, 0x28));
        title.setBorder(new EmptyBorder(16, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        String[] headers = {"商品名", "単価", "数量", "小計金額"};
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (FoodOrder o : SharedOrderData.getInstance().getOrderList()) {
            model.addRow(new Object[]{
                o.getFood().getFoodName(),
                format(o.getFood().getPrice()),
                o.getQuantity() + "個",
                format(o.getTotalPrice())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setGridColor(new Color(0xE2, 0xE2, 0xE8));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 20, 10, 20));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(0xF7, 0xF7, 0xF9));
        bottom.setBorder(new CompoundBorder(new LineBorder(new Color(0xE2, 0xE2, 0xE8), 1), new EmptyBorder(12, 20, 12, 20)));
        
        JLabel totalTitle = new JLabel("飲食注文 総合計額:");
        totalTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel totalVal = new JLabel(format(SharedOrderData.getInstance().calculateTotalPrice()));
        totalVal.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalVal.setForeground(new Color(0xE4, 0x57, 0x2E));
        
        bottom.add(totalTitle, BorderLayout.WEST);
        bottom.add(totalVal, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String format(int price) {
        return NumberFormat.getNumberInstance(Locale.JAPAN).format(price) + "円";
    }
}