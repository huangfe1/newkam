package com.dreamer.view.mobile;

import com.dreamer.domain.account.GoodsAccount;
import com.dreamer.domain.user.Agent;
import com.dreamer.service.mobile.AgentHandler;
import com.dreamer.util.ExcelFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/count")
@Controller
public class CountController {

    //按照团队统计所有人的账户余额
    @RequestMapping("/goodsAccounts.json")
    public void countGA(HttpServletResponse response) {
//        List<Agent> agents = agentHandler.getList("parent.id", 03);//找出公司所有的直接代理
        Map<String,Object> params = new HashMap<>();
//        params.put("id",237975);
//        params.put("id",87721);
//        params.put("id",86375);
        params.put("id",86440);
        List<Agent> agents = agentHandler.getOr(params);
        System.out.println(agents.size()+"--");
        List<String> ss = new ArrayList<>();//页名
        List<List> hs = new ArrayList<>();//列名
        List<List<Map>> ds = new ArrayList<>();//数据
        for (int i = 0; i < agents.size(); i++) {
            Agent tem = agents.get(i);
            ss.add(tem.getRealName());//页名
            List<String> headers = new ArrayList<>();
            headers.add("名字");
            headers.add("心生爱目");
            headers.add("玻尿酸精华液");
            hs.add(headers);//列名
            List<Agent> childrens = agentHandler.getAllChildrens(tem.getAgentCode());
            System.out.println(i);
            List<Map> sdatas = new ArrayList<>();
            for (Agent c : childrens) {
               Map m = new HashMap();
                m.put(0, c.getRealName() + c.getAgentCode());
                for (GoodsAccount cg : c.getGoodsAccounts()) {
                    if (cg.getGoods().getName().equals(headers.get(1))) {
                        m.put(1, cg.getCurrentBalance());
                    }
                    if (cg.getGoods().getName().equals(headers.get(2))) {
                        m.put(2, cg.getCurrentBalance());
                    }
                    System.out.println(cg.getCurrentBalance());
                }
                sdatas.add(m);//装载数据
            }
            ds.add(sdatas);//数据
        }
        ExcelFile.ExpExs("",ss,hs,ds,response);//创建表格并写入
    }

    @Autowired
    private AgentHandler agentHandler;

}
