package com.wxw.tc.dependencyparsing.evaluate;

import java.io.IOException;

import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGenerator;
import com.wxw.tc.dependencyparsing.maxent.DependencyParsingME;
import com.wxw.tc.dependencyparsing.maxent.DependencyParsingModel;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 交叉验证
 * @author 王馨苇
 *
 */
public class DependencyParsingCrossValidator {
	
	private final String languageCode;
	private final TrainingParameters params;
	private DependencyParsingEvaluateMonitor[] listeners;
	private DependencyParsingMeasure measure = new DependencyParsingMeasure();
	private DependencyParsingCount count = new DependencyParsingCount();
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public DependencyParsingCrossValidator(String languageCode,TrainingParameters params,
			DependencyParsingEvaluateMonitor... listeners){
		this.languageCode = languageCode;
		this.params = params;
		this.listeners = listeners;
	}
	
	/**
	 * 交叉验证十折评估
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluate(ObjectStream<DependencyParsingSample> sample, int nFolds,
			DependencyParsingContextGenerator contextGenerator) throws IOException{
		CrossValidationPartitioner<DependencyParsingSample> partitioner = new CrossValidationPartitioner<DependencyParsingSample>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<DependencyParsingSample> trainingSampleStream = partitioner.next();
	        DependencyParsingModel model = DependencyParsingME.train(languageCode, trainingSampleStream, params, contextGenerator);

	        DependencyParsingEvaluatorNoNull evaluator = new DependencyParsingEvaluatorNoNull(new DependencyParsingME(model, contextGenerator), listeners);
	        evaluator.setCount(count);
	        evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
		System.out.println(count);
		System.out.println(measure);
	}
}
