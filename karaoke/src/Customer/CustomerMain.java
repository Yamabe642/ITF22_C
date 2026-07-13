package Customer;

import javax.swing.SwingUtilities;

public class CustomerMain {
	
	
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FoodOrderView();
            }

        });

    }

}