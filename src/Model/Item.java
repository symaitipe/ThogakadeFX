package Model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString

public class Item {
    private String code;
    private String description;
    private double unitPrice;
    private int qtyOnHand;

    public void setQtyOnHand(int qty){
        if(qtyOnHand>=0){
            qtyOnHand = qty;
        }
    }
}
