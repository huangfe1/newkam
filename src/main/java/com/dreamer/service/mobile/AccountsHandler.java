package com.dreamer.service.mobile;

import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.domain.user.Accounts;
import com.dreamer.domain.user.AccountsRecord;
import com.dreamer.domain.user.Agent;
import com.dreamer.domain.user.enums.AccountsType;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangfei on 02/07/2017.
 */
public interface AccountsHandler extends BaseHandler<Accounts>{


    AccountsRecord deductAccountAndRecord(AccountsType accountsType, Agent agent, Agent casedAgent, Double amount, String more);

    AccountsRecord deductAccountAndRecord(AccountsType accountsType, Agent agent, Agent casedAgent, Double amount, String more,Boolean validate);

    AccountsRecord increaseAccountAndRecord(AccountsType accountsType, Agent agent, Agent casedAgent, Double amount, String more);

    Double getAvailablePurchase(Agent agent, Double amount);

    Double getAvailableVoucher(Agent agent, Double amount);

    Double getAvailableAccounts(Agent agent, Double amount,AccountsType accountsType);

    Map<Agent,HashMap<String,Object>>  rewardVoucher(Agent agent, Goods goods, List<Agent> parents, String voucherStr, Integer qunantity,Boolean isAdd);

    Map<Agent,HashMap<String,Object>> rewardVoucher(String startTime, String endTime, String agentCode, HttpServletResponse response);

    Map<Agent, Double> rewardPmallVoucher(List<Agent> parents, Integer qunantity,Double profit);

}
