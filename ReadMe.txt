应用说明模板[v0.3]：
    工程名:xxx
       平台项目名:xxx
       配套项目名:xxx
       基线:xxx[xxxx]
       客户名：xxx
       创建人：xxxx
       创建时间：xxxx
       应用用途说明：xxxx
       基本情况:xxxxx


应用说明模板设定说明[v0.3的部分项]：
      工程名[命名建议]:
            01 非硬件耦合工程[通用]：
                01.1 用途_客户 ， 例如：BeidouApp_ZB
                01.1 用途_类型 ， 例如：Wristband_SDK

            02 硬件耦合工程[专用]：
                02.1 硬件平台_用途      ， 例如：W135B_BeidouApp
                02.2 硬件平台_用途_客户 ， 例如：W135B_BeidouApp_DZ
                02.3 硬件平台_用途_类型 ， 例如：W135B_CorseRTCM_SDK
            03  命名易混淆点说明[部分]
                03.1 关于01.2和01.3各自第三项用途易混淆说明：由于历史遗留原因无法变动。 防止出现团队协作出错，
                     请在新建应用说明时严格填写相关项
       客户名：APP所属项目对应的客户
       平台项目名:APP专属运行项目
       配套项目名:APP控制的设备的项目名,没有的话请删除此项
       基线:基于已存在本git仓库内的APP1创建APP2，APP1就是基线，没有的话请删除此项。基线说明中的中括号里面为基线当时的git commit的hash值
       
       
==========================================================================================================================================================

应用说明：

    工程名:KU09_Modules
       平台项目名:KU09
       客户名：安全帽
       创建人：吴国献<wugx@kutechs.com>
       基线:
            01 KU09_808ProtocolClient [16c1ecaa6c011ab3f27eee4a8693f24ff0d18993]
            02 KU09_OpenLive [16c1ecaa6c011ab3f27eee4a8693f24ff0d18993]
            03 KU09_USCDemo [16c1ecaa6c011ab3f27eee4a8693f24ff0d18993]
            04 KU09_USCDemo_TTS [16c1ecaa6c011ab3f27eee4a8693f24ff0d18993]
       应用用途说明：云之声语音控制,云之声TTS合成,808客户端,声网推流SDK实现视频推送的整合工程
       基本情况:
            01 工程集成了四个模块,对应四个独立的APP
            02 批量生成4个APP安装包方法:
                02.1 执行脚本: KU09_Modules > Tasks > other > assembleRelease
                02.2 生成APK的路径: KU09_Modules/xxx/build/outputs/apk/release/xxx.apk
            02 808等相关模块的协议文档：北斗终端数据协议20201028



    工程名:KU_Common 
       创建人：吴国献<wugx@kutechs.com>
       基线:KU19_NFC_RFID [efd5269]的common模块
       应用用途说明：
            01 APP间通用库
       基本情况:
            01 串口操作的抽象
            02 串口数据校验器和编解码器的抽象
          


    工程名:Jt808
       平台项目名:KU09 / 其他
       创建人：吴国献<wugx@kutechs.com>
       基线:无
       应用用途说明：
            01 Jt808/client : Android平台的实现Jt808协议的客户端
            02 Jt808/server : Web平台Spring框架下的Jt808协议服务器


