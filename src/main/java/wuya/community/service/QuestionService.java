package wuya.community.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wuya.community.dto.PaginationDTO;
import wuya.community.mapper.QuestionMapper;
import wuya.community.mapper.UserMapper;
import wuya.community.model.Question;
import wuya.community.model.QuestionDTO;
import wuya.community.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO pagination = new PaginationDTO();
        Integer totalCount = questionMapper.count();
        pagination.setPagination(totalCount,page,size);
        if(page<=0) page=1;
        if(page > pagination.getPagesNumber()) page=pagination.getPagesNumber();
        Integer offset = size*(page-1);
        List<Question> questionList = questionMapper.list(offset,size);
        List<QuestionDTO> questions = new ArrayList<>();
        for (Question question:questionList) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            User user = userMapper.findById(question.getCreator());
            questionDTO.setUser(user);
            questions.add(questionDTO);
        }
        pagination.setQuestions(questions);
        return pagination;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {

        PaginationDTO pagination = new PaginationDTO();
        Integer totalCount = questionMapper.countByUserId(userId);
        pagination.setPagination(totalCount,page,size);
        if(page<=0) page=1;
        if(page > pagination.getPagesNumber()) page=pagination.getPagesNumber();
        Integer offset = size*(page-1);
        List<Question> questionList = questionMapper.listByUserId(userId,offset,size);
        List<QuestionDTO> questions = new ArrayList<>();
        for (Question question:questionList) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            User user = userMapper.findById(question.getCreator());
            questionDTO.setUser(user);
            questions.add(questionDTO);
        }
        pagination.setQuestions(questions);
        return pagination;

    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        }else{
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.update(question);

        }
    }
}
