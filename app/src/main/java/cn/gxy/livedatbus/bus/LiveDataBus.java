package cn.gxy.livedatbus.bus;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;

/**
 * 数据总线
 * <p>
 * 观察者模式中的 代理层
 * <p>
 * 发布（华为） --》代理（天猫）---》订阅（消费者）
 *
 * 发布了所有对应订阅者 都可以接受到消息（可以出现 订阅代码在发布后执行也能接受 发布的信息）
 */
public class LiveDataBus {

    /**
     * 用个map保存 订阅者
     */
    private Map<String, Object> map;

    /**
     * 单利
     */
    private LiveDataBus() {
        map = new HashMap<>();
    }

    private static volatile LiveDataBus liveDataBus;

    public static synchronized LiveDataBus getInstance() {
        if (liveDataBus == null) {
            synchronized (LiveDataBus.class) {
                if (liveDataBus == null) {
                    liveDataBus = new LiveDataBus();
                }
            }
        }

        return liveDataBus;
    }


    /**
     * 订阅者 保存到map 的方法
     */
    public synchronized<T> MutableLiveData<T> with(String key,Class<T> type){

        //如果map没有包含传进来的键（key） 就保存到map
        if (!map.containsKey(key)){
            map.put(key,new MutableLiveData<T>());
        }

        return (MutableLiveData<T>) map.get(key);
    }



}
