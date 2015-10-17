package io.bega.kduino.datamodel.events;


/**
 * Created by usuario on 04/08/15.
 */
public class KdUinoDataFileMessageBusEvent {

    private String  path;

    public String getPath()
    {
        return this.path;
    }

    private KdUinoDataFileMessageBusEvent()
    {

    }

    public KdUinoDataFileMessageBusEvent(String path)
    {

        this.path = path;
    }



}
