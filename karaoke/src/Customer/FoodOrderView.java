package Customer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FoodOrderView extends JFrame {

    // メニュー選択
    private JComboBox<String> menuCombo;

    // 数量入力
    private JTextField quantityField;

    // ボタン
    private JButton orderButton;
    private JButton clearButton;


    // コンストラクタ
    public FoodOrderView() {

        // タイトル
        setTitle("飲食注文システム");

        // ウィンドウサイズ
        setSize(800, 600);

        // 画面中央
        setLocationRelativeTo(null);

        // ×ボタンで終了
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // レイアウト
        setLayout(new BorderLayout());


        // ==========================
        // タイトル
        // ==========================

        JLabel titleLabel = new JLabel(
                "飲食注文システム",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("MS ゴシック", Font.BOLD, 24)
        );

        add(titleLabel, BorderLayout.NORTH);



        // ==========================
        // 中央パネル
        // ==========================

        JPanel centerPanel = new JPanel();

        centerPanel.setFont(
        		new Font("MS ゴシック", Font.PLAIN, 20)
        );


        // メニュー
        menuCombo = new JComboBox<>(
                new String[]{
                		"1.フライドポテト             (450円)",
                		"2.唐揚げ                     (580円)",
                		"3.ピザ                          (980円)",
                		"4.チャーハン                  (750円)",
                		"5.オムライス                  (850円)",
                		"6.カレーライス                (780円)",
                		"7.グリーンサラダ　　　　(500円)",
                		"8.たこ焼き 　　　　　 (550円)",
                		"9.焼きそば　　　　     (780円)",
                		"10.ナゲット                   (480円）",
                		"11.チョコパフェ               (650円)",
                		"12.いちごパフェ              (650円)",
                		"13.バニラアイス             (350円)",
                		"14.チョコアイス              (350円)",
                		"15.いちごアイス             (350円)"
                }
        );


        // 数量
        quantityField = new JTextField(10);


        // ボタン
        orderButton = new JButton("注文する");

        clearButton = new JButton("クリア");



        centerPanel.add(
                new JLabel("メニュー")
        );

        centerPanel.add(menuCombo);


        centerPanel.add(
                new JLabel("数量")
        );

        centerPanel.add(quantityField);



        // ボタンパネル
        JPanel buttonPanel = new JPanel(
                new FlowLayout()
        );

        buttonPanel.add(orderButton);
     // 注文ボタン処理
        orderButton.addActionListener(e -> {

            String menu = (String) menuCombo.getSelectedItem();

            String quantity = quantityField.getText();


            System.out.println("注文商品：" + menu);
            System.out.println("数量：" + quantity);

        });
        buttonPanel.add(clearButton);


        centerPanel.add(buttonPanel);


        add(centerPanel, BorderLayout.CENTER);
        
        quantityField.requestFocus();

        // ==========================
        // 最後に表示
        // ==========================

        setVisible(true);
    }


    // Getter
    public JComboBox<String> getMenuCombo() {
        return menuCombo;
    }


    public JTextField getQuantityField() {
        return quantityField;
    }


    public JButton getOrderButton() {
        return orderButton;
    }


    public JButton getClearButton() {
        return clearButton;
    }
 // 数量取得
 // 数量取得
    public int getQuantity() {

        return Integer.parseInt(quantityField.getText());

    }

}