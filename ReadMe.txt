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

Android Studio 秘钥说明
     01 释放release需要的秘钥和相关密码配置存放路径：smb://10.168.1.253/f$/smart/tools/app/android_studio_keys

==========================================================================================================================================================

应用说明：
    AutoScan
       平台项目名:W170
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:W170用扫码设置APP

    工程名:BeaconScanner
       平台项目名:W101
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:蓝牙Beacon报警器，连接蓝牙Beacon后，超出一定距离就会报警

    工程名:BeidouApp_Serial_ZB
       平台项目名:W171
       创建人：吕延兵<lvyanbing@sxtech.com>
       需求客户:中兵
       基本情况:连接外置北斗设备的北斗应用(旧)

    工程名:W171_BeidouApp_Serial_ZB
       平台项目名:W171
       配套项目名:W190
       创建人：吴国献<wugx@kutechs.com>
       基线:BeidouApp_Serial_ZB
       旧工程名:BeidouApp_Serial_ZB_W171       
       需求客户:安徽客户
       基本情况:
        01 中兵用的连接外置北斗设备的北斗应用[W190+W171显控],UI从 W135/apps/packages/apps/BeidouApp移植
        02 协议文档：星宇芯联 BDS&GPS 双模定位通信模块外设数据接口规范(V1.03).doc
        03 APP和W190之间的频度判断未同步,所以报文发送存在10到40秒的延迟,暂时无解

    工程名:BeidouApp_ZB
       平台项目名:W135
       创建人：吕延兵<lvyanbing@sxtech.com>
       需求客户:中兵
       基本情况:北斗应用

    工程名:BeidouPartner
       配套项目名:W98
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:北斗伴侣

    工程名:BLECorseApp
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:通用的蓝牙Corse站APP

    工程名:BLETianTong
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:蓝牙天通背夹配套的APP

    工程名:BluetoothBDMessage
       配套项目名:W98
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:早期的蓝牙北斗短报文

    工程名:CorseRTCM
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:基于Ntrip协议的差分定位APP

    工程名:EncryptBeidouAPK
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:加密北斗短报文

    工程名:EncryptBeidouAPK[eclipse]
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:加密北斗短报文,eclipse格式的工程

    工程名:FJXHCommunication、Library_PullToRefreshSwipeMenuListView
       平台项目名:W97
       创建人：吕延兵<lvyanbing@sxtech.com>
       需求客户:福建星海
       基本情况:北斗应急通信

    工程名:PresetDeyiAisFileApp
       平台项目名:W158[AIS] / W101[AIS]
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:预置AIS配置文件的APP

    工程名:PresetRailwayFileApp
       平台项目名:W126
       创建人：吕延兵<lvyanbing@sxtech.com>
       需求客户:成都智云
       基本情况:铁路APP

    工程名:ReportedLocaion
       平台项目名:W175
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:通用的位置上报APP

    工程名:RtcmDemo、RtcmDemoService
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:基于千寻平台的差分定位DEMO APP

    工程名:Tianditong
       平台项目名:W187
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:天地通用户版

    工程名:TiandiTong_Administrator
       平台项目名:W187
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:天地通管理员版

    工程名:TiandiTong_Administrator_BD
       配套项目名:D20
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:天地通管理员+北斗短信版

    工程名:TT_MmsToGateway
       平台项目名:W175
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:天通短信网关

    工程名:TT_SafeMessage
       平台项目名:W175
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:天通报平安

    工程名:TT_Sos
       平台项目名:W175
       创建人：卢大旭<ldx@sxtechs.com>
       基本情况:天通SOS

    工程名:W101R_TwoWayRadio
       平台项目名:W101R
       创建人：吕延兵<lvyanbing@sxtech.com>
       旧工程名：TwoWayRadio_W101R
       基本情况:对讲APP

    工程名:W158_TwoWayRadio
       平台项目名:W158
       创建人：吕延兵<lvyanbing@sxtech.com>
       旧工程名：TwoWayRadio_W158
       基本情况:对讲APP

    工程名:UHFRfidNanRui
       平台项目名:W101
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:高频RFID Demo 使用南瑞协议

    工程名:UHFRfidOldProtocol
       平台项目名:W101
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:高频RFID Demo 使用旧协议

    工程名:W170_kaili_scanxservice
       平台项目名:W170
       创建人：吕延兵<lvyanbing@sxtech.com>
       旧工程名：w170_kaili_scanxservice
       基本情况:Kaili 扫码服务

    工程名:W170_scanx、W170_scanxservice
       平台项目名:W170
       旧工程名：w170_scanx、w170_scanxservice
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:扫码应用

    工程名:YModem_OLD_NEW
       创建人：吕延兵<lvyanbing@sxtech.com>
       基本情况:对讲模块固件升级APP

    工程名:W135_ZhuHaiBeidou
       平台项目名:W135
       创建人：吕延兵<lvyanbing@sxtech.com>
       旧工程名：ZhuHaiBeidou_W135
       基本情况:早期珠海航展使用的北斗应用APP

    工程名:W97_EmergencyTele_FuJian_Xinghai
       平台项目名:W97
       创建人：吕延兵<lvyanbing@sxtech.com>
       旧工程名：Library_PullToRefreshSwipeMenuListView
       客户名：福建星海
       应用用途说明：应急通信
       基本情况:
            01 Eclipse项目

    工程名:UHF_RFID
       平台项目名:W181
       创建人：吴国献<wugx@kutechs.com>
       基本情况:RFID模块测试demo

    工程名:SecurityModuleDemo
       平台项目名:W196
       客户名：国网芯
       创建人：吴国献<wugx@kutechs.com>
       基本情况:安全模块基本功能测试demo

    工程名:SecurityModuleDemo_RFID
       平台项目名:W196
       客户名：国网芯
       创建人：吴国献<wugx@kutechs.com>
       基线:SecurityModuleDemo
       基本情况:RFID调试DEMO,有bug

    工程名:W171_UHF_RFID
       平台项目名:W171
       创建人：吴国献<wugx@kutechs.com>
       基线:UHF_RFID
       旧工程名：UHF_RFID_W171
       基本情况:900M RFID背夹测试demo

    工程名:W135_CorseRTCM
       平台项目名:W135
       创建人：吴国献<wugx@kutechs.com>
       应用用途说明：差分定位

    工程名:W196_R2000-Demo-UHFRFID
       平台项目名:W196
       客户名：国网芯
       创建人：吴国献<wugx@kutechs.com>
       基线:R2000-Demo-UHFRFID_20191022_OPV2
       应用用途说明：RFID客户DEMO
       基本情况:
       
    工程名:W135_Wristband
       平台项目名:W135
       客户名：蓝天救援/杭州极英
       创建人：吴国献<wugx@kutechs.com>
       基线:基于手环方提供的SDKdemo
       旧工程名:W135WristbandSDK > W135_WristbandSDK >W135_Wristband
       应用用途说明：智慧居的手环配套APP
       基本情况:
            01 只用于W135平台
            02 手环数据通过[http协议]同步到平台
            03 过滤带123456的log,确认正确获取到手环数据并自动发送后，就不需要重新连接。
            04 使用高德地图的定位SDK
       
    工程名:SmartWristband
       平台项目名:Android通用[W135除外]
       客户名：蓝天救援/杭州极英
       创建人：吴国献<wugx@kutechs.com>
       基线:W135WristbandSDK
       应用用途说明：智慧居的手环配套APP
       基本情况:
            01 由于UI存在兼容问题，所以单独建立了：W135_Wristband
            02 用于普通安卓手机
            03 手环数据通过[http协议]同步到平台
            04 过滤带123456的log,确认正确获取到手环数据并自动发送后，就不需要重新连接。
            05 使用高德地图的定位SDK


    工程名:W135_BeidouApp_ZB_Wristband
       平台项目名:W135
       客户名：蓝天救援/杭州极英
       创建人：吴国献<wugx@kutechs.com>
       基线:BeidouApp_ZB [bc8bb00ace87f08bbd363616fec85c07d2cc356b]
       旧工程名:BeidouApp_ZB_Wristband_W135
       应用用途说明：北一短报文客户端，带自动回复功能，搭配W135_Wristband
       基本情况:
            01 北1协议默认版本为：4.0
            02 北1协议的扩展协议对应文档：北斗一代自定义通讯协议
	        03 北1协议的扩展协议在代码实现上的具体版本可能会有更新，具体以代码注释为准
            04 基于[北斗一代自定义通讯协议V1.3],时间长度改为4，高位补零
            05 实现位置短报文[TXSQ]自动[65秒一次]上报到指挥机，确认和指挥机对接的平台可以正确解析


    工程名:SerialDebugTools
       平台项目名:Android通用
       创建人：吴国献<wugx@kutechs.com>
       基线:SecurityModuleDemo_RFID [8a8bbe7676a8521f89f906f84ecde6592156a36a]
       应用用途说明：串口调试工具
       基本情况:
            01 W196,W135B项目上使用过


    工程名:W135B_CorseRTCM
       平台项目名:W135B
       客户名：华晨北斗,其他
       创建人：吴国献<wugx@kutechs.com>
       基线:CorseRTCM [1dda4f3cd40687e186614e60dc71898e180527e6]
       应用用途说明：差分定位的验证demo
       基本情况:
            01 北2模块为梦芯
            02 APP启动时使用串口对应RTCM协议版本配置命令为梦芯专用
            03 命令实现在方法
            04 差分定位基本原理 :
                    04.1 开启模块的RTCM协议解析
                    04.2 将模块输出的NMEA协议的GGA相关数据通过NRTIP协议发送给差分服务器
                    04.3 将服务器返回的差分补偿参数，通过串口写入模块，模块根据RTCM协议解析参数
                    04.4 模块输出差分补偿后的定位信息
                    
    
    工程名:W135B_CorseRTCM_SDK
       平台项目名:W135B
       客户名：华晨北斗,其他
       创建人：吴国献<wugx@kutechs.com>
       基线:W135B_CorseRTCM [7917c11]
       应用用途说明：差分定位SDK
       基本情况:
            01 北2模块为梦芯,对应文档MXT Protocol Specification数据接口协议_V1.25_1211_update.pdf
            02 SDK库 生成方式说明
                02.1 点击 Android Studio的Gradlede 的脚本列表
                02.2 点开 W135B_CorseRTCMSDK > Tasks > other > arrRelease ，双击arrRelease执行脚本生成SDK库
                02.3 SDK库生成路径 kuw135brtcm/release/KU_W135B_RTCM_release_V日期.aar
           03 SDK库的使用demo为编译app模块
                    
  


    工程名:W135B_BeidouApp_DZ
       平台项目名:W135B
       客户名：地质
       创建人：吴国献<wugx@kutechs.com>
       基线:W135B_MT6755/alps/packages/apps/BeidouApp [fbc2ac6642542144d12d2eff95ef3b0cdbcf4f36]
       应用用途说明：北1的短报文APP
       基本情况:
            01 北2模块为梦芯,对应文档MXT Protocol Specification数据接口协议_V1.25_1211_update.pdf
            02 北1协议默认版本为：4.0
            03 北1协议的扩展协议对应文档：报文协议_v0.5
            04 GPRS的数据同步协议对应文档：地调协议.doc [未实现]
	        05 北1协议的扩展协议在代码实现上的具体版本可能会有更新，具体以代码注释为准



    工程名:W135B_BeidouApp
       平台项目名:W135B / KU135C[W135C]
       客户名：中性
       创建人：吴国献<wugx@kutechs.com>
       基线:W135B_BeidouApp_DZ [9ca5113210efc202feb6d2753f884f92b10910ba]
       应用用途说明：北1的短报文APP
       基本情况:
            01 北1协议默认版本根据渠道
            02 单双卡配置根据渠道，也调整设备的persist.bd.card.double值[1为双卡,0为单卡]，强制修改
            03 合并 W135B_BeidouApp_SDK 相关修改进入本工程,渠道为HuaChenSDK
            04 模块demo为渠道HuaChenSDK的测试专用模块

    工程名:W135_BeiDouMsg_UartDemo
       平台项目名:W135
       客户名：中性
       创建人：吴国献<wugx@kutechs.com>
       应用用途说明：framework封装的北斗短报文API的串口基本读写演示demo


    工程名:W195_MediaTest
       平台项目名:W195
       客户名：无
       创建人：吴国献<wugx@kutechs.com>
       应用用途说明：内部测试用，视频自动录制自动保存测试
       基本情况:
           01 具体说明见 W195_VieoTest/README.md
           
           
    工程名:W135B_BeidouApp_CSHG
       平台项目名:W135B
       客户名：长沙海格
       创建人：吴国献<wugx@kutechs.com>
       基线:W135B_BeidouApp [594c874cc55d7dd3b6fed9c3a0f7b4e9ce48ce7c]
       应用用途说明：北1的短报文APP+客户定制扩展协议
       基本情况:
            01 北1协议默认版本为：2.1
            02 北2模块为华大,对应文档 10.168.1.253/f$/smart/document/GPS/北斗/RN/华大/T-5-1908-001-GNSS_Protocol_Specification-V2.3.pdf
            03 添加单卡和双卡配置开关相关UI变化，默认单卡
            04 单双卡配置：app > build.gradle
                03.1 isEnableCardSingle 设定true 为单卡，false 为双卡
            05 北1协议的扩展协议对应文档：10.168.1.253/f$/smart/document/GPS/北斗/RD/扩展协议/TCP_RD位置上传协议-V2.docx ，协议暂未实现
            06 GPRS的数据同步协议对应文档：10.168.1.253/f$/smart/document/GPS/北斗/RD/扩展协议/TCP_RD位置上传协议-V2.docx 
	        07 北1协议的扩展协议在代码实现上的具体版本可能会有更新，具体以代码注释为准


    工程名:W135B_BeidouApp_ZB
       平台项目名:W135B
       客户名：中兵
       创建人：肖彦<xiaoyan@kutechs.com> /陈浩瀚<chenhaohan@kutechs.com>
       基线:W135B_BeidouApp [594c874cc55d7dd3b6fed9c3a0f7b4e9ce48ce7c]
       应用用途说明：北1的短报文APP
       基本情况:
            01 北1协议默认版本为：4.0
            02 添加单卡和双卡配置开关相关UI变化，默认单卡
            03 单双卡配置：app > build.gradle 
                03.1 isEnableCardSingle 设定true 为单卡，false 为双卡
            04 4.0版本位置上报扩展基于[北斗一代自定义通讯协议V1.4][客户版本协议]

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

    工程名:KU16_BLE_UHFRFID
       平台项目名:KU16
       客户名：瑞芯谷
       创建人：肖彦<xiaoyan@kutechs.com>
       基线:无
       应用用途说明：通过BLE控制RFID模块实现扫描等测试功能的客户端
       基本情况:
            01 BLE框架移植于  KU07_BLEHangThermometer 的子模块 ble  [65ccc78e712028ad7ab85eda9e29e327744c72f7]
            02 BLE为透传实现
            03 RFID的扫描已测试ok



    工程名:SpeechSynthesisEngine
       平台项目名:W195 / Android通用
       客户名：北京远光
       创建人：吴国献<wugx@kutechs.com>
       基线:无
       应用用途说明：通过云之声SDK封装成Android标准TTS引擎
       基本情况:
            01 目前为离线版本,安装包体积较大


    工程名:BLEMesh  
       平台项目名:KU17 / Android通用 
       客户名：无
       创建人：吴国献<wugx@kutechs.com>
       基线:https://github.com/NordicSemiconductor/Android-nRF-Mesh-Library [05ccaf3]
       应用用途说明：BLE的SIG Mesh 组网demo
       基本情况:
            01 Mesh组网方式为SIG


    工程名:KU19_NFC_RFID  
       平台项目名:KU19 / 其他
       客户名：无
       创建人：吴国献<wugx@kutechs.com>
       基线:KU19_RFID_HSURM [9d714a1]
       应用用途说明：
            01 HSURM的RFID模块盘点900MHZ标签
            02 ICM523的13.56MHZ NFC标签读取
       基本情况:
            01 工程KU19_RFID_HSURM已合入本工程，变成rfid模块
            02 工程模块说明[nfc]：
                02.1 路径: KU_NFC_RFID/nfc 
                02.2 说明: 实现了NFC模块ICM523读取13.56MHZ标签
                02.3 协议: smb://10.168.1.253/smart/projects/SmartPhone/KU19/NFC/ICM523/ICM522_Datasheet+V1.6-无LOG.pdf
                02.4 本模块为独立APP
            03 工程模块说明[rfid]：
                03.1 路径: KU_NFC_RFID/rfid 
                03.2 说明: 实现了RFID模块HSURM盘点900MHZ标签
                03.3 协议: smb://10.168.1.253/smart/projects/SmartPhone/KU19/RFID/内部文档_ZRM超高频读写器模块ISO协议对接开发手册-V2.8.pdf
                03.4 本模块为独立APPmo
                

    工程名:KU_Common 
       创建人：吴国献<wugx@kutechs.com>
       基线:KU19_NFC_RFID [efd5269]的common模块
       应用用途说明：
            01 APP间通用库
       基本情况:
            01 串口操作的抽象
            02 串口数据校验器和编解码器的抽象
                

    工程名:KU_VideoWallpaper
       创建人：吴国献<wugx@kutechs.com>
       基线:无
       应用用途说明：
            01 视频壁纸通用服务
       基本情况:
            01 默认视频路径为 /sdcard/kuyou/wallpaper/normal.mp4
            02 修改路径使用 setToWallPaper.setToWallPaper

    工程名:Jt808
       平台项目名:KU09 / 其他
       创建人：吴国献<wugx@kutechs.com>
       基线:无
       应用用途说明：
            01 Jt808/client : Android平台的实现Jt808协议的客户端
            02 Jt808/server : Web平台Spring框架下的Jt808协议服务器


    工程名:KU07_BLEHangThermometer
       平台项目名:KU07
       客户名：无
       创建人：吴国献<wugx@kutechs.com>
       基线:无
       应用用途说明：
            01 读取KU20测量的体温,显示并保存为excel,并可以后期查询


    工程名:KU20_SmartSwitch
       平台项目名:KU20/KU17
       客户名：无
       创建人：吴国献<wugx@kutechs.com>
       基线:KU07_BLEHangThermometer [65ccc78e712028ad7ab85eda9e29e327744c72f7]
       应用用途说明：
            01 读取刀闸传感器参数，并进行配置
       基本情况:
            01 实现协议：一键刀闸传输协议_V0.4.6.docx

