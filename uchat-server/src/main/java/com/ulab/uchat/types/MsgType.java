package com.ulab.uchat.types;
public enum MsgType {
	Connect(0), Text(1), Picture(2), Notify(3), Select(4);
	
	private int val;
	
	private MsgType(int val) {
		this.val = val;
	}
	
	public int getVal() {
		return val;
	}
	
	public static MsgType Int2MsgType(int val) {
		switch(val) {
		case 0: return Connect;
		case 1: return Text;
		case 2: return Picture;
		case 3: return Notify;
		case 4: 
		default: 
			return Notify;
		}
	}
}
