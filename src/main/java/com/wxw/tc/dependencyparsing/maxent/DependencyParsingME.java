package com.wxw.tc.dependencyparsing.maxent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.wxw.tc.dependencyparsing.evaluate.DependencyParsingCount;
import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGenerator;
import com.wxw.tc.dependencyparsing.feature.DependencyParsingContextGeneratorConf;
import com.wxw.tc.dependencyparsing.kResTool.Data;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingContext;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingDependencySample;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingSampleParser;
import com.wxw.tc.dependencyparsing.samplestream.FileInputStreamFactory;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSample;
import com.wxw.tc.dependencyparsing.samplestream.DependencyParsingSampleStream;
import com.wxw.tc.dependencyparsing.samplestream.PlainTextBySpaceLineStream;
import com.wxw.tc.dependencyparsing.tree.DependencyParserTree;
import com.wxw.tc.dependencyparsing.tree.DependencyParsingBestProba;
import com.wxw.tc.dependencyparsing.tree.MaxSpinningTree;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.GIS;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.maxent.io.PlainTextGISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 * 训练模型
 * @author 王馨苇
 *
 */
public class DependencyParsingME implements DependencyParsing{

	public static final int DEFAULT_BEAM_SIZE = 3;
	
	private DependencyParsingContextGenerator contextGenerator;
	private int size;
	
	private MaxentModel mm;
	private double[][] proba;
	private String[][] dependencyRelation;
	
	public DependencyParsingME(){
		
	}
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public DependencyParsingME(DependencyParsingModel model, DependencyParsingContextGenerator contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(DependencyParsingModel model, DependencyParsingContextGenerator contextGen) {
		int beamSize = DependencyParsingME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        mm = model.getPhraseAnalysisModel();

        contextGenerator = contextGen;
        size = beamSize;
		
	}

	/**
	 * 训练模型并输出
	 * @param file 训练语料的文件
	 * @param modelbinaryFile 持久化模型文件，二进制
	 * @param modeltxtFile 输出模型文件
	 * @param params 训练参数
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return PhraseAnalysisModel类，包装模型的信息
	 */
	public static DependencyParsingModel train(File file, File modelbinaryFile, File modeltxtFile ,TrainingParameters params,
			DependencyParsingContextGenerator contextGen, String encoding) {
		DependencyParsingModel model = null;
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		
		try {
			//FileInputStreamFactory用于判断文件是否存在
			//PlainTextBySpaceLineStream读取训练语料中两个空格行之间的数据
            ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(new FileInputStreamFactory(file), encoding);
            //sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<DependencyParsingSample> sampleStream = new DependencyParsingSampleStream(lineStream);
            //训练模型
            model = DependencyParsingME.train("zh", sampleStream, params, contextGen);
            //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
            model.serialize(modelOut);
            //模型的写出，文本文件
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getPhraseAnalysisModel(), modeltxtFile);
            modelWriter.persist();
            return model;
        } catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		return null;
	}
	
	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练参数
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 */
	public static DependencyParsingModel train(File file, TrainingParameters params,
			DependencyParsingContextGenerator contextGen, String encoding) {
		DependencyParsingModel model = null;

		try {
			//FileInputStreamFactory用于判断文件是否存在
			//PlainTextBySpaceLineStream读取训练语料中两个空格行之间的数据
            ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(new FileInputStreamFactory(file), encoding);
            //sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<DependencyParsingSample> sampleStream = new DependencyParsingSampleStream(lineStream);
            //训练模型
            model = DependencyParsingME.train("zh", sampleStream, params, contextGen);
            return model;
        } catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 训练模型
	 * @param languageCode 编码
	 * @param sampleStream 样本流
	 * @param params 训练参数
	 * @param contextGen 特征
	 * @return 模型
	 * @throws IOException IO异常
	 */
	public static DependencyParsingModel train(String languageCode, ObjectStream<DependencyParsingSample> sampleStream,
			TrainingParameters params, DependencyParsingContextGenerator contextGen) throws IOException {
		//beamSizeString为空
        String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
      
        int beamSize = DependencyParsingME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel posModel = null;

        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new DependencyParsingSampleEventStream(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);                       
        }

        if (posModel != null) {
            return new DependencyParsingModel(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new DependencyParsingModel(languageCode, seqPosModel, manifestInfoEntries);
        }
	}
	
	/**
	 * 根据输出的模型文件读取模型
	 * @param file 模型文件
	 * @param params 参数
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return
	 */
	public static DependencyParsingModel readModel(File modelFile, TrainingParameters params,
			DependencyParsingContextGenerator contextGen, String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		DependencyParsingModel model = null;
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = DependencyParsingME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new DependencyParsingModel(encoding, abModel, beamSize,manifestInfoEntries);
	
			System.out.println("读取模型成功");
            return model;
        } catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	
	/**
	 * 获得最大概率与最大概率对应的依赖标记，为最大生成树做准备
	 * @param sentence  语句
	 * @param pos 词性标注
	 * @param additionaContext 额外的信息
	 * @return PhraseAnalysisBestProba对象包装概率和依存关系的信息
	 */
	public DependencyParsingBestProba tagNull(String[] sentence, String[] pos, Object[] additionaContext){

		int i=1,j=0;
		proba = new double[sentence.length][sentence.length];
		dependencyRelation = new String[sentence.length][sentence.length];
		for (int m = 0; m < sentence.length; m++) {
			for (int n = 0; n < sentence.length; n++) {
				proba[m][n] = 0.0;
				dependencyRelation[m][n] = "null";
			}
		}
		 int lenLeft,lenRight;
		 if(DependencyParsingContextGeneratorConf.LEFT == -1 && DependencyParsingContextGeneratorConf.RIGHT == -1){
			 lenLeft = -sentence.length;
			 lenRight = sentence.length;
		 }else{
			 lenLeft = DependencyParsingContextGeneratorConf.LEFT;
			 lenRight = DependencyParsingContextGeneratorConf.RIGHT;
		 }
		// if()
		 while(i < sentence.length){
			 while(j < sentence.length){
//			 while(j - i <= lenRight && j - i >= lenLeft && j < sentence.length){
				if(i != j){
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);					
					String str = mm.getBestOutcome(temp);
//					System.out.println(mm.getAllOutcomes(temp));
//					for (int k = 0; k < temp.length; k++) {
//						System.out.println(temp[k]+":"+mm.getOutcome(k));
//					}
					
					Arrays.sort(temp);
					//根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = str;
					//最大的概率
					proba[i][j] = temp[temp.length-1];	
					//System.out.println(sentence[i]+" "+sentence[j]+":"+str);
				}
				j++;	
			}
			i++;
			j=0;
		}
		//测试用
//		for (int j2 = 0; j2 < proba.length; j2++) {
//			
//			for (int k = 0; k < proba.length; k++) {
//				//System.out.println(j2+"\t");
//				System.out.print(proba[j2][k]+"\t");
//			}
//			System.out.println();
//		}
//		for (int j2 = 0; j2 < dependencyRelation.length; j2++) {
//			for (int k = 0; k < dependencyRelation.length; k++) {				
//				System.out.print(dependencyRelation[j2][k]+"\t");
//			}
//			System.out.println();
//		}
		return new DependencyParsingBestProba(sentence,pos,proba,dependencyRelation);
	}
	
	/**
	 * 获得关系非null的最大概率及其对应的关系
	 * @param sentence 词语
	 * @param pos 词性
	 * @param additionaContext 额外的信息
	 * @return PhraseAnalysisBestProba对象包装概率和依存关系的信息
	 */
	public DependencyParsingBestProba tagNoNull(String[] sentence, String[] pos, Object[] additionaContext){

		//return tagK(1,sentence,pos,additionaContext);
		int i=1,j=0;
		proba = new double[sentence.length][sentence.length];
		dependencyRelation = new String[sentence.length][sentence.length];
		for (int m = 0; m < sentence.length; m++) {
			for (int n = 0; n < sentence.length; n++) {
				proba[m][n] = 0.0;
				dependencyRelation[m][n] = "null";
			}
		}
		
		int lenLeft,lenRight;
		 if(DependencyParsingContextGeneratorConf.LEFT == -1 && DependencyParsingContextGeneratorConf.RIGHT == -1){
			 lenLeft = -sentence.length;
			 lenRight = sentence.length;
		 }else{
			 lenLeft = DependencyParsingContextGeneratorConf.LEFT;
			 lenRight = DependencyParsingContextGeneratorConf.RIGHT;
		 }
		// if()
		 while(i < sentence.length){
//			 while(j < sentence.length){
			 while(j - i <= lenRight && j - i >= lenLeft && j < sentence.length){
				if(i != j){
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);	
					String tempDependency[] = new String[temp.length];
					for (int k = 0; k < temp.length; k++) {
						tempDependency[k] = mm.getOutcome(k);
					}
					double max = -1;
					int record = -1;
					for (int k = 0; k < temp.length; k++) {
						if((temp[k] > max) && (tempDependency[k].compareTo("null") != 0)){
							max = temp[k];
							record = k;							
						}
					}
					//根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = tempDependency[record];
					//最大的概率
					proba[i][j] = temp[record];					
				}
				j++;	
			}	
			i++;
			j=0;
		}
			//测试用
//			for (int j2 = 0; j2 < proba.length; j2++) {
//				
//				for (int k = 0; k < proba.length; k++) {
//					//System.out.println(j2+"\t");
//					System.out.print(proba[j2][k]+"\t");
//				}
//				System.out.println();
//			}
//			for (int j2 = 0; j2 < dependencyRelation.length; j2++) {
//				for (int k = 0; k < dependencyRelation.length; k++) {				
//					System.out.print(dependencyRelation[j2][k]+"\t");
//				}
//				System.out.println();
//			}
		return new DependencyParsingBestProba(sentence,pos,proba,dependencyRelation);
	}
	
	/**
	 * 获得关系非null的最大K个概率及其对应的关系
	 * @param kRes 获得的最好的结果的个数
	 * @param sentence 词语
	 * @param pos 词性
	 * @param additionaContext 额外的信息
	 * @return PhraseAnalysisBestProba对象包装概率和依存关系的信息
	 */
	public DependencyParsingBestProba tagK(int kRes, String[] sentence, String[] pos, Object[] additionaContext){

		int i=1,j=0;
		String[][] proba = new String[sentence.length][sentence.length];
		//proba = new double[sentence.length][sentence.length];
		dependencyRelation = new String[sentence.length][sentence.length];
//		for (int m = 0; m < sentence.length; m++) {
//			for (int n = 0; n < sentence.length; n++) {
//				proba[m][n] = 0.0;
//				dependencyRelation[m][n] = "null";
//			}
//		}		
		int lenLeft,lenRight;
		 if(DependencyParsingContextGeneratorConf.LEFT == -1 && DependencyParsingContextGeneratorConf.RIGHT == -1){
			 lenLeft = -sentence.length;
			 lenRight = sentence.length;
		 }else if(DependencyParsingContextGeneratorConf.LEFT == -1 && DependencyParsingContextGeneratorConf.RIGHT != -1){
			 lenLeft = -sentence.length;
			 lenRight = DependencyParsingContextGeneratorConf.RIGHT;
		 }else if(DependencyParsingContextGeneratorConf.LEFT != -1 && DependencyParsingContextGeneratorConf.RIGHT == -1){
			 lenLeft = DependencyParsingContextGeneratorConf.LEFT;
			 lenRight = sentence.length;
		 }else{
			 lenLeft = DependencyParsingContextGeneratorConf.LEFT;
			 lenRight = DependencyParsingContextGeneratorConf.RIGHT;
		 }
		
		 while(i < sentence.length){
//			 while(j < sentence.length){
			 while(j - i <= lenRight && j - i >= lenLeft && j < sentence.length){
				if(i != j){
					Queue<Data> queue = new PriorityQueue<>();
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);	
					String tempDependency[] = new String[temp.length];
					for (int k = 0; k < temp.length; k++) {
						if(mm.getOutcome(k).compareTo("null") != 0){
							tempDependency[k] = mm.getOutcome(k);
							queue.add(new Data(temp[k],k));
						}						
					}
					String tempProba = "";
					String tempDepen = "";
					for (int k = 0; k < kRes; k++) {
						Data data = queue.poll();						
						if(k == kRes - 1){
							tempProba += data.getValue();
							tempDepen += tempDependency[data.getIndex()];
						}else{
							tempProba += data.getValue()+"_";
							tempDepen += tempDependency[data.getIndex()]+"_";
						}
					}					
					//根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = tempDepen;
					//最大的概率
					proba[i][j] = tempProba;
				}
				j++;	
			}	
			i++;
			j=0;
		}
			//测试用
//			for (int j2 = 0; j2 < proba.length; j2++) {
//				
//				for (int k = 0; k < proba.length; k++) {
//					//System.out.println(j2+"\t");
//					System.out.print(proba[j2][k]+"\t");
//				}
//				System.out.println();
//			}
//			for (int j2 = 0; j2 < dependencyRelation.length; j2++) {
//				for (int k = 0; k < dependencyRelation.length; k++) {				
//					System.out.print(dependencyRelation[j2][k]+"\t");
//				}
//				System.out.println();
//			}
		return new DependencyParsingBestProba(sentence,pos,proba,dependencyRelation);
	}
	
	/**
	 * 获得关系非null的最大概率及其对应的关系
	 * @param sentence 词语
	 * @param pos 词性
	 * @return PhraseAnalysisBestProba对象包装概率和依存关系的信息
	 */
	public DependencyParsingBestProba tagNoNull(String[] sentence, String[] pos){
	
		return tagNoNull(sentence, pos,null);
	}
	
	/**
	 * 获得关系非null的最大K个概率及其对应的关系
	 * @param k 获得的最好的结果的个数
	 * @param sentence 词语
	 * @param pos 词性
	 * @return PhraseAnalysisBestProba对象包装概率和依存关系的信息
	 */
	public DependencyParsingBestProba tagK(int k,String[] sentence, String[] pos){
	
		return tagK(k,sentence, pos,null);
	}
	
	/**
	 * 得到依存分析后的输出的样本样式
	 * @param sentence 分词+词性标注之后的句子
	 */
	@Override
	public DependencyParserTree dependencyparsing(String sentence) {
		DependencyParsingSampleParser parse = new DependencyParsingDependencySample();
		DependencyParsingContext context = new DependencyParsingContext(parse, sentence);
	
		DependencyParsingSample sample = context.testParse();
		
		return dependencyparsing(sample.getWords(),sample.getPos());
	}

	/**
	 * 解析语句得到依存分析的结果
	 * @param words 分词之后的词语
	 * @param poses 词性标记
	 * @return 依存分析之后的结果
	 */
	@Override
	public DependencyParserTree dependencyparsing(String[] words, String[] poses) {
		List<String> wordslist = Arrays.asList(words);
		List<String> allwords = new ArrayList<>();
		allwords.add(0, "核心");
		allwords.addAll(wordslist);
		List<String> poseslist = Arrays.asList(poses);	
		List<String> allposes = new ArrayList<>();
		allposes.add(0, "root");
		allposes.addAll(poseslist);
		DependencyParsingBestProba proba = tagNoNull(allwords.toArray(new String[allwords.size()]), 
				allposes.toArray(new String[allposes.size()]));
		DependencyParsingSample sample;
		sample = MaxSpinningTree.getMaxTree(proba);
		DependencyParserTree parse = new DependencyParserTree();
		parse.setTree(sample);
		return parse;
	}

	/**
	 * 解析语句得到依存分析的结果
	 * @param wordsandposes 分词+词性标记的词语组成的数组
	 * @return 依存分析之后的结果
	 */
	@Override
	public DependencyParserTree dependencyparsing(String[] wordsandposes) {
		String sentence = "";
		for (int i = 0; i < wordsandposes.length; i++) {
			sentence += wordsandposes[i]+" ";
			if(i == wordsandposes.length-1){
				sentence += wordsandposes[i];
			}
		}
		DependencyParsingSampleParser parse = new DependencyParsingDependencySample();
		DependencyParsingContext context = new DependencyParsingContext(parse, sentence);
	
		DependencyParsingSample sample = context.testParse();
		
		return dependencyparsing(sample.getWords(),sample.getPos());
	}

	@Override
	public DependencyParserTree[] dependencyparsing(int k, String sentence) {
		DependencyParsingSampleParser parse = new DependencyParsingDependencySample();
		DependencyParsingContext context = new DependencyParsingContext(parse, sentence);
	
		DependencyParsingSample sample = context.testParse();
		
		return dependencyparsing(k,sample.getWords(),sample.getPos());
	}

	@Override
	public DependencyParserTree[] dependencyparsing(int k, String[] words, String[] poses) {
		List<String> wordslist = Arrays.asList(words);
		List<String> allwords = new ArrayList<>();
		allwords.add(0, "核心");
		allwords.addAll(wordslist);
		List<String> poseslist = Arrays.asList(poses);	
		List<String> allposes = new ArrayList<>();
		allposes.add(0, "root");
		allposes.addAll(poseslist);
		DependencyParsingBestProba proba = tagK(k,allwords.toArray(new String[allwords.size()]), 
				allposes.toArray(new String[allposes.size()]));
		DependencyParsingSample sample;
		DependencyParserTree[] parse = new DependencyParserTree[k];
		parse = MaxSpinningTree.getMaxFromKres(k,proba);
		return parse;
	}

	@Override
	public DependencyParserTree[] dependencyparsing(int k, String[] wordsandposes) {
		String sentence = "";
		for (int i = 0; i < wordsandposes.length; i++) {
			sentence += wordsandposes[i]+" ";
			if(i == wordsandposes.length-1){
				sentence += wordsandposes[i];
			}
		}
		DependencyParsingSampleParser parse = new DependencyParsingDependencySample();
		DependencyParsingContext context = new DependencyParsingContext(parse, sentence);
	
		DependencyParsingSample sample = context.testParse();
		
		return dependencyparsing(k,sample.getWords(),sample.getPos());
	}	
}
