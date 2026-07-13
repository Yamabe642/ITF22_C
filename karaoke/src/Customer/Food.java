package Customer;

public class Food {

    // 商品ID
    private int foodId;

    // 商品名
    private String foodName;

    // 価格
    private int price;

    // カテゴリ（フード・ドリンクなど）
    private String category;

    // デフォルトコンストラクタ
    public Food() {
    }

    // コンストラクタ
    public Food(int foodId, String foodName, int price, String category) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.category = category;
    }

    // Getter・Setter
    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return foodName + "　¥" + price;
    }
}