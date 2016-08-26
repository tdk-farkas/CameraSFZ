package farkas.tdk.app;

import android.app.Application;
import java.util.HashMap;
import java.util.Map;

import farkas.tdk.handler.ProgressHandler;

/**
 * author：Administrator
 * time：2016/8/26.9:39
 */
public class MyApp extends Application {
    private static MyApp instance;//全局上下文

    private Map<String,Object> map;//全局共享数据
    
    public void clear(){
        map.clear();
    }
    
    public Object getItem(String key){
        return map.get(key);
    }
    
    public boolean addItem(String key,Object obj){
        map.put(key,obj);
        return map.containsKey(key);
    }
    
    public boolean removeItem(String key){
        map.remove(key);
        return !map.containsKey(key);
    }
    
    /**
     * 获取application实例对象
     * @return  MyApplication实例对象
     */
    public static MyApp getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        map = new HashMap<String, Object>();
    }
}