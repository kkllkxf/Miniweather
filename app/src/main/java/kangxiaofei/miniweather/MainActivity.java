package kangxiaofei.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import cn.edu.pku.kangxiaofei.util.NetUtil;
/**
 * Created by Administrator on 2016/10/11.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.weather_info);

        if ( NetUtil. getNetworkState( this) != NetUtil. NETWORN_NONE) {
            Log.d( "myWeather", "网络OK") ;
            Toast.makeText( MainActivity. this, "网络OK！", Toast.LENGTH_LONG).show( ) ;
        }
        else
        {
            Log.d( "myWeather", " 网络挂了 ") ;
            Toast.makeText( MainActivity. this, " 网络挂了！", Toast.LENGTH_LONG).show( ) ;
        }
    }
}
