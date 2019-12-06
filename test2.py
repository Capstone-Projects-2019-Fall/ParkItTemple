import cv2
import picamera
from picamera.array import PiRGBArray
from picamera import PiCamera
import time
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate('/home/pi/Downloads/creds.json')
firebase_admin.initialize_app(cred)

#firebase_admin.initialize_app(cred, {'projectId': 'com.example.parkittemple'})
db= firestore.client()
pi_doc_1 = db.collection(u'pi').document(u'pi-1')

camera =PiCamera()
#rawCapture = PiRGBArray(camera)

time.sleep(1)

  
# Trained XML classifiers describes some features of some object we want to detect 
car_cascade = cv2.CascadeClassifier('cars.xml') 
i=0
# loop runs if capturing has been initialized. 
while (i==0):
    count = 0
    spots=110
    # reads frames from a video 
    #ret, frames = cap.read() 
    #i=i+1
    #if i%10 == 0:
     #   print(i)
        # convert to gray scale of each frames 
        #gray = cv2.cvtColor(frames, cv2.COLOR_BGR2GRAY) 
      #  if ret is True:
       #     gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        #    print('here')
        #else:
         #   continue
        # Detects cars of different sizes in the input image 
    #camera.capture(rawCapture, format="bgr")
    #image = rawCapture.array
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
    #cv2.imshow('Image', image)
    cv2.imwrite('image1.jpg', image)
    pi_doc_1.update({u'total_spots':110})
    pi_doc_1.update({u'available_spots':spots})
        # To draw a rectangle in each cars
         
   # Display frames in a window  
    #cv2.imshow('video2', frames) 
      
    # Wait for Esc key to stop 
    if cv2.waitKey(33) == 27: 
        break
  
# De-allocate any associated memory usage 
#cv2.destroyAllWindows() 
