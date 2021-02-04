package demo.pipeline;

import java.util.List;
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

    @GetMapping
    public String index(Model model, String searchTerm) {
        List<Product> list = productService.getAllProducts();
        if (list.size() == 0) {
            InitData();
            list = productService.getAllProducts();
        }
        model.addAttribute("Products", list);
        return "index";
    }

    private void InitData() {

        Product prod = new Product();
        prod.Name = "Smarties Torte";
        prod.Price = 15.00;
        prod.Image = "/images/Smarties-Torte.jpg";
        productService.saveOrUpdate(prod);

        Product prod1 = new Product();
        prod1.Name = "Igel Torte";
        prod1.Price = 20.00;
        prod1.Image = "/images/Igel-Torte.jpg";
        productService.saveOrUpdate(prod1);

        Product prod2 = new Product();
        prod2.Name = "Peanutbutter Torte";
        prod2.Price = 25.00;
        prod2.Image = "/images/Peanutbutter-Torte.jpg";
        productService.saveOrUpdate(prod2);
    }
}
