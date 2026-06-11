package model;

public class Passenger {
    public int id;
    public String firstName ;
    public String lastName ;
    public String passportNumber;
    public String nationality ;
    public String date_of_birth ;
    public int phone_number ;
    public String email ;

    public Passenger(int id, String firstName,String lastName,
                     String passport_number,String nationality,String date_of_birth,int phone_number,String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passport_number;
        this.nationality = nationality;
        this.date_of_birth = date_of_birth;
        this.phone_number = phone_number;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public  String getFirstName() {
        return firstName;
    }

    public  String getLastName() {
        return lastName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public String getDateOfBirth() {
       return date_of_birth;
    }

    public String getPhoneNumber() {
        return email;
    }

    public String getEmail() {
        return email;
    }


    @Override
    public String toString() {
        return "Passanger: " +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';

    }
}
