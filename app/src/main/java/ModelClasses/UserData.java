package ModelClasses;

public class UserData {

    String Address ;//
    String Name;//
    String PhoneNumber;//
    String Profession;//
    String UserName;//
    String Email;//
    String BirthData;//

    public UserData()
    {

    }

    public UserData(String address, String name, String phoneNumber, String profession, String userName, String birthData , String email) {

        Email = email;
        Address = address;
        Name = name;
        PhoneNumber = phoneNumber;
        Profession = profession;
        UserName = userName;
        BirthData = birthData;


    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
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

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBirthData() {
        return BirthData;
    }

    public void setBirthData(String birthData) {
        BirthData = birthData;
    }
}
