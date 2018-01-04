package com.wxw.tc.dependencyparsing.parsesample;

import java.util.ArrayList;
import java.util.List;

import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingCount;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

import opennlp.tools.tokenize.WhitespaceTokenizer;


/**
 * 对依存关系语料库的解析
 * @author 王馨苇
 *
 */
public class DependencyParsingDependencySample implements DependencyParsingSampleParser{

	private static DependencyParsingCount count = new DependencyParsingCount();
	
	/**
	 * 解析样本
	 * @param 待解析的语句
	 * @return 解析后的样本流
	 */
	public DependencyParsingSample parseIn(String sentence) {
		String words[] = sentence.split("\\n");
		//词
		List<String> word = new ArrayList<String>();
		//词性
		List<String> pos = new ArrayList<String>();
		//依赖关系
		List<String> dependency = new ArrayList<String>();
		//依赖词语的下标
		List<String> dependencyIndices = new ArrayList<String>();
		//依赖的词语
		List<String> dependencyWords = new ArrayList<String>();
		
		word.add("核心");
		pos.add("root");
		for (int i = 0; i < words.length; i++) {
			String[] temp = words[i].split("\\t");
			word.add(temp[1]);
			pos.add(temp[3]);
			dependencyIndices.add(temp[6]);
			dependency.add(temp[7]);		
		}
		for (int i = 0; i < dependencyIndices.size(); i++) {
			String tempword = word.get(Integer.parseInt(dependencyIndices.get(i)));
			dependencyWords.add(tempword);
		}
//	return null;
		count.count(word.toArray(new String[word.size()]), 
				pos.toArray(new String[pos.size()]), 
				dependency.toArray(new String[dependency.size()]));
		return new DependencyParsingSample(word.toArray(new String[word.size()]), 
				pos.toArray(new String[pos.size()]), 
				dependency.toArray(new String[dependency.size()]), 
				dependencyWords.toArray(new String[dependencyWords.size()]), 
				dependencyIndices.toArray(new String[dependencyIndices.size()]));
	}

	/**
	 * 解析测试语料的样本
	 */
	public DependencyParsingSample parseTest(String sentenceTest) {
		String[] wordsandpoese = WhitespaceTokenizer.INSTANCE.tokenize(sentenceTest);
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();
		words.add("核心");
		poses.add("root");
		//假设词语和词性之间的/分割的
		for (int i = 0; i < wordsandpoese.length; i++) {
			String[] temp = wordsandpoese[i].split("/");
			words.add(temp[0]);
			words.add(temp[1]);
		}
		return new DependencyParsingSample(words,poses);
	}
	
	/**
	 * 统计语料
	 * @return 语料统计的结果
	 */
	public static DependencyParsingCount getCountRes(){
		return count;
	}

	/**
	 * 输出最终对测试文本进行句法分析的结果
	 * @param pas 样本信息
	 */
	public void printPhraseAnalysisRes(DependencyParsingSample pas) {
		String[] words = pas.getWords();
		String[] pos = pas.getPos();
		String[] dependency = pas.getDependency();
		String[] dependencyIndice = pas.getDependencyIndices();
		int length = dependency.length;
		for (int i = 0; i < length; i++) {
			System.out.println((i+1)+"\t"+words[i+1]+"\t"+words[i+1]+"\t"
					+pos[i+1]+"\t"+pos[i+1]+"\t"
					+"_"+"\t"
					+dependencyIndice[i]+"\t"
					+dependency[i]+"\t"
					+"_"+"\t"+"_");
		}
		System.out.println();
		
	}

}
