package com.luhuiguo.speech;

import static com.iflytek.msc.MSC.QTTSAudioGet;
import static com.iflytek.msc.MSC.QTTSFini;
import static com.iflytek.msc.MSC.QTTSInit;
import static com.iflytek.msc.MSC.QTTSSessionBegin;
import static com.iflytek.msc.MSC.QTTSSessionEnd;
import static com.iflytek.msc.MSC.QTTSTextPut;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.isExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.msc.MSCSessionInfo;

public class Synthesizer {

	public static final String AMR_MAGIC_NUMBER = "#!AMR\n";

	public static final int PCM_FRAME_SIZE = 160; // 8khz 8000*0.02=160
	public static final int MAX_AMR_FRAME_SIZE = 32;
	public static final int AMR_FRAME_COUNT_PER_SECOND = 50;

	public static final String CONFIG_FILE = "/config.properties";
	public static final String APP_ID = "appId";
	public static final String SYNTHESIZE_PARAMS = "synthesizeParams";
	public static final String AMR_MODE = "amrMode";

	public static final String PCM_EXTENSION = "pcm";
	public static final String AMR_EXTENSION = "amr";

	private static String appId;
	private static String synthesizeParams;
	private static Mode amrMode;

	private static final Logger logger = LoggerFactory
			.getLogger(Synthesizer.class);

	static {
		Properties prop = new Properties();
		InputStream in = Synthesizer.class.getResourceAsStream(CONFIG_FILE);
		try {
			prop.load(in);
			appId = prop.getProperty(APP_ID).trim();
			synthesizeParams = prop.getProperty(SYNTHESIZE_PARAMS).trim();
			amrMode = Mode.valueOf(prop.getProperty(AMR_MODE).trim());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int initialization() {
		String configs = "appid=" + appId;
		logger.info("QTTSInit, appid is {}", appId);
		int ret = QTTSInit(configs.getBytes());
		if (0 != ret) {
			logger.error("QTTSInit failed, error code is {}", ret);
		}
		return ret;
	}

	public static int finalization() {

		logger.info("QTTSFini");
		int ret = QTTSFini();
		if (0 != ret) {
			logger.error("QTTSFini failed, error code is {}", ret);
		}
		return ret;
	}

	public static int textToAmr(String text, String filename, String params) {

		if (!isExtension(filename, AMR_EXTENSION)) {
			return -1;
		}
		String pcmFilename = removeExtension(filename) + EXTENSION_SEPARATOR
				+ PCM_EXTENSION;
		File amrFile = getFile(filename);
		deleteQuietly(amrFile);
		File pcmFile = getFile(pcmFilename);
		deleteQuietly(pcmFile);
		logger.info("\nTEXT: {} \n>>>>>\n PCM: {}", text, pcmFile.getAbsolutePath());
		int ret = textToPcm(text, pcmFile, params);
		if (0 != ret) {
			logger.error("textToPcm failed, error code is {}", ret);
			return ret;
		}
		logger.info("\nPCM: {} \n>>>>>\n AMR: {}", pcmFile.getAbsolutePath(), amrFile.getAbsolutePath());
		ret = pcmToAmr(pcmFile, amrFile);
		if (0 != ret) {
			logger.error("pcmToAmr failed, error code is {}", ret);
		}
		return ret;

	}

	public static int textToPcm(String text, File file, String params) {

		String synthParams = synthesizeParams;
		if (null != params) {
			synthParams = params;
		}
		MSCSessionInfo info = new MSCSessionInfo();
		logger.info("QTTSSessionBegin, params is {}", synthParams);
		char[] sessionId = QTTSSessionBegin(synthParams.getBytes(), info);
		int ret = info.errorcode;
		if (0 != ret) {
			logger.error("QTTSSessionBegin failed, error code is {}", ret);
			return ret;
		}
		logger.info("QTTSSession: {}", new String(sessionId));
		logger.info("QTTSTextPut, text is {}", text);
		ret = QTTSTextPut(sessionId, text.getBytes());
		if (0 != ret) {
			logger.error("QTTSTextPut failed, error code is {}", ret);
			QTTSSessionEnd(sessionId, "TextPutError".getBytes());
			return ret;
		}

		/* 获取合成音频 */
		while (true) {
			byte[] result = QTTSAudioGet(sessionId, info);
			//logger.info("{}",result);
			ret = info.errorcode;
			if (0 != ret) {
				logger.error("QTTSAudioGet failed, error code is {}", ret);
				break;
			}
			try {
				writeByteArrayToFile(file, result, true);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				break;
			}
			if (2 == info.sesstatus) {
				logger.info("QTTSAudioGet: get end of data.");
				break;
			}
		}
		logger.info("QTTSSessionEnd");
		ret = QTTSSessionEnd(sessionId, "Normal".getBytes());
		if (0 != ret) {
			logger.error("QTTSSessionEnd failed, error code is {}" + ret);
		}

		return ret;

	}

	public static int pcmToAmr(File pcmFile, File amrFile) {

		return 0;

	}

}
