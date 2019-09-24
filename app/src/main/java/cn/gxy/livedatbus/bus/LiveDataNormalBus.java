package cn.gxy.livedatbus.bus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * 数据总线
 * <p>
 * 观察者模式中的 代理层
 * <p>
 * 发布（华为） --》代理（天猫）---》订阅（消费者）
 * <p>
 * 发布了所有对应订阅者 都可以接受到消息（只有订阅代码比 发送代码先执行才能收到 发布信息）
 */
public class LiveDataNormalBus {

    /**
     * 用个map保存 订阅者
     */
    private Map<String, BusMutableLiveData<Object>> map;

    /**
     * 单利
     */
    private LiveDataNormalBus() {
        map = new HashMap<>();
    }

    private static volatile LiveDataNormalBus liveDataBus;

    public static synchronized LiveDataNormalBus getInstance() {
        if (liveDataBus == null) {
            synchronized (LiveDataNormalBus.class) {
                if (liveDataBus == null) {
                    liveDataBus = new LiveDataNormalBus();
                }
            }
        }

        return liveDataBus;
    }


    /**
     * 订阅者 保存到map 的方法
     */
    public synchronized <T> BusMutableLiveData<T> with(String key, Class<T> type) {

        //如果map没有包含传进来的键（key） 就保存到map
        if (!map.containsKey(key)) {
            map.put(key, new BusMutableLiveData<Object>());
        }

        return (BusMutableLiveData<T>) map.get(key);
    }


    /**
     * 反射修改源码
     */



    public static class BusMutableLiveData<T> extends MutableLiveData<T> {
        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            super.observe(owner, observer);

            hook(observer);

        }


        /**
         * 修改值
         * mLastVersion 和mVersion
         *
         * @param observer
         */
        private void hook(Observer<? super T> observer) {
            try {
                //1获取mLastVersion
                Class<LiveData> liveDataClass = LiveData.class;

                //获取map集合变量
                Field mObservers = liveDataClass.getDeclaredField("mObservers");

                //权限
                mObservers.setAccessible(true);

                //获取map 类实例
                Object mapObject = mObservers.get(this);

                //获取map 类
                Class<?> mObserversClass = mapObject.getClass();

                //获取map的get方法 (参数第一个是方法名，第二个是方法参数类型)
                Method getMethod = mObserversClass.getDeclaredMethod("get", Object.class);

                //权限
                getMethod.setAccessible(true);
                //执行get方法  第一个参数 方法所在类是实例，第二个参数是map值对应的key --->Observer
//                private SafeIterableMap<Observer<? super T>, ObserverWrapper> mObservers =
//                        new SafeIterableMap<>();
                Object invokeEntry = getMethod.invoke(mapObject, observer);

                //获取map中的value--》ObserverWrapper
                Object mapValue = null;
                if (invokeEntry != null && invokeEntry instanceof Map.Entry) {
                    mapValue = ((Map.Entry) invokeEntry).getValue();
                }

                if (mapValue == null) {
                    throw new NullPointerException("mapValue is null");
                }

                //获取ObserverWrapper类
                /**
                 *  ObserverWrapper(Observer<? super T> observer) {
                 *             mObserver = observer;
                 *         }
                 *  注意ObserverWrapper类个构造方法 参数使用 super T 容易导致 多态冲突；使用反射获取这个类 需要调用getSuperclass()方法
                 */
                Class<?> observerWrapperClass = mapValue.getClass().getSuperclass();
                //获取ObserverWrapper类里面的变量mLastVersion
                Field mLastVersion = observerWrapperClass.getDeclaredField("mLastVersion");
                //权限
                mLastVersion.setAccessible(true);


                //2获取和mVersion
                Field mVersion = liveDataClass.getDeclaredField("mVersion");
                mVersion.setAccessible(true);


                //3修改值 mLastVersion=mVersion
                //获取mVersion值
                Object mVersionObject = mVersion.get(this);
                //参数 字段所在类的实例 设置的值
                mLastVersion.set(mapValue, mVersionObject);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


}
