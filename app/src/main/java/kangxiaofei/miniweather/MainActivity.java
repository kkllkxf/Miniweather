package kangxiaofei.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.kangxiaofei.util.NetUtil;
/**
 * Created by Administrator on 2016/10/11.
 */
public class MainActivity extends Activity implements View.OnClickListener
{
    private ImageView mUpdateBtn;       //在UI线程中,为更新按钮(ImageView)增加单击事件.

    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn) ;
        mUpdateBtn. setOnClickListener( this) ;

        if ( NetUtil. getNetworkState( this) != NetUtil.NETWORN_NONE) {
            Log.d( "myWeather", "网络OK") ;
            Toast.makeText( MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show( ) ;
        }
        else
        {
            Log.d( "myWeather", " 网络挂了 ");
            Toast.makeText( MainActivity.this, " 网络挂了！", Toast.LENGTH_LONG).show( ) ;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_update_btn)
        {
            /*
            *从SharedPreferences中 读取城市的id, 其中101010100为北京城市ID.
             */
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather", cityCode);
            /*
            *if-else 调用检测网络连接状态方法,以检测网络是否连接
            */
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather", " 网 络OK");
                queryWeatherCode(cityCode);       //调用方法，获取网络数据.
            }
            else
            {
                Log.d("myWeather", " 网 络挂了 ");
                Toast.makeText(MainActivity.this, " 网 络挂了 ！ ", Toast.LENGTH_LONG).show();
            }
        }
    }
        /*
        *使用**获取网络数据
        *@param cityCode
        */
        private void queryWeatherCode( String cityCode)
        {
            final String address = "http: //wthrcdn.etouch.cn/Weather Api?citykey="+cityCode;
            Log. d( "myWeather", address) ;
            new Thread( new Runnable( ) {
                @Override
                public void run( ) {
                    HttpURLConnection con=null;
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
                        while( ( str=reader. readLine( ) ) != null) {
                            response. append( str) ;
                            Log. d( "myWeather", str) ;
                        }
                        String responseStr=response. toString( ) ;
                        Log. d( "myWeather", responseStr) ;
                    }catch ( Exception e) {
                        e. printStackTrace( ) ;
                    }finally {
                        if( con != null) {
                            con. disconnect( ) ;
                        }
                    }
                }
            }) .start( ) ;
    }

}
