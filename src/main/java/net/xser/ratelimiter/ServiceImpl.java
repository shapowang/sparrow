package net.xser.ratelimiter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceImpl implements IService {

    @Override
    public void run() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()));
    }
}