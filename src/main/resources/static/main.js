function logout() {
    $.ajax({
        url: '/logout',
        type: 'POST',
        success: function (response) {
            alert('로그아웃 되었습니다.');
            window.location.href = '/';
        },
        error: function (xhr, status, error) {
            alert('로그아웃 실패: ' + xhr.responseText);
        }
    });
}