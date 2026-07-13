package Customer;

/**
 * 画面間でフード注文データを一元管理するためのデータ共有用クラス
 */
public class SharedOrderData {
    private static FoodOrderService instance;

    public static synchronized FoodOrderService getInstance() {
        if (instance == null) {
            instance = new FoodOrderService();
        }
        return instance;
    }
}