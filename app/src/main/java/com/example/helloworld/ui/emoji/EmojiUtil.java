package com.example.helloworld.ui.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.example.helloworld.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmojiUtil {
    private static ArrayList<Emoji> emojiList;

    public static ArrayList<Emoji> getEmojiList() {
        if (emojiList == null) {
            emojiList = generateEmojis();
        }
        return emojiList;
    }

    private static ArrayList<Emoji> generateEmojis() {
        ArrayList<Emoji> list = new ArrayList<>();
        for (int i = 0; i < EmojiResArray.length; i++) {
            Emoji emoji = new Emoji();
            emoji.setImageUri(EmojiResArray[i]);
            emoji.setContent(EmojiTextArray[i]);
            list.add(emoji);
        }
        return list;
    }


    public static final int[] EmojiResArray = {
            R.drawable.d_aini,
            R.drawable.d_aoteman,
            R.drawable.d_baibai,
            R.drawable.d_beishang,
            R.drawable.d_bishi,
            R.drawable.d_bizui,
            R.drawable.d_chanzui,
            R.drawable.d_chijing,
            R.drawable.d_dahaqi,
            R.drawable.d_dalian,
            R.drawable.d_ding,
            R.drawable.d_doge,
            R.drawable.d_feizao,
            R.drawable.d_ganmao,
            R.drawable.d_guzhang,
            R.drawable.d_haha,
            R.drawable.d_haixiu,
            R.drawable.d_han,
            R.drawable.d_hehe,
            R.drawable.d_heixian,
            R.drawable.d_heng,
            R.drawable.d_huaxin,
            R.drawable.d_jiyan,
            R.drawable.d_keai,
            R.drawable.d_kelian,
            R.drawable.d_ku,
            R.drawable.d_kun,
            R.drawable.d_landelini,
            R.drawable.d_lei,
            R.drawable.d_madaochenggong,
            R.drawable.d_miao,
            R.drawable.d_nanhaier,
            R.drawable.d_nu,
            R.drawable.d_numa,
            R.drawable.d_numa,
            R.drawable.d_qian,
            R.drawable.d_qinqin,
            R.drawable.d_shayan,
            R.drawable.d_shengbing,
            R.drawable.d_shenshou,
            R.drawable.d_shiwang,
            R.drawable.d_shuai,
            R.drawable.d_shuijiao,
            R.drawable.d_sikao,
            R.drawable.d_taikaixin,
            R.drawable.d_touxiao,
            R.drawable.d_tu,
            R.drawable.d_tuzi,
            R.drawable.d_wabishi,
            R.drawable.d_weiqu,
            R.drawable.d_xiaoku,
            R.drawable.d_xiongmao,
            R.drawable.d_xixi,
            R.drawable.d_xu,
            R.drawable.d_yinxian,
            R.drawable.d_yiwen,
            R.drawable.d_youhengheng,
            R.drawable.d_yun,
            R.drawable.d_zhajipijiu,
            R.drawable.d_zhuakuang,
            R.drawable.d_zhutou,
            R.drawable.d_zuiyou,
            R.drawable.d_zuohengheng,
            R.drawable.f_geili,
            R.drawable.f_hufen,
            R.drawable.f_jiong,
            R.drawable.f_meng,
            R.drawable.f_shenma,
            R.drawable.f_v5,
            R.drawable.f_xi,
            R.drawable.f_zhi,
            R.drawable.h_buyao,
            R.drawable.h_good,
            R.drawable.h_haha,
            R.drawable.h_lai,
            R.drawable.h_ok,
            R.drawable.h_quantou,
            R.drawable.h_ruo,
            R.drawable.h_woshou,
            R.drawable.h_ye,
            R.drawable.h_zan,
            R.drawable.h_zuoyi,
            R.drawable.l_shangxin,
            R.drawable.l_xin,
            R.drawable.o_dangao,
            R.drawable.o_feiji,
            R.drawable.o_ganbei,
            R.drawable.o_huatong,
            R.drawable.o_lazhu,
            R.drawable.o_liwu,
            R.drawable.o_lvsidai,
            R.drawable.o_weibo,
            R.drawable.o_weiguan,
            R.drawable.o_yinyue,
            R.drawable.o_zhaoxiangji,
            R.drawable.o_zhong,
            R.drawable.w_fuyun,
            R.drawable.w_shachenbao,
            R.drawable.w_taiyang,
            R.drawable.w_weifeng,
            R.drawable.w_xianhua,
            R.drawable.w_xiayu,
            R.drawable.w_yueliang,
    };

    public static final String[] EmojiTextArray = {
            "[??????]",
            "[?????????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[???]",
            "[doge]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[???]",
            "[??????]",
            "[??????]",
            "[???]",
            "[???]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[???]",
            "[???]",
            "[??????]",
            "[???]",
            "[????????????]",
            "[??????]",
            "[?????????]",
            "[???]",
            "[??????]",
            "[?????????]",
            "[???]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[?????????]",
            "[??????]",
            "[???]",
            "[???]",
            "[??????]",
            "[?????????]",
            "[??????]",
            "[???]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[???cry]",
            "[??????]",
            "[??????]",
            "[???]",
            "[??????]",
            "[??????]",
            "[?????????]",
            "[???]",
            "[????????????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[?????????]",
            "[??????]",
            "[??????]",
            "[???]",
            "[???]",
            "[??????]",
            "[??????]",
            "[???]",
            "[???]",
            "[NO]",
            "[good]",
            "[haha]",
            "[???]",
            "[OK]",
            "[??????]",
            "[???]",
            "[??????]",
            "[???]",
            "[???]",
            "[??????]",
            "[??????]",
            "[???]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[?????????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[?????????]",
            "[???]",
            "[??????]",
            "[?????????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
            "[??????]",
    };

    static {
        emojiList = generateEmojis();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // ???????????????????????????
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // ?????????????????????????????????????????????
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // ???????????????????????????????????????inSampleSize???????????????????????????????????????????????????
            // ?????????????????????????????????????????????
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // ??????????????????inJustDecodeBounds?????????true????????????????????????
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // ?????????????????????????????????inSampleSize???
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // ??????????????????inSampleSize?????????????????????
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void handlerEmojiText(TextView comment, String content, Context context) throws IOException {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        Iterator<Emoji> iterator;
        Emoji emoji = null;
        while (m.find()) {
            iterator = emojiList.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emoji = iterator.next();
                if (tempText.equals(emoji.getContent())) {
                    //?????????Span?????????Span?????????
                    sb.setSpan(new ImageSpan(context, decodeSampledBitmapFromResource(context.getResources(), emoji.getImageUri()
                                    , dip2px(context, 18), dip2px(context, 18))),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        comment.setText(sb);
    }

    public static SpannableStringBuilder EmojiText(String content, Context context) throws IOException {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        Iterator<Emoji> iterator;
        Emoji emoji = null;
        while (m.find()) {
            iterator = emojiList.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emoji = iterator.next();
                if (tempText.equals(emoji.getContent())) {
                    //?????????Span?????????Span?????????
                    sb.setSpan(new ImageSpan(context, decodeSampledBitmapFromResource(context.getResources(), emoji.getImageUri()
                            , dip2px(context, 18), dip2px(context, 18))),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        return sb;
    }
}
