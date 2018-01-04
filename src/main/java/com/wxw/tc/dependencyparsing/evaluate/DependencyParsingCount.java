package com.wxw.tc.dependencyparsing.evaluate;

import java.util.HashSet;
import java.util.Set;

/**
 * 语料计数类
 * @author 王馨苇
 *
 */
public class DependencyParsingCount {

	//句子计数
	private int sentenceCount = 0;
	//词计数
	private int wordCount = 0;
	//依存关系对计数
	private int dependencyPairCount = 0;
	//对长句子的统计
	private int longSentence = 0;
		
	Set<String> dependency = new HashSet<String>();
	Set<String> pos = new HashSet<String>();
	
	/**
	 * 进行一些信息的统计
	 * @param wordRef 词
	 * @param posRef 词性
	 * @param dependencyRef 依存关系
	 */
	public void count(String[] wordRef,String[] posRef,String[] dependencyRef){

		//句子个数计数
		sentenceCount++;
		if(wordRef.length > 27){
			longSentence++;
		}
		//依存关系计数
		for (int i = 0; i < dependencyRef.length; i++) {
			dependencyPairCount++;
			dependency.add(dependencyRef[i]);
			
		}
		//词性标记计数
		for (int i = 0; i < posRef.length; i++) {
			pos.add(posRef[i]);
		}
		//词语个数计数
		for (int i = 1; i < wordRef.length; i++) {
			wordCount++;
		}
		
	}
	
	/**
	 * 
	 * @return 词的总数
	 */
	public int getWordCount(){
		return wordCount;
	}
	
	/**
	 * 
	 * @return 句子总数
	 */
	public int getSentenceCount(){
		return sentenceCount;
	}
	
	/**
	 * 
	 * @return 依存关系总数
	 */
	public int getDependencyCount(){
		return dependency.size();
	}

	/**
	 * 
	 * @return 依存关系对总数
	 */
	public int getDependencyPairCount(){
		return dependencyPairCount;
	}
	
	/**
	 * 
	 * @return 词性总数
	 */
	public int getPosCount(){
		return pos.size();
	}
	
	/**
	 * 
	 * @return 长句子总数
	 */
	public int getLongSentence(){
		return longSentence;
	}
	
	/**
	 * 重写toString()打印信息
	 */
	@Override
	public String toString() {
		return "句子的个数："+getSentenceCount()+"\n"+
	           "长句子的个数："+getLongSentence()+"\n"+
				"词语的个数："+getWordCount()+"\n"+
				"依存关系对的个数："+getDependencyPairCount()+"\n"+
				"依存关系的个数："+getDependencyCount()+"\n"+
				"词性标记的个数："+getPosCount();
	}
}
