package Controller;

import DB.DBConnection;
import Model.Customer;
import Model.Item;
import Model.Order;
import Model.OrderDetail;
import Model.tableModel.ItemTableModel;
import Model.tableModel.OrderDetailTm;
import Model.tableModel.OrderTm;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class OrderFormController implements Initializable {
    @FXML
    private AnchorPane orderDetailPane;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton backBtn;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private TreeTableColumn colAmount;

    @FXML
    private TreeTableColumn colCusName;

    @FXML
    private TreeTableColumn colDate;

    @FXML
    private TreeTableColumn colDesc;

    @FXML
    private TreeTableColumn colItmCode;

    @FXML
    private TreeTableColumn colOderID;

    @FXML
    private TreeTableColumn colQty;
    @FXML
    private TreeTableColumn colOption;

    @FXML
    private JFXTreeTableView<OrderTm> orderTbl;

    @FXML
    private JFXTreeTableView<OrderDetailTm> orderDetailTbl;



    @FXML
    private JFXTextField txtOrderID;

    @FXML
    void deleteAction(ActionEvent event) {

    }

    @FXML
    void searchAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colOderID.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new TreeItemPropertyValueFactory<>("Date"));
        colCusName.setCellValueFactory(new TreeItemPropertyValueFactory<>("CustomerName"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadOrders();

        orderTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                loadDetails(newValue);
            }
        });
    }

    private void loadDetails(TreeItem<OrderTm> newValue) {
        ObservableList<OrderDetailTm> tmList = FXCollections.observableArrayList();
        try {
            List<OrderDetail> list = new ArrayList<>();
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM orderdetail WHERE orderId=?");
            preparedStatement.setString(1,newValue.getValue().getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                list.add(new OrderDetail(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getDouble(4)
                ));
            }

            for (OrderDetail detail: list) {
                preparedStatement = connection.prepareStatement("SELECT description FROM item WHERE code=?");
                preparedStatement.setString(1,resultSet.getString(1));
                ResultSet rsSet = preparedStatement.executeQuery();

                rsSet.next();
                tmList.add(new OrderDetailTm(
                        detail.getCode(),
                        rsSet.getString(1),
                        detail.getQty(),
                        detail.getUnitPrice()*detail.getQty()
                ));
            }
            TreeItem<OrderDetailTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            orderDetailTbl.setRoot(treeItem);
            orderDetailTbl.setShowRoot(false);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void loadOrders() {
        ObservableList<OrderTm> tmList = FXCollections.observableArrayList();
        try {
            List<Order> orderList = new ArrayList<>();
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM orders";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                orderList.add(new Order(
                        resultSet.getString(1),
                        resultSet.getDate(2).toLocalDate(),
                        resultSet.getString(3)
                ));
            }

            for (Order order: orderList) {
                JFXButton btn = new JFXButton("Delete");
                btn.setTextFill(Color.rgb(135, 206, 235, 0.47));
                btn.setStyle("-fx-background-color: #800080; ");
                //Method for delete button inside the table
                btn.setOnAction(actionEvent -> {
                    try {
                        PreparedStatement pstm = connection.prepareStatement("DELETE FROM orders WHERE id=?");
                        pstm.setString(1, order.getId());
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want To Delete " +order.getId() + " order?", ButtonType.YES, ButtonType.NO).showAndWait();
                        if (buttonType.get() == ButtonType.YES) {
                            if (pstm.executeUpdate() > 0) {
                                new Alert(Alert.AlertType.INFORMATION, "Order Deleted Successfully").show();
                                loadOrders();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Order Delete Failed").show();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                PreparedStatement pt = connection.prepareStatement("SELECT name FROM customer WHERE id=?");
                pt.setString(1,order.getCustomerId());
                ResultSet rst = pt.executeQuery();
                rst.next();
                tmList.add(new OrderTm(
                        order.getId(),
                        order.getDate(),
                        rst.getString(1),
                        btn
                ));
            }
            TreeItem<OrderTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            orderTbl.setRoot(treeItem);
            orderTbl.setShowRoot(false);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void backAction(ActionEvent actionEvent) {
        Stage stage = (Stage) orderDetailPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/DashBoard.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }
}