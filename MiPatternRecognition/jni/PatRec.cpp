#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#include <stdio.h>
#include <stdlib.h>

#include "opencv2/nonfree/features2d.hpp"

using namespace std;
using namespace cv;

extern "C" {

const int SURF_DETECTION = 0; //SURF (nonfree module)
const int SIFT_DETECTION = 1; //SIFT (nonfree module)
const int FAST_DETECTION = 2; //FastFeatureDetector
const int STAR_DETECTION = 3; //StarFeatureDetector
const int ORB_DETECTION = 4; //ORB
const int MSER_DETECTION = 5; //MSER
const int GFTT_DETECTION = 6; //GoodFeaturesToTrackDetector
const int HARRIS_DETECTION = 7; //GoodFeaturesToTrackDetector with Harris detector enabled

JNIEXPORT bool JNICALL Java_com_example_mipatternrecognition_MainActivity_FindFeatures(
		JNIEnv*, jobject, jlong addrGray, jlong addrRgba, jint TypeDetection) {
	Mat& mGr = *(Mat*) addrGray;
	Mat& mRgb = *(Mat*) addrRgba;
	vector<KeyPoint> v;
	Mat descriptor;
	int minHessian = 500;
	SurfFeatureDetector detector_Surf(minHessian);
	SiftFeatureDetector detector_Sift(minHessian);

	FastFeatureDetector detector_Fast(50);

	OrbFeatureDetector detector_Orb(500, 1.2f, 8, 14, 0, 2, 0, 14);

	MserFeatureDetector detector_Mser(5, 60, 14400, 0.25, 0.2, 200, 1.01, 0.003,
			5);

	int maxCorners = 1000;
	double qualityLevel = 0.01;
	double minDistance = 1.;
	int blockSize = 3;
	bool useHarrisDetector;
	double k = 0.04;
	useHarrisDetector = false;
	GoodFeaturesToTrackDetector detector_Gftt(maxCorners, qualityLevel,
			minDistance, blockSize, useHarrisDetector, k);
	useHarrisDetector = true;
	GoodFeaturesToTrackDetector detector_Harris(maxCorners, qualityLevel,
			minDistance, blockSize, useHarrisDetector, k);

	int maxSize = 45;
	int responseThreshold = 30;
	int lineThresholdProjected = 10;
	int lineThresholdBinarized = 8;
	int suppressNonmaxSize = 5;
	StarFeatureDetector detector_Star(maxSize, responseThreshold,
			lineThresholdProjected, lineThresholdBinarized, suppressNonmaxSize);

	//http://stackoverflow.com/questions/14808429/classification-of-detectors-extractors-and-matchers

	SurfDescriptorExtractor extractor_Surf;
	SiftDescriptorExtractor extractor_Sift;
	//FastDescriptorExtractor extractor_Fast;
	OrbDescriptorExtractor extractor_Orb;
	//MserDescriptorExtractor extractor_Mser;
	//GFTTDescriptorExtractor extractor_GFTT;
	//HarrisDescriptorExtractor extractor_Harris;
	//StarDescriptorExtractor extractor_Star;

	switch (TypeDetection) {
	case SURF_DETECTION:
		detector_Surf.detect(mGr, v);
		extractor_Surf.compute(mGr, v, descriptor);
		break;
	case SIFT_DETECTION:
		detector_Sift.detect(mGr, v);
		extractor_Sift.compute(mGr, v, descriptor);
		break;
	case FAST_DETECTION:
		detector_Fast.detect(mGr, v);
		extractor_Surf.compute(mGr, v, descriptor);
		break;
	case ORB_DETECTION:
		detector_Orb.detect(mGr, v);
		extractor_Orb.compute(mGr, v, descriptor);
		break;
	case MSER_DETECTION:
		detector_Mser.detect(mGr, v);
		extractor_Surf.compute(mGr, v, descriptor);
		break;
	case GFTT_DETECTION:
		detector_Gftt.detect(mGr, v);
		extractor_Sift.compute(mGr, v, descriptor);
		break;
	case HARRIS_DETECTION:
		detector_Harris.detect(mGr, v);
		extractor_Orb.compute(mGr, v, descriptor);
		break;
	case STAR_DETECTION:
		detector_Star.detect(mGr, v);
		extractor_Orb.compute(mGr, v, descriptor);
		break;
	}

	for (unsigned int i = 0; i < v.size(); i++) {
		const KeyPoint& kp = v[i];
		circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(255, 0, 0, 255));
	}


	return true;
}

}

