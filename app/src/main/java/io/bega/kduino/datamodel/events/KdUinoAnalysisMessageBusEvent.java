package io.bega.kduino.datamodel.events;

import io.bega.kduino.datamodel.DataSet;

/**
 * Created by usuario on 04/08/15.
 */
public class KdUinoAnalysisMessageBusEvent{

    private DataSet data;

    public DataSet getData()
    {
        return this.data;
    }

    private KdUinoAnalysisMessageBusEvent()
    {

    }

    public KdUinoAnalysisMessageBusEvent(DataSet data)
    {
        this.data = data;
    }



}
