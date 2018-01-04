package com.wxw.tc.dependencyparsing.maxent;

import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;
import com.wxw.tc.dependencyparsing.tree.DependencyParserTree;

/**
 * 句法分析器接口
 * @author 王馨苇
 *
 */
public interface DependencyParsing {

	/**
	 * 解析语句得到依存分析的结果
	 * @param sentence 分词后并进行词性标注后的句子
	 * @return 解析之后的样本的样式
	 */
	public DependencyParserTree dependencyparsing(String sentence);
	
	/**
	 * 解析语句得到依存分析的结果
	 * @param words 分词之后的词语
	 * @param poses 词性标记
	 * @return 依存分析之后的结果
	 */
	public DependencyParserTree dependencyparsing(String[] words,String[] poses);
	
	/**
	 * 解析语句得到依存分析的结果
	 * @param wordsandposes 分词+词性标记的词语组成的数组
	 * @return 依存分析之后的结果
	 */
	public DependencyParserTree dependencyparsing(String[] wordsandposes);
	
	/**
	 * 解析语句得到依存分析的结果
	 * @param sentence 分词后并进行词性标注后的句子
	 * @return 解析之后的样本的样式
	 */
	public DependencyParserTree[] dependencyparsing(int k,String sentence);
	
	/**
	 * 解析语句得到依存分析的结果
	 * @param words 分词之后的词语
	 * @param poses 词性标记
	 * @return 依存分析之后的结果
	 */
	public DependencyParserTree[] dependencyparsing(int k,String[] words,String[] poses);
	
	/**
	 * 解析语句得到依存分析的结果
	 * @param wordsandposes 分词+词性标记的词语组成的数组
	 * @return 依存分析之后的结果
	 */
	public DependencyParserTree[] dependencyparsing(int k,String[] wordsandposes);
}
