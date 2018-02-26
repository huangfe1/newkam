package com.dreamer.service.mobile;

import com.dreamer.domain.account.GoodsAccount;
import com.dreamer.domain.mall.goods.Goods;
import com.dreamer.domain.user.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ps.mx.otter.exception.ApplicationException;
import ps.mx.otter.utils.date.DateUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class AuthorizationLetterHandler {

	public byte[] generateLetter(Agent agent, Path path) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
	    try {
            GoodsAccount goodsAccount = goodsAccountHandler.getMainGoodsAccount(agent);
			Goods goods = goodsAccount.getGoods();
			GoodsAccount gac=agent.loadAccountForGoodsNotNull(goods);
			ByteArrayInputStream in = new ByteArrayInputStream(Files.readAllBytes(path));
			BufferedImage image = ImageIO.read(in);
			BufferedImage tag = new BufferedImage(600, 866, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gp = (Graphics2D) tag.getGraphics();
			gp.drawImage(image, 0, 0, null);
			gp.setColor(Color.BLACK);
			gp.setFont(new Font("宋体", Font.BOLD, 18));
			gp.drawString(agent.getRealName(), 211, 395);
			gp.drawString(agent.getWeixin(), 370, 395);
//			gp.drawString(replacePrivacy(agent.getMobile()), 250, 468);
			gp.drawString("********", 250, 435);
			gp.drawString(agent.getAgentCode(), 260, 463);
			String levelName = gac.getAgentLevel().getName();
			gp.setFont(new Font("宋体", Font.BOLD, 20));
			gp.drawString(levelName, 280-levelName.length()*10, 563);
			gp.setFont(new Font("宋体", Font.BOLD, 18));
			gp.drawString(DateUtil.date2string(agent.getJoinDate(),DateUtil.DATE_FORMAT), 170, 663);
			gp.dispose();
			ImageIO.write(tag,"png",out);
			return out.toByteArray();
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new ApplicationException("生成授权证书失败");
		}finally {
            out.close();
        }
    }

	private String replacePrivacy(String data){
		if(Objects.isNull(data)){
			return "";
		}
		if(data.length()>4){
			return data.substring(0, data.length()-4)+PRIVACY;
		}else{
			return PRIVACY;
		}
	}

	private final static String PRIVACY="****";

	@Autowired
	private GoodsAccountHandler goodsAccountHandler;
}
