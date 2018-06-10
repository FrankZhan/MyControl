package com.frank.mycontrol;
/*
记录编码
开始码+用户码+数据码+数据反码+连续码
开始码：9000+4500
用户码：00FF
数据码需要逆序
连续码：9000+2250+2250+94000
NEC编码详解：https://wenku.baidu.com/view/76f27849fe4733687e21aaf1.html
 */
public class CodeCommand {

    //编码规则
    //起始码S电平宽度为：9000us低电平+4500us高电平
    public static final int startdown = 9000;
    public static final int startup = 4500;

    // 结束码
    public static final int enddown = 560;
    public static final int endup = 20000;

    //连接码C电平宽度为：9000+2250+2250+9400
    public static final int connectdown = 9000;
    public static final int connectup = 2250;
    public static final int connectend = 94000;

    //数据码由0，1组成：
    //0的电平宽度为：600us低电平+600us高电平，
    public static final int zerodown = 600;
    public static final int zeroup = 600;

    //1的电平宽度为：600us低电平+1600us高电平
    public static final int onedown = 600;
    public static final int oneup = 1600;

    //命令格式（数组内的数值拼接）

    //方案1：客户码：0X00ff 数据码：0X0C, 逆序编码 没有结束码，增加连续码
    // 编码1：投放  客户码：0X00ff 数据码：0X0C, 逆序编码
    public static final int[] start = {
            startdown, startup,
            //用户码
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            //数据码
            zerodown, zeroup, zerodown, zeroup, onedown, oneup, onedown, oneup, //0011
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            //数据反码
            onedown, oneup, onedown, oneup, zerodown, zeroup, zerodown, zeroup, //1100
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            //连续码
            connectdown, connectup, connectup, connectend,
            connectdown, connectup, connectup, connectend
    };


    // 编码2：回收  客户码：0X00ff 数据码：0X18
    public static final int[] stop = {
            startdown, startup,
            //用户码
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            //数据码
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, onedown, oneup, //0001
            onedown, oneup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //1000
            //数据反码
            onedown, oneup, onedown, oneup, onedown, oneup, zerodown, zeroup, //1110
            zerodown, zeroup, onedown, oneup, onedown, oneup, onedown, oneup, //0111
            //连续码
            connectdown, connectup, connectup, connectend,
            connectdown, connectup, connectup, connectend
    };

    // 编码3：暂停  客户码：0X00ff 数据码：0X5E
    public static final int[] pause = {
            startdown, startup,
            //用户码
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //0000
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            onedown, oneup, onedown, oneup, onedown, oneup, onedown, oneup, //1111
            //数据码
            zerodown, zeroup, onedown, oneup, onedown, oneup, onedown, oneup, //0111
            onedown, oneup, zerodown, zeroup, onedown, oneup, zerodown, zeroup, //1010
            //数据反码
            onedown, oneup, zerodown, zeroup, zerodown, zeroup, zerodown, zeroup, //1000
            zerodown, zeroup, onedown, oneup, zerodown, zeroup, onedown, oneup, //0101
            //连续码
            connectdown, connectup, connectup, connectend,
            connectdown, connectup, connectup, connectend
    };
}
