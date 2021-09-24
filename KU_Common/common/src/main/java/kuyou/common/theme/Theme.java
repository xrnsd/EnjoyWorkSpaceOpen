package kuyou.common.theme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;

import java.util.List;

public class Theme extends Activity implements Itheme {

    protected static final String TAG = "kuyou.common.theme > Theme";

    private List<String> mThemePackageList;
    private String mPackageNameThemeSel = null;

    private RadioButton[] mRadioButtons = null;

    private ThemeUtils mThemeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void UpdateLauncherThemeById(Context context, String theme_packagename) {
        if (theme_packagename == null) {
            android.util.Log.d(TAG, "Theme>UpdateLauncherThemeById=null");
            return;
        }
        int theme_id = Integer.valueOf(theme_packagename.replaceAll(THEME_PACKAGE_NAME_BASE, ""));
        UpdateLauncherThemeById(context, theme_id);
    }

    public void UpdateLauncherThemeById(Context context, int theme_id) {
        android.util.Log.d(TAG, "Theme>UpdateLauncherUIByThemeId=" + theme_id);
        //wait
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Wallpaper settings …");
        dialog.show();

        //save id android reset theme context
        mThemeUtils.setThemeId(context, theme_id);

        //update wallpaper
//        try {
//            int wallpaperResId=mThemeUtils.getThemeResourceByName("drawable", FILENAME_THEME);
//            WallpaperManager   wm=(WallpaperManager)context.getSystemService(WallpaperService.WALLPAPER_SERVICE);
//            wm.setBitmap(BitmapFactory.decodeResource(mThemeUtils.getLocalThemeResources(),wallpaperResId));
//            android.util.Log.d(TAG, "Theme>UpdateLauncherThemeById update wallpaper");
//        } catch (IOException e) {
//            e.printStackTrace();
//            android.util.Log.d(TAG, "Theme>UpdateLauncherThemeById update wallpaper fail="+e);
//        }


        //reboot launcher
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY, theme_id);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }
}
