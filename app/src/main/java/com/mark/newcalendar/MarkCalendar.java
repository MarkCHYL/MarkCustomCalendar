package com.mark.newcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 日历控件自定义
 *
 * @作者 mark
 * @时间 2018/12/17
 */
public class MarkCalendar extends LinearLayout {

    private ImageView btnPrew, btnNext;//月份的控制
    private TextView tvDate;//年月显示
    private GridView gvDays;//日子的渲染
    //调用系统日历控件信息对象
    private Calendar curDate = Calendar.getInstance();
    private String disPlayFormat;
    public MarkClendarListener markClendarListener;

    public MarkCalendar(Context context) {
        super(context);
    }

    public MarkCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化
        initControl(context, attrs);
    }

    public MarkCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        initControl(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void initControl(Context context, AttributeSet attrs) {
        //初始化布局控件
        bindControl(context);
        //事件初始化
        bindControlEvent();

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MarkCalendar);
        try {
            String format = ta.getString(R.styleable.MarkCalendar_dateFormat);
            disPlayFormat = format;
            if (disPlayFormat == null) {
                disPlayFormat = "MMM yyyy";
            }

        } finally {
            ta.recycle();
        }

        renderCalendar();
    }

    /**
     * 事件初始化
     */
    private void bindControlEvent() {
        btnPrew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一月
                curDate.add(Calendar.MONTH, -1);
                //渲染日历
                renderCalendar();
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一月
                curDate.add(Calendar.MONTH, 1);
                //渲染日历
                renderCalendar();
            }
        });
    }

    /**
     * 渲染日历，业务处理部分
     */
    private void renderCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat(disPlayFormat);
        tvDate.setText(sdf.format(curDate.getTime()));

        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) curDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int prevDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -prevDays);

        int maxCellCount = 6 * 7;
        while (cells.size() < maxCellCount) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        gvDays.setAdapter(new CalendarAdapter(getContext(), cells));
        gvDays.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (markClendarListener == null) {
                    return false;
                }else {
                    markClendarListener.onItemLongPress((Date) parent.getItemAtPosition(position));
                    return true;
                }
            }
        });
    }

    /**
     * 初始化布局控件
     *
     * @param context
     */
    private void bindControl(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.markcalendar_view, this);

        btnPrew = findViewById(R.id.btn_pre);
        btnNext = findViewById(R.id.btn_next);
        tvDate = findViewById(R.id.tv_date);
        gvDays = findViewById(R.id.gv_days);
    }


    private class CalendarAdapter extends ArrayAdapter<Date> {

        LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days) {
            super(context, R.layout.calendar_text_day, days);
            inflater = LayoutInflater.from(context);
        }

        @Nullable
        @Override
        public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
            Date date = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_text_day, parent, false);
            }

            int day = date.getDate();
            ((TextView) convertView).setText(String.valueOf(day));

            Date now = new Date();
            boolean isSameMonth = false;
            if (date.getMonth() == now.getMonth()) {
                isSameMonth = true;
            }
            if (isSameMonth) {
                ((TextView) convertView).setTextColor(Color.parseColor("#000000"));
            } else {
                ((TextView) convertView).setTextColor(Color.parseColor("#666666"));
            }

            if (date.getDate() == now.getDate() && date.getMonth() == now.getMonth()
                    && date.getYear() == now.getYear()) {
                ((TextView) convertView).setTextColor(Color.parseColor("#ff0000"));
                ((Calendar_day_textView) convertView).isToday = true;
            }

            return convertView;
        }
    }

    /**
     * 添加点击事件
     */
    public interface MarkClendarListener{
        void onItemLongPress(Date day);
    }
}
