package kong.qingwei.kqwhcidemo;

import android.app.Activity;
import android.util.Log;

import com.sinovoice.hcicloudsdk.android.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;
import com.sinovoice.hcicloudsdk.recorder.RecorderEvent;


/**
 * Created by kqw on 2016/8/15.
 * 语音识别类
 */
public class AsrUtil {

    private static final String TAG = "AsrUtil";
    private Activity mActivity;
    private ASRRecorder mAsrRecorder;
    private AsrConfig asrConfig;
    private OnAsrRecogListener mOnAsrRecogListener;

    public AsrUtil(Activity activity) {
        mActivity = activity;
        initAsr();
    }

    private void initAsr() {
        Log.i(TAG, "initAsr: ");
        // 初始化录音机
        mAsrRecorder = new ASRRecorder();

        // 配置初始化参数
        AsrInitParam asrInitParam = new AsrInitParam();
        String dataPath = mActivity.getFilesDir().getPath().replace("files", "lib");
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, ConfigUtil.CAP_KEY_ASR_CLOUD_DIALOG);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
        Log.v(TAG, "init parameters:" + asrInitParam.getStringConfig());

        // 设置初始化参数
        mAsrRecorder.init(asrInitParam.getStringConfig(), new ASRResultProcess());

        // 配置识别参数
        asrConfig = new AsrConfig();
        // PARAM_KEY_CAP_KEY 设置使用的能力
        asrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, ConfigUtil.CAP_KEY_ASR_CLOUD_DIALOG);
        // PARAM_KEY_AUDIO_FORMAT 音频格式根据不同的能力使用不用的音频格式
        asrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_AUDIO_FORMAT, AsrConfig.AudioConfig.VALUE_OF_PARAM_AUDIO_FORMAT_PCM_16K16BIT);
        // PARAM_KEY_ENCODE 音频编码压缩格式，使用OPUS可以有效减小数据流量
        asrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE, AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);
        // 其他配置，此处可以全部选取缺省值

        asrConfig.addParam("intention", "weather");
    }

    /**
     * 开始语音识别
     */
    public void start(OnAsrRecogListener listener) {
        mOnAsrRecogListener = listener;
        if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_IDLE) {
            asrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "no");
            mAsrRecorder.start(asrConfig.getStringConfig(), null);
        } else {
            Log.i(TAG, "start: 录音机未处于空闲状态，请稍等");
        }
    }

    private class ASRResultProcess implements ASRRecorderListener {
        @Override
        public void onRecorderEventError(RecorderEvent event, int errorCode) {
            Log.i(TAG, "onRecorderEventError: errorCode = " + errorCode);
            if (null != mOnAsrRecogListener) {

                mOnAsrRecogListener.onError(errorCode);
            }
        }

        @Override
        public void onRecorderEventRecogFinsh(RecorderEvent recorderEvent, final AsrRecogResult arg1) {
            if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_COMPLETE) {
                Log.i(TAG, "onRecorderEventRecogFinsh: 识别结束");
            }
            if (null != mOnAsrRecogListener) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOnAsrRecogListener.onAsrRecogResult(arg1);
                    }
                });
            }
        }

        @Override
        public void onRecorderEventStateChange(RecorderEvent recorderEvent) {
            if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECORD) {
                Log.i(TAG, "onRecorderEventStateChange: 开始录音");
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECOGNIZE) {
                Log.i(TAG, "onRecorderEventStateChange: 开始识别");
            } else if (recorderEvent == RecorderEvent.RECORDER_EVENT_NO_VOICE_INPUT) {
                Log.i(TAG, "onRecorderEventStateChange: 无音频输入");
            } else {
                Log.i(TAG, "onRecorderEventStateChange: recorderEvent = " + recorderEvent);
            }
        }

        @Override
        public void onRecorderRecording(byte[] volumedata, int volume) {
            if (null != mOnAsrRecogListener) {
                mOnAsrRecogListener.onVolume(volume);
            }
        }

        @Override
        public void onRecorderEventRecogProcess(RecorderEvent recorderEvent, AsrRecogResult arg1) {
            if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_PROCESS) {
                Log.i(TAG, "onRecorderEventRecogProcess: 识别中间反馈");
            }
            if (arg1 != null) {
                if (arg1.getRecogItemList().size() > 0) {
                    Log.i(TAG, "onRecorderEventRecogProcess: 识别中间结果结果为：" + arg1.getRecogItemList().get(0).getRecogResult());
                } else {
                    Log.i(TAG, "onRecorderEventRecogProcess: 未能正确识别,请重新输入");
                }
            }
        }
    }

    /**
     * 语音识别的回调接口
     */
    public interface OnAsrRecogListener {
        // 识别结果
        void onAsrRecogResult(AsrRecogResult asrRecogResult);

        // 识别错误码
        void onError(int errorCode);

        // 录音音量
        void onVolume(int volume);
    }
}
