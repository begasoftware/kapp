package io.bega.kduino.datamodel.events;

/**
 * Created by usuario on 04/08/15.
 */
public class KdUinoMessageBusEvent<T> {

    private int message;

    private T data;

    public int getMessage()
    {
        return this.message;
    }

    public T getData()
    {
        return this.data;
    }

    private KdUinoMessageBusEvent()
    {

    }

    public KdUinoMessageBusEvent(int message, T  data)
    {
        this.message = message;
        this.data = data;
    }



}
