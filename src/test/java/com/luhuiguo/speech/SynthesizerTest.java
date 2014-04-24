package com.luhuiguo.speech;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SynthesizerTest {

	@Before
	public void setUp() throws Exception {
		Synthesizer.initialization();
	}

	@After
	public void tearDown() throws Exception {
		Synthesizer.finalization();
	}

	@Test
	public void test() {
		int ret = Synthesizer.textToAmr("我在测试语音合成", "test.amr", null);
		assertEquals(0, ret);
	}

}
