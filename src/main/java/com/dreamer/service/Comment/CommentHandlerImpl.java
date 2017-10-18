package com.dreamer.service.Comment;

import com.dreamer.domain.comment.Comment;
import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.domain.pmall.goods.PmallGoods;
import com.dreamer.domain.user.Agent;
import com.dreamer.repository.comment.CommentDao;
import com.dreamer.service.mobile.GoodsHandler;
import com.dreamer.service.mobile.MallGoodsHandler;
import com.dreamer.service.mobile.impl.BaseHandlerImpl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.mx.otter.utils.SearchParameter;

import java.util.Date;
import java.util.List;

@Service
public class CommentHandlerImpl extends BaseHandlerImpl<Comment> implements CommentHandler  {

    @Override
    @Transactional
    public void changePublishStatus(Integer cid) {
        Comment comment = get(cid);
        if(comment.getCanPublish()){
            comment.setCanPublish(false);
        }else {
            comment.setCanPublish(true);
        }
        merge(comment);
    }

    @Override
    public List<Comment> findByGid(Integer mallType, Integer gid, Integer limit) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Comment.class);
        criteria.add(Restrictions.eq("gid",gid));
        criteria.add(Restrictions.eq("type",mallType));
        criteria.add(Restrictions.eq("canPublish",true));
        criteria.addOrder(Order.desc("id"));
        return commentDao.findByCriteria(criteria,0,100);
    }

    @Override
    @Transactional
    public Comment addComment(String body, Integer gid, Integer mallType, Agent agent) {
        String goodsName;
        if(mallType==0){//特权商城
            Goods goods = goodsHandler.get(gid);
            goodsName=goods.getName();
        }else {
            PmallGoods pmallGoods = mallGoodsHandler.get(gid);
            goodsName=pmallGoods.getName();
        }
        Comment comment = new Comment();
        comment.setAgent(agent);
        comment.setGoodsName(goodsName);
        comment.setCanPublish(false);
        comment.setBody(body);
        comment.setGid(gid);
        comment.setType(mallType);
        comment.setUpdateTime(new Date());
        merge(comment);
        return comment;
    }

    @Override
    public List<Comment> findByPage(SearchParameter<Comment> searchParameter) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Comment.class);
        criteria.addOrder(Order.desc("id"));
        List<Comment> comments =  commentDao.searchByPage(searchParameter,criteria);
        return comments;
    }

    private CommentDao commentDao;

    public CommentDao getCommentDao() {
        return commentDao;
    }

    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        super.setBaseDao(commentDao);
        this.commentDao = commentDao;
    }

    @Autowired
    private MallGoodsHandler mallGoodsHandler;

    @Autowired
    private GoodsHandler goodsHandler;

}
