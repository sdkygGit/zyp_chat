package com.wiz.dev.wiztalk.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ThumbnailUtils {

	private static final String TAG = "ThumbnailUtils";

//	private static final int MAX_NUM_PIXELS_YOUXIN = 1024 * 768;
	private static final int MAX_NUM_PIXELS_YOUXIN = 1024 * 1024;
	private static final int UNCONSTRAINED = -1;

	/* Options used internally. */
    private static final int OPTIONS_NONE = 0x0;
    private static final int OPTIONS_SCALE_UP = 0x1;

    /**
     * Constant used to indicate we should recycle the input in
     * {@link #extractThumbnail(Bitmap, int, int, int)} unless the output is the input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;
	
	public static final int TARGET_SIZE_YOUXIN = 720; //960x720
	
	public static final int TARGET_SIZE_YOUXIN_W = 960; //960x720
	public static final int TARGET_SIZE_YOUXIN_H = 720; //960x720

	/**
	 * This method first examines if the thumbnail embedded in EXIF is bigger
	 * than our target sizeBigger. If not, then it'll create a thumbnail from original
	 * image. Due to efficiency consideration, we want to let MediaThumbRequest
	 * avoid calling this method twice for both kinds, so it only requests for
	 * MICRO_KIND and set saveImage to true.
	 *
	 * This method always returns a "square thumbnail" for MICRO_KIND thumbnail.
	 *
	 * @param filePath
	 *            the path of image file
	 * @param kind
	 *            could be MINI_KIND or MICRO_KIND
	 * @return Bitmap, or null on failures
	 *
	 * @hide This method is only used by media framework and media provider
	 *       internally.
	 */
	public static Bitmap createImageThumbnailYouXin(String filePath) {
		int targetSize = TARGET_SIZE_YOUXIN;
		int maxPixels = MAX_NUM_PIXELS_YOUXIN;
		Bitmap bitmap = null;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			FileDescriptor fd = fis.getFD();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
//			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeFileDescriptor(fd, null, options);
//			if (options.mCancel || options.outWidth == -1
//					|| options.outHeight == -1) {
//				return null;
//			}
//			options.inSampleSize = computeSampleSize(options, targetSize,
//					maxPixels);
			options.inJustDecodeBounds = false;

			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
		} catch (IOException ex) {
			Log.e(TAG, "", ex);
		} catch (OutOfMemoryError oom) {
			Log.e(TAG, "Unable to decode file " + filePath
					+ ". OutOfMemoryError.", oom);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

        bitmap = extractThumbnail(bitmap,
        		TARGET_SIZE_YOUXIN_W,
        		TARGET_SIZE_YOUXIN_H, OPTIONS_RECYCLE_INPUT|OPTIONS_NONE);
		
		return bitmap;
	}

	/*
	 * Compute the sample sizeBigger as a function of minSideLength and
	 * maxNumOfPixels. minSideLength is used to specify that minimal width or
	 * height of a bitmap. maxNumOfPixels is used to specify the maximal sizeBigger in
	 * pixels that is tolerable in terms of memory usage.
	 * 
	 * The function returns a sample sizeBigger based on the constraints. Both sizeBigger
	 * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
	 * indicates no care of the corresponding constraint. The functions prefers
	 * returning a sample sizeBigger that generates a smaller bitmap, unless
	 * minSideLength = IImage.UNCONSTRAINED.
	 * 
	 * Also, the function rounds up the sample sizeBigger to a power of 2 or multiple
	 * of 8 because BitmapFactory only honors sample sizeBigger this way. For example,
	 * BitmapFactory downsamples an image by 2 even though the request is 3. So
	 * we round up the sample sizeBigger to avoid OOM.
	 */
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
//			while (roundedSize < initialSize) {
//				roundedSize <<= 1;
//			}
			while (roundedSize < initialSize) {
				if (roundedSize << 1 > initialSize) {
					break;
				}
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
				.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == UNCONSTRAINED)
				&& (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static boolean compress(Bitmap bitmap, String path, int quality) {
		return compress(bitmap, new File(path), quality);
	}
	
	public static boolean compress(Bitmap bitmap, File file, int quality) {
		if (bitmap == null) {
			return false;
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
			fos.flush();
			return compress;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
     * Creates a centered bitmap of the desired sizeBigger.
     *
     * @param source original bitmap source
     * @param width targeted width
     * @param height targeted height
     * @param options options used during thumbnail extraction
     */
    public static Bitmap extractThumbnail(
            Bitmap source, int width, int height, int options) {
        if (source == null) {
            return null;
        }

        if (source.getWidth() < source.getHeight()) {
        	int temp = width;
        	width = height;
        	height = temp;
        } else {
        }
        
        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = transform(matrix, source, width, height, options);
        return thumbnail;
    }
	
	/**
     * Transform source Bitmap to targeted width and height.
     */
    private static Bitmap transform(Matrix scaler,
            Bitmap source,
            int targetWidth,
            int targetHeight,
            int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect   = (float) targetWidth / targetHeight;

        if (!scaleUp && deltaX < 0 && deltaY < 0) {
        	scaler.setScale(1, 1);
        } else {
        	if (bitmapAspect > viewAspect) {
        		float scale = targetWidth / bitmapWidthF;
        		scaler.setScale(scale, scale);
        	} else {
        		float scale = targetHeight / bitmapHeightF;
        		scaler.setScale(scale, scale);
        	}
        }
        
        Bitmap b1 = null;
        if (scaler != null) {
            b1 = Bitmap.createBitmap(source, 0, 0,
            source.getWidth(), source.getHeight(), scaler, true);
        } 
        
        if (recycle && b1 != source) {
            source.recycle();
        }

        return b1;
    }
    
    /**
     * @param scaleUp
     * @param sourceWidth
     * @param sourceHeight
     * @param targetWidth
     * @param targetHeight
     * @return int[]:int[0] scaled width;int[1] scaled height;
     */
    public static int[] getScaledSize(boolean scaleUp, int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
    	int[] ret = new int[]{sourceWidth, sourceHeight};
    	
    	float[] scale = getScale(scaleUp, sourceWidth, sourceHeight, targetWidth, targetHeight);
    	
    	ret[0] = (int) (scale[0] * sourceWidth);
    	ret[1] = (int) (scale[1] * sourceHeight);
    	
    	return ret;
    }
    
    /**
     * @param scaleUp
     * @param sourceWidth
     * @param sourceHeight
     * @param targetWidth
     * @param targetHeight
     * @return float[]:float[0] scale for width;float[1] scale for height;
     */
    private static float[] getScale(boolean scaleUp, int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
    	float[] ret = new float[]{1.0f, 1.0f};
    	
    	int deltaX = sourceWidth - targetWidth;
        int deltaY = sourceHeight - targetHeight;
        
        float bitmapWidthF = sourceWidth;
        float bitmapHeightF = sourceHeight;

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect   = (float) targetWidth / targetHeight;
    	
    	if (!scaleUp && deltaX < 0 && deltaY < 0) {
//        	scaler.setScale(1, 1);
    		ret[0] = 1;
    		ret[1] = 1;
        } else {
        	if (bitmapAspect > viewAspect) {
        		float scale = targetWidth / bitmapWidthF;
//        		scaler.setScale(scale, scale);
        		ret[0] = scale;
        		ret[1] = scale;
        	} else {
        		float scale = targetHeight / bitmapHeightF;
//        		scaler.setScale(scale, scale);
        		ret[0] = scale;
        		ret[1] = scale;
        	}
        }
    	
    	return ret;
    }
    
    public static BitmapFactory.Options getOptions(String filePath) {
    	FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePath);
			FileDescriptor fd = fis.getFD();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			if (options.mCancel || options.outWidth == -1
					|| options.outHeight == -1) {
				return null;
			}
			return options;
		} catch (IOException ex) {
			Log.e(TAG, "", ex);
		} catch (OutOfMemoryError oom) {
			Log.e(TAG, "Unable to decode file " + filePath
					+ ". OutOfMemoryError.", oom);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
    }
}
