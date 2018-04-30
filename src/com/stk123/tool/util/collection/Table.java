package com.stk123.tool.util.collection;

import java.util.List;

public class Table {
	
	private List<String> topHeads;
	private List<String> leftHeads;
	private List<TableCell> data;
	
	public Table(List<String> topHeads, List<String> leftHeads, List<TableCell> list){
		this.topHeads = topHeads;
		this.leftHeads = leftHeads;
		this.data = list;
	}
	
	public TableCell getCell(int x, int y){
		String topHead = topHeads.get(x);
		String leftHead = leftHeads.get(y);
		for(TableCell cell : data){
			if(topHead != null && topHead.equalsIgnoreCase(cell.getX()) 
					&& leftHead != null && leftHead.equalsIgnoreCase(cell.getY())){
				return cell;
			}
		}
		return null;
	}
	
	public List<String> getTopHeads(){
		return this.topHeads;
	}
	
	public List<String> getLeftHeads(){
		return this.leftHeads;
	}
	
}
