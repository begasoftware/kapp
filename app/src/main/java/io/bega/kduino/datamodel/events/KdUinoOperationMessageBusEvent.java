package io.bega.kduino.datamodel.events;

import io.bega.kduino.datamodel.KdUINOOperations;

/**
 * Created by usuario on 04/08/15.
 */
public class KdUinoOperationMessageBusEvent<T> {

    private KdUINOOperations operations;

    private T data;

    public KdUINOOperations getMessage()
    {
        return this.operations;
    }

    public T getData()
    {
        return this.data;
    }

    private KdUinoOperationMessageBusEvent()
    {

    }

    public KdUinoOperationMessageBusEvent(KdUINOOperations operations, T data)
    {
        this.operations = operations;
        this.data = data;
    }



}
