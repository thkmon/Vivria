package com.bb.vivria.data;

public class UnitData {
	
	
	// 색상값
	private int color = 0;
	
	
	// 유저아이디
	private String userId = null;
	
	
	// 비브리아 개수
	private int number = 0;
	
	
	// 왕 여부
	private boolean king = false;
	
	
	public int getColor() {
		return color;
	}
	
	
	public void setColor(int color) {
		this.color = color;
	}
	
	
	public String getUserId() {
		return userId;
	}
	
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	public int getNumber() {
		return number;
	}
	
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	
	public boolean isKing() {
		return king;
	}
	
	
	public void setKing(boolean king) {
		this.king = king;
	}
}