package wuya.community.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wuya.community.dto.CommentDTO;
import wuya.community.enums.CommentTypeEnum;
import wuya.community.enums.NotificationStatusEnum;
import wuya.community.enums.NotificationTypeEnum;
import wuya.community.exception.CustomizeErrorCode;
import wuya.community.exception.CustomizeException;
import wuya.community.mapper.CommentExtMapper;
import wuya.community.mapper.CommentMapper;
import wuya.community.mapper.NotificationMapper;
import wuya.community.mapper.QuestionExtMapper;
import wuya.community.mapper.QuestionMapper;
import wuya.community.mapper.UserMapper;
import wuya.community.model.Comment;
import wuya.community.model.CommentExample;
import wuya.community.model.Notification;
import wuya.community.model.Question;
import wuya.community.model.User;
import wuya.community.model.UserExample;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private NotificationMapper notificationMapper;


    @Transactional
    public void insert(Comment comment, User commentator) {
        if(comment.getParentId()==null || comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PATH_NOT_FOUND);
        }
        if(comment.getType()==null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if(comment.getType()==CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //评论入库
            commentMapper.insert(comment);
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            //通知入库
            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(NotificationTypeEnum.REPLY_COMMENT.getType());
            notification.setOuterId(question.getId());
            notification.setNotifier(comment.getCommentator());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notification.setReceiver(dbComment.getCommentator());
            notification.setNotifierName(commentator.getName());
            notification.setOuterTitle(question.getTitle());
            notificationMapper.insert(notification);
        }else{
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //评论入库
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
            //通知入库
            Notification notification = new Notification();
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setType(NotificationTypeEnum.REPLY_QUESTION.getType());
//            notification.setOuterId(question.getId());
            notification.setOuterId(question.getId());
            notification.setNotifier(comment.getCommentator());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notification.setReceiver(question.getCreator());
            notification.setNotifierName(commentator.getName());
            notification.setOuterTitle(question.getTitle());
            notificationMapper.insert(notification);
        }
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria().andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);
        if(comments.size()==0){
            return new ArrayList<>();
        }
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);

        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
