package wuya.community.exception;

public enum  CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001,"问题已被删除或不存在！"),
    TARGET_PATH_NOT_FOUND(2002,"未选中任何问题或评论！"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重试！"),
    SYSTEM_ERROR(2004,"服务器冒烟了，请稍后再试！"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在！"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在，要不换个试试！"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空！"),
    READ_NOTIFICATION_FAIL(2008,"非本用户权限操作！"),
    NOTIFICATION_NOT_FOUND(2009,"通知消息已被删除或不存在！");
    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code,String message) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

}
