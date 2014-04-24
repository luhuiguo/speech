package com.luhuiguo.speech;

public class SpeechException extends Exception {

	private static final long serialVersionUID = -5903203640397094814L;

	private int errorCode;

	public SpeechException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		return "SpeechException [errorCode=" + errorCode + ", message="
				+ getMessage() + "]";
	}
	
	

}
