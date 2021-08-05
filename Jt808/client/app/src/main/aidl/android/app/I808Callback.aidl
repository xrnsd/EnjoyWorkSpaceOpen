package android.app;  
  
interface I808Callback {
    void onLiveResult(int resultCode,String msg);//服务端返回打开对应Live界面的结果
    void onCameraShootKey();//相机按键
}