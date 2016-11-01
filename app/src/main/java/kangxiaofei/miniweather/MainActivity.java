package kangxiaofei.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;    //第七步
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.content.SharedPreferences;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.kangxiaofei.bean.TodayWeather;
import cn.edu.pku.kangxiaofei.util.NetUtil;
/**
 * Created by Administrator on 2016/10/11.
 */
public class MainActivity extends Activity implements View.OnClickListener
        {
            private static final int UPDATE_TODAY_WEATHER = 1;
            private ImageView mUpdateBtn;       //在UI线程中,为更新按钮(ImageView)增加单击事件.

    private ImageView mCitySelect;      //为选择城市ImageView添加OnClick事件

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,
            temperatureTv,climateTv,windTv,city_name_Tv;
    private ImageView weatherImg,pmImg;

    private Handler mHandler = new Handler( ) {
        public void handleMessage( android. os. Message msg) {
            switch ( msg. what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather( ( TodayWeather) msg. obj ); //通过消息机制,将解析的天气对象， 通过消息发送给主线程，主线程接收到消息数据后 ，调用 updateTodayWeather函数，更新UI界面上的数据。
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener( this);

        if ( NetUtil. getNetworkState( this) != NetUtil.NETWORN_NONE) {
            Log.d( "myWeather", "网络OK") ;
            Toast.makeText( MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show( ) ;
        }
        else
        {
            Log.d( "myWeather", " 网络挂了");
            Toast.makeText( MainActivity.this, " 网络挂了！", Toast.LENGTH_LONG).show( ) ;
        }
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId()==R.id.title_city_manager)
        {
            Intent i = new Intent(this,SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);  //修改更新按钮的单击事件处理程序

        }
        if (view.getId() == R.id.title_update_btn)
        {
            /*
            *从SharedPreferences中 读取城市的id, 其中101010100为北京城市ID.
             */
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather",cityCode);
            /*
            *if-else 调用检测网络连接状态方法,以检测网络是否连接
            */
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);       //调用方法，获取网络数据.
            }
            else
            {
                Log.d("myWeather", " 网络挂了 ");
                Toast.makeText(MainActivity.this, " 网络挂了 ！", Toast.LENGTH_LONG).show();
            }
        }
    }
    /*
    *编写onActivityResult函数, 用于接收返回的数据。
     */
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        if ( requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data. getStringExtra( "cityCode") ;

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit(); //SharedPreference中写入数据需要使用Editor
            editor.putString("main_city_code", newCityCode); //键值对newCityCode——>main_city_code（值）
            editor.commit();            //点击返回后，使用SharedPreferences保存当前数据，参考 http://www.cnblogs.com/ywtk/p/3795184.html

            Log. d( "myWeather", " 选择的城市代码为 "+newCityCode) ;
            if ( NetUtil. getNetworkState( this) != NetUtil. NETWORN_NONE) {
                Log. d( "myWeather", " 网络OK") ;
                queryWeatherCode(newCityCode) ;   //此处可以进一步优化，选择在city_select页面选择后刷新还是在返回后刷新,都可以.
            } else {
            Log. d( "myWeather", " 网络挂了 ") ;
            Toast. makeText( MainActivity. this, " 网络挂了 ！ ", Toast. LENGTH_LONG) . show( );
        }
        }
    }
    /*
   *初始化控件TextView内容
    */
    void initView( ) {
        city_name_Tv = (TextView) findViewById( R.id.title_city_name) ;
        cityTv = ( TextView) findViewById( R.id.city) ;
        timeTv = ( TextView) findViewById( R.id.time) ;
        humidityTv = (TextView) findViewById( R.id.humidity) ;
        weekTv = (TextView) findViewById( R.id.week_today) ;
        pmDataTv = (TextView) findViewById( R.id.pm_data) ;
        pmQualityTv = (TextView) findViewById( R.id.pm2_5_quality) ;
        pmImg = ( ImageView) findViewById( R.id.pm2_5_img) ;
        temperatureTv = (TextView) findViewById( R.id.temperature) ;
        climateTv = (TextView) findViewById( R.id.climate) ;
        windTv = (TextView) findViewById( R.id.wind) ;
        weatherImg = (ImageView) findViewById( R.id.weather_img);
        city_name_Tv.setText( "N/A") ;
        cityTv.setText( "N/A") ;
        timeTv.setText( "N/A") ;
        humidityTv.setText("N/A") ;
        pmDataTv.setText( "N/A") ;
        pmQualityTv.setText("N/A") ;
        weekTv. setText( "N/A") ;
        temperatureTv.setText("N/A") ;
        climateTv.setText("N/A") ;
        windTv.setText("N/A") ;
    }
    /*
    *编写解析函数， 解析出城市名称已经更新时间信息
     */
    private TodayWeather parseXML( String xmldata)
    {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try{
                XmlPullParserFactory fac = XmlPullParserFactory.newInstance( ) ;
                XmlPullParser xmlPullParser = fac.newPullParser( );
                xmlPullParser.setInput( new StringReader( xmldata));
                int eventType = xmlPullParser.getEventType( ) ;
                Log.d( "myWeather", "parseXML") ;
                while ( eventType != XmlPullParser.END_DOCUMENT)
                {
                    switch ( eventType)
                    {
                        // 判 断当 前事件是否为 文档 开始事件
                        case XmlPullParser.START_DOCUMENT:
                        break;
                        // 判 断当 前事件是否为 标签元素开始事件
                        case XmlPullParser.START_TAG:
                            if(xmlPullParser. getName( ).equals( "resp")) {
                                todayWeather= new TodayWeather( );
                            }
                            if ( todayWeather != null) {
                                if (xmlPullParser.getName().equals("city")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather. setCity( xmlPullParser.getText( ));
                                    //Log.d("myWeather", "city: " + xmlPullParser.getText());  //将城市信息以及更新时间解析出来
                                } else if (xmlPullParser.getName().equals("updatetime")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setUpdatetime( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "updatetime:" + xmlPullParser.getText());  //将城市信息以及更新时间解析出来
                                } else if (xmlPullParser.getName().equals("shidu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather. setShidu( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("wendu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather. setWendu( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("pm25")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setPm25( xmlPullParser.getText( ) ); ;
                                    //Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("quality")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setQuality( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengxiang( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                    fengxiangCount++;
                                } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "date: " + xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh( xmlPullParser.getText( ).substring(2).trim( ));
                                    //Log.d("myWeather", "high: " + xmlPullParser.getText());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow( xmlPullParser.getText( ).substring(2).trim( ));
                                    //Log.d("myWeather", "low: " + xmlPullParser.getText());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "type: " + xmlPullParser.getText());
                                    typeCount++;
                                }
                            }
                    break;
             // 判断当前事件是否为标签元素结束事件
                 case XmlPullParser.END_TAG:
                    break;
            }// 进入下一个元素并触发相应事件
                    eventType = xmlPullParser.next();
        }
        }
        catch ( XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
        /*
        *使用**获取网络数据
        *@param cityCode
        */
     private void queryWeatherCode( String cityCode)
     {
            final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
            Log.d( "myWeather", address) ;
            new Thread( new Runnable( ) {
                @Override
                public void run( ) {
                    HttpURLConnection con=null;
                    TodayWeather todayWeather = null;
                    try{
                        URL url = new URL(address);
                        con = (HttpURLConnection) url.openConnection() ;
                        con.setRequestMethod( "GET") ;
                        con. setConnectTimeout( 8000) ;
                        con. setReadTimeout( 8000) ;
                        InputStream in = con.getInputStream( ) ;
                        BufferedReader reader = new BufferedReader( new InputStreamReader( in) ) ;
                        StringBuilder response = new StringBuilder( );
                        String str;
                        while(( str=reader.readLine( )) != null) {
                            response. append( str) ;
                            Log.d( "myWeather", str) ;
                        }
                        String responseStr=response.toString( ) ;
                        Log.d( "myWeather", responseStr) ;

                        todayWeather = parseXML(responseStr);        //获取网络数据后，调用解析函数
                        if(todayWeather != null){
                            Log.d("myWeather",todayWeather.toString());  //调用 parseXML，并返回TodayWeather对象。

                            Message msg =new Message( );        //通过消息机制，将解析的天气对象，通过消息发送给主线程，主线程接收到消息数据后 ，调用updateTodayWeather函数,更新UI界面上的数据。
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = todayWeather;
                            mHandler.sendMessage( msg) ;
                        }
                    }catch ( Exception e) {
                        e. printStackTrace( ) ;
                    }finally {
                        if( con != null) {
                            con.disconnect( ) ;
                        }
                    }
                }
            }) .start( );
        }
    /*
    *编写 updateTodayWeather 函数,利 用 TodayWeather对象更新UI中 的控件
     */
    void updateTodayWeather( TodayWeather todayWeather) {
        city_name_Tv. setText( todayWeather. getCity( ) +" 天气") ;
        cityTv. setText( todayWeather. getCity( ) ) ;
        timeTv. setText( todayWeather. getUpdatetime( ) + " 发布") ;
        humidityTv. setText( " 湿度： "+todayWeather. getShidu( ) ) ;
        pmDataTv. setText( todayWeather. getPm25( ) ) ;
        pmQualityTv. setText( todayWeather. getQuality( ) ) ;
        weekTv. setText( todayWeather. getDate( ) ) ;
        temperatureTv. setText( todayWeather. getHigh( ) +"~"+todayWeather. getLow( ) ) ;
        climateTv. setText( todayWeather. getType( ) ) ;
        windTv. setText( " 风力 : "+todayWeather. getFengli( ) ) ;
        Toast. makeText( MainActivity. this, " 更新成功！ ", Toast. LENGTH_SHORT) . show( ) ;
    }

}
