package com.wxw.tc.dependencyparsing.samplestream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
/**
 * 实现ObjectStream，完成其中的read操作，每次读取两个空行之间的内容
 * ObjectStream<String>接口ObjectStream<T>
 * @author 王馨苇
 *
 */
public class PlainTextBySpaceLineStream implements ObjectStream<String>{

	private final FileChannel channel;//缓冲道
	private final String encoding;
	private InputStreamFactory inputStreamFactory;
	private BufferedReader in;
	
	/**
	 * 构造
	 * @param inputStreamFactory 输入文件样本流
	 * @param charsetName 编码
	 * @throws UnsupportedOperationException 异常
	 * @throws IOException 异常
	 */
	public PlainTextBySpaceLineStream(InputStreamFactory inputStreamFactory, String charsetName) throws UnsupportedOperationException, IOException{
		this(inputStreamFactory,Charset.forName(charsetName));
	}
	
	/**
	 * 构造
	 * @param inputStreamFactory 输入文件样本流
	 * @param charsetName 编码
	 * @throws UnsupportedOperationException 异常
	 * @throws IOException 异常
	 */
	public PlainTextBySpaceLineStream(InputStreamFactory inputStreamFactory, Charset charsetName) throws UnsupportedOperationException, IOException{
		this.encoding = charsetName.name();
		this.inputStreamFactory  = inputStreamFactory;
		this.channel = null;
		this.reset();
	}
	/**
	 * 关闭文件
	 */
	public void close() throws IOException {
		// TODO Auto-generated method stub
		if (this.in != null && this.channel == null) {
			this.in.close();
		} else if (this.channel != null) {
			this.channel.close();
		}
	}

	/**
	 * 读取训练语料两个空行直接的内容，将内容拼接后返回
	 * @return 拼接后的结果
	 */
	public String read() throws IOException {
		// 一次读取n行
		String line = "";
		String readContent = "";
		while((line = this.in.readLine())!=null){
			if(line == " " || (line.equals(" ")) || line == "" || line.equals("")){
				break;		
			}else{
				readContent += (line + "\n");
			}
		}
		return readContent;
	}

	/**
	 * 重置读取的位置
	 */
	public void reset() throws IOException, UnsupportedOperationException {
		
		if (this.inputStreamFactory != null) {
			this.in = new BufferedReader(
					new InputStreamReader(this.inputStreamFactory.createInputStream(), this.encoding));
		}else if (this.channel == null) {
			this.in.reset();
		} else {
			this.channel.position(0L);
			this.in = new BufferedReader(Channels.newReader(this.channel, this.encoding));
		}
	}

}
