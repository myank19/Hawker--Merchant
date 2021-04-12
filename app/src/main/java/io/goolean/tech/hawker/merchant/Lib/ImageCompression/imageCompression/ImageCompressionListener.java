package io.goolean.tech.hawker.merchant.Lib.ImageCompression.imageCompression;

public interface ImageCompressionListener {
    void onStart();

    void onCompressed(String filePath);
}
