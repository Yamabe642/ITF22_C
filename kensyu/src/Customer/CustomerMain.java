package Customer;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CustomerMain {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 同じパッケージ内なので、インポート無しで直接起動可能
                new MainMenuView();
            }
        });
    }
}