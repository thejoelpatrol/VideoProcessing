import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

public class PPMReader extends Thread {
    private int frames = 0;
    private InputStream stream;
    private BlockingQueue<Image> queue;
    private Object listener;

    public PPMReader(InputStream stream, BlockingQueue<Image> queue, Object listener) {
        this.stream = stream;
        this.queue = queue;
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            PPMFile ppm = null;
            try {
                ppm = readPPMFile(ppm);
            } catch (EOFException e) {
                System.out.println("read whole file");
                Image sentinel = new Image(null, 0, 0);
                putUninterruptibly(sentinel);
                synchronized (listener) {
                    listener.notify();
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Something broke");
                System.exit(1);
            }
            frames++;
            /*if (ppm == null) {
                System.err.println("error");
                Image sentinel = new Image(null, 0, 0);
                putUninterruptibly(sentinel);
                synchronized (listener) {
                    listener.notify();
                }
                return;
            }*/
            //System.out.println("read a ppm file");
            Image image = new Image(ppm.data, ppm.height, ppm.width);
            putUninterruptibly(image);

            synchronized (listener) {
                listener.notify();
            }
        }
    }

    private void putUninterruptibly(Image image) {
        while (true) {
            try {
                queue.put(image);
                return;
            } catch (InterruptedException e) {}
        }
    }

    private PPMFile readPPMFile(PPMFile ppmFile) throws Exception {
        //PPMFile result = new PPMFile();

        //FileReader fileReader = new FileReader(reader.getFD());
        //long pos = reader.getChannel().position();
        //InputStreamReader fileReader = new InputStreamReader(stream);
        InputStream fileReader = stream;

        String magic = "";
        char c = readChar(fileReader);

        if (frames == 6641)
            System.out.println("hey");
        //pos = reader.getChannel().position();
        while (c != '\n') {
            magic += c;
            c = readChar(fileReader);
        }
        //pos = reader.getChannel().position();

        if (!magic.equals("P6"))
            throw new IOException("wrong file type -- not ppm -- no P6 magic number");

        c = readChar(fileReader);
        String line = "";
        while (c != '\n') {
            line += c;
            c = readChar(fileReader);
        }
        String[] width_height = line.split(" ");
        int width = Integer.parseInt(width_height[0]);
        int height = Integer.parseInt(width_height[1]);
        if (ppmFile == null)
            ppmFile = new PPMFile();
        if (width != ppmFile.width || height != ppmFile.height) {
            ppmFile.width = width;
            ppmFile.height = height;
            ppmFile.data = new byte[ppmFile.width * ppmFile.height * 3];
        }

        /*result.width = Integer.parseInt(width_height[0]);
        result.height = Integer.parseInt(width_height[1]);*/

        c = readChar(fileReader);
        String maxVal = "";
        while (c != '\n') {
            maxVal += c;
            c = readChar(fileReader);
        }
        ppmFile.maxVal = Integer.parseInt(maxVal);
        if (ppmFile.maxVal > 255)
            throw new IOException("ppm file uses 2-byte color values -- can only handle maxVal 255");

        int toRead = ppmFile.data.length;

        int totalRead = 0;
        while (toRead > 0) {
            int read = fileReader.read(ppmFile.data, totalRead, toRead);
            if (read < 0)
                break;
            totalRead += read;
            toRead -= read;
        }
        if (toRead > 0)
            throw new Exception("corrupt PPM file");

        return ppmFile;
    }

    private char readChar(InputStream fileReader) throws EOFException, IOException {
        int rc = fileReader.read();
        if (rc < 0)
            throw new EOFException();
        char c = (char)rc;
        return c;
    }

    private class PPMFile {
        public int width;
        public int height;
        public int maxVal;
        public byte[] data;
    }
}
