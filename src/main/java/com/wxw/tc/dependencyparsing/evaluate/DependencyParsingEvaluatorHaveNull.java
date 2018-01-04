package com.wxw.tc.dependencyparsing.evaluate;

import com.wxw.tc.dependencyparsing.graph.IsHaveForestOrLoop;
import com.wxw.tc.dependencyparsing.maxent.DependencyParsingME;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingContext;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingDependencySample;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;
import com.wxw.tc.dependencyparsing.tree.MaxSpinningTree;
import com.wxw.tc.dependencyparsing.tree.DependencyParsingBestProba;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估句法分析器的性能【此时句法树中的关系无Null】
 * @author 王馨苇
 *
 */
public class DependencyParsingEvaluatorHaveNull extends Evaluator<DependencyParsingSample>{

	private int forestcount = 0;
	private DependencyParsingME tagger;
	private DependencyParsingMeasure measure;
	private DependencyParsingCount count;

	/**
	 * 构造
	 * @param tagger 模型和特征的组合结果
	 * @param monitors 评估的监听管理
	 */
	public DependencyParsingEvaluatorHaveNull(DependencyParsingME tagger,DependencyParsingEvaluateMonitor... monitors){
		super(monitors);
		this.tagger = tagger;
	}
	/**
	 * 构造
	 * @param tagger 模型和特征的组合结果
	 */
	@SuppressWarnings("unchecked")
	public DependencyParsingEvaluatorHaveNull(DependencyParsingME tagger){
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
		DependencyParsingBestProba proba = tagger.tagNull(wordsRef, posRef, sample.getAditionalContext());
		sample = MaxSpinningTree.getMaxTree(proba);
		String[] dependencyWordsPre = sample.getDependencyWords();
		String[] dependencyPre = sample.getDependency();
		String[] dependencyIndicesPre = sample.getDependencyIndices();
//		PhraseAnalysisContext context = new PhraseAnalysisContext(new PhraseAnalysisDependencySample(), sample);
//		context.printRes();
			
		boolean forest = IsHaveForestOrLoop.isHaveForest(wordsRef,dependencyIndicesPre);
		if(forest){
			forestcount++;
		}
//		System.out.println("森林的数量是："+forest);
		measure.updateScore(dependencyWordsRef,dependencyRef,dependencyWordsPre,dependencyPre);
		DependencyParsingSample samplePre = new DependencyParsingSample(wordsRef, posRef, dependencyPre, dependencyWordsPre, dependencyIndicesPre);
		
		return samplePre;
	}

}
