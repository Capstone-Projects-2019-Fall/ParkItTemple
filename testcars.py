import picamera
import time
import cv2

camera =PiCamera()
#rawCapture = PiRGBArray(camera)

time.sleep(1)
spots=110
camera.start_preview()
#sleep(2)
time.sleep(3)
camera.capture('/home/pi/Downloads/image1.jpg')
camera.stop_preview()
image = cv2.imread("image1.jpg")
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
cars = car_cascade.detectMultiScale(gray, 1.1, 1)
for (x,y,w,h) in cars: 
    cv2.rectangle(image,(x,y),(x+w,y+h),(0,0,255),2)
    count= count+1
    print('Number of cars: ' + str(count))
spots=spots -count
print('Number of open spots: '+ str(spots))
cv2.imshow('Image', image)
