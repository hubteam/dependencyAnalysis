package com.wxw.tc.dependencyparsing.evaluate;

import java.io.OutputStream;
import java.io.PrintStream;

import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;

/**
 * 打印错误信息类 
 * @author 王馨苇
 *
 */
public class DependencyParsingErrorPrinter extends DependencyParsingEvaluateMonitor{
	private PrintStream errOut;
	private int countErrorRes = 0;
	
	public DependencyParsingErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}

	/**
	 * 样本和预测的不一样的时候进行输出
	 * @param reference 参考的样本
	 * @param predict 预测的结果
	 */
	@Override
	public void missclassified(DependencyParsingSample reference, DependencyParsingSample predict) {
		 errOut.println("样本的结果：");
		 errOut.println(reference.toSample());	 
		 errOut.println("预测的结果：");
		 errOut.println(predict.toSample());
		 errOut.println("错误的总数：");
		 errOut.println(countErrorRes++);
	}
}
