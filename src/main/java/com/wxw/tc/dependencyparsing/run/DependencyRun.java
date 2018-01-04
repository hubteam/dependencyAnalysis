package com.wxw.tc.dependencyparsing.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingCount;
import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingErrorPrinter;
import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingEvaluatorNoNull;
import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingMeasure;
import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingCrossValidator;
import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGenerator;
import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGeneratorConf;
import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGeneratorConfExtend;
import com.wxw.tc.dependencyparsing.maxent.DependencyParsingME;
import com.wxw.tc.dependencyparsing.maxent.DependencyParsingModel;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingDependencySample;
import com.wxw.tc.dependencyparsing.samplestream.FileInputStreamFactory;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSampleStream;
import com.wxw.tc.dependencyparsing.samplestream.PlainTextBySpaceLineStream;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 运行
 * @author 王馨苇
 *
 */
public class DependencyRun {

	private static String flag = "train";
	//静态内部类
	public static class Corpus{
		//文件名和编码
		public String name;
		public String encoding;
		public String trainFile;
		public String testFile;
		public String modelbinaryFile;
		public String modeltxtFile;
		public String errorNullFile;
		public String errorNoNullFile;
	}
	//trainHIT语料比较大
	private static String[] corpusName = {"THU"};
	
	/**
	 * 主函数
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void main(String[] args) throws IOException {
		String cmd = args[0];
		if(cmd.equals("-train")){
			flag = "train";
			runFeature();
		}else if(cmd.equals("-model")){
			flag = "model";
			runFeature();
		}else if(cmd.equals("-evaluate")){
			flag = "evaluate";
			runFeature();
		}else if(cmd.equals("-cross")){
			String corpus = args[1];
			crossValidation(corpus);
		}
	}

	/**
	 * 交叉验证
	 * @param corpus 语料名称
	 * @throws IOException IO异常
	 */
	private static void crossValidation(String corpusName) throws IOException {
		//加载语料文件
        Properties config = new Properties();
        InputStream configStream = DependencyRun.class.getClassLoader().getResourceAsStream("com/wxw/tc/phraseanalysis/run/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);
        //定位到某一语料
        Corpus corpus = getCorpus(corpora, corpusName);
        DependencyParsingContextGenerator contextGen = getContextGenerator(config);
        ObjectStream<String> linesStream = new PlainTextBySpaceLineStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);
        ObjectStream<DependencyParsingSample> sampleStream = new DependencyParsingSampleStream(linesStream);
      //配置参数
      	TrainingParameters params = TrainingParameters.defaultParams();
      	params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
      	//交叉验证
      	DependencyParsingCrossValidator crossValidator = new DependencyParsingCrossValidator("ZH", params);
      	System.out.println(contextGen);
        crossValidator.evaluate(sampleStream, 10, contextGen);
	}

	/**
	 * 根据语料名称获取某个语料
	 * @param corpora 语料类
	 * @param corpusName 语料名称
	 * @return
	 */
	private static Corpus getCorpus(Corpus[] corpora, String corpusName) {
		for (Corpus c : corpora) {
            if (c.name.equalsIgnoreCase(corpusName)) {
                return c;
            }
        }
        return null;
	}

	/**
	 * 获取特征
	 * @throws IOException IO异常
	 */
	private static void runFeature() throws IOException {
		//配置参数
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(3));
	
		//加载语料文件
        Properties config = new Properties();
        InputStream configStream = DependencyRun.class.getClassLoader().getResourceAsStream("com/wxw/tc/phraseanalysis/run/corpus.properties");
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);//获取语料

        DependencyParsingContextGenerator contextGen = getContextGenerator(config);

        runFeatureOnCorporaByFlag(contextGen, corpora, params);
	}

	/**
	 * 根据flag标记判断进行哪种操作
	 * @param contextGen 特征
	 * @param corpora 语料信息
	 * @param params 训练参数
	 * @throws UnsupportedOperationException 异常
	 * @throws IOException 异常
	 */
	private static void runFeatureOnCorporaByFlag(DependencyParsingContextGenerator contextGen, Corpus[] corpora,
			TrainingParameters params) throws UnsupportedOperationException, IOException {
		if(flag == "train" || flag.equals("train")){
			for (int i = 0; i < corpora.length; i++) {
				trainOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "model" || flag.equals("model")){
			for (int i = 0; i < corpora.length; i++) {
				modelOutOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "evaluate" || flag.equals("evaluate")){
			for (int i = 0; i < corpora.length; i++) {
				evaluateOnCorpus(contextGen,corpora[i],params);
			}
		}
		
	}

	/**
	 * 读取模型，评估模型
	 * @param contextGen 上下文
	 * @param corpus 语料
	 * @param params 参数
	 * @throws UnsupportedOperationException 异常
	 * @throws FileNotFoundException 异常
	 * @throws IOException 异常
	 */	
	private static void evaluateOnCorpus(DependencyParsingContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        //训练模型
        DependencyParsingModel model = DependencyParsingME.readModel(new File(corpus.modeltxtFile), params, contextGen, corpus.encoding);
        DependencyParsingME tagger = new DependencyParsingME(model,contextGen);
        
       // 最大概率中包含null的情况
//        PhraseAnalysisMeasure measureHaveNull = new PhraseAnalysisMeasure();
//        PhraseAnalysisEvaluatorHaveNull evaluatorHaveNull = null;
//        PhraseAnalysisErrorPrinter printerHaveNull = null;
//        if(corpus.errorNullFile != null){
//        	System.out.println("Print error to file " + corpus.errorNullFile);
//        	printerHaveNull = new PhraseAnalysisErrorPrinter(new FileOutputStream(corpus.errorNullFile));
//        	evaluatorHaveNull = new PhraseAnalysisEvaluatorHaveNull(tagger, printerHaveNull);
//        }else{
//        	evaluatorHaveNull = new PhraseAnalysisEvaluatorHaveNull(tagger);
//        }
//        evaluatorHaveNull.setMeasure(measureHaveNull);
//        ObjectStream<String> linesStream = new PlainTextBySpaceLineStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);
//        ObjectStream<PhraseAnalysisSample> sampleStream = new PhraseAnalysisSampleStream(linesStream);
//        evaluatorHaveNull.evaluate(sampleStream);
//        PhraseAnalysisMeasure measureResHaveNull = evaluatorHaveNull.getMeasure();
//        System.out.println("--------包含null时的结果--------");
//        System.out.println(measureResHaveNull);
//        System.out.println("森林个数："+evaluatorHaveNull.getCount());
        
        //最大概率中不包含null的情况   
        DependencyParsingMeasure measureNoNull = new DependencyParsingMeasure();
        DependencyParsingCount count = new DependencyParsingCount();
        DependencyParsingEvaluatorNoNull evaluatorNoNull = null;
        DependencyParsingErrorPrinter printerNoNull = null;
        if(corpus.errorNoNullFile != null){
        	System.out.println("Print error to file " + corpus.errorNoNullFile);
        	printerNoNull = new DependencyParsingErrorPrinter(new FileOutputStream(corpus.errorNoNullFile));    	
        	evaluatorNoNull = new DependencyParsingEvaluatorNoNull(tagger,printerNoNull);
        }else{
        	evaluatorNoNull = new DependencyParsingEvaluatorNoNull(tagger);
        }
        evaluatorNoNull.setMeasure(measureNoNull);
        evaluatorNoNull.setCount(count);
        ObjectStream<String> linesStreamNoNull = new PlainTextBySpaceLineStream(new FileInputStreamFactory(new File(corpus.testFile)), corpus.encoding);
        ObjectStream<DependencyParsingSample> sampleStreamNoNull = new DependencyParsingSampleStream(linesStreamNoNull);
        evaluatorNoNull.evaluate(sampleStreamNoNull);
        DependencyParsingMeasure measureResNoNull = evaluatorNoNull.getMeasure();
        DependencyParsingCount countRes = evaluatorNoNull.getCount();
        System.out.println("--------语料的一些信息如下--------");
        System.out.println(countRes);
        System.out.println("--------结果--------");
        System.out.println(measureResNoNull);
        //System.out.println("森林个数："+evaluatorNoNull.getForestCount());		
	}

	/**
	 * 输出模型
	 * @param contextGen 特征
	 * @param corpus 语料
	 * @param params 参数
	 * @throws IOException  异常
	 * @throws FileNotFoundException 异常
	 * @throws UnsupportedOperationException 异常
	 */
	private static void modelOutOnCorpus(DependencyParsingContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, FileNotFoundException, IOException {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        //训练模型，输出模型
        DependencyParsingME.train(new File(corpus.trainFile), new File(corpus.modelbinaryFile), new File(corpus.modeltxtFile), params, contextGen, corpus.encoding);
        DependencyParsingCount count = DependencyParsingDependencySample.getCountRes();
        System.out.println("--------语料的信息如下--------");
        System.out.println(count);
	}

	/**
	 * 训练模型
	 * @param contextGen 特征
	 * @param corpus 语料
	 * @param params 训练参数
	 * @throws UnsupportedOperationException 异常
	 * @throws IOException 异常
	 */
	private static void trainOnCorpus(DependencyParsingContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) throws UnsupportedOperationException, IOException {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        //训练模型
        DependencyParsingME.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);

	}

	/**
	 * 根据配置文件的信息确定生成上下文环境的类
	 * @param config 配置文件
	 * @return 特征生成的类
	 */
	private static DependencyParsingContextGenerator getContextGenerator(Properties config) {
		String featureClass = config.getProperty("feature.class");
		if(featureClass.equals("com.wxw.tc.phraseanalysis.feature.PhraseAnalysisContextGeneratorConf")){
			//初始化需要哪些特征
        	return  new DependencyParsingContextGeneratorConf(config);
		}else if(featureClass.equals("com.wxw.tc.phraseanalysis.feature.PhraseAnalysisContextGeneratorConfExtend")){
			//初始化需要哪些特征
        	return  new DependencyParsingContextGeneratorConfExtend(config);
		}else{
			return null;
		} 
	}

	/**
	 * 从配置文件中获取语料的信息
	 * @param config 配置文件
	 * @return 特征生成的类
	 */
	private static Corpus[] getCorporaFromConf(Properties config) {
		Corpus[] corpuses = new Corpus[corpusName.length];
		for (int i = 0; i < corpuses.length; i++) {
			String name = corpusName[i];
			String encoding = config.getProperty(name + "." + "corpus.encoding");
			String trainFile = config.getProperty(name + "." + "corpus.train.file");
			String testFile = config.getProperty(name+"."+"corpus.test.file");
			String modelbinaryFile = config.getProperty(name + "." + "corpus.modelbinary.file");
			String modeltxtFile = config.getProperty(name + "." + "corpus.modeltxt.file");
			String errorNullFile = config.getProperty(name + "." + "corpus.errorNull.file");
			String errorNoNullFile = config.getProperty(name + "." + "corpus.errorNoNull.file");
			Corpus corpus = new Corpus();
			corpus.name = name;
			corpus.encoding = encoding;
			corpus.trainFile = trainFile;
			corpus.testFile = testFile;
			corpus.modeltxtFile = modeltxtFile;
			corpus.modelbinaryFile = modelbinaryFile;
			corpus.errorNullFile = errorNullFile;
			corpus.errorNoNullFile = errorNoNullFile;
			corpuses[i] = corpus;			
		}
		return corpuses;
	}
}
