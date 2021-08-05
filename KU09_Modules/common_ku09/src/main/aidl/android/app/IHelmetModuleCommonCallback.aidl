package android.app;  

interface IHelmetModuleCommonCallback {

    void onKeyClick(int keyCode);           //发生按键单击,请处理
    void onKeyDoubleClick(int keyCode);     //发生按键双击,请处理
    void onKeyLongClick(int keyCode);       //发生按键长按,请处理

    void onPowerStatus(int status);  //电源状态改变
}
