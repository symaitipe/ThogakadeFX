package Model.tableModel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter @ToString
public class OrderTm extends RecursiveTreeObject<OrderTm> {
    private String id;
    private LocalDate date;
    private String customerName;
    private JFXButton btn;
}
