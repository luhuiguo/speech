语音合成
=============

语音合成 AMR（Adaptive multi-Rate）文件

使用
--------------------
### 编译安装 opencore-amr

```
tar zxf opencore-amr-0.1.3.tar.gz
cd opencore-amr-0.1.3
./configure
make install
```

### 下载讯飞语音 Java SDK

复制 libmsc32.so 和 libmsc64.so 到 /usr/local/lib
并修改 src/main/config.properties
```
appId=你申请的讯飞语音云的APP_ID
amrMode=MR122
synthesizeParams=ssm=1,auf=audio/L16;rate=8000,vcn=xiaoyu,tte=UTF8
```

### 设置 LD_LIBRARY_PATH

```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
```

### 编译工程

```
mvn clean package
```


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
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
