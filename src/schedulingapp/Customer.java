/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 *
 * @author brandon
 */
public class Customer {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();
    private StringProperty phone = new SimpleStringProperty();
    private IntegerProperty addressID = new SimpleIntegerProperty();
    
    public Customer() {
    
    }
    
    public Customer(Integer id, String name, String address,String phone, Integer addressID) {
        setID(id);
        setName(name);
        setAddress(address);
        setPhone(phone);
        setAddressID(addressID);
    }
    
    public Integer getID() {
        return IDProperty().get();
    }
    public IntegerProperty IDProperty() {
        return id;
    }
    
    public void setID(Integer id) {
        IDProperty().set(id);
    }
    public Integer getAddressID() {
        return addressIDProperty().get();
    }
    public IntegerProperty addressIDProperty() {
        return addressID;
    }
    
    public void setAddressID(Integer id) {
        addressIDProperty().set(id);
    }
    
    public String getName() {
        return nameProperty().get();
    }
    
    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public String getAddress() {
        return addressProperty().get();
    }
    
    public void setAddress(String address) {
        addressProperty().set(address);
    }
    
    public StringProperty addressProperty() {
        return address;
    }
    public StringProperty phoneProperty() {
        return phone;
    }
    public String getPhone() {
        return phoneProperty().get();
    }
    public void setPhone(String phone) {
        phoneProperty().set(phone);
    }
    public static Callback<Customer, Observable[]> extractor() {
        return (Customer a) -> {
            return new Observable[]
            {   a.IDProperty(), 
                a.nameProperty(), 
                a.addressProperty(), 
                a.phoneProperty()};
        };
    }
    
}
