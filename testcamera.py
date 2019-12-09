import picamera
import time

camera =PiCamera()
#rawCapture = PiRGBArray(camera)

time.sleep(1)

camera.start_preview()
#sleep(2)
time.sleep(3)
camera.capture('/home/pi/Downloads/image1.jpg')
camera.stop_preview()