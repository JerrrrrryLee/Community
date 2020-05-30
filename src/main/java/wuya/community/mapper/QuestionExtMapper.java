package wuya.community.mapper;

import wuya.community.model.Question;

public interface QuestionExtMapper {
    int incView(Question question);
    int incCommentCount(Question question);

}