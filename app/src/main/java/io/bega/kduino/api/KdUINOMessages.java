package io.bega.kduino.api;

/**
 * Created by usuario on 01/08/15.
 */
public class KdUINOMessages {

    public static final int DEFAULT = 0x00;

    public static final  int INTERNET_ON =  0x01; //  "INTERNET_ON";

    public static final int INTERNET_OFF = 0x02; // "INTERNET_OFF";

    public static final int SHOW_DATA = 0x03; // "SHOW_DATA";

    public static final int MAKE_KDUINO = 0x04; //"MAKE_KDUINO";

    public static final int LAUNCH_TUTORIAL = 0x05; // "LAUNCH_TUTORIAL";

    public static final int ADD_MAKE_KDUINO = 0x06; //"ADD_MAKE_KDUINO";

    public static final int EDIT_MAKE_KDUINO = 0x07; // "EDIT_MAKE_KDUINO";

    public static final int SETTINGS = 0x09; //"SETTINGS";

    public static final int CONNECT_KDUINO = 0x0A; //"CONNECT_KDUINO";

    public static final int SELECT_KDUINO_MODEL = 0x0B; //"SELECT_KDUINO_MODEL";

    public static final int SEND_COMMAND_KDUINO = 0x0C; // "SEND_COMMANDS";

    public static final int DOWNLOAD_MAP = 0x0D; //"DOWNLOAD_MAP";

    public static final int HELP_VIDEO = 0x0E; //"HELP_VIDEO";

    public static final int HELP_ABOUT = 0x0F; // "HELP_ABOUT";

    public static final int SEARCH_DEVICES = 0x10; //"SEARCH_DEVICES";

    public static final int BLUETOOTH_ON = 0x011; //"BLUETOOTH_ON";

    public static final int BLUETOOTH_OFF = 0x12; //"BLUETOOTH_OFF";

    public static final int CONNECT_BLUETOOTH = 0x13; //"CONNECT_BLUETOOTH";

    public static final int CONNECT_BLUETOOTH_ACTIVATE = 0x14; //"CONNECT_BLUETOOTH_ACTIVATE";

    public static final int POSITION_KDUINO = 0x15; //"POSITION_KDUINO";

    public static final int DISCONNECT_KDUINO = 0x16; //"DISCONNECT_KDUINO";

    public static final int SEND_COMMAND_DATA_KDUINO = 0x17; //"COMMAND_DATA_KDUINO";

    public static final int SHOW_MY_DATA = 0x018;

    public static final int BLUETOOTH_ERROR = 0x019;

    public static final int BLUETOOTH_CONNECTION_LOST = 0x01A;

    public static final int KDUINO_STATUS_RECIEVED = 0x01B;

    public static final int BLUETOOTH_CONNECT_TO_KDUINO = 0x01C;

    public static final int PROMPT_LOGIN = 0x01D;

    public static final int LOGIN_OK = 0x01E;

    public static final int LOGIN_KO = 0x01F;

    public static final int RECIEVE_INFO = 0x020;

    public static final int DATA_SEND_OK = 0x021;

    public static final int DATA_SEND_KO = 0x022;

    public static final int REFRESH_INFO = 0x023;
}
