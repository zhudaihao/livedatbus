package cn.gxy.livedatbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import cn.gxy.livedatbus.bus.LiveDataNormalBus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /**
         * 使用总线
         * 消费者 订阅
         * 注意订阅在主线程
         */
        LiveDataNormalBus.getInstance()
                //订阅的key ，第二个参数 key对应的值
                .with("homebean", HomeBean.class)
                .observe(MainActivity.this, new Observer<HomeBean>() {
                    @Override
                    public void onChanged(HomeBean phoneBean) {
                        //发布结果回调
                        Toast.makeText(MainActivity.this, "" + phoneBean.getName(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


    /**
     * 发布
     * <p>
     * 华为厂家 发布手机
     * 可以在子线程
     *
     * @param view
     */
    public void send(View view) {
//        PhoneBean phoneBean = new PhoneBean("华为M30");
//        LiveDataBus.getInstance().with("huawei", PhoneBean.class).postValue(phoneBean);


        HomeBean homeBean = new HomeBean("张三");
        LiveDataNormalBus.getInstance().with("homebean", HomeBean.class).postValue(homeBean);

    }


    public void lick(View view) {
        startActivity(new Intent(this, TextActivity.class));
    }

}
