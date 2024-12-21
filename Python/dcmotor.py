from gpiozero import OutputDevice
from time import sleep

# GPIO 핀 번호 설정
stepPin = [19, 20, 26, 16]

# GPIO 핀을 제어하는 OutputDevice 인스턴스를 생성
def create_step_pins():
    return [OutputDevice(pin) for pin in stepPin]

# 스텝 시퀀스를 설정 (각 코일에 동시에 전류를 보내 힘을 증가)
def aStep(stepPins, s):
    if s == 0:
        stepPins[0].on()
        stepPins[1].off()
        stepPins[2].off()
        stepPins[3].on()
    elif s == 1:
        stepPins[0].on()
        stepPins[1].on()
        stepPins[2].off()
        stepPins[3].off()
    elif s == 2:
        stepPins[0].off()
        stepPins[1].on()
        stepPins[2].on()
        stepPins[3].off()
    elif s == 3:
        stepPins[0].off()
        stepPins[1].off()
        stepPins[2].on()
        stepPins[3].on()

def bStep(stepPins, s):
    if s == 0:
        stepPins[0].off()
        stepPins[1].off()
        stepPins[2].on()
        stepPins[3].on()
    elif s == 1:
        stepPins[0].off()
        stepPins[1].on()
        stepPins[2].on()
        stepPins[3].off()
    elif s == 2:
        stepPins[0].on()
        stepPins[1].on()
        stepPins[2].off()
        stepPins[3].off()
    elif s == 3:
        stepPins[0].on()
        stepPins[1].off()
        stepPins[2].off()
        stepPins[3].on()

# 정방향으로 스텝 모터를 회전 (90도)
def doStep1(stepPins, dir, del_):
    nSteps = 16  # 90도 회전을 위한 스텝 수 (적절히 조정)
    for _ in range(nSteps):
        aStep(stepPins, (nSteps - _ - 1) % 4 if dir else _ % 4)
        sleep(del_ / 1000.0)  # 각 스텝 사이의 지연 시간 (ms)

# 역방향으로 스텝 모터를 회전 (90도)
def doStep2(stepPins, dir, del_):
    nSteps = 16  # 90도 회전을 위한 스텝 수 (적절히 조정)
    for _ in range(nSteps):
        bStep(stepPins, (nSteps - _ - 1) % 4 if dir else _ % 4)
        sleep(del_ / 1000.0)  # 각 스텝 사이의 지연 시간 (ms)

def setup():
    pass

# 모터를 90도 정방향으로 회전
def loop1():
    stepPins = create_step_pins()
    try:
        doStep1(stepPins, False, 30)  # 90도 회전, 적절한 지연 시간 설정
    finally:
        for pin in stepPins:
            if pin.is_active:
                pin.close()  # 활성화된 핀만 닫기

# 모터를 90도 역방향으로 회전
def loop2():
    stepPins = create_step_pins()
    try:
        doStep2(stepPins, False, 30)  # 90도 회전, 적절한 지연 시간 설정
    finally:
        for pin in stepPins:
            if pin.is_active:
                pin.close()  # 활성화된 핀만 닫기

if __name__ == '__main__':
    setup()
    while True:
        k = input('command = ')
        if k == 'yes':
            try:
                loop1()
            except KeyboardInterrupt:
                break
        elif k == 'no':
            try:
                loop2()
            except KeyboardInterrupt:
                break
        else:
            print("Invalid command. Please enter 'yes' or 'no'.")
