package wuya.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import wuya.community.dto.PaginationDTO;
import wuya.community.model.User;
import wuya.community.service.QuestionService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;
    @GetMapping("/profile/{action}")
    public String profile(@RequestParam(value = "page",defaultValue = "1") Integer page,
                          @RequestParam(value = "size",defaultValue = "5") Integer size,
                          @PathVariable(name = "action") String action,
                          HttpServletRequest request,
                          Model model){
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        if("question".equals(action)){
            model.addAttribute("section","question");
            model.addAttribute("sectionName","我的提问");
        }
        if("reply".equals(action)){
            model.addAttribute("section","reply");
            model.addAttribute("sectionName","我的回复");
        }
        PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
        model.addAttribute("pagination",paginationDTO);
        return "profile";
    }

}
