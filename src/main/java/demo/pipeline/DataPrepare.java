package demo.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;

public class DataPrepare {// https://www.tutorialspoint.com/h2_database/h2_database_jdbc_connection.htm

    // // JDBC driver name and database URL
    // static final String JDBC_DRIVER = "org.h2.Driver";
    // static final String DB_URL = "jdbc:h2:~/test";

    // // Database credentials
    // static final String USER = "sa";
    // static final String PASS = "password";
    @Autowired
    ProductService productService;

    public void InitData() {

        Product prod = new Product();
        prod.Name = "Smarties Torte";
        prod.Price = 15.00;

        File file = new File("images/Smarties-Torte.jpg");

        byte[] bFile = new byte[(int) file.length()];
        
        try {
         FileInputStream fileInputStream = new FileInputStream(file);
         //convert file into array of bytes
         fileInputStream.read(bFile);
         fileInputStream.close();
        } catch (Exception e) {
         e.printStackTrace();
        }



        //prod.Image =  bFile;

        productService.saveOrUpdate(prod);
        // Connection conn = null;
        // Statement stmt = null;
        // try {

        // Class.forName(JDBC_DRIVER);

        // conn = DriverManager.getConnection(DB_URL, USER, PASS);

        // stmt = conn.createStatement();
        // String sql = "INSERT INTO products (product_name, product_price,
        // product_image) " + "VALUES (?, ?, ?)";
        // PreparedStatement prepStmt = conn.prepareStatement(sql);
        // String imagePath = "images/Smarties-Torte.jpg";
        // prepStmt.setString(1, "Smarties Torte");
        // prepStmt.setDouble(2, 15);
         //prepStmt.setBinaryStream(3, new FileInputStream(new File(imagePath)), (int)
        // new File(imagePath).length());
        // prepStmt.executeUpdate();
        // imagePath = "images/Peanutbutter-Torte.jpg";
        // prepStmt.setString(1, "Peanutbutter Torte");
        // prepStmt.setDouble(2, 50);
        // prepStmt.setBinaryStream(3, new FileInputStream(new File(imagePath)), (int)
        // new File(imagePath).length());
        // prepStmt.executeUpdate();
        // imagePath = "images/Igel-Torte.jpg";
        // prepStmt.setString(1, "Igel Torte");
        // prepStmt.setDouble(2, 30);
        // prepStmt.setBinaryStream(3, new FileInputStream(new File(imagePath)), (int)
        // new File(imagePath).length());
        // prepStmt.executeUpdate();

        // prepStmt.close();
        // stmt.close();
        // conn.close();
        // } catch (SQLException se) {
        // // Handle errors for JDBC
        // se.printStackTrace();
        // } catch (Exception e) {
        // // Handle errors for Class.forName
        // e.printStackTrace();
        // } finally {
        // // finally block used to close resources
        // try {
        // if (stmt != null)
        // stmt.close();
        // } catch (SQLException se2) {
        // } // nothing we can do
        // try {
        // if (conn != null)
        // conn.close();
        // } catch (SQLException se) {
        // se.printStackTrace();
        // } // end finally try
        // } // end try
    }
}
