package Controller;

import DB.DBConnection;
import Model.Customer;
import Model.tableModel.CustomerTableModel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class CustomerFormController implements Initializable {

    @FXML
    private AnchorPane CustomerPane;
    @FXML
    private JFXTreeTableView<CustomerTableModel> cusTbl;
    @FXML
    private TreeTableColumn colCusId;
    @FXML
    private TreeTableColumn colCusName;
    @FXML
    private TreeTableColumn colCusAddress;
    @FXML
    private TreeTableColumn colCusSalary;
    @FXML
    private TreeTableColumn colCusOption;
    @FXML
    private JFXTextField cusTxtSearch;

    @FXML
    private JFXButton btnCusClose;

    @FXML
    private Label lblCus_Id;

    @FXML
    private JFXTextField txtCus_Adrs;

    @FXML
    private JFXTextField txtCus_Name;

    @FXML
    private JFXTextField txtCus_Sal;

    public void cstmrUpdate(ActionEvent actionEvent) {
        Customer cstmr = new Customer(lblCus_Id.getText(), txtCus_Name.getText(), txtCus_Adrs.getText(), Double.parseDouble(txtCus_Sal.getText()));
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE customer SET name=?, address=?,salary= ? WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, cstmr.getName());
            pstm.setString(2, cstmr.getAddress());
            pstm.setDouble(3, cstmr.getSalary());
            pstm.setString(4, cstmr.getId());
            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Update Successfully").show();
                loadTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "Update Error").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void cstmrSave(ActionEvent actionEvent) {
        Customer cstmr = new Customer(lblCus_Id.getText(), txtCus_Name.getText(), txtCus_Adrs.getText(), Double.parseDouble(txtCus_Sal.getText()));
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO customer VALUES (?,?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, cstmr.getId());
            pstm.setString(2, cstmr.getName());
            pstm.setString(3, cstmr.getAddress());
            pstm.setDouble(4, cstmr.getSalary());
            int count = pstm.executeUpdate();
            if (count > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Added Succesfully").show();
                clearFields();
                loadTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "Added Failed").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void cstmrClose(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCusClose.getScene().getWindow();
        stage.close();
    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) CustomerPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/DashBoard.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }


    public void cstmrSearch(ActionEvent actionEvent) {
        cusTxtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                cusTbl.setPredicate(new Predicate<TreeItem<CustomerTableModel>>() {
                    @Override
                    public boolean test(TreeItem<CustomerTableModel> customerTableModelTreeItem) {
                        boolean flag = customerTableModelTreeItem.getValue().getId().contains(newValue)||
                                customerTableModelTreeItem.getValue().getName().contains(newValue);
                        return flag;
                    }
                });
            }
        });
    }

    public void cstmrClear(ActionEvent actionEvent) {
        clearFields();
    }

    private void clearFields() {
        genarateId();
        txtCus_Name.clear();
        txtCus_Adrs.clear();
        txtCus_Sal.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCusId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colCusName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colCusAddress.setCellValueFactory(new TreeItemPropertyValueFactory<>("address"));
        colCusSalary.setCellValueFactory(new TreeItemPropertyValueFactory<>("salary"));
        colCusOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        genarateId();
        loadTable();

        cusTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                setData(newValue);
            }
        });
    }

    private void setData(TreeItem<CustomerTableModel> value) {
        lblCus_Id.setText(value.getValue().getId());
        txtCus_Name.setText(value.getValue().getName());
        txtCus_Adrs.setText(value.getValue().getAddress());
        txtCus_Sal.setText(String.valueOf(value.getValue().getSalary()));
    }

    private void loadTable() {
        ObservableList<CustomerTableModel> tmList = FXCollections.observableArrayList();
        try {
            List<Customer> list = new ArrayList<>();
            Connection conn = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM customer");
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                list.add(new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4)

                ));
            }
            for (Customer customer : list) {
                JFXButton btn = new JFXButton("Delete");
                btn.setTextFill(Color.rgb(135, 206, 235, 0.47));
                btn.setStyle("-fx-background-color: #800080; ");
                //Method for delete button inside the table
                btn.setOnAction(actionEvent -> {
                    try {
                        PreparedStatement pstm2 = conn.prepareStatement("DELETE FROM customer WHERE id=?");
                        pstm2.setString(1, lblCus_Id.getText());
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want To Delete" + lblCus_Id.getText() + "customer?", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (buttonType.get() == ButtonType.YES) {
                            if (pstm2.executeUpdate() > 0) {
                                new Alert(Alert.AlertType.INFORMATION, "Delete Success").show();
                                genarateId();
                                loadTable();
                                clearFields();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Delete Failed").show();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                tmList.add(new CustomerTableModel(
                        customer.getId(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getSalary(),
                        btn
                ));
            }
            TreeItem<CustomerTableModel> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            cusTbl.setRoot(treeItem);
            cusTbl.setShowRoot(false);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void genarateId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT id FROM customer ORDER BY id DESC LIMIT 1 ");
            ResultSet resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                int num = Integer.parseInt(resultSet.getString(1).split("[C]")[1]);
                num++;
                lblCus_Id.setText(String.format("C%03d", num));
            } else {
                lblCus_Id.setText("C001");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }
    }


}
