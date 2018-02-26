package com.dreamer.service.mobile.impl;

import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.domain.mall.transfer.Transfer;
import com.dreamer.domain.user.Accounts;
import com.dreamer.domain.user.AccountsRecord;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.enums.AccountsType;
import com.dreamer.repository.mobile.AccountsDao;
import com.dreamer.repository.mobile.TransferDao;
import com.dreamer.service.mobile.AccountsHandler;
import com.dreamer.service.mobile.AgentHandler;
import com.dreamer.service.mobile.TransferHandler;
import com.dreamer.util.CommonUtil;
import com.dreamer.util.ExcelFile;
import com.dreamer.util.PreciseComputeUtil;
import com.dreamer.util.RewardUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ps.mx.otter.exception.ApplicationException;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by huangfei on 02/07/2017.
 */
@Service
public class AccountsHandlerImpl extends BaseHandlerImpl<Accounts> implements AccountsHandler {


    /**
     * 减少账户
     * 生成记录
     *
     * @param accounts
     * @param amount
     */
    public AccountsRecord deductAccountAndRecord(AccountsType accountsType, Agent agent, Agent casedAgent, Double amount, String more) {
        Accounts accounts = agent.getAccounts();
        Double nowAmount = deductAccount(accountsType, accounts, amount);
        AccountsRecord record = new AccountsRecord(agent, casedAgent, accountsType, more, amount, nowAmount, new Date(), AccountsRecord.SUB);
        return record;
    }


    /**
     * 减少账户
     * 生成记录
     *
     * @param accounts
     * @param amount
     */
    public AccountsRecord deductAccountAndRecord(AccountsType accountsType, Agent agent, Agent casedAgent, Double amount, String more, Boolean validate) {
        Accounts accounts = agent.getAccounts();
        Double nowAmount = deductAccount(accountsType, accounts, amount, validate);
        AccountsRecord record = new AccountsRecord(agent, casedAgent, accountsType, more, amount, nowAmount, new Date(), AccountsRecord.SUB);
        return record;
    }

    /**
     * 增加账户
     * 生成记录
     *
     * @param accounts
     * @param amount
     */
    public AccountsRecord increaseAccountAndRecord(AccountsType accountsType, Agent agent, Agent casedAgent, Double amount, String more) {
        Accounts accounts = agent.getAccounts();
        Double nowAmount = increaseAccount(accountsType, accounts, amount);
        AccountsRecord record = new AccountsRecord(agent, casedAgent, accountsType, more, amount, nowAmount, new Date(), AccountsRecord.ADD);
        return record;
    }


    /**
     * 提高账户
     *
     * @param accounts
     * @param amount
     */
    private Double increaseAccount(AccountsType accountsType, Accounts accounts, Double amount) {
        validateAmount(amount);
        Double balance = accounts.getAccount(accountsType);//获取当前要转换的库存
        balance = PreciseComputeUtil.add(balance, amount);//加法计算
        accounts.setAccount(balance, accountsType);//设置为新的值
        return balance;
    }


    /**
     * 减少账户
     *
     * @param accounts
     * @param amount
     */
    private Double deductAccount(AccountsType accountsType, Accounts accounts, Double amount) {
//        validateAmount(amount);
//        Double balance = accounts.getAccount(accountsType);//获取当前要转换的库存
//        balance = PreciseComputeUtil.sub(balance, amount);//计算
//        validateResult(balance, accountsType);//验证结果
//        accounts.setAccount(balance, accountsType);//设置为新的值
        return deductAccount(accountsType, accounts, amount, true);
    }

    /**
     * 减少账户
     *
     * @param accounts
     * @param amount
     */
    private Double deductAccount(AccountsType accountsType, Accounts accounts, Double amount, boolean validate) {
        //四舍五入 保留两位小数
        validateAmount(amount);
        Double balance = accounts.getAccount(accountsType);//获取当前要转换的库存
        balance = PreciseComputeUtil.sub(balance, amount);//计算
        if (validate) {
            validateResult(balance, accountsType);//验证结果
        }
        accounts.setAccount(balance, accountsType);//设置为新的值
        return balance;
    }


    /**
     * 验证金额
     *
     * @param amount
     */
    private void validateAmount(Double amount) {
        if (amount <= 0 || !PreciseComputeUtil.isMoneyAmount(amount)) {//值非法
            throw new ApplicationException("金额输入错误！" + amount);
        }
    }


    /**
     * 验证结果
     *
     * @param result
     * @param accountsType
     * @return
     */
    private void validateResult(Double result, AccountsType accountsType) {
        if (result < 0) {
            throw new ApplicationException(accountsType.getStateInfo() + "余额不足");
        }
    }


//    /**
//     * 返回可用的代金券
//     *
//     * @param agent
//     * @param amount
//     * @return
//     */
//    public Double getAvailableVoucher(Agent agent, Double amount) {
//       return getAvailableAccounts(agent,amount,AccountsType.VOUCHER);
//    }

    //返回可用的券
    @Override
    public Double getAvailableAccounts(Agent agent, Double amount, AccountsType accountsType) {
        Double tem = agent.getAccounts().getAccount(accountsType);
        amount = PreciseComputeUtil.round(amount);//进货使用3%
        if (tem >= amount) {
            return amount;
        } else {
            return tem;//全用了
        }
    }


    /**
     * 返回可用的代金券
     *
     * @param agent
     * @param amount
     * @return
     */
    public Double getAvailableVoucher(Agent agent, Double amount) {
        Double voucher = agent.getAccounts().getAccount(AccountsType.VOUCHER);
        if (voucher >= amount) {
            return amount;
        } else {
            throw new ApplicationException("代金券余额不足,需要" + amount);
        }
    }


    /**
     * 返回可用的进货券
     *
     * @param agent
     * @param amount
     * @return
     */
    public Double getAvailablePurchase(Agent agent, Double amount) {
        return getAvailableAccounts(agent, amount, AccountsType.PURCHASE);
    }


    @Override
    public Map<Agent, HashMap<String, Object>> rewardVoucher(Agent agent, Goods goods, List<Agent> parents, String voucherStr, Integer qunantity, Boolean isAdd) {
        Map<Agent, HashMap<String, Object>> maps = new HashMap<>();
        //返利参数
        Double[] vs = RewardUtil.getVsFromStr(voucherStr);
        //循环返利
        for (int i = 0; i < parents.size(); i++) {
            Double result = 0.0;//该返的奖金
            //加上基础的返利
            result += RewardUtil.getVipVoucher(vs, i, qunantity, parents.get(i).getJoinDate());
            //如果返利为0不加入
            if (result.equals(0.0)) {
                continue;
            }
            String more;
            if (isAdd) {
                more = agent.getRealName() + "买" + goods.getId() + "X" + qunantity + "=" + result + "   ";
            } else {
                more = agent.getRealName() + "退" + goods.getId() + "X" + qunantity + "=" + result + "   ";
            }

            //加入返利map
            if (maps.containsKey(parents.get(i))) {
                HashMap<String, Object> temsb = maps.get(parents.get(i));
                Double temp = (Double) temsb.get("v");
                if (isAdd) {
                    temsb.put("v", PreciseComputeUtil.add(temp, result));
                } else {
                    temsb.put("v", PreciseComputeUtil.sub(temp, result));
                }

                temsb.put("s", temsb.get("s") + more);
                maps.put(parents.get(i), temsb);
            } else {
                HashMap<String, Object> temsb = new HashMap<>();

                temsb.put("s", more);
                if (isAdd) {
                    temsb.put("v", result);
                } else {
                    temsb.put("v", -result);
                }
                maps.put(parents.get(i), temsb);
            }
        }
        return maps;
    }

    @Override
    public Map<Agent, HashMap<String, Object>> rewardVoucher(String startTime, String endTime, String agentCode, HttpServletResponse response) {
        //转出所有股东的已成交的退货或者转货
        List<Transfer> transfers = transferHandler.findRecords(startTime, endTime, agentCode);
        HashMap<Agent, HashMap<String, Object>> map = new HashMap<>();
        HashMap<String, HashMap<Agent, Integer>> qmap = new HashMap<>();
        for (Transfer transfer : transfers) {
            HashMap<Agent, HashMap<String, Object>> tem = new HashMap<>();
            HashMap<String, HashMap<Agent, Integer>> qtem = new HashMap<>();
            List<Agent> parents = new ArrayList<>();
            Agent agent;
            //进货
            if (transfer.getFromAgent().isMutedUser()) {
                agent = transfer.getToAgent();
            } else {//退货
                agent = transfer.getFromAgent();
            }
            final Boolean isAdd = transfer.getFromAgent().isMutedUser();//转出人是公司就是进货
            Agent parent = agent.getParent();
            //返利的上级
            while (parent != null && !parent.isMutedUser()) {
                if (agentHandler.canReward(parent)) parents.add(parent);
                parent = parent.getParent();
            }
            transfer.getItems().forEach(item -> {
                //返利
                Map<Agent, HashMap<String, Object>> mapMap = rewardVoucher(agent, item.getGoods(), parents, item.getGoods().getVoucher(), item.getQuantity(), isAdd);
                CommonUtil.putAll(tem, mapMap);//订单内汇总
                //产品数量
                String key = item.getGoods().getName();
                Integer qv = item.getQuantity();
                if(!isAdd)qv=-qv;
                if (qtem.containsKey(key)) {//有当前产品统计
                    HashMap<Agent, Integer> qt = qtem.get(key);
                    if (qt.containsKey(agent)) {
                        Integer nowq = qt.get(agent);
                        qt.put(agent, nowq + qv);
                    } else {
                        qt.put(agent, qv);
                    }
                } else {
                    HashMap<Agent, Integer> qt = new HashMap<>();
                    qt.put(agent, qv);
                    qtem.put(key, qt);
                }
                CommonUtil.putAllN(qmap,qtem);//产品总数汇总
            });
            CommonUtil.putAll(map, tem);//返利数汇总
        }

        //导出
        List<String> headers = new ArrayList<>();

        headers.add("名字");
        headers.add("金额");
        headers.add("明细");

        List<String> ss = new ArrayList<>();
        ss.add("返利详情");
        List<Map> datas = new ArrayList<>();
        List<List> sh = new ArrayList<>();
        sh.add(headers);
        map.keySet().forEach(p->{
            Map m=new HashedMap();
            m.put(0,p.getAgentCode()+p.getRealName());
            m.put(1,map.get(p).get("v"));
            m.put(2,map.get(p).get("s"));
            datas.add(m);
//            System.out.println(p.getRealName()+"---"+map.get(p).get("v")+"---"+map.get(p).get("s"));
        });
        List<List<Map>> ds = new ArrayList<>();
        ds.add(datas);
        qmap.keySet().forEach(p->{
            HashMap<Agent,Integer> tem = qmap.get(p);
            ss.add(p+"购买数量");
            List<String> ht = new ArrayList<>();
            ht.add("名字");
            ht.add("数量");
            List<Map> dt = new ArrayList<>();
            tem.keySet().forEach(t->{
                Map m=new HashedMap();
//                m.put(0,t.getAgentCode()+t.getRealName());
                m.put(0,t.getAgentCode()+t.getRealName());
                m.put(1,tem.get(t));
                dt.add(m);
            });
            ds.add(dt);
            sh.add(ht);
        });



        ExcelFile.ExpExs("", ss, sh, ds, response);

        return map;
    }

    @Override
    public Map<Agent, Double> rewardPmallVoucher(List<Agent> parents, Integer qunantity, Double profit) {
        HashMap<Agent, Double> maps = new HashMap<>();
//        Map<String, Double> mapV = new HashMap<>();
        //截流总数
        Double sumReward = 0.0;
        //崔秀娟 特殊处理
        Boolean hasFenGs = false;
        //循环返利
        for (int i = 0; i < parents.size(); i++) {
            Double result;//该返的奖金
            String levelName = agentHandler.getLevelName(parents.get(i));
            //增加返利
            Double tem = getVipPerCent(parents.get(i)) * profit * qunantity;//获取额外的返利
            result = tem - sumReward;
            if (result < 0) {//减去已经返了的利润
                result = 0.0;
            }
            sumReward += result;//累积已经返了的利

            if (i < basePercent.length) {
                //加上基础的返利
                result += basePercent[i] * profit * qunantity;//返利比*毛利润*数量
            }

            result = PreciseComputeUtil.round(result);

            //如果返利为0不加入
            if (result.equals(0.0)) {
                continue;
            }
            //加入返利map
            if (maps.containsKey(parents.get(i))) {
                Double temp = maps.get(parents.get(i));
                maps.put(parents.get(i), PreciseComputeUtil.add(temp, result));
            } else {
                maps.put(parents.get(i), result);
            }
        }
        return maps;
    }

    private final String[] levelNames = {"VIP", "市代", "省代", "大区", "董事", "金董", "分公司", "联盟单位"};

    private final Integer[] paySum = {310, 2500, 4200, 16000, 32000, 64000, 128000, 1000000000};

    private final Double[] vipPercent = {0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.6};

    private final Double[] basePercent = {0.3, 0.05, 0.05};


    //获取该拿多少百分比
    private Double getVipPerCent(Agent agent) {
//        System.out.println(agent.getRealName()+"===");
        Double payBalance = agent.getAccounts().getAccount(AccountsType.CONSUME);//消费金额
        if (payBalance == null) payBalance = 0.0;
        //如果不是游客
        if (agent.getAgentCode() != null) {
            String levelName = agentHandler.getLevel(agent).getName();
            //不是亲情价
            //如果是联盟单位
            if (levelName.equals("联盟单位")) {
                return vipPercent[vipPercent.length - 1];//返回最后一个百分比
            }
            //如果不是联盟单位 获取级别对用的消费
            Integer index = getIndexFromArray(levelNames, levelName);
            if (index < paySum.length && index > -1) {
                payBalance += paySum[index];
            }
        }
        //获取对应消费对应的返利百分比
        for (int i = 0; i < paySum.length; i++) {
            if (payBalance < paySum[i]) {
                Integer ii = i - 1;
                if (ii < 0) {
                    return 0.0;
                } else {
                    return vipPercent[ii];
                }
            }
        }
        return vipPercent[vipPercent.length - 1];
    }


    /**
     * 从数组中获取对应位置
     *
     * @param objects
     * @param o
     * @return
     */
    private Integer getIndexFromArray(Object[] objects, Object o) {
        for (int i = 0; i < objects.length; i++) {
            if (o.equals(objects[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从数组中获取对应区间
     *
     * @param objects
     * @param o
     * @return
     */
    private Integer getIndexFromArray(Integer[] objects, Object o) {
        for (int i = 0; i < objects.length; i++) {
            if (o.equals(objects[i])) {
                return i;
            }
        }
        return -1;
    }


    public static void main(String[] args) {
        System.out.println(2.2 / 2);
        final String[] levelNames = {"VIP", "市代", "省代", "大区", "董事", "金董", "分公司", "联盟单位"};

        final Integer[] paySum = {310, 2500, 4200, 16000, 32000, 64000, 128000, 1000000000};

        final Double[] percent = {0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.6};

        Double payBalance = 1222222.0;//消费金额
        if (payBalance == null) payBalance = 2200.0;
        //如果不是游客
        if (true) {
            String levelName = "金董";
            //不是亲情价
            //如果是联盟单位
            if (levelName.equals("联盟单位")) {
                System.out.println(percent[percent.length - 1]);
                return;
            }
            //如果不是联盟单位 获取级别对用的消费
            Integer index = new AccountsHandlerImpl().getIndexFromArray(levelNames, levelName);
            System.out.println(index);
            if (index < paySum.length && index > -1) {
                payBalance += paySum[index];
                System.out.println(payBalance);
            }
        }
        //获取对应消费对应的返利百分比
        for (int i = 0; i < paySum.length; i++) {
            if (payBalance < paySum[i]) {
                Integer ii = i - 1;
                if (ii < 0) {
                    System.out.println(0.0);
                    return;
                } else {
                    System.out.println(ii);
                    System.out.println(percent[ii]);
                    return;
                }
            }
        }
        System.out.println(percent[percent.length - 1]);
    }

    private AccountsDao accountsDao;

    private TransferDao transferDao;

    @Autowired
    private AgentHandler agentHandler;

    @Autowired
    private TransferHandler transferHandler;


    public AccountsDao getAccountsDao() {
        return accountsDao;
    }

    @Autowired
    public void setAccountsDao(AccountsDao accountsDao) {
        this.accountsDao = accountsDao;
        super.setBaseDao(accountsDao);
    }
}
