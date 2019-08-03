package com.ngsl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LazyBoy {
	private static String parentDir = "com/neuqsoft/zx/reward";
	private static String moduleName = "lhjc";
	private static List<String> layerNames;
	private static String author = "yinzw@neuqsoft.com";
	private static String apiUrl = "V0/lhjc";
	private static String tarDir = "C:/Users/Administrator/Desktop";

	public static String getParentDir() {
		return parentDir;
	}
	public static void setParentDir(String parentDir) {
		LazyBoy.parentDir = parentDir;
	}

	public static String getModuleName() {
		return moduleName;
	}

	public static void setModuleName(String moduleName) {
		LazyBoy.moduleName = moduleName;
	}
	public static List<String> getLayerNames() {
		return layerNames;
	}
	public static void setLayerName(List<String> layerNames) {
		LazyBoy.layerNames = layerNames;
	}
	public static String getAuthor() {
		return author;
	}
	public static void setAuthor(String author) {
		LazyBoy.author = author;
	}
	public static String getApiUrl() {
		return apiUrl;
	}

	public static void setApiUrl(String apiUrl) {
		LazyBoy.apiUrl = apiUrl;
	}
	public static String getTarDir() {
		return tarDir;
	}

	public static void setTarDir(String tarDir) {
		LazyBoy.tarDir = tarDir;
	}

	//生成文件
	public static void generate() {
		for(String layer :layerNames) {
			StringBuilder path = new StringBuilder();
			path.append(tarDir + "/");
			path.append(parentDir + "/" + moduleName + "/" + layer);
			
			path.append("/");
			File dir = new File(path.toString());
			dir.mkdirs();
			
			String fileName = (moduleName.charAt(0) + "").toUpperCase() + moduleName.substring(1);
			fileName += (layer.charAt(0) + "").toUpperCase() + layer.substring(1);
			String fileSuffix = fileName;
			fileName += ".java";
			path.append(fileName);
			
			//make file
			File file = new File(path.toString());
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("生成成功！");
			}
			
			//write sth
			OutputStream out = null;
			//pack name
			String packName = path.toString();
			try {
				out = new FileOutputStream(path.toString());
				//package
				String pac = "package " + parentDir.replaceAll("/", ".") + "." + moduleName + "." + layer +";\r\n";
				//time
				Calendar calendar= Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				//类注释
				StringBuilder classComment = new StringBuilder();
				classComment.append("/**\r\n" + 
						" * \r\n" + 
						" * <p>Class       : " + parentDir.replaceAll("/", ".") + "." + moduleName + "." + layer + "." + fileSuffix +"\r\n" + 
						" * <p>Description: "+ layer +"\r\n" + 
						" *\r\n" + 
						" * @author  " + author + "\r\n" + 
						" * @version 1.0.0\r\n" + 
						" *<p>\r\n" + 
						" *--------------------------------------------------------------<br>\r\n" + 
						" * 修改履历：<br>\r\n" + 
						" *        <li> " + sdf.format(calendar.getTime()) + "，" + author + "，创建文件；<br>\r\n" + 
						" *--------------------------------------------------------------<br>\r\n" + 
						" *</p>\r\n" + 
						" */\r\n");
				
				//class body
				StringBuilder classBody = new StringBuilder();
				
				//annotations
				String ctrAno = "@RestController\r\n" + 
						"@RequestMapping(\"" + apiUrl +"\")\r\n";
				String serAno = "@Service\r\n" + 
						"@Transactional\r\n";
				String repAno = "@Repository\r\n";
				
				String entAno = "@Entity\r\n" + 
						"@Table(name=\"\")\r\n";
				
				if(layer.equals("controller")) {
					classBody.append(ctrAno);
					
				}else if(layer.equals("service")) {
					classBody.append(serAno);
					
				}else if(layer.equals("repository")) {
					classBody.append(repAno);
					
				}else if(layer.equals("entity")) {
					classBody.append(entAno);
				}
				
				
				
				classBody.append(
						"public " + (layer.equals("repository") ? " interface " : " class ")  + " " + fileSuffix);
				
				if(layer.equals("entity")) {
					classBody.append(" implements Serializable");
				
				}else if(layer.equals("repository")) {
					classBody.append("  extends JpaRepository<" + (moduleName.charAt(0)+ "").toUpperCase() + moduleName.substring(1) + "Entity, String>");
					
				}
				
				
				
				classBody.append(" {\r\n" + 
						"\r\n");  
				
				if(layer.equals("controller")) {
					classBody.append("@Autowired\r\n");
					classBody.append("private " + (moduleName.charAt(0)+ "").toUpperCase() + moduleName.substring(1) + "Service service;\r\n");
					
				}else if(layer.equals("service")) {
					classBody.append("@Autowired\r\n");
					classBody.append("private " + (moduleName.charAt(0)+ "").toUpperCase() + moduleName.substring(1) + "Repository repository;\r\n");
					
				}else if(layer.equals("repository")) {
					
				}else if(layer.equals("entity")) {
					classBody.append("private static final long serialVersionUID = 1L;\r\n");
					
				}
				
				classBody.append("}");
				
				FileWriter writer = new FileWriter(path.toString(), true);
				
				
				writer.write(pac);
				writer.write(classComment.toString());
				writer.write(classBody.toString());
				writer.close();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
			
		}
		
		
	}


	public static void main(String[] args) {
		{
			LazyBoy.layerNames = new ArrayList<>();	
			LazyBoy.layerNames.add("controller");
			LazyBoy.layerNames.add("service");
			LazyBoy.layerNames.add("entity");
			LazyBoy.layerNames.add("repository");
		}
		
		
		
		LazyBoy.generate();
	}

}
