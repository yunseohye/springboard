package com.utill;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;


public class FileManager {
	
	//파일 업로드
	public static String doFileUpload(File upload,String originalFileName, String path) throws Exception {
		
		String newFileName;
		
		if(upload==null) {
			return null;
		}
		
		//클라이언트가 업로드한 파일 이름
		
		if(originalFileName.equals("")) {
			return null;
		}
		
		//확장자
		//abc.txt
		String fileExt = 
				originalFileName.substring(originalFileName.lastIndexOf("."));
		
		if(fileExt==null || fileExt.equals("")) {
			return null;
		}
		
		//서버에 저장할 새로운 파일명 생성
		newFileName = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS",
				Calendar.getInstance());
		
		newFileName += System.nanoTime(); //10의 -9
		
		newFileName += fileExt;
		
		//업로드할 경로 생성
		File f = new File(path);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		String fullFilePath = path + File.separator + newFileName;
		
		//Struts2의 파일 업로드
		FileInputStream fis = new FileInputStream(upload);
		FileOutputStream fos = new FileOutputStream(fullFilePath);
		
		int data = 0;
		byte[] buffer = new byte[1024];
		while((data=fis.read(buffer, 0, 1024))!=-1) {
			fos.write(buffer,0,data);
		}		
		
		fis.close();
		fos.close();
		
		return newFileName;
		
	}
	
	//파일 다운로드
	public static boolean doFileDownload(HttpServletResponse response,
			String saveFileName,String originalFileName,String path) {
		
		try {
			
			String fullPath = path + File.separator + saveFileName;
			
			if(originalFileName==null || originalFileName.equals("")) {
				originalFileName = saveFileName;
			}
			
			//한글파일이름 깨짐 방지
			originalFileName = 
				new String(originalFileName.getBytes("euc-kr"),"ISO-8859-1");
			
			File f = new File(fullPath);
			
			if(!f.exists()) {
				return false;
			}
			
			//파일 다운로드
			response.setContentType("application/octet-stream");
			
			response.setHeader("Content-disposition",
					"attachment;fileName=" + originalFileName);
			
			BufferedInputStream bis = 
					new BufferedInputStream(new FileInputStream(f));
			
			OutputStream out = response.getOutputStream();
			
			int data;
			byte[] bytes = new byte[4096];
			while((data=bis.read(bytes, 0, 4096))!=-1) {
				out.write(bytes, 0, data);
			}
			
			out.flush();
			out.close();
			bis.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		
		return true;
		
	}
	
	
	//파일삭제
	public static void doFileDelete(String fileName,String path) {
		
		try {		
		
			String filePath = path + File.separator + fileName;
			
			File f = new File(filePath);
			
			if(f.exists()) {
				f.delete();	//물리적 파일 삭제
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	

}
