package Customer;

public class FoodOrder {

    // 注文ID
    private int orderId;

    // 部屋番号
    private int roomNumber;

    // 商品情報
    private Food food;

    // 注文数
    private int quantity;

    // 合計金額
    private int totalPrice;

    // デフォルトコンストラクタ
    public FoodOrder() {
    }

    // コンストラクタ
    public FoodOrder(int orderId, int roomNumber, Food food, int quantity, int totalPrice) {
        this.orderId = orderId;
        this.roomNumber = roomNumber;
        this.food = food;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getter・Setter
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

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

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "部屋番号：" + roomNumber
                + "　商品：" + food.getFoodName()
                + "　数量：" + quantity
                + "　合計：" + totalPrice + "円";
    }
}