package com.bb.vivria.data;

import com.bb.vivria.common.GameConst;

public class TileData implements GameConst {

	// - 비브리아 있는지 없는지 => 통행불가/통행가능
	// - 색상
	// - 어느 유저의 비브리아인지 
	// - 숫자가 몇인지
	
	// 통행가능한 타일인지
	private boolean canMove = false;
	private int gamerIndex = -1;
	private int vivriaCount = 0;
	private boolean kingVivria = false;
	
	
	@Override
	public String toString() {
		
		String result = "";
		
		// 통행불가타일
		if (!canMove) {
			return "XXX";
		}
		
		// 통행타일
		if (vivriaCount == 0) {
			return "OOO";
		}
		
		// 색상
		if (gamerIndex == 1) {
			result += "R";
			
		} else if (gamerIndex == 2) {
			result += "Y";

		} else if (gamerIndex == 3) {
			result += "B";
			
		} else if (gamerIndex == 4) {
			result += "G";
			
		} else {
			result += "?";
		}
		
		result += gamerIndex;
		
		// if (vivriaCount == VIVRIA_COUNT_KING) {
		if (this.isKingVivria() == true) {
			result += "K";
			
		} else if (vivriaCount == 10) {
			result += "A";
			
		} else if (vivriaCount == 11) {
			result += "B";
			
		} else if (1 <= vivriaCount && vivriaCount <= 9) {
			result += vivriaCount;
		
		} else {
			result += "?";
		}
		
		return result;
	}
	

	public boolean isCanMove() {
		return canMove;
	}

	
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	
	public int getGamerIndex() {
		return gamerIndex;
	}

	
	public void setGamerIndex(int gamerIndex) {
		this.gamerIndex = gamerIndex;
	}

	
	public int getVivriaCount() {
		return vivriaCount;
	}

	
	public void setVivriaCount(int vivriaCount) {
		this.vivriaCount = vivriaCount;
	}


	public boolean isKingVivria() {
		return kingVivria;
	}


	public void setKingVivria(boolean kingVivria) {
		this.kingVivria = kingVivria;
	}
}