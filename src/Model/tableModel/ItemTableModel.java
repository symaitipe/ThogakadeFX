package Model.tableModel;

import com.jfoenix.controls.JFXButton;
import lombok.*;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class ItemTableModel extends RecursiveTreeObject<ItemTableModel> {
    private String code;
    private String description;
    private Double unitPrice;
    private int qtyOnHand;
    private JFXButton btn;

    public void setUnitPrice(double unitPrice) {
        if (unitPrice > 0) {
            this.unitPrice = unitPrice;
        }
    }
}
