package cn.com.jiutairural;

import android.content.Context;

/**
 * @author Mr.kang
 * @date 2019-07-02
 * @desc
 */
public class GetResId {
    public static int getId(Context context, String paramString1, String paramString2) {
        return context.getResources().getIdentifier(paramString2, paramString1, context.getPackageName());
    }
}
