import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcesssamentoComMultiplosThreads {

    private static List<String> imagensProcessadas = new ArrayList<>();

    public static void main(String[] args){
        String inputDirectory = "C:\\Users\\mailo\\Videos\\img";
        String outputDirectory = "C:\\Users\\mailo\\Videos\\img\\output";

        File inputFolder = new File(inputDirectory);
        File[] imageFiles = inputFolder.listFiles();

        if (imageFiles != null){

            int numThreads = Runtime.getRuntime().availableProcessors();
            List<Thread> usedThreads = new ArrayList<>();

            for (File imageFile : imageFiles){
                if (imageFile.isFile() && isImageFile(imageFile) && !foiProcessada(imageFile.getName())){

                    imagensProcessadas.add(imageFile.getName());

                    Thread imageProcessingThread = new Thread(() -> processImage(imageFile, outputDirectory));
                    usedThreads.add(imageProcessingThread);
                    imageProcessingThread.start();

                    if(usedThreads.size() >= numThreads){
                        aguardarThreads(usedThreads);
                    }
                }
            }

            aguardarThreads(usedThreads);
        }else {
            System.out.println("O diretorio de entrada não existe ou está vazia");

        }
    }
    private static boolean isImageFile(File file){
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
    }
    private static boolean foiProcessada(String imageName){
        return imagensProcessadas.contains(imageName);
    }
    private static void processImage(File imageFile, String outputDirectory){
        try{
            BufferedImage originalImage = ImageIO.read(imageFile);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            for (int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    Color color = new Color(originalImage.getRGB(i,j));
                    int grayscaleValue = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                    int grayscaleColor = new Color(grayscaleValue, grayscaleValue, grayscaleValue).getRGB();
                    originalImage.setRGB(i,j, grayscaleColor);
                }
            }

            File outputImageFile = new File(outputDirectory, imageFile.getName());
            ImageIO.write(originalImage, "png", outputImageFile);

            System.out.println("Imagem processada e salva: " + outputImageFile.getAbsolutePath());

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    private static void aguardarThreads(List<Thread> threads){
        for (Thread thread : threads){
            try {
                thread.join();
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
        threads.clear();
    }

}
