package com.example.t.msc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.R;
import com.example.t.model.Inventory;
import com.example.t.model.Item;
import com.example.t.model.Room;
import com.example.t.model.StorageSpace;
import com.example.t.model.result.Result;
import com.example.t.model.result.Ws;
import com.example.t.util.IdNameHelper;
import com.example.t.util.JsonParser;
import com.example.t.util.MyConstants;
import com.example.t.util.StringUtil;
import com.example.t.view.BaseActivity;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;

import java.util.List;
import java.util.Map;

public class MscController {

    private static final String TAG = MscController.class.getSimpleName();

    private MyAsr myAsr;
    public MyTts myTts;
    private MyIat myIat;
    public TextView feed;
    int ret = 0;// 函数调用返回值
    private Toast mToast;
    private BaseActivity mContext;
    private boolean lastAsr = true;

    private List<Item> mItems = null;
    private List<Room> mRooms = null;
    private List<StorageSpace> mStorageSpaces = null;
    private RecordListener mRecordListener;

    private int tempRoomId = 0;
    private int tempItemId = 0;
    private int tempBoxId = 0;
    private boolean needSpaceName = false;
    private boolean needConfirm = false;

    private String queryRoom;
    private String queryBox;
    private String queryItemName;

    private int audioType = MyConstants.ASR_AUDIO;
    private int instructType = MyConstants.NO_INSTRUCTION;
    private int objType = MyConstants.NO_OBJ;

    public MscController(Context context) {
        mContext = (BaseActivity) context;
        Log.d(TAG, "constructor:" + context.toString());
        myAsr = new MyAsr(this);
        myAsr.initial(context);
        myTts = new MyTts(this);
        myTts.initial(context);
        myIat = new MyIat(this);
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public void setmItems(List<Item> items) {
        this.mItems = items;
        putLexicon("items", mItems);
        Log.d(TAG, "set items");
    }

    public void setmRooms(List<Room> rooms) {
        this.mRooms = rooms;
        putLexicon("rooms", mRooms);
        Log.d(TAG, "set rooms");
    }

    public void setmStorageSpaces(List<StorageSpace> storageSpaces) {
        this.mStorageSpaces = storageSpaces;
        putLexicon("boxes", mStorageSpaces);
        Log.d(TAG, "set spaces");
    }

    public void close() {
        if (null != myAsr) {
            // 退出时释放连接
            myAsr.close();
        }
        if (null != myTts) {
            myTts.close();
        }
        if (null != myIat) {
            // 退出时释放连接
            myIat.close();
        }
    }

    public void setTextFeed(TextView feed) {
        Log.d(TAG, "feed from:" + feed.getText());
        this.feed = feed;
    }

    public <T> void putLexicon(String slot, List<T> list) {
        myAsr.putLexicon(slot, list);
    }

    public void listen(RecordListener recordListener) {
        mRecordListener = recordListener;
        switch (audioType) {
            // 开始识别
            case MyConstants.ASR_AUDIO:
                // TODO
                Log.d(TAG, "start recording [asr]");
                resetAsr();
                myAsr.buildLexicon();
                ret = myAsr.startListening(mAsrRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    showTip("识别失败,错误码: " + ret);
                    Log.d(TAG, "识别失败,错误码: " + ret);
                }
                break;
            case MyConstants.IAT_AUDIO:
                resetIat();
                Log.d(TAG, "start recording [iat]");
                ret = myIat.startListening(mIatRecognizerListener);
                Log.d(TAG, "iat recog");
                if (ret != ErrorCode.SUCCESS) {
                    showTip("识别失败,错误码: " + ret);
                    Log.d(TAG, "识别失败,错误码: " + ret);
                } else {
                    showTip(mContext.getString(R.string.text_begin));
                }
                break;
        }
    }

    public void speak(String text) {
        feed.setText(text);
        myTts.beginSpeaking(text);
    }

    private void resetAsr() {

        if (!lastAsr) {
            Log.d(TAG, "reset Asr");
            myIat.close();
            myAsr.initial(mContext);
            lastAsr = true;
        }
    }

    private void resetIat() {

        if (lastAsr) {
            Log.d(TAG, "reset Iat");
            myAsr.close();
            myIat.initial(mContext);
            lastAsr = false;
        }
    }

    /**
     * 识别监听器。
     */
    private final RecognizerListener mAsrRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            //Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Log.d(TAG, "\n【ASR识别结果】\n" + result.getResultString());
                String text = "";
                if (myAsr.mResultType.equals("json")) {
                    text = JsonParser.parseGrammarResult(result.getResultString(), SpeechConstant.TYPE_LOCAL);
                    Gson gson = new Gson();
                    Result res = gson.fromJson(result.getResultString(), Result.class);
                    List<Ws> ws = res.getWs();
                    myAsr.clearSlot();
                    for (int i = 0; i < ws.size(); i++) {
                        myAsr.putSlot(ws.get(i).getSlot(), ws.get(i).getCw().get(0).getW());
                        Log.d(TAG, ws.get(i).getSlot() + " : " + ws.get(i).getCw().get(0).getW());
                    }
                    parseResult();
                }
                // 显示
                feed.setText(text);
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            mRecordListener.onRecordEnd();
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            mRecordListener.onRecordBegin();
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip("onError Code：" + error.getErrorCode());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 解析语音识别结果
     */
    public void parseResult() {

        instructType = MyConstants.NO_INSTRUCTION;
        objType = MyConstants.NO_OBJ;
        queryRoom = null;
        queryBox = null;
        queryItemName = null;

        for (Map.Entry<String, String> entry : myAsr.mSlotMap.entrySet()) {
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
            switch (entry.getKey()) {
                case "<rooms>":
                    queryRoom = entry.getValue();
                    break;
                case "<boxes>":
                    queryBox = entry.getValue();
                    break;
                case "<items>":
                    queryItemName = entry.getValue();
                    break;
                case "<new>":
                    instructType = MyConstants.CREATE;
                    break;
                case "<del>":
                    instructType = MyConstants.DELETE;
                    break;
                case "<item>":
                    objType = MyConstants.ITEM;
                    break;
                case "<room>":
                    objType = MyConstants.ROOM;
                    break;
                case "<box>":
                    objType = MyConstants.BOX;
                    break;
                case "<delete>":
                    instructType = MyConstants.DELETE_INVENTORY;
                    break;
                case "<where>":
                    instructType = MyConstants.FIND_ITEM;
                    break;
                default:
                    break;
            }
        }

        // 创建命令
        if (instructType == MyConstants.CREATE) {
            if (objType == MyConstants.ITEM) {
                instructType = MyConstants.CREATE_ITEM;
            } else if (objType == MyConstants.ROOM) {
                instructType = MyConstants.CREATE_ROOM;
            } else if (objType == MyConstants.BOX) {
                instructType = MyConstants.CREATE_BOX;
            }
        }

        // 删除命令
        if (instructType == MyConstants.DELETE) {
            if (objType == MyConstants.ITEM) {
                instructType = MyConstants.DELETE_ITEM;
            } else if (objType == MyConstants.ROOM) {
                instructType = MyConstants.DELETE_ROOM;
            } else if (objType == MyConstants.BOX) {
                instructType = MyConstants.DELETE_BOX;
            }
        }

        if (instructType == MyConstants.NO_INSTRUCTION) {
            instructType = MyConstants.POST_INVENTORY;
        }

        Log.d(TAG, "instruct type: " + instructType);

        switch (instructType) {
            case MyConstants.CREATE_ITEM:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入物件的名字");
                break;
            case MyConstants.CREATE_ROOM:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入房间的名字");
                break;
            case MyConstants.CREATE_BOX:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入所在房间的名字");
                break;
            case MyConstants.DELETE_ITEM:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入删除物件的名字");
                break;
            case MyConstants.DELETE_ROOM:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入删除房间的名字");
                break;
            case MyConstants.DELETE_BOX:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入删除储物空间的名字");
                break;
            case MyConstants.FIND_ITEM:
                find();
                break;
            case MyConstants.DELETE_INVENTORY:
                delete();
                break;
            case MyConstants.POST_INVENTORY:
                audioType = MyConstants.IAT_AUDIO;
                myTts.beginSpeaking("请用语音输入物件更详细的位置");
                break;
        }

    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mIatRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, "\n【IAT识别结果】\n" + results.getResultString());
            String text = JsonParser.parseIatResult(results.getResultString());
            text = StringUtil.format(text);
            feed.setText(text);
            if (!TextUtils.isEmpty(text)) {
                switch (instructType) {
                    case MyConstants.CREATE_ITEM:
                        create(new Item(text));
                        audioType = MyConstants.ASR_AUDIO;
                        break;
                    case MyConstants.CREATE_ROOM:
                        create(new Room(text));
                        audioType = MyConstants.ASR_AUDIO;
                        break;
                    case MyConstants.CREATE_BOX:
                        if (needSpaceName) {
                            // 输入的是储物空间的名字
                            create(new StorageSpace(text, tempRoomId));
                            audioType = MyConstants.ASR_AUDIO;
                            needSpaceName = false;
                            tempRoomId = 0;
                        } else {
                            // 输入的是房间名字
                            tempRoomId = IdNameHelper.findIdByName(text, mRooms);
                            if (tempRoomId == 0) {
                                myTts.beginSpeaking("抱歉，找不到您要存放的房间，请重新创建");
                                audioType = MyConstants.ASR_AUDIO;
                            } else {
                                myTts.beginSpeaking("您说的是 " + text + "，请输入储物空间的名字");
                                needSpaceName = true;
                            }
                        }
                        break;
                    case MyConstants.DELETE_ITEM:
                        if (needConfirm) {
                            if (text.equals("确认") || text.equals("是")) {
                                delete(tempItemId);
                                Log.d(TAG, "type = " + instructType + " 确认删除 item: " + text);
                                audioType = MyConstants.ASR_AUDIO;
                                needConfirm = false;
                                tempItemId = 0;
                            }
                        } else {
                            tempItemId = IdNameHelper.findIdByName(text, mItems);
                            if (tempItemId == 0) {
                                myTts.beginSpeaking("抱歉，找不到您要删除的物件，请重新输入");
                                audioType = MyConstants.ASR_AUDIO;
                            } else {
                                myTts.beginSpeaking("您说的是 " + text + "，请您确认是否要删除？\n回答\"确认\"或\"是\"");
                                needConfirm = true;
                            }
                        }
                        break;
                    case MyConstants.DELETE_ROOM:
                        if (needConfirm) {
                            if (text.equals("确认") || text.equals("是")) {
                                delete(tempRoomId);
                                Log.d(TAG, "type = " + instructType + " 确认删除 room: " + text);
                                audioType = MyConstants.ASR_AUDIO;
                                needConfirm = false;
                                tempRoomId = 0;
                            }
                        } else {
                            tempRoomId = IdNameHelper.findIdByName(text, mRooms);
                            if (tempRoomId == 0) {
                                myTts.beginSpeaking("抱歉，找不到您要删除的房间，请重新输入");
                                audioType = MyConstants.ASR_AUDIO;
                            } else {
                                myTts.beginSpeaking("您说的是 " + text + "，请您确认是否要删除？\n回答\"确认\"或\"是\"");
                                needConfirm = true;
                            }
                        }
                        break;
                    case MyConstants.DELETE_BOX:
                        if (needConfirm) {
                            if (text.equals("确认") || text.equals("是")) {
                                delete(tempBoxId);
                                Log.d(TAG, "type = " + instructType + " 确认删除 space: " + text);
                                audioType = MyConstants.ASR_AUDIO;
                                needConfirm = false;
                                tempBoxId = 0;
                            }
                        } else {
                            tempBoxId = IdNameHelper.findIdByName(text, mStorageSpaces);
                            if (tempBoxId == 0) {
                                myTts.beginSpeaking("抱歉，找不到您要删除的储物空间，请重新输入");
                                audioType = MyConstants.ASR_AUDIO;
                            } else {
                                myTts.beginSpeaking("您说的是 " + text + "，请您确认是否要删除？\n回答\"确认\"或\"是\"");
                                needConfirm = true;
                            }
                        }
                        break;
                    case MyConstants.POST_INVENTORY:
                        put(text);
                        audioType = MyConstants.ASR_AUDIO;
                        break;
                }
            }

            if (isLast) {
                //TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            //Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                //Log.d(TAG, "session id =" + sid);
            }
        }
    };

    private <T> void create(T base) {
        if (instructType == MyConstants.CREATE_ITEM) {
            mContext.postItem((Item) base);
        } else if (instructType == MyConstants.CREATE_ROOM) {
            mContext.postRoom((Room) base);
        } else if (instructType == MyConstants.CREATE_BOX) {
            mContext.postStorageSpace((StorageSpace) base);
        }
    }

    private void delete() {
        Log.d(TAG, "【delete】" + queryItemName);
        int itemId = IdNameHelper.findIdByName(queryItemName, mItems);
        if (itemId == 0) {
            myTts.beginSpeaking("很抱歉，没有查到您的物品,请重试");
            return;
        }
        mContext.deleteInventory(itemId);
    }

    private void delete(int id) {
        if (instructType == MyConstants.DELETE_ITEM) {
            mContext.deleteItem(id);
            Log.d(TAG, "type = " + instructType + " 发起删除item请求");
        } else if (instructType == MyConstants.DELETE_ROOM) {
            mContext.deleteRoom(id);
            Log.d(TAG, "type = " + instructType + " 发起删除room请求");
        } else if (instructType == MyConstants.DELETE_BOX) {
            mContext.deleteStorageSpace(id);
            Log.d(TAG, "type = " + instructType + " 发起删除space请求");
        }
    }

    private void find() {
        Log.d(TAG, "【find】" + queryItemName);
        int itemId = IdNameHelper.findIdByName(queryItemName, mItems);
        mContext.getItemInventory(new Inventory(itemId));
    }

    public void findCallBack(Inventory ivt) {
        if (ivt == null) {
            myTts.beginSpeaking("很抱歉，没有查询到位置");
            return;
        }
        String roomName = IdNameHelper.findNameById(ivt.getRoom_id(), mRooms);
        String storageSpaceName = IdNameHelper.findNameById(ivt.getStorage_space_id(), mStorageSpaces);
        if (!queryItemName.equals("") && !roomName.equals("") && !storageSpaceName.equals("")) {
            Log.d(TAG, "【find】room = " + roomName + " & box = " + storageSpaceName);
            myTts.beginSpeaking("您的" + queryItemName + "被存放在" + roomName + "的" + storageSpaceName + "里");
        } else {
            myTts.beginSpeaking("很抱歉，查询失败，请重新输入");
        }
    }

    private void put(String info) {
        Log.d(TAG, "【put】" + queryItemName);
        Log.d(TAG, "【put】" + queryRoom);
        Log.d(TAG, "【put】" + queryBox);
        int itemId = IdNameHelper.findIdByName(queryItemName, mItems);
        int roomId = IdNameHelper.findIdByName(queryRoom, mRooms);
        int boxId = IdNameHelper.findIdByName(queryBox, mStorageSpaces);

        if (itemId == 0) {
            myTts.beginSpeaking("很抱歉，没有查到您的物品,请重试");
            return;
        }
        if (roomId == 0) {
            myTts.beginSpeaking("很抱歉，没有查到您目标房间,请重试");
            return;
        }
        if (boxId == 0) {
            myTts.beginSpeaking("很抱歉，没有查到您目标位置,请重试");
            return;
        }
        Inventory ivt = new Inventory(itemId, roomId, boxId, info);
        mContext.postInventory(ivt);
    }

    private void showTip(final String str) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }
}
