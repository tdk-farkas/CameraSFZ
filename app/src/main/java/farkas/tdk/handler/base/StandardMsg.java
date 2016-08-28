package farkas.tdk.handler.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * 子类需要实现处理消息的函数
 */
public interface StandardMsg {
    void handleStandardMessage(Activity activity, int what, Bundle bundle);

    void handleStandardMessage(Activity activity, int what, Object obj);

    void error(String e);
}