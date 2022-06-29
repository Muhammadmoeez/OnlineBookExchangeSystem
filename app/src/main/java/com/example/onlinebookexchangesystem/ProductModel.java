package com.example.onlinebookexchangesystem;

public class ProductModel {

    private String UserNumber;
    private String BookImage;
    private String BookName;
    private String BookAuthor;

    private double BookLongitude;
    private double BookLatitude;

    public ProductModel() {
    }

    public ProductModel(String userNumber, String bookImage, String bookName, String bookAuthor, double bookLongitude, double bookLatitude) {
        UserNumber = userNumber;
        BookImage = bookImage;
        BookName = bookName;
        BookAuthor = bookAuthor;
        BookLongitude = bookLongitude;
        BookLatitude = bookLatitude;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getBookImage() {
        return BookImage;
    }

    public void setBookImage(String bookImage) {
        BookImage = bookImage;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getBookAuthor() {
        return BookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        BookAuthor = bookAuthor;
    }

    public double getBookLongitude() {
        return BookLongitude;
    }

    public void setBookLongitude(double bookLongitude) {
        BookLongitude = bookLongitude;
    }

    public double getBookLatitude() {
        return BookLatitude;
    }

    public void setBookLatitude(double bookLatitude) {
        BookLatitude = bookLatitude;
    }
}
