# MazeGenerate
迷宫生成，可以生成 2000 * 2000 以内的迷宫，耗时20秒左右，目前只支持打印到控制台或者 txt 文本，不支持图形化。

具体实现原理，可以看我的博客：https://blog.csdn.net/sinat_34067387/article/details/108007434

# 使用范例
1.引入jar包
```xml
<dependency>
	<groupId>com.github.zwluo</groupId>
	<artifactId>MazeGenerate</artifactId>
	<version>1.0.1</version>
</dependency>
```
2.测试代码
```java
import com.github.zwluo.MazeGenerate;

@Test
public void testMaze() {
	// 初始化迷宫,指定阶数
	MazeGenerate mazeGenerate = new MazeGenerate(10);
	// 打印迷宫到控制台
	mazeGenerate.printToConsole();
	// 输出迷宫到Txt文本
	mazeGenerate.saveToText("d:" + File.separator + "11.txt");
}
```
3.15 * 15 迷宫打印效果
```
 _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ 
   _| |_   _|  _  | |      _  |
| | |  _  |    _|_| |_| | |_  |
|  _ _| |_ _|_ _ _  |  _|_|   |
|_|  _ _|_ _ _ _          |_| |
| |    _|   |_   _|_| |_| | |_|
|_ _| |  _|_ _ _  |_  |_ _|  _|
|  _ _| |_ _  | |_ _|  _ _   _|
|     |  _   _| |  _   _ _|  _|
|_| |  _| |  _|    _|_ _ _|_  |
| | |_ _ _|_| |_| |  _ _ _| |_|
| |_ _   _|_ _   _|_     _| | |
|_  |   | |_ _   _ _| | |_    |
|_    |_|  _|_   _  |_| |  _| |
| |_|   |  _| |_|  _|  _ _  | |
|_ _ _|_ _|_ _ _ _ _ _|_ _ _|_ 

```
