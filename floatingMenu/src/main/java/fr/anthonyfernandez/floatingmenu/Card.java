package fr.anthonyfernandez.floatingmenu;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.anthonyfernandez.floatingmenu.R;

public class Card extends RelativeLayout {
    private TextView header;
    private TextView description;
    private ImageView thumbnail;
    private ImageView icon;

    public Card(Context context) {
        super(context);
        init();
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Card(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.card, this);
//        this.header = (TextView)findViewById(R.id.header);
        this.description = (TextView)findViewById(R.id.description);
        this.thumbnail = (ImageView)findViewById(R.id.thumbnail);
        this.icon = (ImageView)findViewById(R.id.icon);
    }
}