package com.wxw.tc.dependencyparsing.graph;

/**
 * 用矩阵表示图
 * @author 王馨苇
 *
 */
public class MyGraph {

	//节点数目
	private int node;
	private int[] data ;//存放节点标签
	private int[][] edge;//边
	private String[] begin;
	private int[] visited;
	
	/**
	 * 构造
	 * @param begin 节点的数组
	 */
	public MyGraph(String[] begin){
		this.node = begin.length + 1;
		this.begin = begin;
		this.edge = new int[node][node];
		this.data = new int[node];
		this.visited = new int[node];
	}
	/**
	 * 构造
	 * @param begin 词语组成的节点数组
	 * @param length 节点个数
	 */
	public MyGraph(String[] begin,int length){
		this.node = length + 1;
		this.begin = begin;
		this.edge = new int[node][node];
		this.data = new int[node];
		this.visited = new int[node];
	}
	
	/**
	 * 获取图的矩阵结果
	 * @return 包含图中边的关系的二维数组
	 */
	public int[][] getMetric(){
		
		for (int i = 0; i < node-1; i++) {
			if(Integer.parseInt(begin[i]) != -1){
				this.edge[Integer.parseInt(begin[i])][i+1] = 1;
			}
			
		}
		return this.edge;
	}
	
	/**
	 * 用数字表示节点
	 * @return 节点的数组
	 */
	public int[] getNodeData(){
		
		for (int i = 0; i < node; i++) {
			this.data[i] = i;
		}
		return this.data;
	}
	
	/**
	 * 获取节点
	 * @return 节点
	 */
	public int getNode(){
		return this.node;
	}
	
	/**
	 * 记录节点是否被访问的数组
	 * @return 记录访问与否的数组
	 */
	public int[] getVisited(){
		return this.visited;
	}
	
}
