package com.luhuiguo.speech;

import static java.util.Arrays.copyOf;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.write;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR;
import static org.apache.commons.io.FilenameUtils.isExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

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
		int ret = MscLibrary.INSTANCE.QTTSInit(configs);
		if (0 != ret) {
			logger.error("QTTSInit failed, error code is {}", ret);
		}
		return ret;
	}

	public static int finalization() {

		logger.info("QTTSFini");
		int ret = MscLibrary.INSTANCE.QTTSFini();
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
		logger.info("\nTEXT: {} \n>>>>>\n PCM: {}", text,
				pcmFile.getAbsolutePath());
		int ret = textToPcm(text, pcmFile, params);
		if (0 != ret) {
			logger.error("textToPcm failed, error code is {}", ret);
			return ret;
		}
		logger.info("\nPCM: {} \n>>>>>\n AMR: {}", pcmFile.getAbsolutePath(),
				amrFile.getAbsolutePath());
		ret = pcmToAmr(pcmFile, amrFile);
		if (ret <= 0) {
			logger.error("pcmToAmr failed, error code is {}", ret);
			return -1;
		}
		return 0;

	}

	public static int textToPcm(String text, File file, String params) {

		String synthParams = synthesizeParams;
		if (null != params) {
			synthParams = params;
		}
		logger.info("QTTSSessionBegin, params is {}", synthParams);
		IntByReference errorCode = new IntByReference();
		String sessionID = MscLibrary.INSTANCE.QTTSSessionBegin(synthParams,
				errorCode);
		int ret = errorCode.getValue();
		if (0 != ret) {
			logger.error("QTTSSessionBegin failed, error code is {}", ret);
			return ret;
		}
		logger.info("QTTSSession: {}", sessionID);
		logger.info("QTTSTextPut, text is {}", text);
		ret = MscLibrary.INSTANCE.QTTSTextPut(sessionID, text, text.getBytes().length,
				null);
		if (0 != ret) {
			logger.error("QTTSTextPut failed, error code is {}", ret);
			MscLibrary.INSTANCE.QTTSSessionEnd(sessionID, "TextPutError");
			return ret;
		}

		/* 获取合成音频 */
		while (true) {
			IntByReference audioLen = new IntByReference();
			IntByReference synthStatus = new IntByReference();
			Pointer result = MscLibrary.INSTANCE.QTTSAudioGet(sessionID,
					audioLen, synthStatus, errorCode);
			ret = errorCode.getValue();
			if (0 != ret) {
				logger.error("QTTSAudioGet failed, error code is {}", ret);
				break;
			}
			try {
				int len = audioLen.getValue();
				logger.info("audioLen: {}", len);
				byte[] data = result.getByteArray(0, len);

				writeByteArrayToFile(file, data, true);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				break;
			}
			if (2 == synthStatus.getValue()) {
				logger.info("QTTSAudioGet: get end of data.");
				break;
			}
		}
		logger.info("QTTSSessionEnd");
		ret = MscLibrary.INSTANCE.QTTSSessionEnd(sessionID, "Normal");
		if (0 != ret) {
			logger.error("QTTSSessionEnd failed, error code is {}" + ret);
		}

		return ret;

	}

	public static int pcmToAmr(File pcmFile, File amrFile) {

		int dtx = 0;
		int count = 0;
		int frames = 0;
		int bytes = 0;

		try {
			write(amrFile, AMR_MAGIC_NUMBER);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}

		Pointer amr = AmrLibrary.INSTANCE.Encoder_Interface_init(dtx);
		short[] speech = null;

		byte[] amrFrame = new byte[MAX_AMR_FRAME_SIZE];

		try {
			FileInputStream in = new FileInputStream(pcmFile);
			while (null != (speech = readFrame(in))) {
				frames++;
				count = AmrLibrary.INSTANCE.Encoder_Interface_Encode(amr,
						amrMode.ordinal(), speech, amrFrame, 0);
				bytes += count;

				logger.debug("frame: {} bytes: {}", frames, bytes);

				try {
					writeByteArrayToFile(amrFile,
							amrFrame = copyOf(amrFrame, count), true);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					break;
				}

			}
			in.close();

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}

		AmrLibrary.INSTANCE.Encoder_Interface_exit(amr);

		return frames;

	}

	public static short[] readFrame(InputStream in) {
		byte[] buf = new byte[PCM_FRAME_SIZE * 2];
		short[] pcmFrame = new short[PCM_FRAME_SIZE];
		int count = 0;
		try {
			count = in.read(buf);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		for (int i = 0; i < PCM_FRAME_SIZE; i++) {
			pcmFrame[i] = ((short) ((buf[i * 2] & 0xff) | (buf[i * 2 + 1] << 8)));
		}
		if (count < PCM_FRAME_SIZE * 2) {
			return null;
		}

		return pcmFrame;
	}

	public static void main(String[] args) {

		if (args.length < 2) {

			System.out
					.println("usage: java -jar speech[-version].jar text filename [params] ");

		} else {

			String text = args[0];
			String filename = args[1];
			String params = null;
			if (args.length > 2) {
				params = args[2];
			}

			Synthesizer.initialization();

			Synthesizer.textToAmr(text, filename, params);

			Synthesizer.finalization();
		}

	}
}
