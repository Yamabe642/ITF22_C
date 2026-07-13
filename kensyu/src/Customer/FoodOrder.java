package Customer;

public class FoodOrder {

    private Food food;
    private int quantity;

    // ─── ★このコンストラクターを追加してください ───
    public FoodOrder(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }
    // ──────────────────────────────────────────────

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * 小計金額（単価 × 数量）を計算して返すメソッド
     */
    public int getTotalPrice() {
        if (food == null) {
            return 0;
        }
        return food.getPrice() * quantity;
    }
}