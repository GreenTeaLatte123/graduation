import cv2
import firebase_admin
import time
import os
from firebase_admin import credentials, storage, db
from ultralytics import YOLO

# Firebase 서비스 계정 키 경로
cred = credentials.Certificate("D:/yolov8/raspberrykey.json")

# Firebase 프로젝트 초기화
firebase_admin.initialize_app(cred, {
    "databaseURL": "https://raspberry-b22a0-default-rtdb.firebaseio.com/",
    "storageBucket": "raspberry-b22a0.appspot.com"
})

# Load a pretrained YOLOv8n model
model = YOLO('D:/yolov8/best11.pt')

# Firebase Storage 클라이언트
storage_client = storage.bucket()

# Firebase Realtime Database 참조
db_ref = db.reference()

# 비디오 파일 및 이미지 저장 경로 설정
video_path = "http://192.168.184.87:8081/?action=stream"
cap = cv2.VideoCapture(video_path)

if not cap.isOpened():
    print("Error: Could not open video.")
    exit()

timestamp = time.strftime("%Y%m%d%H%M%S")

d_drive_folder = 'D:/results/videos/'
local_filename = os.path.join(d_drive_folder, f'{timestamp}.mp4')

storage_filename = f"video_{timestamp}.mp4"

image_folder = 'D:/results/images/'
frame_counter = 0

frame_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
frame_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
fps = cap.get(cv2.CAP_PROP_FPS)

fourcc = cv2.VideoWriter_fourcc(*'XVID')
out = cv2.VideoWriter(local_filename, fourcc, fps, (frame_width, frame_height))

def upload_to_firebase(local_path, storage_path, database_ref, detection_type):
    blob = storage_client.blob(storage_path)
    blob.upload_from_filename(local_path)
    print(f"{storage_path} upload success")
    db_ref.child(database_ref).push(detection_type)

while cap.isOpened():
    success, frame = cap.read()

    if success:
        results = model(frame, conf=0.5)
        annotated_frame = results[0].plot()

        out.write(annotated_frame)
        
        # OpenCV 창에 이미지 표시
        cv2.imshow("YOLOv8 Inference", annotated_frame)

        for r in results:
            if 4 in r.boxes.cls and frame_counter % 20 == 0:
                image_filename = os.path.join(image_folder, f'frame_{timestamp}_{frame_counter}.jpg')
                cv2.imwrite(image_filename, annotated_frame)
                print(f"Class 1 detected. Image saved as {image_filename}")

                upload_to_firebase(image_filename, f'images/frame_{timestamp}_{frame_counter}.jpg', 'class1_detection', 'class1 detection')
                
            elif 9 in r.boxes.cls and frame_counter % 30 == 0:
                image_filename = os.path.join(image_folder, f'frame_{timestamp}_{frame_counter}.jpg')
                cv2.imwrite(image_filename, annotated_frame)
                print(f"Class 2 detected. Image saved as {image_filename}")

                upload_to_firebase(image_filename, f'images/frame_{timestamp}_{frame_counter}.jpg', 'class2_detection', 'class2 detection')

        frame_counter += 1
        print(f"Frame Counter: {frame_counter}")

        # OpenCV 창에서 키 입력 대기
        if cv2.waitKey(1) & 0xFF == ord("q"):
            break
    else:
        break

blob = storage_client.blob('videos/' + storage_filename)
blob.upload_from_filename(local_filename)
print(f"{storage_filename} upload success")

cap.release()
out.release()
cv2.destroyAllWindows()
