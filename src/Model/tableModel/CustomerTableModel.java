package Model.tableModel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class CustomerTableModel extends RecursiveTreeObject<CustomerTableModel> {
    private String id;
    private String name;
    private String address;
    private double salary;
    private JFXButton btn;


    public void setSalary(double salary) {
        if(salary>0){
            this.salary = salary;
        }
    }

}
