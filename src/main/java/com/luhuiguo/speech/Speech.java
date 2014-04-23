package com.luhuiguo.speech;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.iflytek.msc.MSC;
import com.iflytek.msc.MSCSessionInfo;

public class Speech {
	
	private static String appid;
	
	static { 
        Properties prop = new Properties(); 
        InputStream in = Speech.class.getResourceAsStream("/config.properties"); 
        try { 
            prop.load(in); 
            appid = prop.getProperty("appid").trim(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 
	
	
	public static int tts(String text, String filename){

		String configs = "appid="+appid;
		int ret = MSC.QTTSInit(configs.getBytes());
		if (0 != ret) {
			System.out.println("QTTSInit failed, error code is " + ret);
			return ret;
		}
		String synth_params = "ssm=1,auf=audio/L16;rate=16000,vcn=xiaoyu,tte=UTF8";
		MSCSessionInfo info = new MSCSessionInfo();
		char[] session_id = MSC.QTTSSessionBegin(synth_params.getBytes(), info);
		if (0 != info.errorcode) {
			System.out.println("QTTSSessionBegin failed, error code is "
					+ info.errorcode);
			return info.errorcode;
		}
		/* 写入合成文本 */
		String synth_text = "语音合成测试";
		ret = MSC.QTTSTextPut(session_id, synth_text.getBytes());
		if (0 != ret) {
			System.out.println("QTTSTextPut failed, error code is " + ret);
			return ret;
		}
		while (true) {
			byte[] result = MSC.QTTSAudioGet(session_id, info);
			System.out.println(info);
			System.out.println(result);
			if (info.errorcode != 0) {
				System.out.println("QTTSAudioGet failed, error code is "
						+ info.errorcode);
				break;
			}

			if (info.sesstatus == 2) {
				System.out.println("QTTSAudioGet: get end of data.");
				break;
			}
		}
		/* 获取合成音频 */
		ret = MSC.QTTSSessionEnd(session_id, "Normal".getBytes());
		if (ret != 0) {
			System.out.println("QTTSSessionEnd: qtts end failed Error code "
					+ ret);
		}
		
		return 0;
	}

}
