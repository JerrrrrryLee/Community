package wuya.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import wuya.community.dto.CommentDTO;
import wuya.community.enums.CommentTypeEnum;
import wuya.community.model.QuestionDTO;
import wuya.community.service.CommentService;
import wuya.community.service.QuestionService;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id, Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestionDTOs = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        questionService.incView(id);
        model.addAttribute("questionDTO",questionDTO);
        model.addAttribute("relatedQuestionDTOs",relatedQuestionDTOs);
        model.addAttribute("comments",comments);
        return "question";
    }
}
