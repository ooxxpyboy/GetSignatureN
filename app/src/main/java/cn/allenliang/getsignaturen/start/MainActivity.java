package cn.allenliang.getsignaturen.start;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.shehuan.niv.NiceImageView;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.allenliang.getsignaturen.R;
import cn.allenliang.getsignaturen.sha1.AppUtils;
import cn.allenliang.getsignaturen.sha1.Utils;

public class MainActivity extends AppCompatActivity implements ItemOnClick {

    List<InstallBean> list = new ArrayList();
    @BindView(R.id.start_xlist_view)
    XRecyclerView recyclerView;
    @BindView(R.id.start_activity_search_bar)
     MaterialSearchBar searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utils.init(getApplicationContext());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
            {
                InstallBean installBean = new InstallBean();
                installBean.setMyname(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                installBean.setMypackageName(packageInfo.packageName);
                installBean.setSigStr(getSign(packageInfo.packageName));
                Bitmap bitmap = AppIconHelper.getAppIcon(getPackageManager(), packageInfo.packageName);
                installBean.setMyappIcon(bitmap);
                installBean.setPackageInfo(packageInfo);
                list.add(installBean);
            }
        }

        recyclerView.setPullRefreshEnabled(false);
        recyclerView.setLoadingMoreEnabled(false);
        ListviewAdapter listviewAdapter = new ListviewAdapter(getApplicationContext(),list,onClick);
        recyclerView.setAdapter(listviewAdapter);


        searchBar.setHint("Custom hint");
        searchBar.setSpeechMode(true);
        searchBar.setSearchIcon(R.drawable.search_icon);
//        //enable searchbar callbacks
//        searchBar.sets
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Log.e("搜索的字符串","-"+text);
                List<InstallBean> result = new ArrayList();
                for (int i=0;i<list.size();i++){
                    InstallBean installBean = list.get(i);
                    String name = installBean.getMyname();
                    if (name.contains(text)){
                        result.add(installBean);
                    }
                }
                if (result.size()>0){
                    ListviewAdapter listviewAdapter = new ListviewAdapter(getApplicationContext(),result,onClick);
                    recyclerView.setAdapter(listviewAdapter);
                }else{
                    Toast.makeText(getApplicationContext(),"您要搜索的应用不存在",Toast.LENGTH_SHORT).show();
                    ListviewAdapter listviewAdapter = new ListviewAdapter(getApplicationContext(),list,onClick);
                    recyclerView.setAdapter(listviewAdapter);
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        searchBar.hideSuggestionsList();




    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            //需要处理
            ListviewAdapter listviewAdapter = new ListviewAdapter(getApplicationContext(),list,onClick);
            recyclerView.setAdapter(listviewAdapter);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListviewAdapter listviewAdapter = new ListviewAdapter(getApplicationContext(),list,onClick);
        recyclerView.setAdapter(listviewAdapter);
    }

    ItemOnClick onClick = this;
    //点击回调
    @Override
    public void itemOnClickForXRList(InstallBean installBean) {
        TempData.installBean = installBean;
        String result = "\n包名："+installBean.getMypackageName()+"\n"+"MD5："+getSign(installBean.MypackageName)
                +"\nSha1: "+AppUtils.getAppSignatureSHA1(installBean.MypackageName);
        Log.e("点击复制的内容","--:"+ result);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", result);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(getApplicationContext(),"复制成功",Toast.LENGTH_SHORT).show();

    }

    class ListviewAdapter extends RecyclerView.Adapter{
        Context context;
        List<InstallBean> data ;
        LayoutInflater layoutInflater;
        ItemOnClick mitemOnClick;
        public ListviewAdapter(Context context, List data,ItemOnClick itemOnClick) {
            this.context = context;
            this.data = data;
            this.mitemOnClick= itemOnClick;
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.listview_item_forxr,null,false);
            LisHolder lisHolder = new LisHolder(view);
            return lisHolder;
        }
        @SuppressLint("NewApi")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final InstallBean installBean = data.get(position);
//            AppUtils.isInstallApp(installBean.MypackageName);
//            Log.e("sha1","--:"+ AppUtils.getAppSignatureSHA1(installBean.MypackageName));
            ((LisHolder)holder).APname.setText(installBean.getMyname());
            ((LisHolder)holder).APackageName.setText("包名："+installBean.getMypackageName());
            ((LisHolder)holder).APicon.setImageBitmap(installBean.MyappIcon);
            ((LisHolder)holder).APicon.isCircle(true);
            ((LisHolder)holder).sha1.setText("SHA1: "+AppUtils.getAppSignatureSHA1(installBean.MypackageName));
             ((LisHolder)holder).sigTV.setText("MD5: "+getSign(installBean.MypackageName));
            ((LisHolder)holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mitemOnClick.itemOnClickForXRList(installBean);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private Signature[] getRawSignature(String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            return null;
        }
        PackageManager localPackageManager = getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(paramString, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return null;
        }
        return localPackageInfo.signatures;
    }


    /**
     * 开始获得签名 * @param packageName 报名 * @return
     *
     * @param
     */
    private String getSign(String packageName) {
        Signature[] arrayOfSignature = getRawSignature(packageName);
        String messageDigest = getMessageDigest(arrayOfSignature[0].toByteArray());
        return messageDigest;

    }


    public String getMessageDigest(byte[] paramArrayOfByte) {
        char[] arrayOfChar1 = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) {
                if (j >= i) return new String(arrayOfChar2);
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException) {
        }
        return null;
    }

    public byte[] getRawDigest(byte[] paramArrayOfByte) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            return arrayOfByte;
        } catch (Exception localException) {
        }
        return null;
    }

    class LisHolder extends RecyclerView.ViewHolder{
        public NiceImageView APicon;
        public TextView APname;
        public TextView APackageName;
        public RelativeLayout root;
        public TextView sigTV;
        public Button button;
        public TextView sha1;
        public LisHolder(View itemView) {
            super(itemView);
            APicon = itemView.findViewById(R.id.list_imageview);
            APname = itemView.findViewById(R.id.list_name);
            APackageName= itemView.findViewById(R.id.list_package);
            root = itemView.findViewById(R.id.list_item_root);
            sigTV= itemView.findViewById(R.id.list_item_sigtv);
            button =itemView.findViewById(R.id.list_item_bt);
            sha1 = itemView.findViewById(R.id.list_item_sha1tv);
        }
    }


}
