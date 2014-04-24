package com.luhuiguo.speech;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface AmrLibrary extends Library {

	AmrLibrary INSTANCE = (AmrLibrary) Native.loadLibrary("opencore-amrnb",
			AmrLibrary.class);

	Pointer Decoder_Interface_init();

	void Decoder_Interface_exit(Pointer state);

	void Decoder_Interface_Decode(Pointer state, byte[] in, short[] out, int bfi);

	Pointer Encoder_Interface_init(int dtx);

	void Encoder_Interface_exit(Pointer state);

	int Encoder_Interface_Encode(Pointer state, Mode mode, short[] speech,
			byte[] out, int forceSpeech);

}
