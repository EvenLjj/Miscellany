package cn.com.evenliu.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by liu on 2016/11/19.
 */
@Controller
@RequestMapping("/live")// url:  /模块/资源/{id}细分
public class MainController {

    @RequestMapping(value="/index",method= RequestMethod.GET)
    public String getIndex(Model model){
        System.out.println("test");
        return "index";
    }
}
