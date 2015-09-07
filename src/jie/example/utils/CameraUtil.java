package jie.example.utils;

import android.hardware.Camera;

public class CameraUtil {

	public void getCamera() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraNumber = Camera.getNumberOfCameras();
	}
}
