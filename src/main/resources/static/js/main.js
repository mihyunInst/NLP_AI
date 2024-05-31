document.addEventListener('DOMContentLoaded', function() {
    const sendBtn = document.getElementById('sendBtn');
    const textInput = document.getElementById('textInput');
    const answerSection = document.getElementById('answerSection');

     // 보내기 버튼 클릭 이벤트 핸들러
    sendBtn.addEventListener('click', function() {
        const symptom = textInput.value;
        
        // 서버에 POST 요청을 보내어 추천 결과를 받아옴
        fetch('/recommend', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ symptom: symptom })
        }).then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답에 문제가 있습니다: ' + response.statusText);
            }
            return response.text();
        }).then(data => {
            // 서버로부터 받은 데이터를 answerSection에 표시
            answerSection.innerText = data;
        }).catch(error => {
            console.error('fetch 작업 중 문제가 발생했습니다 : ', error);
            // 에러가 발생하면 사용자에게 오류 메시지 표시
            answerSection.innerText = '오류: 추천을 받을 수 없습니다.';
        });
    });
});
