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
package org.example.twainprint.jtwain.transfer;


import org.example.twainprint.entity.TwainMachineName;
import org.example.twainprint.jtwain.Twain;
import org.example.twainprint.jtwain.TwainSource;
import org.example.twainprint.jtwain.exceptions.TwainException;
import org.example.twainprint.jtwain.utils.TwainUtils;

import java.io.File;
import java.util.Random;

/**
 *
 * @author lucifer
 */
public class TwainFileTransfer extends TwainTransfer {

    protected File file;

    public TwainFileTransfer(TwainSource source) {
        super(source);
        file = null;
    }

    public int getImageFileFormat() {
        return source.getImageFileFormat();
    }

    public void setFile(File f) {
        file = f;
    }

    public String getFile() {

            String ext = Twain.ImageFileFormatExts[getImageFileFormat()];
            try {
                File dir = new File(System.getProperty("user.home"), "mmsc/tmp");
                dir.mkdirs();
                if(TwainMachineName.getXhr() == 4){
                //file = File.createTempFile("mmsctwain", ".jpg", dir);
                 return    dir.getPath()+"/mms"+new Random().nextInt(999999999) +".jpg";
                }else if (TwainMachineName.getXhr() == 10){
                  //  file = File.createTempFile("mmsctwain", ".pdf", dir);
                    return dir.getPath()+"/mms"+new Random().nextInt(999999999)+".pdf";
                }
            } catch (Exception e) {
                file = new File("c:\\mmsctwain." + ext);
            }

      //  return file;
        return null;
    }

    @Override
    public void initiate() throws TwainException {
        super.initiate();

        String file = getFile();
        int iff = getImageFileFormat();

        byte[] setup = new byte[260];
        TwainUtils.setString(setup, 0, file);
        TwainUtils.setINT16(setup, 256, iff);
        TwainUtils.setINT16(setup, 258, 0);

        source.call(Twain.DG_CONTROL, Twain.DAT_SETUPFILEXFER, Twain.MSG_SET, setup);
        source.call(Twain.DG_IMAGE, Twain.DAT_IMAGEFILEXFER, Twain.MSG_GET, null);
    }

    @Override
    public void finish() throws TwainException {
        Twain.transferFileImage(file);
    }

    @Override
    public void cancel() throws TwainException {
        if ((file != null) && file.exists()) {
            file.delete();
        }
    }

    @Override
    public void cleanup() throws TwainException {
        setFile(null);
    }

}
