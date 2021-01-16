package demo.pipeline;

import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/demo" })
public class DemoController {

    @GetMapping
    public ModelAndView demo(Map<String, Object> model) {

        Random rand = new Random();
        model.put("text", rand.nextInt());
        return new ModelAndView("demo", model);
    }

}
