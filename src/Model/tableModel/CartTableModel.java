package Model.tableModel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class CartTableModel extends RecursiveTreeObject<CartTableModel> {

    private String itmCode;
    private String description;
    private double unitPrice;
    private int qty;
    private double amount;
    private JFXButton btn;


}
