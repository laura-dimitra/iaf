/*
 * $Log: StatGroupTable.java,v $
 * Revision 1.1  2009-08-26 15:35:11  L190409
 * support for storing statistics in a database
 *
 */
package nl.nn.adapterframework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import nl.nn.adapterframework.util.JdbcUtil;
import nl.nn.adapterframework.util.LogUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utility class to populate and reference groups used in statistics.
 * 
 * @author  Gerrit van Brakel
 * @since   4.9.8
 * @version Id
 */
public class StatGroupTable {
	protected Logger log = LogUtil.getLogger(this);
	
	private String selectRootByNameQuery;
	private String selectByNameQuery;
	private String selectByTypeQuery;
	private String selectByNameAndTypeQuery;
	private String selectNextValueQuery;
	private String insertQuery;
	private String insertRootQuery;
	

	public StatGroupTable(String tableName, String keyColumn, String parentKeyColumn, String instanceKeyColumn, String nameColumn, String typeColumn, String sequence) {
		super();
		createQueries(tableName,keyColumn,parentKeyColumn,instanceKeyColumn,nameColumn,typeColumn,sequence);
	}

	private void createQueries(String tableName, String keyColumn, String parentKeyColumn, String instanceKeyColumn, String nameColumn, String typeColumn, String sequence) {
		selectRootByNameQuery="SELECT "+keyColumn+" FROM "+tableName+" WHERE "+parentKeyColumn+" IS NULL AND "+instanceKeyColumn+"=? AND "+nameColumn+"=?";
		selectByNameQuery="SELECT "+keyColumn+" FROM "+tableName+" WHERE "+parentKeyColumn+"=? AND "+instanceKeyColumn+"=? AND "+nameColumn+"=?";
		selectByTypeQuery="SELECT "+keyColumn+" FROM "+tableName+" WHERE "+parentKeyColumn+"=? AND "+instanceKeyColumn+"=? AND "+typeColumn+"=?";
		selectByNameAndTypeQuery="SELECT "+keyColumn+" FROM "+tableName+" WHERE "+parentKeyColumn+"=? AND "+instanceKeyColumn+"=? AND "+nameColumn+"=? AND "+typeColumn+"=?";
		selectNextValueQuery="SELECT "+sequence+".nextval FROM DUAL";
		insertQuery="INSERT INTO "+tableName+"("+keyColumn+","+parentKeyColumn+","+instanceKeyColumn+","+nameColumn+","+typeColumn+") VALUES (?,?,?,?,?)";
		insertRootQuery="INSERT INTO "+tableName+"("+keyColumn+","+instanceKeyColumn+","+nameColumn+","+typeColumn+") VALUES (?,?,?,?)";
	}

	public int findOrInsert(Connection connection, int parentKey, int instanceKey, String name, String type) throws JdbcException {
		int result;
		
		if (parentKey<0) {
			result = JdbcUtil.executeIntQuery(connection,selectRootByNameQuery,instanceKey,name);
		} else if (StringUtils.isNotEmpty(name)) {
			result = JdbcUtil.executeIntQuery(connection,selectByNameAndTypeQuery,parentKey,instanceKey,name,type);
		} else {
			result = JdbcUtil.executeIntQuery(connection,selectByTypeQuery,parentKey,instanceKey,type);

		}
		if (result>=0) {
			return result;
		}
		result = JdbcUtil.executeIntQuery(connection,selectNextValueQuery);
		if (parentKey<0) {
			JdbcUtil.executeStatement(connection,insertRootQuery,instanceKey,name);
		} else {
			JdbcUtil.executeStatement(connection,insertQuery,result,parentKey,instanceKey,name==null?"":name,type);
		}
		return result;
	}
}