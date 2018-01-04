package com.wxw.tc.dependencyparsing.kResTool;

/**
 * 存放数组中的值和对应的下标
 * @author 王馨苇
 *
 */
public class Data implements Comparable<Data>{

	private double value;
	private int index;
	private String dependency;
	private int wordIndex;
	private String word;
	private String pos;
	/**
	 * 构造
	 * @param value 数组中的值
	 * @param index 值对应的下标
	 */
	public Data(double value,int index) {
		this.value = value;
		this.index = index;		
	}
	
	public Data(double value,int index, String dependency,int wordIndex,String word,String pos){
		this.value = value;
		this.index = index;	
		this.dependency = dependency;
		this.wordIndex = wordIndex;
		this.word = word;
		this.pos = pos;
	}
	
	/**
	 * 获得依赖关系
	 * @return
	 */
	public String getDependency(){
		return this.dependency;
	}
	
	/**
	 * 与其有依赖的词语对应的下标
	 * @return
	 */
	public int getWordIndex(){
		return this.wordIndex;
	}
	
	public String getWord(){
		return this.word;
	}
	
	public String getPos(){
		return this.pos;
	}
	/**
	 * 获取值
	 * @return 值
	 */
	public double getValue(){
		return this.value;
	}
	
	/**
	 * 获取值对应的下标
	 * @return 下标
	 */
	public int getIndex(){
		return this.index;
	}
	
	/**
	 * 重写的用于比较值的大小的方法
	 */
	@Override
	public int compareTo(Data arg0) {
		if(this.getValue() > arg0.getValue()){
			return -1;
		}else if(this.getValue() == arg0.getValue()){
			return 0;
		}else{
			return 1;
		}
		
	}

}
