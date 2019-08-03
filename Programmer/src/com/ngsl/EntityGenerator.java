package com.ngsl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.ngsl.util.NamingUtil;

/**
 * 根据数据库表结构生成相应的JavaBean
 *
 * 思路
 * 1、创建数据库连接
 * 2、获取数据库表、表注释
 * 3、获取数据库表中字段名、字段类型、字段注释
 * 4、构建StringBuffer缓存
 * 5、写入文件
 * 6、关闭连接、输入流等等
 *
 */
public class EntityGenerator {

	    private String packageOutPath = "com.files";// 指定实体生成所在包的路径
	    private String authorName = "yinzw";// 作者名字
	    private String[] tableNames;
	    //需要移除的多余的前缀名
	    private String[] tablePre = { "db_","WEB_XYCN_","AU_","WEB_" };
	    private String[] colsPre = {};
	    //需要添加的后缀
	    private String suffix = "Entity";
	    private String[] colnames; // 列名数组
	    private String[] colTypes; // 列名类型数组
	    private int[] colSizes; // 列名大小数组
	    private boolean f_util = true; // 是否需要导入包java.util.*
	    private boolean f_sql = false; // 是否需要导入包java.sql.*
	 
	    // 数据库连接
	    private static final String URL = "jdbc:oracle:thin:@172.30.4.101:1521:orcl";
	    private static final String NAME = "zx_yf";
	    private static final String PASS = "zx_yf1";
	    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	    private static Connection con = null;
	    private static Statement pStemt = null;
	    private ResultSet rs = null;
	    private ResultSetMetaData rsmd = null;
	 
	    public EntityGenerator() {
	        connect();
	        //getAllTables();
	    }
	 
	    /**
	     * 1、创建数据库连接
	     */
	    private void connect() {
	        try {
	            Class.forName(DRIVER);
	            con = DriverManager.getConnection(URL, NAME, PASS);
	            pStemt =con.createStatement();
	        } catch (ClassNotFoundException | SQLException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    private  void colseConnect() {
	        try {
	            if (con != null) {
	                con.close();
	                con = null;
	            }
	            if (pStemt != null) {
	                pStemt.close();
	                pStemt = null;
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	 
	    }
	 
	    /**
	     * 获取所有表名
	     */
	    private void getAllTables() {
	        String sql = "SELECT TABLE_NAME FROM USER_TABLES";
	        try {
	            rs = pStemt.executeQuery(sql);
	            String s = "";
	            while (rs.next()) {
	                s = s + rs.getString("TABLE_NAME") + ",";
	            }
	            tableNames = s.substring(0,s.length()-1).split(",");
	            for (String tableName : tableNames) {
	                getTableInfo(tableName);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	            //colseConnect();
	        }
	    }
	 
	    /**
	     * 生成某张数据库表对应的javaBean类文件
	     * @param tableName
	     */
	    private void getTableInfo(String tableName) {
	        int size = 0;
	        String sql = "SELECT * FROM " + tableName;
	        try {
	            rs = pStemt.executeQuery(sql);
	            rsmd = rs.getMetaData();
	            size = rsmd.getColumnCount();
	            colnames = new String[size];
	            colTypes = new String[size];
	            colSizes = new int[size];
	            for (int i = 0; i < size; i++) {
	            	//获取所有列名
	                colnames[i] = rsmd.getColumnName(i + 1).toLowerCase();
	                //获取所有列对应的java数据类型
	                colTypes[i] = rsmd.getColumnTypeName(i + 1);
	                //date类型特殊处理
	                if (colTypes[i].equalsIgnoreCase("date")
	                        || colTypes[i].equalsIgnoreCase("timestamp")) {
	                    f_util = true;
	                }
	                if (colTypes[i].equalsIgnoreCase("blob")
	                        || colTypes[i].equalsIgnoreCase("char")) {
	                    f_sql = true;
	                }
	                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
	            }
	            //根据表名生成规范的实体类大驼峰类名
	            String className = getClassName(tableName);
	            //类名添加后缀
	            className = NamingUtil.addSuffix(className, suffix);
	            //获取单张数据库表注释
	            String tableComment = getTableComment(tableName);
	            //获取单张数据库表对应的类属性和方法字符串
	            StringBuffer tempSb = getColsInfo(tableName);
	            //生成JavaBean文件
	            genFile(className, getClassBody(className, tableComment, tempSb));
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    /**
	     * 生成规范的类名
	     *
	     * @param tableName
	     * @return
	     */
	    public String getClassName(String tableName) {
	    	//兼容大小写表名
	    	tableName = tableName.toLowerCase();
	        String result = "";
	        //去掉表名前缀
	        for (String prefix : tablePre) {
	        	if(NamingUtil.hasPrefix(tableName, prefix)) {
	        		tableName = NamingUtil.removePrefix(tableName, prefix);
	        	}
            	//大驼峰命名
	        	result = NamingUtil.toUpperCamelCase(tableName);
	        }
	        return result;
	    }
	 
	    /**
	     * 获取单张数据库表注释
	     *
	     * @param tableName
	     * @return
	     */
	    private String getTableComment(String tableName) {
	        String str = "";
	        String sql = "select * from user_tab_comments where table_name = '"
	                + tableName + "'";
	        try {
	            rs = pStemt.executeQuery(sql);
	            while (rs.next()) {
	                str = rs.getString("comments");
	                str = str == null ? "":str;
	                if (null != str && str.indexOf("\r\n") != -1) {
	                    str = str.replace("\r\n", "");
	                }
	                if (null != str && str.indexOf("\n") != -1) {
	                    str = str.replace("\n", "");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return str;
	    }
	 
	    /**
	     * 获取单张数据库表对应的实体类所有属性和set get方法字符串
	     *
	     * @param tableName
	     */
	    private StringBuffer getColsInfo(String tableName) {
	        StringBuffer temp = new StringBuffer();
	        //属性
	        for (int i = 0; i < colnames.length; i++) {
	        	//属性注释
	        	temp.append("	/**\r\n" + 
	        			"	 * " + getColComment(tableName, colnames[i]) + "\r\n" +
	        			"	 */\r\n");
	        	//属性定义
	            temp.append("\tprivate " + getColsType(colTypes[i]) + " "
	                    + getAttrName(colnames[i]) + ";   \r\n");
	        }
	        //getters and setters
	        for (int i = 0; i < colnames.length; i++) {
	        	String attrName = getAttrName(colnames[i]);
	        	String upperAttrName = NamingUtil.capitalize(attrName);
	        	temp.append("\n\tpublic void set" + upperAttrName + "("
	        			+ getColsType(colTypes[i]) + " " + attrName + "){\r\n");
	        	temp.append("\t\tthis." + attrName + "=" + attrName + ";\r\n");
	        	temp.append("\t}\r\n");
	        	temp.append("\n\tpublic " + getColsType(colTypes[i]) + " get"
	        			+ upperAttrName + "(){\r\n");
	        	temp.append("\t\treturn " + attrName + ";\r\n");
	        	temp.append("\t}\r\n");
	        }
	        return temp;
	    }
	 
	    /**
	     * 获取列类型
	     *
	     * @param sqlType
	     * @return
	     */
	    private String getColsType(String sqlType) {
	        if (sqlType.equalsIgnoreCase("binary_double")) {
	            return "double";
	        } else if (sqlType.equalsIgnoreCase("binary_float")) {
	            return "float";
	        } else if (sqlType.equalsIgnoreCase("blob")) {
	            return "byte[]";
	        } else if (sqlType.equalsIgnoreCase("blob")) {
	            return "byte[]";
	        } else if (sqlType.equalsIgnoreCase("char")
	                || sqlType.equalsIgnoreCase("nvarchar2")
	                || sqlType.equalsIgnoreCase("varchar2")) {
	            return "String";
	        } else if (sqlType.equalsIgnoreCase("date")
	                || sqlType.equalsIgnoreCase("timestamp")
	                || sqlType.equalsIgnoreCase("timestamp with local time zone")
	                || sqlType.equalsIgnoreCase("timestamp with time zone")) {
	            return "Date";
	        } else if (sqlType.equalsIgnoreCase("number")) {
	            return "Long";
	        }
	        return "String";
	    }
	 
	    /**
	     * 获取列名对应的类属性名
	     *
	     * @param str
	     * @return
	     */
	    private String getAttrName(String str) {
	        for (String prefix : colsPre) {
	        	if(NamingUtil.hasPrefix(str, prefix)) {
	        		str = NamingUtil.removePrefix(str, prefix);
	        	}
	        }
	        return NamingUtil.toLowerCamelCase(str);
	    }
	 
	    /**
	     * 获取列注释
	     *
	     * @param tableName 大写数据库表名
	     * @param columnName
	     * @return
	     */
	    private String getColComment(String tableName, String columnName) {
	        String str = "";
	        String sql = "select comments from USER_COL_COMMENTS where table_name= '"
	                + tableName + "' and column_name= '" + columnName.toUpperCase() + "'";
	        try {
	            rs = pStemt.executeQuery(sql);
	            while (rs.next()) {
	                str = rs.getString("comments");
	                if (null != str && str.indexOf("\r\n") != -1) {
	                    str = str.replace("\r\n", "");
	                }
	                if (null != str && str.indexOf("\n") != -1) {
	                    str = str.replace("\n", "");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return str;
	    }
	 
	    /**
	     * 构建StringBuffer缓存
	     *
	     * @param className
	     * @param tableComment
	     * @param colSb
	     * @return
	     */
		private StringBuffer getClassBody(String className, String tableComment,
	            StringBuffer colSb) {
	        StringBuffer sb = new StringBuffer();
	        sb.append("package " + this.packageOutPath + ";\r\n");
	        sb.append("import java.io.Serializable;\r\n");
	        // 判断是否导入工具包
	        if (f_util) {
	            sb.append("import java.util.Date;\r\n");
	        }
	        if (f_sql) {
	            sb.append("import java.sql.*;\r\n");
	        }
	        sb.append("\r\n");
	        // 注释部分
	        sb.append("/**\r\n");
	        sb.append(" * " + className + " 实体类              " + tableComment + "\r\n");
	        sb.append(" * " + new Date() + "\r\n");
	        sb.append(" * @author " + this.authorName + "\r\n");
	        sb.append(" */ \r\n");
	        // 实体部分
	        sb.append("\r\n\r\npublic class " + className + " implements Serializable {\r\n");
	        sb.append("\tprivate static final long serialVersionUID = 1L;\r\n");
	        sb.append(colSb);
	        sb.append("}\r\n");
	        return sb; 
	    }
	 
	    /**
	     * 生成JavaBean文件
	     *
	     * @param content
	     */
	    private void genFile(String fileName, StringBuffer content) {
	    	String outputPath = "";
	    	try {
	            File directory = new File("");
	            //类文件输出路径
	            outputPath = directory.getAbsolutePath() + "/src/"
	                    + this.packageOutPath.replace(".", "/") + "/" + fileName
	                    + ".java";
	            FileWriter fw = new FileWriter(outputPath);
	            PrintWriter pw = new PrintWriter(fw);
	            pw.println(content);
	            pw.flush();
	            pw.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println("生成文件：" + outputPath);
	    }
	 
	    /**
	     * 出口 TODO
	     *
	     * @param args
	     */
	    public static void main(String[] args) {
	    	EntityGenerator eg =  new EntityGenerator(); 
	    	eg.getTableInfo("WEB_YQFX_IF_DATA_DETAIL");
	    	
	    	eg.colseConnect();
	    	//eg.genFile("DB_USER", eg.getSb("DB_USER", "", eg.getColsInfo("DB_USER")));
	    }
}
