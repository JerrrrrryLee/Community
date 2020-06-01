package wuya.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import wuya.community.dto.NotificationDTO;
import wuya.community.enums.NotificationTypeEnum;
import wuya.community.model.User;
import wuya.community.service.NotificationService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String notify(@PathVariable("id") Long id,
                         HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);
        if(notificationDTO.getType() == NotificationTypeEnum.REPLY_QUESTION.getType() ||
                notificationDTO.getType() == NotificationTypeEnum.REPLY_COMMENT.getType()){
            return "redirect:/question/"+notificationDTO.getOuterId();
        }
        return "profile";

    }
}
