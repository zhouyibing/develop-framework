package com.yipeng.framework.projectbuilder.model;

import lombok.Data;

import java.util.List;

@Data
public class Table {
	private String primaryKeyName;
	private String originalName;
	private String formattedName;
	private String remarks;
	private List<Column> columns;
}