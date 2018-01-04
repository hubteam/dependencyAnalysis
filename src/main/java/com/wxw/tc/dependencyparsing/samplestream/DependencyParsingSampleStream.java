package com.wxw.tc.dependencyparsing.samplestream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingContext;
import com.wxw.tc.dependencyparsing.parsesample.DependencyParsingDependencySample;

import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 * 读取文件流，并解析成要的格式返回
 * @author 王馨苇
 *
 */
public class DependencyParsingSampleStream extends FilterObjectStream<String, DependencyParsingSample>{

	private static Logger logger = Logger.getLogger(DependencyParsingSampleStream.class.getName());
	public DependencyParsingSampleStream(ObjectStream<String> samples) {
		//samples为PlainTextBySpaceLineStream对象
		super(samples);
		
	}

	/**
	 * 读取两个空行之间的内容
	 * @return 返回解析之后的结果
	 */	
	public DependencyParsingSample read() throws IOException {
		//上面的super,指定了read()读的是哪个文件的
		//这里的read()读取训练语料中两个空行之间的内容
		String sentences = samples.read();
		//用PhraseAnalysisDependencySample的实现来解析文本
		DependencyParsingContext parser = new DependencyParsingContext(new DependencyParsingDependencySample(), sentences);
		if(sentences != "" || !(sentences.equals(""))){
			DependencyParsingSample sample = null ;
			try{
				//System.out.println(sentences);
				sample = parser.sampleParse();
			}catch(Exception e){
				if (logger.isLoggable(Level.WARNING)) {
					
                    logger.warning("Error during parsing, ignoring sentence: " + sentences);
                }
				sample = new DependencyParsingSample(new String[]{},new String[]{},new String[]{},new String[]{},new String[]{});
			}

			return sample;
			
		}else{
			return null;
		}
		
	}

	
}
