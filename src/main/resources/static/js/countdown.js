
    function disableButtonForTwoMinutes(event) {
        event.preventDefault(); // Ngăn form gửi ngay lập tức

        const button = document.getElementById("resendButton");
        button.disabled = true; // Disable nút

        // Hiển thị thông báo đếm ngược
        let countdown = 120; // 2 phút = 120 giây
        const originalText = button.innerText;
        button.innerText = `Vui lòng chờ ${countdown} giây`;

        const timer = setInterval(() => {
            countdown -= 1;
            if (countdown > 0) {
                button.innerText = `Vui lòng chờ ${countdown} giây`;
            } else {
                clearInterval(timer);
                button.disabled = false; // Bật lại nút
                button.innerText = originalText; // Phục hồi text ban đầu
            }
        }, 1000);

        // Gửi form sau khi disable nút
        setTimeout(() => {
            event.target.submit();
        }, 100); // Gửi ngay sau khi disable
    }