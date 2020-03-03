package com.ulab.uchat.server.security.domain;

public class PageResult<T> {
    private int page;
    private int rows;
    private int total;
    private T data;
    
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}    
}