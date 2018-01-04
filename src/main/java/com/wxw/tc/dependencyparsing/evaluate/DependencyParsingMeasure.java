package com.wxw.tc.dependencyparsing.evaluate;

/**
 * 句法分析结果的评估
 * @author 王馨苇
 *
 */
public class DependencyParsingMeasure {

	//所有词的个数，也就是句子的长度
	private double countAllWords = 0;
	//依存关系和正确支配的词都正确
	private double countWordsAndDep = 0;
	//正确支配的词
	private double countWords = 0;
	//非根正确支配词
	private double countWordsAndDepNotRoot = 0;
	//非根总节点
	private double countAllWordsNotRoot = 0;
	//句子
	private double countSentence = 0;
	//正确根节点
	private double countWordsAndDepRoot = 0;
	//整个依存关系正确
	private double countAllDependency = 0;
	private double countAllWordsAndDep = 0;
	
	/**
	 * 输出打印的格式
	 */
	@Override
	public String toString() {
		return "UAS:"+getUAS()+"\n"
				+"LAS:"+getLAS()+"\n"
				+"DA:"+getDA()+"\n"
				+"RA:"+getRA()+"\n"
				+"CM:"+getCM()+"\n"
				+"CM':"+getCMS()+"\n";
	}

	/**
	 * 统计正确的依赖词数，和正确的依赖词和依赖关系数
	 * @param dependencyWordsRef 参考依存词
	 * @param dependencyRef 参考依存关系
	 * @param dependencyWordsPre 预测依存词
	 * @param dependencyPre 预测依存关系
	 */
	public void updateScore(String[] dependencyWordsRef, String[] dependencyRef, String[] dependencyWordsPre,
			String[] dependencyPre) {
		//记录是否完全匹配依存结构
		int dependencyCount = 0;
		int wordsAndDepCount = 0;
		countSentence++;
		countAllWords += dependencyPre.length;
		for (int i = 0; i < dependencyPre.length; i++) {
			if(dependencyPre[i].compareTo("ROOT") != 0
					|| dependencyPre[i].compareTo("核心成分") != 0){
				countAllWordsNotRoot++;
			}
		}
		for (int i = 0; i < dependencyPre.length; i++) {
			//无标记匹配
			if(dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0){
				countWords++;
				dependencyCount++;	
			}
				
			if((dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
					&& (dependencyPre[i].compareTo(dependencyRef[i]) == 0)){
				countWordsAndDep++;
				wordsAndDepCount++;
			}
			
			if((dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
					&& (dependencyPre[i].compareTo(dependencyRef[i]) == 0)
					&& (dependencyPre[i].compareTo("ROOT") != 0
					|| dependencyPre[i].compareTo("核心成分") != 0)){
				countWordsAndDepNotRoot++;
			}
			
			if((dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
					&& (dependencyPre[i].compareTo(dependencyRef[i]) == 0)
					&& ((dependencyPre[i].compareTo("ROOT") == 0)
					|| dependencyPre[i].compareTo("核心成分") == 0)){
				countWordsAndDepRoot++;
			}
			
		}	
		if(dependencyCount == dependencyWordsPre.length){
			countAllDependency++;
		}
		if(wordsAndDepCount == dependencyPre.length){
			countAllWordsAndDep++;
		}
		
	}

	/**
	 * 无标记依存正确率
	 * @return
	 */
	public double getUAS(){
		return countWords/countAllWords;
	}
	/**
	 * 带标记正确依存率
	 * @return
	 */
	public double getLAS(){
		return countWordsAndDep/countAllWords;
	}
	/**
	 * 依存正确率
	 * @return
	 */
	public double getDA(){
		return countWordsAndDepNotRoot/countAllWordsNotRoot;
	}
	
	/**
	 * 根正确率
	 * @return
	 */
	public double getRA(){
		return countWordsAndDepRoot/countSentence;
	}
	
	/**
	 * 完全匹配率
	 * @return
	 */
	public double getCM(){
		return countAllDependency/countSentence;
	}
	
	/**
	 * 改进的CM指标
	 * @return
	 */
	public double getCMS(){
		return countAllWordsAndDep/countSentence;
	}
	
}
