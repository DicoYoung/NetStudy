package com.netstudy.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Dico
 * @version 1.0
 * @description freemarker test
 * @date 2024/5/10 15:49
 **/
@Controller
public class FreemarkerController {
    @GetMapping("/testfreemarker")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        //指定模型
        modelAndView.addObject("name", "Hugh Jackman");
        //指定模板
        modelAndView.setViewName("test");//根据视图名称夹.ftl找到模板
        return modelAndView;
    }
}
