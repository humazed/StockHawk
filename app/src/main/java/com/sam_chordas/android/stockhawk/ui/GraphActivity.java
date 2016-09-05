package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.Collections;

/**
 * User: huma
 * Date: 04-Sep-16
 */
public class GraphActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    private Cursor mCursor;
    private LineChartView lineChartView;
    private LineSet mLineSet;
    int maxRange, minRange, step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        mLineSet = new LineSet();

        lineChartView = (LineChartView) findViewById(R.id.linechart);
        lineChartView
                .setBorderSpacing(Tools.fromDpToPx(15))
                .setAxisBorderValues(0, 125, 25)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(ContextCompat.getColor(this, R.color.line_labels))
                .setXAxis(false)
                .setYAxis(false)
//                .setAxisBorderValues(minRange - 20, maxRange + 100, step)
        ;

        Bundle args = new Bundle();
        args.putString(MyStocksActivity.KEY_SYMBOL,
                getIntent().getStringExtra(MyStocksActivity.KEY_SYMBOL));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{args.getString(MyStocksActivity.KEY_SYMBOL)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
//        findRange(mCursor);
        fillLineSet();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private void fillLineSet() {
        mCursor.moveToFirst();
        for (int i = 0; i < mCursor.getCount(); i++) {
            float price = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            mLineSet.addPoint("" + i, price);
            mCursor.moveToNext();
        }
        mLineSet.setColor(ContextCompat.getColor(this, R.color.line_set))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsColor(ContextCompat.getColor(this, R.color.line_dots));
        lineChartView.addData(mLineSet);
        lineChartView.show();
    }

    public void findRange(Cursor mCursor) {
        ArrayList<Float> mArrayList = new ArrayList<>();
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE))));
        }
        maxRange = Math.round(Collections.max(mArrayList));
        minRange = Math.round(Collections.min(mArrayList));
        if (minRange > 100)
            minRange = minRange - 100;
        if (maxRange - minRange > 10)
            step = Math.round((maxRange * 1.0f - minRange * 1.0f) / 10);
        if (step == 0)
            step = 10;
    }
}