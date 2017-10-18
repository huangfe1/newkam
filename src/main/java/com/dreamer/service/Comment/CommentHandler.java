package com.dreamer.service.Comment;

import com.dreamer.domain.comment.Comment;
import com.dreamer.domain.user.Agent;
import com.dreamer.service.mobile.BaseHandler;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

public interface CommentHandler extends BaseHandler<Comment> {

    void changePublishStatus(Integer cid);//改变评论的状态

    List<Comment> findByGid(Integer mallType,Integer gid,Integer limit);//查找某个产品的评论

    Comment addComment(String body, Integer gid, Integer mallType, Agent agent);//新增评论

    List<Comment> findByPage(SearchParameter<Comment> searchParameter);



}
