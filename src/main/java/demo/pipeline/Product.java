package demo.pipeline;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue
    public int Id;
    public String Name;
    public Double Price;
    public String Image;
}
