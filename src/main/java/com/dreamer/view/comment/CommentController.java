package com.dreamer.view.comment;

import com.dreamer.domain.comment.Comment;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.User;
import com.dreamer.service.Comment.CommentHandler;
import com.dreamer.service.mobile.AgentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ps.mx.otter.utils.SearchParameter;
import ps.mx.otter.utils.WebUtil;
import ps.mx.otter.utils.message.Message;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @ResponseBody
    @RequestMapping("/add.json")
    public Message addComment(HttpServletRequest request,Integer gid,Integer mt,String body){
        try {
            User user = (User) WebUtil.getCurrentUser(request);
            Agent agent = agentHandler.get(user.getId());
            commentHandler.addComment(body,gid,mt,agent);
            return Message.createSuccessMessage();
        }catch (Exception e){
            return  Message.createFailedMessage(e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/change.json")
    public Message changePublish(Integer cid){
        try {
            commentHandler.changePublishStatus(cid);
            return Message.createSuccessMessage();
        }catch (Exception e){
            return Message.createFailedMessage(e.getMessage());
        }
    }

    @RequestMapping("/list")
    public String list(SearchParameter<Comment> comment, Model model,HttpServletRequest request){
        try {
            List<Comment> comments = commentHandler.findByPage(comment);
            model.addAttribute("comments",comments);
            WebUtil.turnPage(comment, request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "/comment/list";
    }


    @Autowired
    private AgentHandler agentHandler;

    @Autowired
    private CommentHandler commentHandler;

}
