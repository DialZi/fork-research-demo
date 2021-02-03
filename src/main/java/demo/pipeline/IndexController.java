package demo.pipeline;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.websocket.Decoder.BinaryStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "/index", "/" })
public class IndexController {

    @Autowired
    ProductService productService;

    // // JDBC driver name and database URL
    // static final String JDBC_DRIVER = "org.h2.Driver";
    // static final String DB_URL = "jdbc:h2:mem:test";

    // // Database credentials
    // static final String USER = "sa";
    // static final String PASS = "password";

    @GetMapping
    public String index(Model model, String searchTerm) {
        List<Product> list = productService.getAllProducts();
        if (list.size() == 0) {
            InitData();
            list = productService.getAllProducts();
        }
        try {
            for (Product product : list) {
                InputStream imageStream = new FileInputStream(new File(product.ImagePath));
                product.Image = Base64.getEncoder().encodeToString(imageStream.readAllBytes());
            }
        } catch (Exception ex) {

        }

        model.addAttribute("Products", list);
        // Connection conn = null;
        // Statement stmt = null;
        // try {
        // // STEP 1: Register JDBC driver
        // Class.forName(JDBC_DRIVER);
        // // STEP 2: Open a connection
        // conn = DriverManager.getConnection(DB_URL, USER, PASS);

        // // STEP 3: Execute a query
        // stmt = conn.createStatement();
        // String sql = "SELECT * FROM products";
        // ResultSet rs = stmt.executeQuery(sql);
        // List<Product> productList = new ArrayList<Product>();
        // while (rs.next()) {
        // // Retrieve by column name
        // int id = rs.getInt("id");
        // String Name = rs.getString("product_name");
        // Double Price = rs.getDouble("product_price");
        // InputStream imageStream = rs.getBinaryStream("product_image");
        // String Image =
        // Base64.getEncoder().encodeToString(imageStream.readAllBytes());
        // productList.add(new Product(Name, Price, Image));
        // }
        // model.addAttribute("Products", productList);

        // // STEP 4: Clean-up environment
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
        // System.out.println("Goodbye!");

        return "index";
    }

    private void InitData() {

        Product prod = new Product();
        prod.Name = "Smarties Torte";
        prod.Price = 15.00;
        prod.ImagePath = "images/Smarties-Torte.jpg";
        productService.saveOrUpdate(prod);

        Product prod1 = new Product();
        prod1.Name = "Igel Torte";
        prod1.Price = 20.00;
        prod1.ImagePath = "images/Igel-Torte.jpg";
        productService.saveOrUpdate(prod1);

        Product prod2 = new Product();
        prod2.Name = "Peanutbutter Torte";
        prod2.Price = 25.00;
        prod2.ImagePath = "images/Peanutbutter-Torte.jpg";
        productService.saveOrUpdate(prod2);
    }
}
