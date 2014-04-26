语音合成
=============

语音合成 AMR（Adaptive multi-Rate）文件

使用
--------------------

### 命令行

```
java -jar speech.jar 文本 AMR文件名 [参数]
```

### Example

```java
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
		int ret = Synthesizer.textToAmr("我在测试语音合成", "target/test.amr", null);
		assertEquals(0, ret);
	}

}

```

### Maven依赖
```xml
<dependency>
    <groupId>com.luhuiguo.speech</groupId>
    <artifactId>speech</artifactId>
    <version>0.1.0</version>
</dependency>
```
