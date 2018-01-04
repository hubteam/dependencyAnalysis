package com.wxw.tc.dependencyparsing.parsesample;

import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

/**
 * 策略模式上下文类
 * @author 王馨苇
 *
 */
public class DependencyParsingContext {

	private DependencyParsingSampleParser parser ;
	private DependencyParsingSample pas;
	private String sentence;
	/**
	 * 构造
	 * @param parser 样本解析器
	 * @param sentence 要解析的语句
	 */
	public DependencyParsingContext(DependencyParsingSampleParser parser,String sentence){
		this.parser = parser;
		this.sentence = sentence;
	}
	public DependencyParsingContext(DependencyParsingSampleParser parser,DependencyParsingSample pas){
		this.parser = parser;
		this.pas = pas;
	}
	/**
	 * 解析预料
	 * @return 解析后的样本流
	 */
	public DependencyParsingSample sampleParse(){
		return this.parser.parseIn(sentence);
	}
	
	/**
	 * 解析测试语料
	 */
	public DependencyParsingSample testParse(){
		return this.parser.parseTest(sentence);
	}
	
	/**
	 * 打印结果
	 */
	public void printRes(){
		
		this.parser.printPhraseAnalysisRes(pas);
	}
}
