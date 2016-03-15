package hu.edudroid.gume.timetableview;

import hu.edudroid.gume.timetableview.LessonPicker.LessonPickerListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.view.View.OnLongClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimetableView extends TableLayout implements OnLongClickListener, LessonPickerListener {

    public class CellTag {
        public int day;
        public int hour;

        CellTag(int d, int h) {
            day = d;
            hour = h;
        }
    }

    public interface CourseChangeListener {
        public void onCourseChange(int day, int hour, String course);
    }

    private CourseChangeListener mccListener = null;

    public static String days[] = { "Hétfõ", "Kedd", "Szerda", "Csütörtök", "Péntek" };

    protected ArrayList<String> courseList = new ArrayList<String>();

    private boolean oneday = false;
    private int today = -1;
    private Context context;

    private CellTag editCell = null;

    private int[] textSizes = { 6, 8, 10, 11, 12, 14, 16, 18, 24, 32, 48 };
    private int textSizeI = 6;

    public ArrayList<ArrayList<String>> courses;

    public TimetableView(Context context) {
        super(context);
        this.context = context;
    }

    public TimetableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setCourses(ArrayList<ArrayList<String>> cs) {
        if (cs != null) {
            this.courses = cs;
        }
        if (this.courses == null) return;

        removeAllViews();

        Paint p = new Paint();
        p.setTextSize(context.getResources().getDisplayMetrics().density * textSizes[textSizeI] * (float) 1.2);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        float cellsize = p.measureText(days[3]);

        for (int h = 7; h < 17; h++) {
            TableRow tr = new TableRow(context);
            if (h < 8) {
                tr.addView(new TextView(context));
                tr.setBackgroundColor(Color.LTGRAY);
            } else {
                TextView th = new TextView(context);
                th.setText(String.valueOf(h)+":00");
                th.setBackgroundColor(Color.LTGRAY);
                th.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (textSizes[textSizeI] * 0.8));
                th.setGravity(Gravity.RIGHT);
                th.setTypeface(null, Typeface.BOLD);
                tr.addView(th, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            }
            for (int d = (oneday ? today : 0); d < (oneday ? today + 1 : 5); d++) {
                if (h < 8) {
                    TextView th = new TextView(context);
                    th.setText(days[d]);
                    th.setBackgroundColor(Color.LTGRAY);
                    th.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (textSizes[textSizeI] * 1.2));
                    th.setPadding(5, 5, 0, 0);
                    th.setGravity(Gravity.CENTER);
                    tr.addView(th, (int) cellsize + 12, LayoutParams.WRAP_CONTENT);
                } else {
                    TextView td = new TextView(context);
                    td.setText(courses.get(d).get(h-8));
                    td.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizes[textSizeI]);
                    td.setGravity(Gravity.CENTER);
                    td.setTag(new CellTag(d,h-8));
                    td.setOnLongClickListener(this);
                    tr.addView(td);
                }
            }
            addView(tr);
        }

        requestLayout();
    }

    public void setOneDay(boolean oneday) {
        this.oneday = oneday;
    }

    public void setToday(int day) {
        this.today = day;
    }

    public void increaseTextSize() {
        if (textSizeI < 10) {
            textSizeI++;

            setCourses(null);
        }
    }

    public void decreaseTextSize() {
        if (textSizeI > 0) {
            textSizeI--;

            setCourses(null);
        }
    }

    public void setCourseList(List<String> cl) {
        courseList.clear();
        courseList.addAll(cl);
    }


    public void setTextSize(int i) {
        textSizeI = i;
    }

    public int getTextSize() {
        return textSizeI;
    }

    @Override
    public boolean onLongClick(View v) {

        CellTag ct = (CellTag) v.getTag();
        if (ct == null) return false;

        editCell = ct;

        LessonPicker df = new LessonPicker();
        FragmentActivity a = (FragmentActivity) context;
        df.setListener(this);
        df.setChoices(courseList);
        df.show(a.getSupportFragmentManager(), "Tárgyválasztás");

        return true;
    }

    @Override
    public void onDialogChoice(DialogFragment dialog) {

        LessonPicker lp = (LessonPicker) dialog;
        String selection = null;

        selection = lp.getSelection();

        if (selection == null) return;
        ArrayList<String> d = courses.get(editCell.day);
        d.set(editCell.hour, selection);
        if (mccListener != null) mccListener.onCourseChange(editCell.day, editCell.hour + 8, lp.getSelection());

        setCourses(courses);
        if (!courseList.contains(lp.getSelection())) {
            courseList.add(lp.getSelection());
        }
    }

    public void setCourseChangeListener(CourseChangeListener ccl) {
        mccListener = ccl;
    }
}
