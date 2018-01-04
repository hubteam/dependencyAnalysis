package com.wxw.tc.dependencyparsing.parsesample;

import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

public interface DependencyParsingSampleParser{

	/**
	 * 从训练语料中读取的要解析的句子
	 * @param sentence 要解析的语句
	 * @return 解析的结果
	 */
	public DependencyParsingSample parseIn(String sentence);
	
	/**
	 * 从测试语料中读取的一行记录
	 * @param sentenceTest 要解析的语句
	 * @return 解析的结果
	 */
	public DependencyParsingSample parseTest(String sentenceTest);
	
	/**
	 * 打印
	 * @param pas 样本
	 */
	public void printPhraseAnalysisRes(DependencyParsingSample pas);

}
