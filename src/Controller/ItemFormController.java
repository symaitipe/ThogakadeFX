package Controller;

import DB.DBConnection;
import Model.Customer;
import Model.Item;
import Model.tableModel.CustomerTableModel;
import Model.tableModel.ItemTableModel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import java.util.*;

public class ItemFormController implements Initializable {
    @FXML
    private JFXButton btnItemClose;
    @FXML
    private JFXButton btnClrItem;

    @FXML
    private JFXButton btnItemUpdate;

    @FXML
    private JFXButton btnSaveItem;
    @FXML
    private JFXButton btnItemBack;
    @FXML
    private AnchorPane itemPane;

    @FXML
    private TreeTableColumn col_Item_Id;

    @FXML
    private TreeTableColumn col_Item_Option;

    @FXML
    private TreeTableColumn  col_Item_desc;

    @FXML
    private TreeTableColumn  col_Item_qtyOnHand;

    @FXML
    private TreeTableColumn  col_Item_unitPrice;

    @FXML
    private JFXTreeTableView <ItemTableModel> itmTbl;

    @FXML
    private Label lblItemCode;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtDescription;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        col_Item_Id.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        col_Item_desc.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        col_Item_unitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        col_Item_qtyOnHand.setCellValueFactory(new TreeItemPropertyValueFactory<>("qtyOnHand"));
        col_Item_Option.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        genarateItemId();
        loadItemTbl();

        itmTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                setData(newValue);
            }
        });
    }
    private void setData(TreeItem<ItemTableModel> value) {
        lblItemCode.setText(value.getValue().getCode());
        txtDescription.setText(value.getValue().getDescription());
        txtUnitPrice.setText(String.valueOf(value.getValue().getUnitPrice()));
        txtQty.setText(String.valueOf(value.getValue().getQtyOnHand()));
    }
    private void genarateItemId() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT code FROM item ORDER BY code DESC LIMIT 1 ");
            ResultSet resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                int num = Integer.parseInt(resultSet.getString(1).split("[P]")[1]);
                num++;
                lblItemCode.setText(String.format("P%03d", num));
            } else {
                lblItemCode.setText("P001");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }
    }
    public void itemSaveAction(ActionEvent actionEvent) {
        Item item = new Item(lblItemCode.getText(),txtDescription.getText(),Double.parseDouble(txtUnitPrice.getText()),Integer.parseInt(txtQty.getText()));
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO item VALUES (?,?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, item.getCode());
            pstm.setString(2, item.getDescription());
            pstm.setDouble(3, item.getUnitPrice());
            pstm.setInt(4, item.getQtyOnHand());
            int count = pstm.executeUpdate();
            if (count > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Added Succesfully").show();
                clearFields();
                loadItemTbl();
            } else {
                new Alert(Alert.AlertType.ERROR, "Added Failed").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void itemUpdateAction(ActionEvent actionEvent) {
        Item item = new Item(lblItemCode.getText(),txtDescription.getText(),Double.parseDouble(txtUnitPrice.getText()),Integer.parseInt(txtQty.getText()));
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, item.getDescription());
            pstm.setDouble(2, item.getUnitPrice());
            pstm.setInt(3, item.getQtyOnHand());
            pstm.setString(4, item.getCode());
            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Update Successfully").show();
                loadItemTbl();
            } else {
                new Alert(Alert.AlertType.ERROR, "Update Error").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void itemClrAction(ActionEvent actionEvent) {
        clearFields();
    }
    private void clearFields() {
        genarateItemId();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
    }

    private void loadItemTbl() {
        ObservableList<ItemTableModel> itmTmList = FXCollections.observableArrayList();
        try {
            List<Item> itmList = new ArrayList<>();
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM item";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                itmList.add(new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getDouble(3),
                        resultSet.getInt(4)
                ));
            }

            for (Item item: itmList) {
                JFXButton btn = new JFXButton("Delete");
                btn.setTextFill(Color.rgb(135, 206, 235, 0.47));
                btn.setStyle("-fx-background-color: #800080; ");
                //Method for delete button inside the table
                btn.setOnAction(actionEvent -> {
                    try {
                        PreparedStatement pstm = connection.prepareStatement("DELETE FROM item WHERE code=?");
                        pstm.setString(1, lblItemCode.getText());
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want To Delete " +lblItemCode.getText() + " Item?", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (buttonType.get() == ButtonType.YES) {
                            if (pstm.executeUpdate() > 0) {
                                new Alert(Alert.AlertType.INFORMATION, "Item Deleted Successfully").show();
                                loadItemTbl();
                                genarateItemId();
                                clearFields();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Item Delete Failed").show();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                itmTmList.add(new ItemTableModel(
                        item.getCode(),
                        item.getDescription(),
                        item.getUnitPrice(),
                        item.getQtyOnHand(),
                        btn
                ));
            }
            TreeItem<ItemTableModel> treeItem = new RecursiveTreeItem<>(itmTmList, RecursiveTreeObject::getChildren);
            itmTbl.setRoot(treeItem);
            itmTbl.setShowRoot(false);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    public void itemCloseAction(ActionEvent actionEvent) {
        Stage stageItem = (Stage) btnItemClose.getScene().getWindow();
        stageItem.close();
    }

    public void itemBackAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnItemBack.getScene().getWindow();

        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/DashBoard.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }
}
