package Customer;

public class FoodOrderController {

    private FoodOrderView view;
    private FoodOrderService service;


    public FoodOrderController(
            FoodOrderView view,
            FoodOrderService service) {

        this.view = view;
        this.service = service;
    }


    // 注文処理
    public void addOrder() {

        int quantity = view.getQuantity();

        System.out.println("数量：" + quantity);

    }

}