package demo.pipeline;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/demo" })
public class DemoController {

    @GetMapping
    public String demoGet(Model model, String name) {

        
        if ((name == null) || (!name.isEmpty())) {
            name = "unknown";
        }
        model.addAttribute("name", name);
        return "demo";
    }

    @PostMapping
    public String demoPost(Model model, String name) {

        
        model.addAttribute("name", name);
        return "demo";
    }

}
