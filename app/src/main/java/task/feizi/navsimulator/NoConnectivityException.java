package task.feizi.navsimulator;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "عدم اتصال به اینترنت ! لطفا اتصال خود به اینترنت را چک نمایید.";
    }
}
