package com.gdlearn.backsidemanager.utils;

import com.google.code.kaptcha.Producer;

import java.awt.image.BufferedImage;

public class MyProducer implements Producer {
    @Override
    public BufferedImage createImage(String s) {
        return null;
    }

    @Override
    public String createText() {
        return null;
    }
}
