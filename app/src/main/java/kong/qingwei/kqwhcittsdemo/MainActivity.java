package kong.qingwei.kqwhcittsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;

public class MainActivity extends AppCompatActivity implements TTSPlayerListener {

    private EditText mEditText;
    private boolean isInitPlayer;
    private TtsUtil mTtsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mEditText = (EditText) findViewById(R.id.edit_text);

        // 灵云语音工具类
        HciUtil mInitTts = new HciUtil(this);
        // 初始化灵云语音
        boolean isInitHci = mInitTts.initHci();
        if (isInitHci) { // 初始化成功
            // 语音合成能力工具类
            mTtsUtil = new TtsUtil(this);
            // 初始化语音合成
            isInitPlayer = mTtsUtil.initPlayer(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 开始合成
     *
     * @param view v
     */
    public void synth(View view) {
        if (!isInitPlayer) {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = mEditText.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "合成内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mTtsUtil.synth(text);
    }


    // 语音合成的回调
    @Override
    public void onPlayerEventStateChange(TTSCommonPlayer.PlayerEvent playerEvent) {

    }

    @Override
    public void onPlayerEventProgressChange(TTSCommonPlayer.PlayerEvent playerEvent, int i, int i1) {

    }

    @Override
    public void onPlayerEventPlayerError(TTSCommonPlayer.PlayerEvent playerEvent, int i) {

    }
}