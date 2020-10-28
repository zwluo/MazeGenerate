package com.github.zwluo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * 2000阶以下迷宫随机生成
 * 原理:
 * 1.获取所有可拆卸的墙
 * 2.从可拆卸的墙里面选择一面墙,判断该墙所对应的两个单元格是连通,不连通的话拆墙;否则随机获取下一面墙.
 * 3.重复步骤2,直到所有单元格全部连通.(生成的迷宫都是标准的,每次拆除的墙的数量都是 n^2-1 面,原因是随机取墙的次数足够多,足以覆盖所有的墙.
 * 不会出现拆除的墙少于 n^2-1 的原因是如果拆除的墙少于 n^2-1,那么肯定会有单元格是孤立的.
 * 不会出现拆除的墙多余 n^2-1 的原因是,只要满足拆的墙对应的两个单元格之前是不连通的,又拆的墙的数量为 n^2-1时,迷宫就已经生成了,不用再循环.
 * PS:测试入口test()在最下面,修改第一个变量private int size = 100;的值就可以生成不同阶数的迷宫啦.
 */
public class MazeGenerate {
    // 迷宫的大小
    private final int size;
    // 迷宫的阶数
    private final int sqrt;
    // 用于存储每个单元格所属的集合
    private final int[] array;
    // 用于存储已经合并过的单元格
    private final HashSet<Integer> allCell = new HashSet<>();

    /**
     * 设置阶数
     * @param size 阶数
     */
    public MazeGenerate(int size) {
        this.size = size * size;
        this.sqrt = size;
        this.array = new int[size * size];
    }

    /**
     * 返回 i 所在集合的最大值
     *
     * @param i 单元格编号
     * @return i 所在集合的最大值
     */
    private int find(int i) {
        int result = i;
        while (array[result] != -1) {
            result = array[result];
        }
        return result;
    }

    /**
     * 将 i 和 j 所在集合进行合并
     *
     * @param i 单元格编号
     * @param j 单元格编号
     */
    private void union(int i, int j) {
        int result1 = find(i);
        int result2 = find(j);
        if (result1 == result2){
            return;
        }
        if(result1 > result2) {
            array[result2] = result1;
            allCell.add(result2);
        }
        else {
            array[result1] = result2;
            allCell.add(result1);
        }
    }

    /**
     * 获取所有的可拆的墙
     */
    private List<Wall> getAllWalls() {
        ArrayList<Wall> allWalls = new ArrayList<>();
        // 保存下来所有的墙
        // 一共size个元素
        for (int i = 0; i < (size - 1); i++) {
            // 右边的墙
            int k = i + 1;
            // 下面的墙
            int l = i + (int) Math.sqrt(size);

            // 排除掉最右边的墙
            if ((i + 1) % ((int) Math.sqrt(size)) == 0) {
                // n-1
                allWalls.add(new Wall(i, l));
                continue;
            }

            // 排除掉最下面的墙
            if ((size - Math.sqrt(size)) <= i) {
                // n-1
                allWalls.add(new Wall(i, k));
                continue;
            }
            // (n-1)^2
            allWalls.add(new Wall(i, k));
            allWalls.add(new Wall(i, l));

        }
        return allWalls;
    }

    /**
     *随机生成迷宫
     *
     * @param data  list
     * @return List<Wall>
     */
    private Set<String> generateMaze(List<Wall> data) {
        Random random = new Random();
        // 用于存储待拆除的墙
        HashSet<String> toDelWalls = new HashSet<>();
        // 拆除首尾节点的墙
        toDelWalls.add("1,0");
        toDelWalls.add(sqrt + "," + (2 * sqrt));

        // 初始化各个单元格所属的集合
        for (int j = 0; j < size; j++) {
            array[j] = -1;
        }
        //int count = 0;
        while(isContinue()) {
            //count++;
            // 随机获取一面墙
            int wallCode = random.nextInt(data.size());
            Wall wall = data.get(wallCode);
            int firstCellCode = wall.getFirstCellCode();
            int secondCellCode = wall.getSecondCellCode();
            // 判断墙对应的两个单元格是否是连通的
            if(find(firstCellCode) != find(secondCellCode)) {
                // 如果不连通,拆除墙
                int maxIndex = data.size() - 1;
                data.set(wallCode, data.get(maxIndex));
                data.remove(maxIndex);

                // 将两个单元格连通
                union(firstCellCode, secondCellCode);
                // 添加到需要拆除的墙集合里面
                toDelWalls.add(wall.getCoordinate());
            }
        }
        //System.out.println("count: " + count);
        return toDelWalls;
    }

    /**
     * 判断是否需要继续拆墙
     * @return 是否继续拆墙
     */
    private boolean isContinue() {
        // 第一个单元格和最后一个单元格是否连通
        if(find(0) != (size - 1)) {
            return true;
        }

        // 是否所有的单元格都连通
        return allCell.size() < (size - 1);
    }

    /**
     * 打印迷宫到控制台
     * @param toDelWalls 带拆除墙集合
     */
    private void printMaze(Set<String> toDelWalls) {
        for(int i=0; i<(sqrt+1); i++) {      // 行
            for(int j=0; j<(2*sqrt+1); j++) {    // 列
                String toPrintStr = getToPrintStr(i, j, toDelWalls);
                System.out.print(toPrintStr);
            }
            System.out.println();
        }
    }

    /**
     * 将迷宫保存到TXT文本中
     * @param toDelWalls 待删除的墙
     * @param fileName 保存文件路径
     */
    private void saveToText(Set<String> toDelWalls, String fileName) {
        File file = new File(fileName);
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file, true));
            StringBuilder builder = new StringBuilder();

            for(int i=0; i<(sqrt+1); i++) {      // 行
                for(int j=0; j<(2*sqrt+1); j++) {    // 列
                    String toPrintStr = getToPrintStr(i, j, toDelWalls);
                    builder.append(toPrintStr);
                }
                builder.append("\r\n");
            }
            writer.write(builder.toString());
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取打印字符串
     * @param i 横坐标
     * @param j 纵坐标
     * @param toDelWalls 待拆除的墙的集合
     * @return 下一个打印的字符串
     */
    private String getToPrintStr(int i, int j, Set<String> toDelWalls) {
        String toPrintStr;
        String temp = i + "," + j;
        if(i % 2 == 0) {    // 奇数行
            if(j % 2 == 0) {    //奇数列
                if(i == 0) {    // 第一行
                    toPrintStr = " ";
                }
                else {
                    toPrintStr = "|";
                }
            }
            else {              // 偶数列
                toPrintStr = "_";
            }
        }
        else {                  // 偶数行
            if(j % 2 == 0) {    //奇数列
                toPrintStr = "|";
            }
            else {              // 偶数列
                toPrintStr = "_";
            }
        }
        if(toDelWalls.contains(temp)) {
            toPrintStr = " ";
            toDelWalls.remove(temp);
        }
        return toPrintStr;
    }

    /**
     * 保存关于墙所有的信息
     */
    private class Wall {
        // 墙对应的第一个单元格
        private final int firstCellCode;
        // 墙对应的第二个单元格
        private final int secondCellCode;
        // x,y组成的坐标系
        private final String coordinate;

        public Wall(int firstCellCode, int secondCellCode) {
            this.firstCellCode = firstCellCode;
            this.secondCellCode = secondCellCode;
            // 纵坐标
            int y = 0;
            if (sqrt == (secondCellCode - firstCellCode)) {
                y = (secondCellCode % sqrt) * 2 + 1;
            } else if (1 == (secondCellCode - firstCellCode)) {
                y = (secondCellCode % sqrt) * 2;
            }
            // 横坐标
            int x = firstCellCode / sqrt + 1;

            this.coordinate = x + "," + y;
        }

        private int getFirstCellCode() {
            return firstCellCode;
        }

        private int getSecondCellCode() {
            return secondCellCode;
        }
        private String getCoordinate() {
            return coordinate;
        }
    }

    /**
     * 打印到控制台
     */
    public void printToConsole() {
        Set<String> toDelWalls = getDeletedWalls();

        // 3.打印迷宫到控制台
        printMaze(toDelWalls);
    }

    /**
     * 保存到指定目录
     * @param savePath 指定目录
     */
    public void saveToText(String savePath) {
        Set<String> toDelWalls = getDeletedWalls();

        // 3.输出迷宫到TXT文本
        if (savePath != null && !"".equals(savePath)) {
            saveToText(toDelWalls, savePath);
        }

    }

    /**
     * 获取拆除掉的墙
     * @return 拆除掉的墙的集合
     */
    private Set<String> getDeletedWalls() {
        //long startTime = System.currentTimeMillis();
        // 1.获取所有可拆卸的墙的信息
        List<Wall> allWalls = getAllWalls();

        //long getWallTime = System.currentTimeMillis();
        //System.out.println("getWallTime: " + (getWallTime - startTime) + "ms");

        // 2.随机生成迷宫,并记录需要被拆除的墙
        return generateMaze(allWalls);

        //long generateMazeTime = System.currentTimeMillis();
        //System.out.println("generateMazeTime: " + (generateMazeTime - getWallTime) + "ms");
    }

    public static void main(String[] args) {

    }

}
