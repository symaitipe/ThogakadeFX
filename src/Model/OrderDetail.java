package Model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetail extends RecursiveTreeObject<OrderDetail> {
    private String orderId;
    private String code;

    private int qty;
    private double unitPrice;

}
