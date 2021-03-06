package com.example.helloworld.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.utils.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoFragment extends Fragment {
    private Bitmap bitmap;
    private PhotoView mPhotoView;
    private String base64;
    Bitmap obmp;

    public static PhotoFragment newInstance(String base64) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString("base64", base64);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        base64 = getArguments().getString("base64");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img, container, false);
        mPhotoView = view.findViewById(R.id.photoview);
        mPhotoView.setScaleType(ImageView.ScaleType.CENTER);
        mPhotoView.enable();
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mPhotoView.setDrawingCacheEnabled(true);
                obmp = Bitmap.createBitmap(mPhotoView.getDrawingCache());
                mPhotoView.setDrawingCacheEnabled(false);
                new AlterDialogManager(getContext(), "?????????????????????", new AlterDialogManager.onClockListner() {
                    @Override
                    public void onClockListner(int i) {
                        saveMyBitmap(getActivity(), obmp);
                    }
                }).show();
                return true;
            }
        });
        if (base64.length()>100) {
            bitmap = ImageUtils.base64ToBitmap(base64);
            Glide.with(getContext())
                    .load(bitmap)
                    .placeholder(R.mipmap.ic_launcher)//??????????????????????????????????????????????????????
                    .error(R.mipmap.ic_launcher)//??????????????????????????????
//                .centerCrop()//???????????????ImageView???????????????
                    .fitCenter()//???????????????????????????????????????ImageView???????????????,???????????????????????????
                    .into(mPhotoView);
        }
        else {
            Glide.with(getContext())
                    .load(new httpPathList().getUrl() + "/" + base64)
                    .placeholder(R.mipmap.ic_launcher)//??????????????????????????????????????????????????????
                    .error(R.mipmap.ic_launcher)//??????????????????????????????
//                .centerCrop()//???????????????ImageView???????????????
                    .fitCenter()//???????????????????????????????????????ImageView???????????????,???????????????????????????
                    .into(mPhotoView);
        }
        return view;
    }


    //???????????????????????????
    public void saveMyBitmap(Context context, Bitmap bitmap) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/DCIM/";
        File appDir = new File(sdCardDir, "hwappimg");
        if (!appDir.exists()) {//?????????
            appDir.mkdir();
        }
        String fileName = "hwappimg" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        getActivity().sendBroadcast(intent);
        Toast.makeText(getActivity(),"??????????????????",Toast.LENGTH_SHORT).show();
    }

}
