package Controller;

import DB.DBConnection;
import Model.Order;
import Model.OrderDetail;
import Model.tableModel.CartTableModel;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceOrderFormController implements Initializable {

    @FXML
    private JFXTreeTableView<CartTableModel> placeOderTbl;

    @FXML
    private AnchorPane placeOrderPane;
    @FXML
    private JFXButton backBtn;

    @FXML
    private JFXButton btnAddToCart;

    @FXML
    private JFXButton btnClrPO;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private JFXButton btnUpdatePO;

    @FXML
    private Label lblOrdID;

    @FXML
    private Label lblQtyOnHand;

    @FXML
    private Label lblTotalAmount;

    @FXML
    private Label lblUnitPrice;

    @FXML
    private JFXComboBox cbCusId;

    @FXML
    private JFXTextField txtCsName;

    @FXML
    private JFXTextField txtDesc;

    @FXML
    private JFXComboBox cbItmCode;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private TreeTableColumn colAmount;

    @FXML
    private TreeTableColumn colDesc;

    @FXML
    private TreeTableColumn colItmCode;

    @FXML
    private TreeTableColumn colOption;

    @FXML
    private TreeTableColumn colQty;

    @FXML
    private TreeTableColumn colUnitPrice;


    ObservableList<CartTableModel> crtTmList = FXCollections.observableArrayList();


    @FXML
    void btnAddToCartAction(ActionEvent event) {

        boolean isExist=false;
        for (CartTableModel tm:crtTmList) {

            if(tm.getItmCode().equals(cbItmCode.getValue().toString())){
                tm.setQty(tm.getQty()+Integer.parseInt(txtQty.getText()));
                tm.setAmount(tm.getQty()+tm.getAmount());
                isExist =true;
            }
        }
        if(!isExist){
            JFXButton btn = new JFXButton("Delete");
            btn.setTextFill(Color.rgb(135, 206, 235, 0.47));
            btn.setStyle("-fx-background-color: #800080; ");



            CartTableModel crtTm = new CartTableModel(
                    cbItmCode.getValue().toString(),
                    txtDesc.getText(),
                    Double.parseDouble(lblUnitPrice.getText()),
                    Integer.parseInt(txtQty.getText()),
                    Double.parseDouble(lblUnitPrice.getText())* Integer.parseInt(txtQty.getText()),
                    btn
            );
            btn.setOnAction(actionEvent -> {
                crtTmList.remove(crtTm);
                lblTotalAmount.setText(String.format("%.2f",findTotal()));
                placeOderTbl.refresh();
            });

            crtTmList.add(crtTm);

            TreeItem<CartTableModel> treeItem = new RecursiveTreeItem<>(crtTmList, RecursiveTreeObject::getChildren);
            placeOderTbl.setRoot(treeItem);
            placeOderTbl.setShowRoot(false);
        }
        lblTotalAmount.setText(String.format("%.2f",findTotal()));
        placeOderTbl.refresh();
    }

    @FXML
    void btnClrPOAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        cbItmCode.setValue("");
        cbCusId.setValue("");
        txtCsName.clear();
        txtDesc.clear();
        lblUnitPrice.setText("");
        lblQtyOnHand.setText("");
        txtQty.clear();
    }

    @FXML
    void btnPlaceOrderAction(ActionEvent event) throws SQLException {

        List<OrderDetail> detailList = new ArrayList<>();
        for (CartTableModel tm: crtTmList) {
            detailList.add(new OrderDetail(
                    lblOrdID.getText(),
                    tm.getItmCode(),
                    tm.getQty(),
                    tm.getUnitPrice()
            ));
        }

        Order order = new Order(
                lblOrdID.getText(),
                LocalDate.now(),
                cbCusId.getValue().toString()

        );
        Connection connection=null;
        boolean isOrderPlaced = true;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            String sql = "INSERT INTO orders VALUES (?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, order.getId());
            pstm.setDate(2, Date.valueOf(order.getDate()));
            pstm.setString(3, order.getCustomerId());


            if (pstm.executeUpdate() > 0) {
                for (OrderDetail detail:detailList) {
                    String query = "INSERT INTO  orderdetail VALUES (?,?,?,?)";
                    PreparedStatement pstm2 = connection.prepareStatement(query);
                    pstm2.setString(1, detail.getOrderId());
                    pstm2.setString(2, detail.getCode());
                    pstm2.setInt(3, detail.getQty());
                    pstm2.setDouble(4, detail.getUnitPrice());

                    if(pstm2.executeUpdate()<=0){
                        isOrderPlaced = false;
                    }
                }
            } else {
                isOrderPlaced = false;
                new Alert(Alert.AlertType.ERROR, "Placing Failed").show();
                connection.rollback();
            }

            if(isOrderPlaced){
                new Alert(Alert.AlertType.INFORMATION, "Order Placed Succesfully").show();
                connection.commit();
                genarateOrderID();
                crtTmList.clear();
                placeOderTbl.refresh();
                clearFields();
            }else{
                new Alert(Alert.AlertType.INFORMATION, "Order Placed Unsuccesful").show();
                connection.rollback();
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            connection.rollback();
        }finally {
            connection.setAutoCommit(true);
        }
    }

    @FXML
    void btnUpdatePOAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItmCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("itmCode"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        genarateOrderID();
        genarateCusID();
        genarateItemCode();
        cbCusId.setOnAction(actionEvent -> {
            setCusName();
        });
        cbItmCode.setOnAction(actionEvent -> {
            setItemDesc();
        });
    }

    private void genarateItemCode() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT code FROM item";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

            ObservableList<String> itemIdList = FXCollections.observableArrayList();
            while(resultSet.next()){
                itemIdList.add(resultSet.getString(1));
            }
            cbItmCode.setItems(itemIdList);
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private double findTotal(){
        double total = 0;
        for (CartTableModel tm:crtTmList) {
            total += tm.getAmount();
        }
        return total;
    }
    private void setItemDesc() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
            pstm.setString(1,cbItmCode.getValue().toString());
            ResultSet resultSet = pstm.executeQuery();
            if(resultSet.next()){
                txtDesc.setText(resultSet.getString(2));
                lblUnitPrice.setText(String.format("%.2f",resultSet.getDouble(3)));
                lblQtyOnHand.setText(resultSet.getString(4));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    private void setCusName() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT name FROM customer WHERE id=?");
            pstm.setString(1,cbCusId.getValue().toString());
            ResultSet resultSet = pstm.executeQuery();
            if(resultSet.next()){
                txtCsName.setText(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void genarateCusID() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT id FROM customer";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

            ObservableList<String> cusIdList = FXCollections.observableArrayList();
            while(resultSet.next()){
                cusIdList.add(resultSet.getString(1));
            }
            cbCusId.setItems(cusIdList);
        }catch (SQLException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void genarateOrderID() {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT id FROM orders ORDER BY id DESC LIMIT 1 ";
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();
            if(resultSet.next()){
                int num = Integer.parseInt(resultSet.getString(1).split("[D]")[1]);
                num++;
                lblOrdID.setText(String.format("D%03d", num));
            }else{
                lblOrdID.setText("D001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void bactBtnActionPO(ActionEvent actionEvent) {
        Stage stg = (Stage) backBtn.getScene().getWindow();
        try {
            stg.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/DashBoard.fxml"))));
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}
