package android.coolweather.com.coolweather.util;

import android.content.Context;
import android.coolweather.com.coolweather.R;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetButton extends RelativeLayout {
    private TextView stringRight;
    private TextView stringLeft;
    public SetButton(Context context){
        super(context,null);
    }
    public SetButton(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        LayoutInflater.from(context).inflate(R.layout.button_doubletext,this,true );
        this.stringLeft=(TextView)findViewById(R.id.text_left);
        this.stringRight=(TextView)findViewById(R.id.text_right);

        this.setClickable(true);
        this.setFocusable(true);
    }
    public void setStringRight(String stringRight){
        this.stringRight.setText(stringRight);
    }
    public void setStringLeft(String stringLeft){
        this.stringLeft.setText(stringLeft);
    }
}
