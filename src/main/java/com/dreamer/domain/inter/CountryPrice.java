package com.dreamer.domain.inter;

import com.dreamer.domain.mall.goods.Goods;

public class CountryPrice {

    private Integer id;

    private Country country;

    private Goods goods;//产品

    private Double price;//实际价格

    private Double profit;//利润

    private String img;//每个不同国家价格都有图片

    private Integer open;//0 代表关闭  1代表打开

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }


}
