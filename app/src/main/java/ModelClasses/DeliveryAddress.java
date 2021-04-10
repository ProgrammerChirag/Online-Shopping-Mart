package ModelClasses;

import java.io.Serializable;

public class DeliveryAddress implements Serializable {
    String Address;
    String Name;
    String Phone;

    public DeliveryAddress(){}

    public DeliveryAddress(String address, String name, String phone) {
        Address = address;
        Name = name;
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
