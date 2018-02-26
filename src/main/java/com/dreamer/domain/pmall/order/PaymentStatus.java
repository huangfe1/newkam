package com.dreamer.domain.pmall.order;

public enum PaymentStatus {
	UNPAID("未支付"), PAID("已支付"),PAID_ERROR("已收钱,异常"),REFUND("已退款"),NOT_ENOUGH("券不够");

	private String name;

	public String getName() {
		return name;
	}

	PaymentStatus(String name) {
		this.name = name;
	}
}
