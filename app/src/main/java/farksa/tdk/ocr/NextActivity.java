package farksa.tdk.ocr;

import android.view.View;
import android.widget.Button;

import farkas.tdk.app.BaseActivity;
import farkas.tdk.handler.NextHandler;

/**
 * author：Administrator
 * time：2016/8/26.17:00
 */
public class NextActivity extends BaseActivity {
    private Button button;
    private NextHandler handler;

    /**
     * 返回上下文视图资源id
     *
     * @return 资源id
     */
    @Override
    protected int initLayout() {
        return R.layout.activity_next;
    }

    /**
     * 初始化视图
     */
    @Override
    protected void initViews() {
        button = (Button) findViewById(R.id.button);
    }

    /**
     * 初始化视图属性
     */
    @Override
    protected void initValues() {
        handler = new NextHandler(context);
    }

    /**
     * 监听视图事件
     */
    @Override
    protected void initListener() {
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        handler.obtainMessage(view.getId(),context);
    }
}
