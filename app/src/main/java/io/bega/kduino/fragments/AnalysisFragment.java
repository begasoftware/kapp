package io.bega.kduino.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import io.bega.kduino.IClickable;
import io.bega.kduino.R;
import io.bega.kduino.SettingsManager;
import io.bega.kduino.activities.BluetoothActivity;
import io.bega.kduino.activities.KdUINOMarkerView;
import io.bega.kduino.datamodel.Analysis;
import io.bega.kduino.datamodel.Captures;
import io.bega.kduino.datamodel.DataSet;
import io.bega.kduino.datamodel.events.KdUinoAnalysisMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoDataFileMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoSendAnalysisDataMessageBusEvent;
import io.bega.kduino.datamodel.events.KdUinoSendDataMessageBusEvent;
import io.bega.kduino.kdUINOApplication;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AnalysisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalysisFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener,
        OnChartGestureListener, OnChartValueSelectedListener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private Button btnSendToServer;

    ArrayList<Analysis> data;

    Bus bus;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalysisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnalysisFragment newInstance(String param1, String param2) {
        AnalysisFragment fragment = new AnalysisFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_analysis, container, false);

        /*btnSendToServer = (Button)rootView.findViewById(R.id.btnSendToServerData);
        btnSendToServer.setOnClickListener(this); */

      /*  tvX = (TextView) rootView.findViewById(R.id.tvXMax);
        tvY = (TextView) rootView.findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) rootView.findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) rootView.findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this); */

        mChart = (LineChart) rootView.findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        KdUINOMarkerView mv = new KdUINOMarkerView(this.getActivity(), R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        DataSet dataSet =  ((BluetoothActivity)getActivity()).getData();
        this.bus = ((kdUINOApplication)getActivity().getApplication()).getBus();
        this.setData(dataSet);
    }

    private void setData(DataSet dataSet)
    {
        ArrayList<Analysis> analysises = dataSet.getAnalysises();
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> kdVals = new ArrayList<Entry>();
        ArrayList<Entry> r2Vals = new ArrayList<Entry>();

        int i = 0;
        for (Analysis analysis : analysises)
        {

            if (Double.isNaN(analysis.R2))
            {
                continue;
            }

            if (Double.isNaN(analysis.Kd))
            {
                continue;
            }

            xVals.add(analysis.date);
            kdVals.add(new Entry((float)analysis.Kd, i));
            r2Vals.add(new Entry((float)analysis.R2, i));
            i++;
        }

        LineDataSet set1 = new LineDataSet(kdVals, "Kd data temporal");
        LineDataSet set2 = new LineDataSet(r2Vals, "R2 data temporal");
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.RED);


        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLUE);
        set2.setLineWidth(1f);
        set2.setCircleSize(3f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.BLUE);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
        mChart.setVisibleXRange(5); // allow 20 values to be displayed at once on the x-axis, not more
        mChart.moveViewToX(kdVals.size() - 5); // se
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        //setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
       /* if (v.getId() == R.id.btnSendToServerData)
        {
            SettingsManager manager = new SettingsManager(getActivity());
            String nameFile = manager.getLastTotalFileName();
            bus.post(new KdUinoDataFileMessageBusEvent(nameFile));
            DataSet dataSet =  ((BluetoothActivity)getActivity()).getData();
            ((BluetoothActivity)getActivity()).sendDataToTask(dataSet);
            dismiss();
            //this.bus.post(new KdUinoSendAnalysisDataMessageBusEvent(dataSet));
        } */
    }
}
