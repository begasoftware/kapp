package io.bega.kduino.services;

/**
 * Created by usuario on 25/07/15.
 */
public interface ICounter {

    void receivedCommand(char command);

    void updateData(int counter);

    void totalCounter(int total);

}
