package com.fido.framework.projectbuilder.utils;

import com.fido.framework.projectbuilder.model.Column;
import com.fido.framework.projectbuilder.model.Table;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.*;

public class DataBaseUtil {
	private static final String DEFAULT_PK = "id";

	private static Connection getConnection() {
		return getConnection(BuildProperties.getString("builder.datasource.driver-class-name"),BuildProperties.getString("builder.datasource.url"),
				BuildProperties.getString("builder.datasource.username"),
				BuildProperties.getString("builder.datasource.password"));
	}
	/**
	 * 获取连接
	 * @param driverClassNameprefixDiscard
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	private static Connection getConnection(String driverClassName, String url, String username, String password) {
		try {
			Class.forName(driverClassName).newInstance();
			Connection connection = DriverManager.getConnection(url, username, password);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 操作结束后需要关闭连接
	 */
	private static void closeConnection(Connection connection) {
		try {
			if (null != connection) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Collection<Table> getTables() {
		return getTables(BuildProperties.getString("builder.datasource.dbName"));
	}

	public static Collection<Table> getTables(String dbName) {
		Connection connection = null;
		Map<String, Table> tableMap = new HashMap<>();
		String include = BuildProperties.getString("builder.tables.include");
		String prefixDiscard = BuildProperties.getString("builder.tables.prefixDiscard");
		String nameMapping = BuildProperties.getString("builder.tables.nameMapping");

		List<String> includeTables = new ArrayList<>();
		List<String> excludeTables = new ArrayList<>();
		List<String> prefixDiscardList = new ArrayList<>();
		Map<String,String> nameMappingMap = new HashMap<>();
		if(null != include) {
			includeTables = Arrays.asList(include.split(","));
		} else {
			String exclude = BuildProperties.getString("builder.tables.exclude");
			excludeTables = Arrays.asList(exclude.split(","));
		}
		if(prefixDiscard != null) {
			prefixDiscardList = Arrays.asList(prefixDiscard.split(","));
		}
		if(nameMapping != null) {
			String[] oldNew = nameMapping.split(",");
			nameMappingMap.put(oldNew[0], oldNew[1]);
		}
		try {
			connection = getConnection();
			DatabaseMetaData dbMetaData = connection.getMetaData();
			//从元数据中获取到所有的表名
			ResultSet rs = dbMetaData.getTables(null, dbName, null, new String[]{"TABLE"});

			while(rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if((!CollectionUtils.isEmpty(includeTables) && !includeTables.contains(tableName))
						|| (!CollectionUtils.isEmpty(excludeTables) && excludeTables.contains(tableName))) {
					continue;
				}
				Table table  = tableMap.get(tableName);
				if (table == null) {
					table = new Table();
				}
				String pk = getPrimaryKeyName(dbMetaData, null, tableName);
				if(pk != null && !pk.equals(DEFAULT_PK)) {
					table.setPrimaryKeyName(pk);
				}
				table.setOriginalName(tableName);
				//先map在discard
				String newName = nameMappingMap.get(tableName);
				ResultSet columSet = dbMetaData.getColumns(dbName, dbName, tableName, "%");
				if (newName != null) {
					tableName = newName;
				}
				for(String discard:prefixDiscardList) {
					if(tableName.startsWith(discard)){
						//只做一次前缀discard
						tableName = tableName.substring(discard.length());
						break;
					}
				}
				if(!StringUtils.isEmpty(tableName)) {
					table.setFormattedName(tableName);
				}
				String remarks = rs.getString("REMARKS");
				if(!StringUtils.isEmpty(tableName)) {
					table.setRemarks(remarks);
				}
				tableMap.put(tableName, table);
				Map<String,Column> columnMap = new HashMap<>();
				while (columSet.next()) {
					//获得字段名
					String columnName = columSet.getString("COLUMN_NAME");
					//获得字段类型
					String typeName = columSet.getString("TYPE_NAME");
					String columnComment = columSet.getString("REMARKS");
					String nullable = columSet.getString("IS_NULLABLE");

					Column column = columnMap.get(columnName);
					if (column == null) {
						column = new Column();
					}
					if(!StringUtils.isEmpty(columnName)) {
						column.setColumnName(columnName);
					}
					if(!StringUtils.isEmpty(typeName)) {
						column.setColumnType(typeName);
					}
					if(!StringUtils.isEmpty(columnComment)) {
						column.setRemarks(columnComment);
					}
					if(!StringUtils.isEmpty(nullable)) {
						column.setNullAble(nullable);
					}

					columnMap.put(columnName, column);
				}
				table.setColumns(new ArrayList<>(columnMap.values()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return tableMap.values();
	}

	private static String getPrimaryKeyName(DatabaseMetaData dbMetaData, String schemaName, String tableName) throws SQLException {
		ResultSet rs = dbMetaData.getPrimaryKeys(null, schemaName, tableName);
		while (rs.next()){
			return rs.getString("COLUMN_NAME");//列名
		}
		return null;
	}
}