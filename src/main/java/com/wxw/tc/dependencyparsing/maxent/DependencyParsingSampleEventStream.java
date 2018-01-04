package com.wxw.tc.dependencyparsing.maxent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGenerator;
import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGeneratorConf;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * 训练模型所需要的事件流
 * @author 王馨苇
 *
 */
public class DependencyParsingSampleEventStream extends AbstractEventStream<DependencyParsingSample> {

	//上下文产生器
	private DependencyParsingContextGenerator pcg;
	
	/**
	 * 构造
	 * @param samples 样本流
	 * @param pcg 特征
	 */
	public DependencyParsingSampleEventStream(ObjectStream<DependencyParsingSample> samples,DependencyParsingContextGenerator pcg) {
		
		super(samples);
		this.pcg = pcg;
	}

	/**
	 * 根据读取的文件流创建事件
	 * @param 解析后的train文本流
	 * @see opennlp.tools.util.AbstractEventStream#createEvents(java.lang.Object)
	 */
	@Override
	protected Iterator<Event> createEvents(DependencyParsingSample sample) {
		String[] words = sample.getWords();
		String[] pos = sample.getPos();
		String[] dependency = sample.getDependency();
		String[] dependencyWords = sample.getDependencyWords();
		String[] dependencyIndices = sample.getDependencyIndices();
		String[][] ac = sample.getAditionalContext();
		List<Event> events = generateEvents(words, pos, dependency, dependencyWords,dependencyIndices,ac);
        return events.iterator();
		//return null;
	}

	/**
	 * 产生每个词对应的事件
	 * @param words 词语
	 * @param pos 词性
	 * @param dependency 词性标记
	 * @param dependencyWords 对应的词
	 * @param dependencyIndices 对应的下标
	 * @param ac 额外的信息
	 * @return 事件列表
	 */
	private List<Event> generateEvents(String[] words, String[] pos, String[] dependency, String[] dependencyWords,
			String[] dependencyIndices, String[][] ac) {
		 List<Event> events = new ArrayList<Event>(words.length);

		 //一层是i的循环
		 //二层是j的循环
		 //由i和j再加上其标签组成一个事件	        
		 int i = 1,j = 0;
		 int lenLeft,lenRight;
		 if(DependencyParsingContextGeneratorConf.LEFT == -1 && DependencyParsingContextGeneratorConf.RIGHT == -1){
			 lenLeft = -words.length;
			 lenRight = words.length;
		 }else if(DependencyParsingContextGeneratorConf.LEFT == -1 && DependencyParsingContextGeneratorConf.RIGHT != -1){
			 lenLeft = -words.length;
			 lenRight = DependencyParsingContextGeneratorConf.RIGHT;
		 }else if(DependencyParsingContextGeneratorConf.LEFT != -1 && DependencyParsingContextGeneratorConf.RIGHT == -1){
			 lenLeft = DependencyParsingContextGeneratorConf.LEFT;
			 lenRight = words.length;
		 }else{
			 lenLeft = DependencyParsingContextGeneratorConf.LEFT;
			 lenRight = DependencyParsingContextGeneratorConf.RIGHT;
		 }

		 while(i < words.length){
//			 while(j < words.length){
			 while(j - i <= lenRight && j - i >= lenLeft && j < words.length){
				 if(i != j){ 
					 String[] context = pcg.getContext(i, j, words, pos, ac);				
					 if(dependencyWords[i-1].equals(words[j]) || dependencyWords[i-1] == words[j]){
						//i到j的关系：有关系统一设置成dependency
						 events.add(new Event(dependency[i-1], context)); 
					 }else{
						 //i到j的关系：没有关系统一设置成null
						 events.add(new Event("null", context)); 
					 }
				 }
				 j++;
			 }
			 i++;
			 j = 0;
		 }   
		 //改变事件，去掉一部分为null的事件
//		 for (int k = 1; k < words.length; k++) {
//			 String[] context = pcg.getContext(k, Integer.parseInt(dependencyIndices[k-1]), words, pos, ac);
//			 events.add(new Event(dependency[k-1], context));
//		}
		 return events;
		 
	}

	
}
