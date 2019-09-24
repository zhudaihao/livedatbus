package cn.gxy.livedatbus;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import cn.gxy.livedatbus.bus.LiveDataNormalBus;

public class TextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        //订阅
        LiveDataNormalBus.getInstance().with("homebean", HomeBean.class).observe(this, new Observer<HomeBean>() {
            @Override
            public void onChanged(HomeBean homeBean) {
                //订阅结果回调
                Toast.makeText(TextActivity.this, "" + homeBean.getName(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void send(View view) {
        HomeBean homeBean=new HomeBean("李四");
        LiveDataNormalBus.getInstance().with("homebean",HomeBean.class).postValue(homeBean);

    }
}
