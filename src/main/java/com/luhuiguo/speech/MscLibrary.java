package com.luhuiguo.speech;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface MscLibrary extends Library {
	MscLibrary INSTANCE = (MscLibrary) Native.loadLibrary(
			Platform.is64Bit() ? "msc64" : "msc32", MscLibrary.class);

	String QTTSSessionBegin(String params, IntByReference errorCode);

	WString QTTSSessionBeginW(WString params, IntByReference errorCode);

	int QTTSTextPut(String sessionID, String textString, int textLen,
			String params);

	int QTTSTextPutW(WString sessionID, WString textString, int textLen,
			WString params);

	Pointer QTTSAudioGet(String sessionID, IntByReference audioLen,
			IntByReference synthStatus, IntByReference errorCode);

	Pointer QTTSAudioGetW(WString sessionID, IntByReference audioLen,
			IntByReference synthStatus, IntByReference errorCode);

	String QTTSAudioInfo(String sessionID);

	WString QTTSAudioInfoW(WString sessionID);

	int QTTSSessionEnd(String sessionID, String hints);

	int QTTSSessionEndW(WString sessionID, WString hints);

	int QTTSGetParam(String sessionID, String paramName, String paramValue,
			IntByReference valueLen);

	int QTTSGetParamW(WString sessionID, WString paramName, WString paramValue,
			IntByReference valueLen);

	int QTTSSynthToFile(String sessionID, String text, int type,
			String waveFile, String params);

	int QTTSSynthToFileW(WString sessionID, WString text, int type,
			WString waveFile, WString params);

	int QTTSInit(String configs);

	int QTTSInitW(WString configs);

	int QTTSFini();

	int QTTSLogEvent(String sessionID, String event, String value);

	int QTTSLogEventW(WString sessionID, WString event, WString value);

	String QISRSessionBegin(String grammarList, String params,
			IntByReference errorCode);

	WString QISRSessionBeginW(WString grammarList, WString params,
			IntByReference errorCode);

	int QISRGrammarActivate(String sessionID, String grammar, String type,
			int weight);

	int QISRGrammarActivateW(WString sessionID, WString grammar, WString type,
			int weight);

	int QISRAudioWrite(String sessionID, Pointer waveData, int waveLen,
			int audioStatus, IntByReference epStatus, IntByReference recogStatus);

	int QISRAudioWriteW(WString sessionID, Pointer waveData, int waveLen,
			int audioStatus, IntByReference epStatus, IntByReference recogStatus);

	String QISRGetResult(String sessionID, IntByReference rsltStatus,
			int waitTime, IntByReference errorCode);

	String QISRGetResultW(WString sessionID, IntByReference rsltStatus,
			int waitTime, IntByReference errorCode);

	String QISRUploadData(String sessionID, String dataName, Pointer data,
			int dataLen, String params, IntByReference errorCode);

	WString QISRUploadDataW(WString sessionID, WString dataName, Pointer data,
			int dataLen, WString params, IntByReference errorCode);

	int QISRDownloadData(String sessionID, String dataName,
			PointerByReference data, IntByReference dataLen);

	int QISRDownloadDataW(WString sessionID, WString dataName,
			PointerByReference data, IntByReference dataLen);

	int QISRSessionEnd(String sessionID, String hints);

	int QISRSessionEndW(WString sessionID, WString hints);

	int QISRGetParam(String sessionID, String paramName, String paramValue,
			IntByReference valueLen);

	int QISRGetParamW(WString sessionID, WString paramName, WString paramValue,
			IntByReference valueLen);

	String QISRWaveformRecog(String sessionID, String waveFile, String waveFmt,
			String grammarList, String params, IntByReference recogStatus,
			IntByReference result);

	String QISRWaveformRecogW(WString sessionID, WString waveFile,
			WString waveFmt, WString grammarList, WString params,
			IntByReference recogStatus, IntByReference result);

	int QISRInit(String configs);

	int QISRInitW(WString configs);

	int QISRFini();

	int QISRLogEvent(String sessionID, String event, String value);

	int QISRLogEventW(WString sessionID, WString event, WString value);

	String QISRGetSessionParams(String sessionID, IntByReference errorCode);
}
