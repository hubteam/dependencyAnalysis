package com.wxw.tc.dependencyparsing.samplestream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.InputStreamFactory;

/**
 * 获取输入文件流的工厂类
 * @author 王馨苇
 *
 */
public class FileInputStreamFactory implements InputStreamFactory{

	private File file;
	
	/**
	 * 获取样本流
	 * @return 样本流
	 */
	public InputStream createInputStream() throws IOException {
		// TODO Auto-generated method stub
		return new FileInputStream(file);
	}
	
	/**
	 * 构造
	 * @param file 文件
	 * @throws FileNotFoundException 文件不存在异常
	 */
	public FileInputStreamFactory(File file) throws FileNotFoundException{
		if(!file.exists()){
			throw new FileNotFoundException("File '" + file + "' cannot be found");
		}
		this.file = file;
	}

}
