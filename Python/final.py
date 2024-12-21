import serial
import socket
import threading
import time
import firebase_admin
from firebase_admin import credentials, db

# Firebase Admin SDK 초기화
cred = credentials.Certificate('/home/pi/python/__pycache__/raspberry-b22a0-firebase-adminsdk-wa55t-22b627371e.json')
firebase_admin.initialize_app(cred, {
    "databaseURL": "https://raspberry-b22a0-default-rtdb.firebaseio.com/",
})

# Firebase Realtime Database 참조
db_ref = db.reference()

# 데이터베이스에 빛의 감지 상태 업데이트하는 함수
def ref_light_status(detection_type):
    db_ref.child('light_status').push({
        'detected': detection_type,
        'timestamp': int(time.time())
    })

# 시리얼 포트 설정
serial_port = '/dev/ttyUSB0'
SerialFromArduino = serial.Serial(serial_port, 9600)
SerialFromArduino.flushInput()

# 소켓 설정
host = ''
port = 8080
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((host, port))
server_socket.listen(1)

print(f"Server is running on port {port}.")

# 아두이노 시리얼 입력을 처리하는 함수
def handle_serial_input(stop_event):
    while True:
        if stop_event.is_set():  # stop_event가 설정되어 있으면 20분간 동작을 멈춥니다.
            time.sleep(1)
            continue
        if SerialFromArduino.in_waiting > 0:
            input_s = SerialFromArduino.readline()
            input_s = input_s.decode('utf-8').strip()
            print(input_s)
            if input_s == 'CDS on':  # 빛이 감지될 때
                ref_light_status(True)  # 빛 감지 상태 True 전달
            elif input_s == 'CDS off':
                ref_light_status(False)  # 빛 감지 상태 False 전달

# 소켓 서버를 처리하는 함수
def handle_socket_server(stop_event):
    while True:
        client_socket, addr = server_socket.accept()
        print(f"Client connected from: {addr}")

        data = client_socket.recv(1024).decode('ascii')
        response = data.strip()

        print(f"Received data: {data}")
        print(f"Response data: {response}")

        if response == 'STOP':
            print("Received STOP command")
            stop_event.set()  # 시리얼 입력 멈추기
            ref_light_status(True)  # 빛 감지 상태 True 전달
            # 20분 후에 시리얼 입력 처리 재개
            threading.Timer(5, lambda: stop_event.clear()).start()

        elif response == 'BACK':
            print("Received BACK command")
            stop_event.set()  # 시리얼 입력 멈추기
            ref_light_status(False)  # 빛 감지 상태 False 전달
            # 20분 후에 시리얼 입력 처리 재개
            threading.Timer(5, lambda: stop_event.clear()).start()

        client_socket.send(response.encode('ascii'))
        client_socket.close()

# 메인 함수
if __name__ == "__main__":
    stop_event = threading.Event()

    # 시리얼 입력 스레드 시작
    serial_thread = threading.Thread(target=handle_serial_input, args=(stop_event,))
    serial_thread.daemon = True
    serial_thread.start()

    # 소켓 서버 스레드 시작
    socket_thread = threading.Thread(target=handle_socket_server, args=(stop_event,))
    socket_thread.daemon = True
    socket_thread.start()

    # 메인 스레드는 두 스레드가 종료될 때까지 대기
    serial_thread.join()
    socket_thread.join()
