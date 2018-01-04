package com.wxw.tc.dependencyparsing.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 中文论文《基于最大熵的依存句法分析》里的特征实现
 * @author 王馨苇
 *
 */
public class DependencyParsingContextGeneratorConfExtend implements DependencyParsingContextGenerator {

	// 定义变量控制feature的使用
	// 一元特征
	private boolean wiSet;
	private boolean wjSet;
	private boolean piSet;
	private boolean pi1Set;
	private boolean pi2Set;
	private boolean pi_1Set;
	private boolean pi_2Set;
	private boolean pjSet;
	private boolean pj1Set;
	private boolean pj2Set;
	private boolean pj_1Set;
	private boolean pj_2Set;

	// 二元特征
	private boolean wiwjSet;
	private boolean pipjSet;
	private boolean wiwjdisSet;
	private boolean pipjdisSet;
	private boolean pipjpi_1Set;
	private boolean pipjpi1Set;
	private boolean pipjpj_1Set;
	private boolean pipjpj1Set;

	// 父亲和儿子之间的位置和距离
	private boolean disSet;
	//窗口大小参数
	public static int LEFT;
	public static int RIGHT;

	/**
	 * 无参构造
	 * @throws IOException IO异常
	 */
	public DependencyParsingContextGeneratorConfExtend() throws IOException {
		Properties featureConf = new Properties();
		InputStream featureStream = DependencyParsingContextGeneratorConf.class.getClassLoader()
				.getResourceAsStream("com/wxw/tc/phraseanalysis/run/feature.properties");
		featureConf.load(featureStream);

		init(featureConf);
	}

	/**
	 * 有参构造
	 * @param config 配置文件
	 */
	public DependencyParsingContextGeneratorConfExtend(Properties config){
		init(config);
	}
	
	/**
	 * 从配置文件中获取特征的信息
	 * @param config 配置文件
	 */
	private void init(Properties config) {
		wiSet = config.getProperty("feature.wi", "true").equals("true");
		wjSet = config.getProperty("feature.wj", "true").equals("true");
		piSet = config.getProperty("feature.pi", "true").equals("true");
		pi1Set = config.getProperty("feature.pi1", "true").equals("true");
		pi2Set = config.getProperty("feature.pi1", "true").equals("true");
		pi_1Set = config.getProperty("feature.pi_1", "true").equals("true");
		pi_2Set = config.getProperty("feature.pi_2", "true").equals("true");
		pjSet = config.getProperty("feature.pj", "true").equals("true");
		pj1Set = config.getProperty("feature.pj1", "true").equals("true");
		pj2Set = config.getProperty("feature.pj2", "true").equals("true");
		pj_1Set = config.getProperty("feature.pj_1", "true").equals("true");
		pj_2Set = config.getProperty("feature.pj_2", "true").equals("true");
		
		wiwjSet = config.getProperty("feature.wiwj", "true").equals("true");
		pipjSet = config.getProperty("feature.pipj", "true").equals("true");
		wiwjdisSet = config.getProperty("feature.wiwjdis", "true").equals("true");
		pipjdisSet = config.getProperty("feature.pipjdis", "true").equals("true");
		pipjpi_1Set = config.getProperty("feature.pipjpi_1", "true").equals("true");
		pipjpi1Set = config.getProperty("feature.pipjpi1", "true").equals("true");
		pipjpj_1Set = config.getProperty("feature.pipjpj_1", "true").equals("true");
		
		pipjpj1Set = config.getProperty("feature.pipjpj1", "true").equals("true");
		
		disSet = config.getProperty("feature.dis", "true").equals("true");
		LEFT = Integer.parseInt(config.getProperty("window.left", "-1"));
		RIGHT = Integer.parseInt(config.getProperty("window.right", "-1"));
	}

	/**
	 * 获取特征
	 * @param indexi i的位置
	 * @param indexj j的位置
	 * @param words 词语
	 * @param pos 词性
	 * @return
	 */
	public String[] getContext( int indexi, int indexj, String[] words, String[] pos, Object[] ac) {
		String pi,pi1,pi2,pi_1,pi_2,pj,pj1,pj2,pj_1,pj_2,wi,wj;
		pi = pi1 = pi2 = pi_1 = pi_2 = pj = pj1 = pj2 = pj_1 = pj_2 = wi = wj = null;
		int dis;
		List<String> features = new ArrayList<String>();
		wi = words[indexi];
		pi = pos[indexi];
		wj = words[indexj];
		pj = pos[indexj];
		if(pos.length > indexi + 1){
			pi1 = pos[indexi+1];
			if(pos.length > indexi + 2){
				pi2 = pos[indexi+2];
			}
		}
		if(pos.length > indexj + 1){
			pj1 = pos[indexj+1];
			if(pos.length > indexj + 2){
				pj2 = pos[indexj+2];
			}
		}
		if(indexi - 1 >= 0){
			pi_1 = pos[indexi-1];
			if(indexi - 2 >= 0){
				pi_2 = pos[indexi-2];
			}
		}
		if(indexj - 1 >= 0){
			pj_1 = pos[indexj-1];
			if(indexj - 2 >= 0){
				pj_2 = pos[indexj-2];
			}
		}
		dis = indexi - indexj;
		if(wiSet){
			features.add("wi="+wi);
		}
		if(wjSet){
			features.add("wj="+wj);
		}
		if(piSet){
			features.add("pi="+pi);
		}
		if(pjSet){
			features.add("pj="+pj);
		}
		if(pi1Set){
			features.add("pi1="+pi1);
		}
		if(pi2Set){
			features.add("pi2="+pi2);
		}
		if(pi_1Set){
			features.add("pi_1="+pi_1);
		}
		if(pi_2Set){
			features.add("pi2="+pi_2);
		}
		if(pj1Set){
			features.add("pj1="+pj1);
		}
		if(pj2Set){
			features.add("pj2="+pj2);
		}
		if(pj_1Set){
			features.add("pj_1="+pj_1);
		}
		if(pj_2Set){
			features.add("pj2="+pj_2);
		}
		
		if(disSet){			
			features.add("dis="+dis);
		}
		if(wiwjSet){
			features.add("wiwj="+wi+wj);
		}
		if(pipjSet){
			features.add("pipj="+pi+pj);
		}
		if(wiwjdisSet){
			features.add("wiwjdis="+wi+wj+dis);
		}
		if(pipjdisSet){
			features.add("pipjdis="+pi+pj+dis);
		}
		if(pi_1 != null){
			if(pipjpi_1Set){
				features.add("pipjpi_1="+pi+pj+pi_1);
			}
		}
		if(pi1 != null){
			if(pipjpi1Set){
				features.add("pipjpi1="+pi+pj+pi1);
			}
		}
		if(pj_1 != null){
			if(pipjpj_1Set){
				features.add("pipjpj_1="+pi+pj+pj_1);
			}
		}
		if(pj1 != null){
			if(pipjpj1Set){
				features.add("pipjpj1="+pi+pj+pj1);
			}
		}
		String[] contexts = features.toArray(new String[features.size()]);
		
		return contexts;
	}

}
