package com.wxw.tc.dependencyparsing.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 根据配置文件的信息提取特征，并组合
 * @author 王馨苇
 *
 */
public class DependencyParsingContextGeneratorConf implements DependencyParsingContextGenerator{

	//定义变量控制feature的使用
	//一元特征
	private boolean PwordPposSet;
	private boolean PwordSet;
	private boolean PposSet;
	private boolean CwordCposSet;
	private boolean CwordSet;
	private boolean CposSet;
	//二元特征
	private boolean PwordPposCwordCposSet;
	private boolean PposCwordCposSet;
	private boolean PwordCwordCposSet;
	private boolean PwordPposCposSet;
	private boolean PwordPposCwordSet;
	private boolean PwordCwordSet;
	private boolean PposCposSet;
	//i j之间的词性
	private boolean PposBposCposSet;
	//父亲儿子周围特征
	private boolean PposPpos1Cpos_1CposSet;
	private boolean Ppos_1PposCpos_1CposSet;
	private boolean PposPpos1CposCpos1Set;
	private boolean Ppos_1PposCposCpos1Set;
	//父亲和儿子之间的位置和距离
	private boolean disSet;
	//和中文论文对比增加的特征
	private boolean Ppos_1Set;
	private boolean Ppos1Set;
	private boolean Cpos_1Set;
	private boolean Cpos1Set;
	private boolean Ppos_2Set;
	private boolean Ppos2Set;
	private boolean Cpos_2Set;
	private boolean Cpos2Set;
	private boolean PwordCwordDisSet;
	private boolean PposCposDisSet;
	private boolean PposCposPpos_1Set;
	private boolean PposCposCpos_1Set;
	private boolean PposCposPpos1Set;
	private boolean PposCposCpos1Set;
	//窗口大小参数
	public static int LEFT;
	public static int RIGHT;
	
	/**
	 * 无参构造
	 * @throws IOException IO异常
	 */
	public DependencyParsingContextGeneratorConf() throws IOException{
		Properties featureConf = new Properties();
        InputStream featureStream = DependencyParsingContextGeneratorConf.class.getClassLoader().getResourceAsStream("com/wxw/tc/phraseanalysis/run/feature.properties");
        featureConf.load(featureStream);
        
        init(featureConf);
	}
	
	/**
	 * 有参构造
	 * @param config 配置文件
	 */
	public DependencyParsingContextGeneratorConf(Properties config){
		init(config);
	}

	/**
	 * 根据配置文件的信息初始化特征，判断该特征是否使用
	 * @param config 配置文件
	 */
	private void init(Properties config) {
		PwordPposSet = config.getProperty("feature.PwordPpos", "true").equals("true");
		PwordSet = config.getProperty("feature.Pword", "true").equals("true");
		PposSet = config.getProperty("feature.Ppos", "true").equals("true");
		CwordCposSet = config.getProperty("feature.CwordCpos", "true").equals("true");
		CwordSet = config.getProperty("feature.Cword", "true").equals("true");
		CposSet = config.getProperty("feature.Cpos", "true").equals("true");
		
		PwordPposCwordCposSet = config.getProperty("feature.PwordPposCwordCpos", "true").equals("true");
		PposCwordCposSet = config.getProperty("feature.PposCwordCpos", "true").equals("true");
		PwordCwordCposSet = config.getProperty("feature.PwordCwordCpos", "true").equals("true");
		PwordPposCposSet = config.getProperty("feature.PwordPposCpos", "true").equals("true");
		PwordPposCwordSet = config.getProperty("feature.PwordPposCword", "true").equals("true");
		PwordCwordSet = config.getProperty("feature.PwordCword", "true").equals("true");
		PposCposSet = config.getProperty("feature.PposCpos", "true").equals("true");
		
		PposBposCposSet = config.getProperty("feature.PposBposCpos", "true").equals("true");
		
		PposPpos1Cpos_1CposSet = config.getProperty("feature.PposPpos1Cpos_1Cpos", "true").equals("true");
		Ppos_1PposCpos_1CposSet = config.getProperty("feature.Ppos_1PposCpos_1Cpos", "true").equals("true");
		PposPpos1CposCpos1Set = config.getProperty("feature.PposPpos1CposCpos1", "true").equals("true");
		Ppos_1PposCposCpos1Set = config.getProperty("feature.Ppos_1PposCposCpos1Set", "true").equals("true");
		
		disSet = config.getProperty("feature.dis", "true").equals("true");
		
		//增加的特征
		Ppos_1Set = config.getProperty("feature.Ppos_1", "true").equals("true");
		Ppos1Set = config.getProperty("feature.Ppos1", "true").equals("true");
		Cpos_1Set = config.getProperty("feature.Cpos_1", "true").equals("true");
		Cpos1Set = config.getProperty("feature.Cpos1", "true").equals("true");
		Ppos_2Set = config.getProperty("feature.Ppos_2", "true").equals("true");
		Ppos2Set = config.getProperty("feature.Ppos2", "true").equals("true");
		Cpos_2Set = config.getProperty("feature.Cpos_2", "true").equals("true");
		Cpos2Set = config.getProperty("feature.Cpos2", "true").equals("true");
		PwordCwordDisSet = config.getProperty("feature.PwordCwordDis", "true").equals("true");
		PposCposDisSet = config.getProperty("feature.PposCposDis", "true").equals("true");
		
		PposCposPpos_1Set = config.getProperty("feature.PposCposPpos_1", "true").equals("true");
		PposCposCpos_1Set = config.getProperty("feature.PposCposCpos_1", "true").equals("true");
		PposCposPpos1Set = config.getProperty("feature.PposCposPpos1", "true").equals("true");
		PposCposCpos1Set = config.getProperty("feature.PposCposCpos1", "true").equals("true");
		
		LEFT = Integer.parseInt(config.getProperty("window.left", "-1"));
		RIGHT = Integer.parseInt(config.getProperty("window.right", "-1"));
		
	}

	/**
	 * 重写的方法，输出的打印格式
	 */
	@Override
	public String toString() {
		return "PwordPpos="+PwordPposSet+" "+"Pword="+PwordSet+" "+"Ppos="+PposSet+" "+"CwordCpos="+CwordCposSet+" "+
	"Cword="+CwordSet+" "+"Cpos="+CposSet+"\n"+"PwordPposCwordCpos="+PwordPposCwordCposSet+" "+
	"PposCwordCpos="+PposCwordCposSet+" "+"PwordCwordCpos="+PwordCwordCposSet+" "+
	"PwordPposCpos="+PwordPposCposSet+" "+"PwordPposCword="+PwordPposCwordSet+" "+
	"PwordCword="+PwordCwordSet+" "+"PposCpos="+PposCposSet+"\n"+
	"PposBposCpos="+PposBposCposSet+"\n"+
	"PposPpos1Cpos_1Cpos="+PposPpos1Cpos_1CposSet+" "+"Ppos_1PposCpos_1Cpos="+Ppos_1PposCpos_1CposSet+" "+
	"PposPpos1CposCpos1="+PposPpos1CposCpos1Set+" "+"Ppos_1PposCposCpos1="+Ppos_1PposCposCpos1Set+"\n"+
	"dis="+disSet+" "+"PwordCwordDis="+PwordCwordDisSet+" "+"PposCposDis="+PposCposDisSet+"\n"+
	"Ppos_1="+Ppos_1Set+" "+"Ppos1="+Ppos1Set+" "+"Cpos_1="+Cpos_1Set+" "+"Cpos1="+Cpos1Set+"\n"+
	"Ppos_2="+Ppos_2Set+" "+"Ppos2="+Ppos2Set+" "+"Cpos_2="+Cpos_2Set+" "+"Cpos2="+Cpos2Set+"\n"+
	"PposCposPpos_1="+PposCposPpos_1Set+" "+"PposCposCpos_1="+PposCposCpos_1Set+" "+"PposCposPpos1="+PposCposPpos1Set+" "+"PposCposCpos1="+PposCposCpos1Set;
	}
	
	/**
	 * 获取特征
	 * @param indexi i的位置
	 * @param indexj j的位置
	 * @param words 词语
	 * @param pos 词性
	 * @param ac 额外的信息
	 * @return
	 */
	public String[] getContext(int indexi, int indexj, String[] words, String[] pos, Object[] ac) {
			return getContext(indexi,indexj,words,pos);
	}

	/**
	 * 获取特征
	 * @param indexi i的位置
	 * @param indexj j的位置
	 * @param words 词语
	 * @param pos 词性
	 * @return
	 */
	private String[] getContext(int indexi, int indexj, String[] words, String[] pos) {
		String Pword,Ppos,Cword,Cpos,Ppos_1,Ppos1,Cpos_1,Cpos1,Ppos_2,Ppos2,Cpos_2,Cpos2;
		Pword = Ppos = Cword = Cpos = Ppos_1 = Ppos1 = Cpos_1 = Cpos1 = Ppos_2 = Ppos2 = Cpos_2 = Cpos2 = null;
		int dis;
		List<String> features = new ArrayList<String>();
		Pword = words[indexi];
		Ppos = pos[indexi];
		Cword = words[indexj];
		Cpos = pos[indexj];
		dis = indexi - indexj;
		if(pos.length > indexi + 1){
			Ppos1 = pos[indexi+1];
			if(pos.length > indexi + 2){
				Ppos2 = pos[indexi+2];
			}
		}
		if(pos.length > indexj + 1){
			Cpos1 = pos[indexj+1];
			if(pos.length > indexj + 2){
				Cpos2 = pos[indexj+2];
			}
		}
		if(indexi - 1 >= 0){
			Ppos_1 = pos[indexi-1];
			if(indexi - 2 >= 0){
				Ppos_2 = pos[indexi-2];
			}
		}
		if(indexj - 1 >= 0){
			Cpos_1 = pos[indexj-1];
			if(indexj - 2 >= 0){
				Cpos_2 = pos[indexj-2];
			}
		}
		if(PwordPposSet){
			features.add("PwordPpos="+Pword+Ppos);			
		}
		if(PwordSet){
			features.add("Pword="+Pword);
		}
		if(PposSet){
			features.add("Ppos="+Ppos);
		}
		if(CwordCposSet){
			features.add("CwordCpos="+Cword+Cpos);
		}
		if(CwordSet){
			features.add("Cword="+Cword);
		}
		if(CposSet){
			features.add("Cpos="+Cpos);
		}
		
		if(PwordPposCwordCposSet){
			features.add("PwordPposCwordCpos="+Pword+Ppos+Cword+Cpos);
		}
		if(PposCwordCposSet){
			features.add("PposCwordCpos="+Ppos+Cword+Cpos);
		}
		if(PwordCwordCposSet){
			features.add("PwordCwordCpos="+Pword+Cword+Cpos);
		}
		if(PwordPposCposSet){
			features.add("PwordPposCpos="+Pword+Ppos+Cpos);
		}
		if(PwordPposCwordSet){
			features.add("PwordPposCword="+Pword+Ppos+Cword);
		}
		if(PwordCwordSet){
			features.add("PwordCword="+Pword+Cword);
		}
		if(PposCposSet){
			features.add("PposCpos="+Ppos+Cpos);
		}
		
		if(indexi - indexj > 1){
			for (int i = indexj + 1; i < indexi; i++) {
				if(PposBposCposSet){
					features.add("PposBposCposSet="+Ppos+pos[i]+Cpos);	
				}
			}	
		}else if(indexj - indexi > 1){
			for (int i = indexi + 1; i < indexj; i++) {
				if(PposBposCposSet){
					features.add("PposBposCposSet="+Ppos+pos[i]+Cpos);	
				}
			}
		}
		
		if(Ppos1 != null && Cpos_1 != null){
			if(PposPpos1Cpos_1CposSet){
				features.add("PposPpos1Cpos_1Cpos="+Ppos+Ppos1+Cpos_1+Cpos);	
			}
		}
		if(Ppos_1 != null && Cpos_1 != null){
			if(Ppos_1PposCpos_1CposSet){
				features.add("Ppos_1PposCpos_1Cpos="+Ppos_1+Ppos+Cpos_1+Cpos);	
			}
		}
		if(Ppos1 != null && Cpos1 != null){
			if(PposPpos1CposCpos1Set){
				features.add("PposPpos1CposCpos1="+Ppos+Ppos1+Cpos+Cpos1);	
			}
		}
		if(Ppos_1 != null && Cpos1 != null){
			if(Ppos_1PposCposCpos1Set){
				features.add("Ppos_1PposCposCpos1Set="+Ppos_1+Ppos+Cpos+Cpos1);	
			}
		}
		
		if(disSet){			
			features.add("dis="+dis);
		}
		
		//新增的特征
		if(Ppos_1 != null){
			if(Ppos_1Set){
				features.add("Ppos_1="+Ppos_1);
			}
		}
		
		if(Ppos_2 != null){
			if(Ppos_2Set){
				features.add("Ppos_2="+Ppos_2);
			}
		}
		
		if(Ppos1 != null){
			if(Ppos_1Set){
				features.add("Ppos1="+Ppos1);
			}
		}
		
		if(Ppos2 != null){
			if(Ppos_2Set){
				features.add("Ppos2="+Ppos2);
			}
		}
		
		if(Cpos_1 != null){
			if(Cpos_1Set){
				features.add("Cpos_1="+Cpos_1);
			}
		}
		
		if(Cpos_2 != null){
			if(Cpos_2Set){
				features.add("Cpos_2="+Cpos_2);
			}
		}
		
		if(Cpos1 != null){
			if(Cpos_1Set){
				features.add("Cpos1="+Cpos1);
			}
		}
		
		if(Cpos2 != null){
			if(Cpos_2Set){
				features.add("Cpos2="+Cpos2);
			}
		}
		
		if(PwordCwordDisSet){
			features.add("PwordCwordDis="+Pword+Cword+dis);
		}
		
		if(PposCposDisSet){
			features.add("PposCposDis="+Ppos+Cpos+dis);
		}
		
		if(Ppos_1 != null){
			if(PposCposPpos_1Set){
				features.add("PposCposPpos_1="+Ppos+Cpos+Ppos_1);
			}
		}
		if(Cpos_1 != null){
			if(PposCposCpos_1Set){
				features.add("PposCposCpos_1="+Ppos+Cpos+Cpos_1);
			}
		}
		if(Ppos1 != null){
			if(PposCposPpos1Set){
				features.add("PposCposPpos1="+Ppos+Cpos+Ppos1);
			}
		}
		if(Cpos1 != null){
			if(PposCposCpos1Set){
				features.add("PposCposCpos1="+Ppos+Cpos+Cpos1);
			}
		}
		
		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
		
	}
}