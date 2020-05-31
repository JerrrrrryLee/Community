package wuya.community.mapper;

import wuya.community.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}