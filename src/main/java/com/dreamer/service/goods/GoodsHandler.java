package com.dreamer.service.goods;

import com.dreamer.domain.account.GoodsAccount;
import com.dreamer.domain.inter.Country;
import com.dreamer.domain.inter.CountryPrice;
import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.domain.mall.goods.GoodsCategory;
import com.dreamer.domain.mall.goods.Price;
import com.dreamer.domain.mall.goods.StockBlotter;
import com.dreamer.domain.user.AgentLevel;
import com.dreamer.domain.user.MutedUser;
import com.dreamer.repository.goods.GoodsDAO;
import com.dreamer.repository.goods.PriceDAO;
import com.dreamer.repository.goods.StockBlotterDAO;
import com.dreamer.repository.mobile.GoodsCategoryDao;
import com.dreamer.repository.system.SystemParameterDAOInf;
import com.dreamer.repository.user.AgentLevelDAO;
import com.dreamer.repository.user.MutedUserDAO;
import com.dreamer.service.inter.CountryHandler;
import com.dreamer.service.inter.CountryPriceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ps.mx.otter.exception.ApplicationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class GoodsHandler {

	@Transactional
	public GoodsCategory saveOrUpdateGoodsCategory(GoodsCategory goods) {
		GoodsCategory instance = categoryDao.merge(goods);
		return instance;
	}

	@Transactional
	public void removeGoodsCategory(GoodsCategory goods) {
		categoryDao.delete(goods);//删除
	}

	@Transactional
	public Goods saveOrUpdateGoods(Goods goods, Double[] levelPrice, Integer[] levelThreshold, Integer[] levelId, Integer[] priceId,Integer[] cids,Integer[] cpids,Double[] cps,Double[] profits,String[] cfNames,Integer[] opens) {
		assemblePrice(goods, levelPrice, levelThreshold, levelId, priceId);//价格重新计算
		assembleCountryPrice(goods,cps,profits,cids,cpids,cfNames,opens);//增加国际价格
        Goods instance = goodsDAO.merge(goods);
		return instance;
	}

	public String saveImg(MultipartFile file) {
		String imgType = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
		String fileName = generateFileName(imgType);
		try {
			writeImgFile(file.getBytes(), fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	@Transactional
	public void addStock(StockBlotter stock) {
		Goods goods = goodsDAO.findById(stock.getGoods().getId());
		MutedUser companyUser = mutedUserDAO.loadFirstOne();
		GoodsAccount companyAccount = companyUser.loadAccountForGoodsNotNull(goods);
		companyAccount.increaseBalance(stock.getChange());
		goods.addStockBlotter(stock);
		stockBlotterDAO.save(stock);
		LOG.debug("新增库存成功");
	}


	private void assemblePrice(Goods goods, Double[] levelPrice,
							   Integer[] levelThreshold, Integer[] levelId, Integer[] priceId) {
		if (levelId != null && levelId.length > 0) {
			for (int index = 0; index < levelId.length; index++) {
				Price price ;
				if (priceId[index] != null) {
					price = priceDAO.findById(priceId[index]);
				} else {
					price = new Price();
					AgentLevel agentLevel = agentLevelDAO
							.findById(levelId[index]);
					price.setAgentLevel(agentLevel);
				}
				price.setPrice(levelPrice[index]);
				price.setThreshold(levelThreshold[index]);
				goods.addPrice(price);
			}
		}
	}



	private void assembleCountryPrice(Goods goods, Double[] cps,Double[] profits
							   , Integer[] cids, Integer[] cpids,String[] cfileNames,Integer[] opens) {
	    if (cids != null && cids.length > 0) {
			for (int index = 0; index < cids.length; index++) {
				CountryPrice price ;
				Integer open;
                if (cpids.length>0&&cpids[index] != null) {
					price = countryPriceHandler.get(cpids[index]);
				} else {
					price = new CountryPrice();
					Country country = countryHandler.get(cids[index]);
					price.setCountry(country);
                }
                if(index<cfileNames.length){
                    if(cfileNames[index]!=null){
                        price.setImg(cfileNames[index]);
                    }
                }
				if(index<opens.length){
					if(opens[index]!=null){
						price.setOpen(opens[index]);
					}
				}
				price.setPrice(cps[index]);
				price.setProfit(profits[index]);
				goods.addCountryPrice(price);
			}
		}
	}

	private String generateFileName(String imgType) {
		String s = UUID.randomUUID().toString();
		StringBuilder sbd = new StringBuilder();
		sbd.append(s.substring(0, 8)).append(s.substring(9, 13)).append(s.substring(14, 18)).append(s.substring(19, 23)).append(s.substring(24)).append(imgType);
		return sbd.toString();
	}


	private void writeImgFile(byte[] imgBytes, String fileName) {
		try {
			Path path = Paths.get(systemParameterDAO.getGoodsImgPath());
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			Path imgFile = path.resolve(fileName);
			if (!Files.exists(imgFile)) {
				Files.createFile(imgFile);
			}
			Files.write(imgFile, imgBytes, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException iop) {
			LOG.error("写图片文件失败", iop);
			throw new ApplicationException(iop);
		}
	}


	@Transactional
	public void removeGoods(Goods goods) {
		goodsDAO.delete(goods);
		LOG.debug("删除产品成功");
	}

	@Autowired
	private GoodsDAO goodsDAO;
	@Autowired
	private GoodsCategoryDao categoryDao;
	@Autowired
	private AgentLevelDAO agentLevelDAO;
	@Autowired
	private PriceDAO priceDAO;
	@Autowired
	private StockBlotterDAO stockBlotterDAO;
	@Autowired
	private SystemParameterDAOInf systemParameterDAO;
	@Autowired
	private MutedUserDAO mutedUserDAO;

	@Autowired
	private CountryPriceHandler countryPriceHandler;

	@Autowired
	private CountryHandler countryHandler;


	private final Logger LOG = LoggerFactory.getLogger(getClass());

}
