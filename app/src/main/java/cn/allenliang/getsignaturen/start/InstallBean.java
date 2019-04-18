package cn.allenliang.getsignaturen.start;

import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * @CreateBy liangxingfu
 * @Blog https://www.allenliang.cn/
 * @Email liangxingfu@boco.com.cn、allenliang1995@gmail.com
 * @CreateTime 2019/4/18 9:28 AM
 * @Description 功能描述
 */
public class InstallBean {
    String Myname;
    String MypackageName;
    Bitmap MyappIcon;
    String sigStr;
    PackageInfo packageInfo;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getSigStr() {
        return sigStr;
    }

    public void setSigStr(String sigStr) {
        this.sigStr = sigStr;
    }

    public String getMyname() {
        return Myname;
    }

    public void setMyname(String myname) {
        this.Myname = myname;
    }

    public String getMypackageName() {
        return MypackageName;
    }

    public void setMypackageName(String mypackageName) {
        this.MypackageName = mypackageName;
    }

    public Bitmap getMyappIcon() {
        return MyappIcon;
    }

    public void setMyappIcon(Bitmap myappIcon) {
        this.MyappIcon = myappIcon;
    }
}
