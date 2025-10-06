package com.rikuthin.graphics.backgrounds;
// package com.rikuthin.entities.backgrounds;

// import java.awt.Graphics2D;
// import java.awt.Image;

// import com.rikuthin.graphics.ImageManager;

// /**
//  * Represents a background image that scrolls infinitely in the vertical
//  * direction. The background consists of two identical images that are
//  * positioned one above the other. As the background scrolls, the images are
//  * repositioned to create the illusion of a continuous, seamless background.
//  */
// public class VerticalScrollingBackground extends OldBackground {
//     /**
//      * The image displayed by the background.
//      */
//     private Image image;

//     /**
//      * The height of the background image.
//      */
//     private int imageHeight;

//     /**
//      * The y coordinate of the first image (within the parent panel).
//      */
//     private int image1_y;

//     /**
//      * The y coordinate of the second image (within the parent panel).
//      */
//     private int image2_y;

//     /**
//      * The fixed x coordinate of the images (within the parent panel).
//      */
//     private final int image1_x;

//     /**
//      * The fixed x coordinate of the images (within the parent panel).
//      */
//     private final int image2_x;

//     /**
//      * Constructs a new InfiniteScrollingBackground object.
//      *
//      * @param panel The parent JPanel of the background.
//      * @param imageFilepath The path to the image file.
//      * @param x The fixed x coordinate of the images.
//      * @param y The initial y coordinate of the first image.
//      */
//     public VerticalScrollingBackground(String imageFilepath, int x, int y) {
//         setImage(imageFilepath, x, y);

//         image1_x = x; // Fixed x-position
//         image2_x = x; // Fixed x-position
//         image1_y = 0;
//         image2_y = imageHeight;
//     }

//     /**
//      * Sets the image for the background.
//      *
//      * @param imageFilepath The path to the image file.
//      * @param x The fixed x coordinate of the images.
//      * @param y The initial y coordinate of the first image.
//      */
//     public final void setImage(String imageFilepath, int x, int y) {
//         image = ImageManager.loadImage(imageFilepath);
//         imageHeight = image.getHeight(null);
//     }

//     /**
//      * Moves the background vertically by the specified amount.
//      *
//      * @param dx The amount to move horizontally (not used for vertical
//      * scrolling).
//      * @param dy The amount to move vertically.
//      */
//     public final void move(final int dx, final int dy) {
//         image1_y = (image1_y + dy + imageHeight) % imageHeight;
//         image2_y = (image2_y + dy + imageHeight) % imageHeight;
//     }

//     /**
//      * Draws the background images.
//      *
//      * @param g2 The Graphics2D object to draw on.
//      */
//     public void draw(Graphics2D g2) {
//         g2.drawImage(image, image1_x, image1_y, null);
//         g2.drawImage(image, image2_x, image2_y, null);
//     }
// }
