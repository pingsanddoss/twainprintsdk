/*
 * Copyright 2018 (c) Denis Andreev (lucifer).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.twainprint.jtwain.scan;


import org.example.twainprint.jtwain.*;
import org.example.twainprint.jtwain.exceptions.TwainException;
import org.example.twainprint.jtwain.transfer.TwainMemoryTransfer;
import org.example.twainprint.jtwain.transfer.TwainTransfer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

/**
 *
 * @author lucifer
 */
public class Source implements TwainListener {

    private double dpi = 300.0;
    private ColorMode color = ColorMode.GRAYSCALE;
    private boolean autoDocumentFeeder = true;
    private boolean systemUI = true;
    private boolean doubleSide = false;
    private boolean removeBlankSide = false;
    private boolean maticdskem = false;
    private boolean maticborderdetection = false;

    private int xhr = 4;

    private String name;

    private final Object syncObject = new Object();
    private ExecutorService exec;

    private List<File> fileList = new ArrayList<>();

    public Source() {
    }

    public int getXhr() {
        return xhr;
    }

    public void setXhr(int xhr) {
        this.xhr = xhr;
    }

    public boolean getMaticborderdetection() {
        return maticborderdetection;
    }

    public void setMaticborderdetection(boolean maticborderdetection) {
        this.maticborderdetection = maticborderdetection;
    }

    public boolean getMaticdskem() {
        return maticdskem;
    }

    public void setMaticdskem(boolean maticdskem) {
        this.maticdskem = maticdskem;
    }

    public boolean getRemoveBlankSide() {
        return removeBlankSide;
    }

    public void setRemoveBlankSide(boolean removeBlankSide) {
        this.removeBlankSide = removeBlankSide;
    }

    public boolean getDoubleSide() {
        return doubleSide;
    }

    public void setDoubleSide(boolean doubleSide) {
        this.doubleSide = doubleSide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDpi() {
        return dpi;
    }

    public void setDpi(double dpi) {
        this.dpi = dpi;
    }

    public ColorMode getColor() {
        return color;
    }

    public void setColor(ColorMode color) {
        this.color = color;
    }

    public boolean isAutoDocumentFeeder() {
        return autoDocumentFeeder;
    }

    public void setAutoDocumentFeeder(boolean autoDocumentFeeder) {
        this.autoDocumentFeeder = autoDocumentFeeder;
    }

    public boolean isSystemUI() {
        return systemUI;
    }

    public void setSystemUI(boolean systemUI) {
        this.systemUI = systemUI;
    }

    @Override
    public void update(TwainIOMetadata.Type type, TwainIOMetadata metadata) {
//        System.out.println(type + " -> " + metadata.getState() + ": " + metadata.getStateStr());
        if (type == TwainIOMetadata.NEGOTIATE && metadata.getState() == 4) {
            setupSource(metadata.getSource());
        } else if (type == TwainIOMetadata.ACQUIRED && metadata.getState() == 7) {
            pushImage(metadata.getImage());
            metadata.setImage(null);
  //          if(type == TwainIOMetadata.MEMORY){

        } else if (type == TwainIOMetadata.STATECHANGE && metadata.getState() == 3 && metadata.getLastState() == 4) {
            jobDone();
        }
    }

    private void setupSource(TwainSource source) {
        try {
            source.setShowProgressBar(true);
            source.setShowUI(systemUI);

            if (true) {
//                source.setShowUI(false);
                source.setResolution(dpi);
                source.setCapability(Twain.CAP_DUPLEXENABLED,doubleSide);
                source.setCapability(Twain.ICAP_XFERMECH,Twain.TWSX_FILE);
                source.setCapability(Twain.ICAP_IMAGEFILEFORMAT,xhr);
                //source.setCapability(Twain.ICAP_IMAGEFILEFORMAT,10);
//                source.setCapability(Twain.ICAP_AUTODISCARDBLANKPAGES,removeBlankSide);
                source.setCapability(Twain.ICAP_AUTOMATICDESKEW,maticdskem);
//                source.setCapability(Twain.ICAP_AUTOMATICBORDERDETECTION,maticborderdetection);
                TwainCapability pt = source.getCapability(Twain.ICAP_PIXELTYPE);
                switch (color) {
                    case BW:
                        pt.setCurrentValue(0);
                        break;
                    case GRAYSCALE:
                        pt.setCurrentValue(1);
                        break;
                    case COLOR:
                        pt.setCurrentValue(2);
                        break;
                }

                source.setCapability(Twain.CAP_FEEDERENABLED, autoDocumentFeeder ? 1 : 0);
            }
        } catch (TwainException e) {
            e.printStackTrace();
        }
    }

    private void pushImage(final BufferedImage image) {
        exec.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    File f = File.createTempFile("img", ".jpg");
                    //File f = new File("");
                    //f.delete();
                    //ImageIO.getImageWritersByFormatName("pdf").next().setOutput(ImageIO.createImageOutputStream(f);
                    ImageIO.write(image, "jpg", f);
                   // ImageIO.write()
                    fileList.add(f);
                } catch (IOException ex) {

                }
            }
        });
    }

    private void jobDone() {
        exec.shutdown();
        try {
            while (!exec.awaitTermination(100, TimeUnit.MILLISECONDS)) {
            }
        } catch (InterruptedException ex) {
        }

        synchronized (syncObject) {
            syncObject.notifyAll();
        }

        exec = null;
    }

    public List<File> scan() {
        exec = Executors.newFixedThreadPool(1);

        TwainScanner scanner = TwainScanner.getScanner();

        try {
            scanner.select(name);
            scanner.addListener(this);
            scanner.acquire();

            synchronized (syncObject) {
                syncObject.wait();
            }

        } catch (TwainException | InterruptedException e) {
        }

        return fileList;
    }

    public static enum ColorMode {
        BW,
        GRAYSCALE,
        COLOR
    }

}
