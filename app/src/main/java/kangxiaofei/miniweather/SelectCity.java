package kangxiaofei.miniweather;
import cn.edu.pku.kangxiaofei.app.MyApplication;
import cn.edu.pku.kangxiaofei.bean.City;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;

    private MyApplication App;
    private List<City> data;
    private String SelectedId;
    private TextView selectcity;
    private TextView cityName;
    private ListView listView;

    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn=(ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        App = (MyApplication)getApplication();
        data=App.getCityList();
        int i = 0;
        while (i<data.size())
        {
            city.add(data.get(i).getCity().toString());
            cityId.add(data.get(i++).getNumber().toString());
        }

        //得到listview控件
        listView = (ListView)findViewById(R.id.city_list);
        //构建适配器Adapter,将数据与显示数据的布局页面绑定； 将ArrayAdapter构造方法的最后一个参数改成city，系统就会加载List对象的数据
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,city);
        //通过setAdapter()方法把适配器设置给ListView
    listView.setAdapter(adapter);

    cityName = (TextView) findViewById(R.id.title_city_name);
    selectcity = (TextView)findViewById(R.id.title_name);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(SelectCity.this, "你单击了:"+city.get(i), Toast.LENGTH_SHORT).show();
            SelectedId = cityId.get(i);
            selectcity.setText("选择城市："+city.get(i));
        }
    });
}

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.title_back:

                Intent i = new Intent(this,selectcity.getClass()) ;
                i.putExtra( "cityCode", SelectedId) ;
                setResult( RESULT_OK, i) ;             //在finish之前， 传递数据
                finish();
                break;
            default:
                break;
        }
    }
}

