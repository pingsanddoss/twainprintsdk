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


import org.example.twainprint.jtwain.Twain;
import org.example.twainprint.jtwain.TwainSource;
import org.example.twainprint.jtwain.exceptions.TwainException;
import org.example.twainprint.jtwain.exceptions.TwainUserCancelException;
import org.example.twainprint.jtwain.utils.TwainUtils;

import java.io.IOException;

/**
 *
 * @author lucifer
 */
public class TwainTransfer {

    protected TwainSource source;
    protected boolean isCancelled;

    public TwainTransfer(TwainSource source) {
        this.source = source;
        isCancelled = false;
    }

    public void initiate() throws TwainException {
        commitCancel();
    }

    public void setCancel(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    protected void commitCancel() throws TwainException {
        if (isCancelled && (source.getState() == Twain.STATE_TRANSFERREADY)) {
            throw new TwainUserCancelException();
        }
    }

    public void finish() throws TwainException {
    }

    public void cancel() throws TwainException {
    }

    public void cleanup() throws TwainException {
    }

    public static class Info{

        private byte[] imx;
        private byte[] buf;
        private int    len;

        Info(byte[] imx,byte[] buf){this.imx=imx;this.buf=buf;}

        public byte[] getBuffer(){return buf;}
        public int getCompression(){return TwainUtils.getINT16(imx,0);}
        public int getBytesPerRow(){return TwainUtils.getINT32(imx,2);}
        public int getWidth(){return TwainUtils.getINT32(imx,6);}         // columns
        public int getHeight(){return TwainUtils.getINT32(imx,10);}       // rows
        public int getLeft(){return TwainUtils.getINT32(imx,14);}         // xoffset
        public int getTop(){return TwainUtils.getINT32(imx,18);}          // yoffset
        public int getLength(){return TwainUtils.getINT32(imx,22);}       // bytesWritten

        public int getMemFlags(){return TwainUtils.getINT32(imx,26);}
        public int getMemLength(){return TwainUtils.getINT32(imx,30);}
        public long getMemPtr(){return TwainUtils.getPtr(imx,34);}

        public String toString(){
            String s=getClass().getName()+"\n";
            s+="\tcompression = "+getCompression()+"\n";
            s+="\tbytes per row = "+getBytesPerRow()+"\n";
            s+="\ttop = "+getTop()+" left = "+getLeft()+" width = "+getWidth()+" height = "+getHeight()+"\n";
            s+="\tbytes = "+getLength()+"\n";

            s+="\tmemory flags   = 0x"+Integer.toHexString(getMemFlags())+"\n";
            s+="\tmemory length  = "+getMemLength()+"\n";
            s+="\tmemory pointer = 0x"+Long.toHexString(getMemPtr())+"\n";

            return s;
        }
    }
}
