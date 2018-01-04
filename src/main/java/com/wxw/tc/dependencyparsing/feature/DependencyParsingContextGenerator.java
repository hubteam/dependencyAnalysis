package com.wxw.tc.dependencyparsing.feature;

/**
 * 特征的接口
 * @author 王馨苇
 *
 */
public interface DependencyParsingContextGenerator{

	/**
	 * 获取特征
	 * @param indexi i的位置
	 * @param indexj j的位置
	 * @param words 词语
	 * @param pos 词性
	 * @param ac 额外的信息
	 * @return
	 */
	 public String[] getContext(int indexi, int indexj, String[] words, String[] pos, Object[] ac);
}
