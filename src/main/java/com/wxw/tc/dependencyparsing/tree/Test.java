package com.wxw.tc.dependencyparsing.tree;

import java.util.PriorityQueue;
import java.util.Queue;

public class Test {

	public static void main(String[] args) {
		Queue<String> queue = new PriorityQueue<>();
		queue.add("wxw");
		queue.add("haha");
		queue.add("www");
		queue.addAll(queue);
		System.out.println(queue.peek());
		System.out.println(queue.poll());
		System.out.println(queue.peek());
	}
}
