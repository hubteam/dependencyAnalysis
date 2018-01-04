package com.wxw.tc.dependencyparsing.evaluate;

import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

import opennlp.tools.util.eval.EvaluationMonitor;

/**
 * 评估
 * @author 王馨苇
 *
 */
public class DependencyParsingEvaluateMonitor implements EvaluationMonitor<DependencyParsingSample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void correctlyClassified(DependencyParsingSample arg0, DependencyParsingSample arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 预测错误的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void missclassified(DependencyParsingSample arg0, DependencyParsingSample arg1) {
		// TODO Auto-generated method stub
		
	}

}
