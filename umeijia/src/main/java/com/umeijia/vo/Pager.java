package com.umeijia.vo;


import java.util.List;

/**
 * Bean类 - 分页
 * ============================================================================
 * 版权所有 醒点科技，并保留所有权利。
 * ----------------------------------------------------------------------------
 * 提示：
 * ----------------------------------------------------------------------------
 * 官方网站：http://www.eureka.com
 * ----------------------------------------------------------------------------
 * KEY: storeD42921D2B935F129755E6FCD4866E661
 * ============================================================================
 */

@SuppressWarnings("unchecked")
public class Pager {

	// 排序方式
	public enum OrderType {
		asc, desc
	}

	public static final Integer MAX_PAGE_SIZE = 21;// 每页最大记录数限制
    public static int normalPageSize = 20; //正常每页显示的条数
	private Integer pageNumber = 1;// 当前页码
	private Integer pageSize = 20;// 每页记录数
	private Integer totalCount = 0;// 总记录数
	private Integer pageCount = 0;// 总页数

	private OrderType orderType = OrderType.desc;// 排序方式
	private List list;// 数据List

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		} else if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}
		this.pageSize = pageSize;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageCount() {
		pageCount = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			pageCount++;
		}
		return pageCount;
	}
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}

}