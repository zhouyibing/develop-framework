package com.yipeng.framework.projectbuilder.model;

import lombok.Data;

@Data
public class Column {
	private String columnName;
	
	private String columnType;
	
	private String remarks;
	
	private String nullAble;
}