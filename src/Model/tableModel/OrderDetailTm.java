package Model.tableModel;

import Model.OrderDetail;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailTm extends RecursiveTreeObject<OrderDetailTm> {
    private String itemCode;
    private  String desc;
    private int qty;
    private double amount;

}
