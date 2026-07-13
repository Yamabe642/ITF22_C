package Customer;

import java.util.ArrayList;
import java.util.List;

public class FoodOrderService {
	
	//test

    // 注文リスト
    private List<FoodOrder> orderList = new ArrayList<>();

    /**
     * 注文を追加する
     * @param order 注文情報
     */
    public void addOrder(FoodOrder order) {
        orderList.add(order);
    }

    /**
     * 注文一覧を取得する
     * @return 注文一覧
     */
    public List<FoodOrder> getOrderList() {
        return orderList;
    }

    /**
     * 合計金額を計算する
     * @return 合計金額
     */
    public int calculateTotalPrice() {

        int total = 0;

        for (FoodOrder order : orderList) {
            total += order.getTotalPrice();
        }

        return total;
    }

    /**
     * 注文をすべて削除する
     */
    public void clearOrder() {
        orderList.clear();
    }

    /**
     * 注文件数を取得する
     * @return 注文件数
     */
    public int getOrderCount() {
        return orderList.size();
    }

}