import socket
import time
import os
from dcmotor import loop1, loop2

host = ''
port = 8080

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # 소켓 옵션 설정
server_socket.bind((host, port))
server_socket.listen(1)

print(f"Server is running on port {port}.")

def phone():
    while True:
        client_socket, addr = server_socket.accept()
        print(f"Client connected from: {addr}")

        data = client_socket.recv(1024).decode('ascii')
        response = data.strip()

        print(f"Received data: {data}")
        print(f"Response data: {response}")
        
        if response == 'STOP':
            print("Received STOP command")
            loop1()
        elif response == 'BACK':
            print("Received BACK command")
            loop2()

        client_socket.send(response.encode('ascii'))
        client_socket.close()
