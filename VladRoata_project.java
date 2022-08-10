//Vlad Roata
//105033440
//COMP-3500-1-R-2020F

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class VladRoata_project {
	public static void main(String[]args) throws InterruptedException {
		Scanner sc = new Scanner(System.in);
		
		//User inputs. Get input file name, output file name, value for n.
		System.out.println("Enter input file name");
		String fullFileName = sc.nextLine();
		BufferedImage initialImage = null;
	    File f = null;
	    String[]fileNameSplit = fullFileName.split("\\."); //Split filename to get extension 
	    String extension = fileNameSplit[1]; //get extension to save the image in the same format
	    System.out.println("Enter output file name");
		String finalImage = sc.next();
	    System.out.println("Enter a value for n");
		int n = sc.nextInt();
		System.out.println("Enter 1 for Horizontal, 2 for Vertical");
		int mode = sc.nextInt();
		
		
		//Attempt to read input file name
		try{
			f = new File(fullFileName); //File should be read as long as it's in the same folder as the java project file.
			initialImage = ImageIO.read(f);
		}
		catch(IOException e){
		      System.out.println(e);
		}
		
		int width = initialImage.getWidth(); //width of image
		int height = initialImage.getHeight(); //height of image
		
		int[][][]scanlines = new int[height/n][n][width]; //Represents the scanlines (horizontal lines) 
		int[][]excess = new int[height%n][width]; //Stores excess scanlines, which are not swapped (they have no pair)
		
		int[][][] vScanLines = new int[width/n][n][height];
		int[][] vExcess = new int[width%n][height];
		
		//Since it doesn't make much sense to scan the image's pixels through a 3 layer nested for loop, I use these counters
		int bigGroupCount = 0; //bigGroupCount represents the 3d array index
		int smallGroupCount = 0; //smallGroupCount represents the 2d array index
		int flag = 0;
		
		if(mode == 1) { //HORIZONTAL
			//Scan through all pixels of the image
			for(int i = 0; i<height; i++) {
				for(int j = 0; j<width; j++) {
					/*This statement exists to catch the scenario where height%n != 0. 
					This means that there are extra scanlines that need to be picked up, I store them in an array called "excess"*/
					if(bigGroupCount == height/n-1 && smallGroupCount == n-1 && j == width-1 && height%n != 0 && flag == 0 ) {
						flag = 1;
						bigGroupCount = 0; //reset bigGroupCount and smallGroupCount
						smallGroupCount = 0;
						i++;
						j=0;
					}
					if(flag == 1) { //When the original array is full, it's time to start filling the excess array
						int pixel = initialImage.getRGB(j, i);			    
					    excess[smallGroupCount][j] = pixel;
					    if(j==width-1) {
							smallGroupCount++;
						}
					}
					else { //fill the original array
						int pixel = initialImage.getRGB(j, i);	//read and store pixel data		    
					    scanlines[bigGroupCount][smallGroupCount][j] = pixel;
					    if(j==width-1) { //increment smallGroupCount when one horizontal line has been read
							smallGroupCount++;
						}
						if(smallGroupCount == n) { //increment bigGroupCount when a 2d-array segment is full
							smallGroupCount = 0;
							bigGroupCount++;
						}
					}
				}
			}
			
			//Swap the even-numbered groups with the odd-numbered ones. (X1 with X2, X3 with X4, etc)
			//The excess lines aren't swapped
			for(int i = 0; i<height/n-1; i+=2) {
				int [][] temp1 = scanlines[i];
				int [][] temp2 = scanlines[i+1];
				scanlines[i] = temp2;
				scanlines[i+1] = temp1;
			}
			
			try {
				//create new image according to the chosen output name
				f = new File(""+finalImage+"."+extension);
				BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				int y = 0;
				
				/*Paint the image with the pixel values in scanlines[][][]. This will not paint the entire
				  image if there are excess scanlines, but they are painted immediately afterwards*/
				for(int i = 0; i<height/n; i++) {
					for(int j = 0; j<n; j++) {
						for(int k = 0; k<width; k++) {
							outputImage.setRGB(k, y, scanlines[i][j][k]);
							if(k == width-1) {
								y++;
							}
						}
					}
				}
				/*Add the extra spare lines, these lines aren't swapped around because they have no 
				even-numbered group to swap with*/
				for(int j = 0; j<height%n; j++) {
					for(int k = 0; k<width; k++) {
						outputImage.setRGB(k, y, excess[j][k]);
						if(k == width-1) {
							y++;
						}
					}
				}
				//commit the changes, write the image
				ImageIO.write(outputImage, extension, f);
			}
			catch(IOException e){
				System.out.println("Error writing to file");
			}
		}

		
		else { //VERTICAL
			//Scan through all pixels of the image
			for(int j = 0; j<width; j++) {
				for(int i = 0; i<height; i++) {
					/*This statement exists to catch the scenario where width%n != 0. 
					This means that there are extra scanlines that need to be picked up, I store them in an array called "vExcess"*/
					if(bigGroupCount == width/n-1 && smallGroupCount == n-1 && i == height-1 && width%n != 0 && flag == 0 ) {
						flag = 1;
						bigGroupCount = 0; //reset bigGroupCount and smallGroupCount
						smallGroupCount = 0;
						j++;
						i=0;
					}
					if(flag == 1) { //When the original array is full, it's time to start filling the excess array
						int pixel = initialImage.getRGB(j, i);			    
						vExcess[smallGroupCount][i] = pixel;
					    if(i==height-1) {
							smallGroupCount++;
						}
					}
					else { //fill the original array
						int pixel = initialImage.getRGB(j, i);	//read and store pixel data		    
					    vScanLines[bigGroupCount][smallGroupCount][i] = pixel;
					    if(i==height-1) { //increment smallGroupCount when one vertical line has been read
							smallGroupCount++;
						}
						if(smallGroupCount == n) { //increment bigGroupCount when a 2d-array segment is full
							smallGroupCount = 0;
							bigGroupCount++;
						}
					}
				}
			}
			
			//Swap the even-numbered groups with the odd-numbered ones. (X1 with X2, X3 with X4, etc)
			//The excess lines aren't swapped
			for(int i = 0; i<width/n-1; i+=2) {
				int [][] temp1 = vScanLines[i];
				int [][] temp2 = vScanLines[i+1];
				vScanLines[i] = temp2;
				vScanLines[i+1] = temp1;
			}
			
			try {
				//create new image according to the chosen output name
				f = new File(""+finalImage+"."+extension);
				BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				int y = 0;
				
				/*Paint the image with the pixel values in vScanLines[][][]. This will not paint the entire
				  image if there are excess scanlines, but they are painted immediately afterwards*/
				for(int i = 0; i<width/n; i++) {
					for(int j = 0; j<n; j++) {
						for(int k = 0; k<height; k++) {
							outputImage.setRGB(y, k, vScanLines[i][j][k]);
							if(k == height-1) {
								y++;
							}
						}
					}
				}
				/*Add the extra spare lines, these lines aren't swapped around because they have no 
				even-numbered group to swap with*/
				for(int j = 0; j<width%n; j++) {
					for(int k = 0; k<height; k++) {
						outputImage.setRGB(y, k, vExcess[j][k]);
						if(k == height-1) {
							y++;
						}
					}
				}
				//commit the changes, write the image
				ImageIO.write(outputImage, extension, f);
			}
			catch(IOException e){
				System.out.println("Error writing to file");
			}
		}

		sc.close();
	}
}
