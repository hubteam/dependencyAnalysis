package com.wxw.tc.dependencyparsing.evaluate;

import com.wxw.tc.dependencyparsing.graph.IsHaveForestOrLoop;
import com.wxw.tc.dependencyparsing.maxent.DependencyParsingME;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingContext;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingDependencySample;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;
import com.wxw.tc.dependencyparsing.tree.MaxSpinningTree;
import com.wxw.tc.dependencyparsing.tree.DependencyParserTree;
import com.wxw.tc.dependencyparsing.tree.DependencyParsingBestProba;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估句法分析器的性能【此时句法树中的关系有Null】
 * @author 王馨苇
 *
 */
public class DependencyParsingEvaluatorNoNull extends Evaluator<DependencyParsingSample>{

	private int forestcount = 0;
	private DependencyParsingME tagger;
	private DependencyParsingMeasure measure;
	private DependencyParsingCount count;

	/**
	 * 构造
	 * @param tagger 模型和特征的组合结果
	 * @param monitors 评估的监听管理
	 */
	public DependencyParsingEvaluatorNoNull(DependencyParsingME tagger,DependencyParsingEvaluateMonitor... monitor){
		super(monitor);
		this.tagger = tagger;
	}
	/**
	 * 构造
	 * @param tagger 模型和特征的组合结果
	 */
	@SuppressWarnings("unchecked")
	public DependencyParsingEvaluatorNoNull(DependencyParsingME tagger){
		this.tagger = tagger;
	}
	/**
	 * 加载用于计算各种指标的类
	 * @param measure DependencyParsingMeasure对象
	 */
	public void setMeasure(DependencyParsingMeasure measure){
		this.measure = measure;
	}
	/**
	 * 获取评价指标
	 * @return 评价指标
	 */
	public DependencyParsingMeasure getMeasure(){
		return measure;
	}
	/**
	 * 获取森林的个数
	 * @return 森林的个数
	 */
	public int getForestCount(){
		return this.forestcount;
	}
	/**
	 * 设置用于统计语料信息的类
	 * @param count 计算语料信息的类DependencyParsingCount的对象
	 */
	public void setCount(DependencyParsingCount count){
		this.count = count;
	}
	/**
	 * 获取统计的语料的信息
	 * @return 语料的信息
	 */
	public DependencyParsingCount getCount(){
		return this.count;
	}
	
	/**
	 * 调用evaluate方法时候自动激活执行，用于生成预测结果，并和参考的结果进行对比评估指标
	 * @param sample 样本流
	 */
	@Override
	protected DependencyParsingSample processSample(DependencyParsingSample sample) {
		String[] wordsRef = sample.getWords();
		String[] posRef = sample.getPos();
		String[] dependencyRef = sample.getDependency();
		String[] dependencyWordsRef = sample.getDependencyWords();
		
		@SuppressWarnings("unused")
		//最大生成树
		DependencyParsingBestProba proba = tagger.tagNoNull(wordsRef, posRef, sample.getAditionalContext());
		sample = MaxSpinningTree.getMax(proba);
		
		//最大的K棵树
//		DependencyParsingBestProba p = tagger.tagK(3,wordsRef,posRef,sample.getAditionalContext());	
//		DependencyParserTree[] parser = new DependencyParserTree[3];
//		parser = MaxSpinningTree.getMaxFromKres(3, p);
//		sample = parser[0].getTree();
		
		String[] dependencyWordsPre = sample.getDependencyWords();
		String[] dependencyPre = sample.getDependency();
		String[] dependencyIndicesPre = sample.getDependencyIndices();
//		for (int i = 0; i < dependencyIndicesPre.length; i++) {
//			System.out.println(dependencyIndicesPre[i]);
//		}
//		PhraseAnalysisContext context = new PhraseAnalysisContext(new PhraseAnalysisDependencySample(), sample);
//		context.printRes();
		boolean forest = IsHaveForestOrLoop.isHaveForest(wordsRef,dependencyIndicesPre);
		if(forest){
			forestcount++;
		}
		count.count(wordsRef, posRef, dependencyRef);
		measure.updateScore(dependencyWordsRef,dependencyRef,dependencyWordsPre,dependencyPre);
		DependencyParsingSample samplePre = new DependencyParsingSample(wordsRef, posRef, dependencyPre, dependencyWordsPre, dependencyIndicesPre);
		return samplePre;
	}

}
