package cn.allenliang.getsignaturen.start;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * @CreateBy liangxingfu
 * @Blog https://www.allenliang.cn/
 * @Email liangxingfu@boco.com.cn、allenliang1995@gmail.com
 * @CreateTime 2019/4/18 10:41 AM
 * @Description 功能描述
 */
public class AppIconHelper {
    public static Bitmap getAppIcon(PackageManager mPackageManager, String packageName) {

        if (Build.VERSION.SDK_INT >= 26) {
            return AppIconHelperV26.getAppIcon(mPackageManager, packageName);
        }

        try {
            Drawable drawable = mPackageManager.getApplicationIcon(packageName);
            return ((BitmapDrawable) drawable).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
