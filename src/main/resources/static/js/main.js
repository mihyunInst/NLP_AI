document.addEventListener('DOMContentLoaded', function() {
    const sendBtn = document.getElementById('sendBtn');
    const textInput = document.getElementById('textInput');
    const answerSection = document.getElementById('answerSection');
    const trainingSection = document.getElementById('trainingSection');

    // 초기 메시지 설정
    answerSection.innerText = '최대한 증상에 맞춰 의사를 추천 해 드릴게요!';

    // 보내기 버튼 클릭 이벤트 핸들러
    sendBtn.addEventListener('click', function() {

        const symptom = textInput.value;

        if(symptom.length == 0) {
            alert("증상을 입력해주세요~");
            return;
        }
        
        // 서버에 POST 요청을 보내어 추천 결과를 받아옴
        fetch('/recommend', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ symptom: symptom })
        }).then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답에 문제가 있습니다: ' + response.statusText);
            }
            return response.text();

        }).then(data => {
            console.log(data);

            let fullText = '';

            if(data == 'nodata') {
                fullText = '이 증상은 잘 모르겠어요...';
            } else {
                fullText = "🤔...제 의견은 이렇습니다 \n\n" + data + "\n\n그럼 쾌차를 빌어요!🙏";
            }
            
            // 서버로부터 받은 데이터를 타이핑 애니메이션으로 표시
            typeEffect(answerSection, fullText, function() {
                showFeedbackButtons(symptom);
            });
            
        }).catch(error => {
            console.error('fetch 작업 중 문제가 발생했습니다 : ', error);
            // 에러가 발생하면 사용자에게 오류 메시지 표시
            answerSection.innerText = '오류: 추천을 받을 수 없습니다.';
        });
    });

    // 타이핑 효과 함수
    function typeEffect(element, text, callback) {
        let index = 0;
        element.innerHTML = '';
        let answerDiv = document.createElement("div");
        answerDiv.innerHTML = '';
        element.append(answerDiv);

        function type() {
            if (index < text.length) {
                const char = text.charAt(index);
                if (char === ' ') {
                    answerDiv.innerHTML += '&nbsp;';
                } else if (char === '\n') {
                    answerDiv.innerHTML += '<br>';
                } else {
                    answerDiv.innerHTML += char;
                }
                index++;
                setTimeout(type, 50); // 타이핑 속도 조절 (밀리초 단위)
            } else if (callback) {
                callback();
            }
        }

        type();
    }

    // 피드백 버튼 표시 함수
    function showFeedbackButtons(symptom) {
        const message = document.createElement('p');
        message.innerText = '혹시 제 대답이 마음에 들지 않으시다면 학습시켜주세요😓';

        const departments = [
            '피부과',
            '내과',
            '정형외과',
            '가정의학과',
            '성형외과',
            '안과',
            '이비인후과',
            '산부인과',
            '비뇨의학과',
            '정신건강의학과',
            '치과'
        ];

        const buttonContainer = document.createElement('div');
        buttonContainer.innerHTML = '';

        departments.forEach(department => {
            const button = document.createElement('button');
            button.innerText = department;
            button.classList.add('recommendButton');

            button.addEventListener('click', function() {
                console.log(symptom);
                console.log(department);
                sendFeedback(symptom, department);
            });

            buttonContainer.append(button);
        });

        trainingSection.innerHTML = '';
        trainingSection.innerHTML = "<hr />";
        trainingSection.append(message);
        trainingSection.append(buttonContainer);

        // Display training section and apply animation
        answerSection.append(trainingSection);
        setTimeout(() => {
            trainingSection.classList.add('show');
        }, 10);
    }

    // 피드백 전송 함수
    function sendFeedback(symptom, department) {
        fetch('/feedback', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ symptom: symptom, department: department })
        }).then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답에 문제가 있습니다: ' + response.statusText);
            }
            return response.text();
        }).then(data => {
            alert('피드백 감사합니다! AI가 학습되었습니다.');
            location.reload();
        }).catch(error => {
            console.error('fetch 작업 중 문제가 발생했습니다 : ', error);
            alert('오류: 피드백을 전송할 수 없습니다.');
        });
    }
});
