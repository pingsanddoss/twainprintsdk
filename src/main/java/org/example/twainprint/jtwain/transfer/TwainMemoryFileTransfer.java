package org.example.twainprint.jtwain.transfer;

import org.example.twainprint.jtwain.Twain;
import org.example.twainprint.jtwain.TwainSource;
import org.example.twainprint.jtwain.exceptions.TwainException;
import org.example.twainprint.jtwain.utils.TwainUtils;

/**
 * @author ：pings
 * @date ：Created in 2024-8-22 15:32
 * @description：
 * @modified By：
 * @version: $
 */
public class TwainMemoryFileTransfer extends TwainTransfer{

    private final byte[] imx = new byte[48];
    private TwainMemoryTransfer.Info info;

    protected int minBufSize = -1;
    protected int maxBufSize = -1;
    protected int preferredSize = -1;

    public TwainMemoryFileTransfer(TwainSource source) {
        super(source);
    }

    protected void retrieveBufferSizes() throws TwainException {
        byte[] setup = new byte[12];
        TwainUtils.setINT32(setup, 0, minBufSize);
        TwainUtils.setINT32(setup, 4, maxBufSize);
        TwainUtils.setINT32(setup, 8, preferredSize);
        source.call(Twain.DG_CONTROL, Twain.DAT_SETUPMEMXFER, Twain.MSG_GET, setup);
        byte[] setup1 = new byte[260];
//        TwainUtils.setString(setup, 0, file);
        TwainUtils.setINT16(setup, 256, 7);
        TwainUtils.setINT16(setup, 258, 0);

        source.call(Twain.DG_CONTROL, Twain.DAT_SETUPFILEXFER, Twain.MSG_SET, setup1);

        minBufSize = TwainUtils.getINT32(setup, 0);
        maxBufSize = TwainUtils.getINT32(setup, 4);
        preferredSize = TwainUtils.getINT32(setup, 8);
    }

    @Override
    public void initiate() throws TwainException {
        super.initiate();
        retrieveBufferSizes();
        Twain.nnew(imx, preferredSize);
        byte[] buf = new byte[preferredSize];
        info = new TwainMemoryTransfer.Info(imx, buf);
        while (true) {
            source.call(Twain.DG_IMAGE, Twain.DAT_IMAGEMEMFILEXFER, Twain.MSG_GET, imx);
            int bytesWritten = TwainUtils.getINT32(imx, 22);
            int bytesCopied = Twain.ncopy(buf, imx, bytesWritten);
            if (bytesCopied == bytesWritten) {
                Twain.transferMemoryBuffer(info);
                break;
            }
        }
    }

    @Override
    public void finish() throws TwainException {
        int bytesWritten = TwainUtils.getINT32(imx, 22);
        int bytesCopied = Twain.ncopy(info.getBuffer(), imx, bytesWritten);
        if (bytesCopied == bytesWritten) {
            Twain.transferMemoryBuffer(info);
        }
    }

    @Override
    public void cleanup() throws TwainException {
        Twain.ndelete(imx);
    }

    public static class Info {

        private byte[] imx;
        private byte[] buf;
        private int len;

        Info(byte[] imx, byte[] buf) {
            this.imx = imx;
            this.buf = buf;
        }

        public byte[] getBuffer() {
            return buf;
        }

        public int getCompression() {
            return TwainUtils.getINT16(imx, 0);
        }

        public int getBytesPerRow() {
            return TwainUtils.getINT32(imx, 2);
        }

        public int getWidth() {
            return TwainUtils.getINT32(imx, 6);
        }

        public int getHeight() {
            return TwainUtils.getINT32(imx, 10);
        }

        public int getTop() {
            return TwainUtils.getINT32(imx, 14);
        }

        public int getLeft() {
            return TwainUtils.getINT32(imx, 18);
        }

        public int getLength() {
            return TwainUtils.getINT32(imx, 22);
        }

    }
}
