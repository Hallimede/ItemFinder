package com.example.t.msc;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.t.util.FucUtil;
import com.example.t.util.ListUtil;
import com.example.t.util.StringUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAsr {

    /**
     * 离线命令词语识别
     */

    private static final String TAG = MyAsr.class.getSimpleName();

    // 语音识别对象
    private SpeechRecognizer mAsr;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地语法构建路径
    private String grmPath;
    // 返回结果格式，支持：xml,json
    public String mResultType = "json";
    // 语法
    private final String GRAMMAR_TYPE_BNF = "bnf";
    // 词典
    private HashMap<String, String> mDynamicLexicon = new HashMap<>();
    // 语音识别结果分词
    public HashMap<String, String> mSlotMap = new HashMap<>();
    private Context context;
    private Toast mToast;
    int ret = 0;// 函数调用返回值
    String mContent;// 语法、词典临时变量
    private MscController mController;

    public MyAsr(MscController controller){
        mController = controller;
    }

    public void initial(Context context) {
        this.context = context;
        mAsr = SpeechRecognizer.createRecognizer(context, mInitListener);
        if (mAsr == null) {
            Log.e(TAG, "masr is null");
        } else {
            Log.d(TAG, "masr is not null");
        }
        grmPath = context.getExternalFilesDir("msc").getAbsolutePath() + "/test";
        Log.d(TAG, "build onCreate grm =" + grmPath);
        buildGrammer();
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    /**
     * 初始化监听器。
     */
    private final InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 更新词典监听器。
     */
    private final LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
                //showTip("词典更新成功");
                Log.d(TAG, lexiconId + "词典更新成功");
                buildLexicon(); //更新下一个
            } else {
                //showTip("词典更新失败,错误码：" + error.getErrorCode());
                Log.d(TAG, "词典更新失败" + error.getErrorCode());
            }
        }
    };

    /**
     * 构建语法监听器。
     */
    private final GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                Log.d(TAG, "语法构建成功：" + grammarId);
            } else {
                Log.d(TAG, "语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 参数设置
     */
    public void setParam() {
        boolean result = true;
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置本地识别资源
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getAsrResourcePath());
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置本地识别使用语法id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
    }

    //获取识别资源路径
    private String getAsrResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }

    /**
     * 构建语法
     */
    public void buildGrammer() {
        mLocalGrammar = FucUtil.readFile(context, "call.bnf", "utf-8");
        // 本地-构建语法文件，生成语法id
        mContent = new String(mLocalGrammar);
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置文本编码格式
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置引擎类型
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置资源路径
        String rs = getAsrResourcePath();
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, rs);
        Log.d(TAG, "rs :" + rs);
        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置本地识别使用语法id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
        ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mContent, grammarListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "语法构建失败,错误码：" + ret);
        } else {
            Log.d(TAG, "语法构建成功");
        }
    }

    /**
     * 更新词典
     */
    public void buildLexicon() {
        for (Map.Entry<String, String> entry : mDynamicLexicon.entrySet()) {
            String slot = entry.getKey();
            mContent = new String(entry.getValue());
            Log.d(TAG, slot);
            Log.d(TAG, mContent);
            // 设置引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置资源路径
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getAsrResourcePath());
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
            // 设置语法名称
            mAsr.setParameter(SpeechConstant.GRAMMAR_LIST, "call");
            // 设置文本编码格式
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");

            ret = mAsr.updateLexicon(slot, mContent, lexiconListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.d(TAG, "更新" + slot + "词典失败,错误码：" + ret);
            } else {
                mDynamicLexicon.remove(slot);
            }
            return;
        }
    }

    private <T> String getLexicon(List<T> list, String index) {
        return StringUtil.join(ListUtil.getElementList(list, index), "\n");
    }

    public <T> void putLexicon(String slot, List<T> list) {
        mDynamicLexicon.put(slot, getLexicon(list, "name"));
    }

    public void clearSlot() {
        mSlotMap.clear();
    }

    public void putSlot(String key, String value) {
        mSlotMap.put(key, value);
    }

    public void close() {
        if (null != mAsr) {
            mAsr.cancel();
            mAsr.destroy();
        }
    }

    public int startListening(RecognizerListener listener) {
        return mAsr.startListening(listener);
    }

}
