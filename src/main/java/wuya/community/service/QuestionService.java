package wuya.community.service;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wuya.community.dto.PaginationDTO;
import wuya.community.exception.CustomizeErrorCode;
import wuya.community.exception.CustomizeException;
import wuya.community.mapper.QuestionExtMapper;
import wuya.community.mapper.QuestionMapper;
import wuya.community.mapper.UserMapper;
import wuya.community.model.Question;
import wuya.community.model.QuestionDTO;
import wuya.community.model.QuestionExample;
import wuya.community.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO pagination = new PaginationDTO();
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
        pagination.setPagination(totalCount,page,size);
        if(page < 1) page =1;
        if(page>pagination.getTotalPage()) page=pagination.getTotalPage();
        Integer offset = size*(page-1);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        List<QuestionDTO> questions = new ArrayList<>();
        for (Question question:questionList) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            questions.add(questionDTO);
        }
        pagination.setQuestions(questions);
        return pagination;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO pagination = new PaginationDTO();
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(example);
        pagination.setPagination(totalCount,page,size);
        if(page < 1) page =1;
        if(page>pagination.getTotalPage()) page=pagination.getTotalPage();
        Integer offset = size*(page-1);
        QuestionExample example1 = new QuestionExample();
        example1.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(example1,new RowBounds(offset,size));
        List<QuestionDTO> questions = new ArrayList<>();
        for (Question question:questionList) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            questionDTO.setUser(user);
            questions.add(questionDTO);
        }
        pagination.setQuestions(questions);
        return pagination;

    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else{
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updated!=1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

        }
    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);

    }
}
