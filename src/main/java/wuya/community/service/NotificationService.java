package wuya.community.service;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wuya.community.dto.NotificationDTO;
import wuya.community.dto.PaginationDTO;
import wuya.community.enums.NotificationTypeEnum;
import wuya.community.mapper.CommentMapper;
import wuya.community.mapper.NotificationMapper;
import wuya.community.mapper.QuestionMapper;
import wuya.community.mapper.UserMapper;
import wuya.community.model.Notification;
import wuya.community.model.NotificationExample;

import java.util.ArrayList;
import java.util.List;
@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> pagination = new PaginationDTO<>();
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(example);
        pagination.setPagination(totalCount,page,size);
        if(page < 1) page =1;
        if(page>pagination.getTotalPage()) page=pagination.getTotalPage();
        Integer offset = size*(page-1);
        NotificationExample example1 = new NotificationExample();
        example1.createCriteria().andReceiverEqualTo(userId);
        List<Notification> notificationList = notificationMapper.selectByExampleWithRowbounds(example1, new RowBounds(offset, size));
        if(notificationList.size()==0){
            return pagination;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notificationList) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setType(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        pagination.setDatas(notificationDTOS);
        return pagination;
    }

    public Long unreadCount(Long userId) {
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        return notificationMapper.countByExample(example);
    }
}
