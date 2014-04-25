package com.luhuiguo.speech;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface MscLibrary extends Library {
	MscLibrary INSTANCE = Platform.is64Bit() ? (MscLibrary) Native.loadLibrary(
			"msc64", MscLibrary.class) : (MscLibrary) Native.loadLibrary(
			"msc32", MscLibrary.class);

	String QTTSSessionBegin(String params, IntByReference errorCode);
	String QTTSSessionBeginW(String params, IntByReference errorCode);
	
	int QTTSTextPut(String sessionID, String textString, int textLen,
			String params);
	
	int QTTSTextPutW(String sessionID, String textString, int textLen,
			String params);

	Pointer QTTSAudioGet(String sessionID, IntByReference audioLen,
			IntByReference synthStatus, IntByReference errorCode);

	String QTTSAudioInfo(String sessionID);
	
	String QTTSAudioInfoW(String sessionID);

	int QTTSSessionEnd(String sessionID, String hints);
	
	int QTTSSessionEndW(String sessionID, String hints);

	int QTTSGetParam(String sessionID, String paramName, String paramValue,
			IntByReference valueLen);
	
	int QTTSGetParamW(String sessionID, String paramName, String paramValue,
			IntByReference valueLen);

	int QTTSSynthToFile(String sessionID, String text, int type,
			String waveFile, String params);
	
	int QTTSSynthToFileW(String sessionID, String text, int type,
			String waveFile, String params);

	int QTTSInit(String configs);
	int QTTSInitW(String configs);

	int QTTSFini();

	int QTTSLogEvent(String sessionID, String event, String value);
	int QTTSLogEventW(String sessionID, String event, String value);
}
