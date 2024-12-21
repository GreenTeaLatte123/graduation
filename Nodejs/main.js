const admin = require('firebase-admin');
const serviceAccount = require('D:/workspace/nodejs/raspberry-b22a0-firebase-adminsdk-wa55t-fc84cd3e13.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://raspberry-b22a0-default-rtdb.firebaseio.com' // Firebase 프로젝트의 데이터베이스 URL
});

const registrationToken = 'cCoVTmoQTFu-znlqkcu6yK:APA91bEbNsvDscvXJhHk8j-JiYKx_uoS7Nd5JHaWgzGVcHjIF8wcqAbPSrG5VRGPuUN_LHBeIDr4DGEtFqHhutpz7Q8es1gt97tKdOMCAWxZIOy2iu8vHvY';

// Firebase Realtime Database의 참조 객체
const db = admin.database();
const refClass1 = db.ref('/class1_detection');
const refClass2 = db.ref('/class2_detection');
const refLightStatus = db.ref('/light_status');

// 이전 데이터를 저장할 객체
let previousDataClass1 = null;
let previousDataClass2 = null;
let previousDataLight = {};

// 초기 로드 플래그
let initialLoad = true;

// 데이터베이스에서 value 이벤트 감지
refClass1.on('value', (snapshot) => {
    const newData = snapshot.val(); // 업데이트된 데이터 가져오기

    // 이전 데이터와 비교하여 변경된 경우에만 알림 보내기
    if (previousDataClass1 && JSON.stringify(previousDataClass1) !== JSON.stringify(newData)) {
        sendNotification1('class1', newData); // FCM 메시지 보내기
    }

    previousDataClass1 = newData; // 현재 데이터를 이전 데이터로 설정
});
  
refClass2.on('value', (snapshot) => {
    const newData = snapshot.val(); // 업데이트된 데이터 가져오기

    // 이전 데이터와 비교하여 변경된 경우에만 알림 보내기
    if (previousDataClass2 && JSON.stringify(previousDataClass2) !== JSON.stringify(newData)) {
        sendNotification1('class2', newData); // FCM 메시지 보내기
    }

    previousDataClass2 = newData; // 현재 데이터를 이전 데이터로 설정
});

refLightStatus.on('value', (snapshot) => {
    const data = snapshot.val(); // 전체 light_status 데이터를 가져오기

    // 초기 로드 시 알림을 보내지 않도록 설정
    if (initialLoad) {
        initialLoad = false;
        previousDataLight = data || {}; // 데이터가 null인 경우 빈 객체로 설정
        return;
    }

    // 각 child에 대해 변경된 경우에만 알림 보내기
    for (const key in data) {
        const newData = data[key];

        if (!previousDataLight[key] || JSON.stringify(previousDataLight[key]) !== JSON.stringify(newData)) {
            sendNotification2('Light Status', newData); // FCM 메시지 보내기
        }

        previousDataLight[key] = newData; // 현재 데이터를 이전 데이터로 설정
    }
});
  
// FCM 메시지 보내는 함수 - Class 1, Class 2 detection에 사용
function sendNotification1(className, newData) {
    const message = {
        notification: {
            title: `Safety Equipment Issue`,
            body: `Check Safety Equipment`
        },
        token: registrationToken
    };

    admin.messaging().send(message)
        .then((response) => {
            console.log('Notification sent successfully:', response);
        })
        .catch((error) => {
            console.error('Error sending notification:', error);
        });
};

function sendNotification2(lightStatus, newData) {
    const message = {
        notification: {
            title: `${lightStatus} Update`,
            body: newData.detected ? 'Person Detected' : 'Person Not Detected'
        },
        token: registrationToken
    };

    admin.messaging().send(message)
    .then((response) => {
        console.log('Notification sent successfully:', response);
    })
    .catch((error) => {
        console.error('Error sending notification:', error);
    });
};