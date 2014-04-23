package com.luhuiguo.speech;

import com.iflytek.msc.MSC;
import com.iflytek.msc.MSCSessionInfo;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		String configs = "appid=52bee2bd";
		int ret = MSC.QTTSInit(configs.getBytes());
		if (0 != ret) {
			System.out.println("QTTSInit failed, error code is " + ret);
			return;
		}
		String synth_params = "ssm=1,auf=audio/L16;rate=16000,vcn=xiaoyu,tte=UTF8";
		MSCSessionInfo info = new MSCSessionInfo();
		char[] session_id = MSC.QTTSSessionBegin(synth_params.getBytes(), info);
		if (0 != info.errorcode) {
			System.out.println("QTTSSessionBegin failed, error code is "
					+ info.errorcode);
			return;
		}
		/* 写入合成文本 */
		String synth_text = "语音合成测试";
		ret = MSC.QTTSTextPut(session_id, synth_text.getBytes());
		if (0 != ret) {
			System.out.println("QTTSTextPut failed, error code is " + ret);
			return;
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
		// System.out.println(info.errorcode);

		// while(result == null){
		// result = MSC.QTTSAudioGet(session_id, info);
		// System.out.println(result);
		// }

	}
}
