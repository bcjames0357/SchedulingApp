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
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

/**
 *
 * @author brandon <bcjames035@gmail.com>
 */
public class Appointment {
    
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty location = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();
    private StringProperty start = new SimpleStringProperty();
    private StringProperty end = new SimpleStringProperty();
    private IntegerProperty customer = new SimpleIntegerProperty();
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
    
    public Appointment() {
    
    }
    
    public Appointment(Integer id, String title, String location, LocalDate date, LocalTime start, LocalTime end, Integer customer) {
        setID(id);
        setTitle(title);
        setLocation(location);
        setDate(date);
        setStart(start);
        setEnd(end);
        setCustomer(customer);
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
    
    
    public String getTitle() {
        return titleProperty().get();
    }
    
    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        titleProperty().set(title);
    }

    public String getLocation() {
        return locationProperty().get();
    }
    
    public void setLocation(String location) {
        locationProperty().set(location);
    }
    
    public StringProperty locationProperty() {
        return location;
    }
    public LocalDate getDate() {
        return LocalDate.parse((dateProperty().get()), dateFormatter);
    }

    public void setDate(String dates) {
        dateProperty().set((LocalDate.parse(dates, dateFormatter)).toString());
    }
    
    public void setDate(LocalDate date) {
        dateProperty().set(date.toString());
    }
    
    public StringProperty dateProperty() {
        return date;
    }
    
    public LocalTime getStart() {
        return LocalTime.parse(startProperty().get(),timeFormatter);
    }

    public void setStart(LocalTime start) {
        startProperty().set(start.toString());
    }
    
    public void setStart(String start) {
        startProperty().set((LocalTime.parse(start,timeFormatter)).toString());
    }
    
    public StringProperty startProperty() {
        return start;
    }

    public LocalTime getEnd() {
        return LocalTime.parse(endProperty().get(), timeFormatter);
    }

    public void setEnd(String end) {
        endProperty().set(LocalTime.parse(end,timeFormatter).toString());
    }
    
    public void setEnd(LocalTime end) {
        endProperty().set(end.toString());
    }
    
    public StringProperty endProperty () {
        return end;
    }

    public Integer getCustomer() {
        return customerProperty().get();
    }
    
    public IntegerProperty customerProperty() {
        return customer;
    }

    public void setCustomer(Integer customer) {
        customerProperty().set(customer);
    }
    
    public static Callback<Appointment, Observable[]> extractor() {
        return (Appointment a) -> {
            return new Observable[]
            {a.IDProperty(), 
                a.titleProperty(), 
                a.locationProperty(), 
                a.dateProperty(), 
                a.startProperty(), 
                a.endProperty(), 
                a.customerProperty()};
        };
    }
    
}
