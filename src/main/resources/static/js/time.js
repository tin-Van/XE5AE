function validateForm() {
			// Lấy giá trị từ các trường nhập liệu
			let name = document.getElementById("name").value.trim();
			let licensePlate = document.getElementById("licensePlate").value
					.trim();
			let capacity = document.getElementById("capacity").value.trim();

			// Kiểm tra trường "Name"
			if (name === "") {
				swal("Error", "Vui lòng nhập tên xe!", "warning");
				return;
			}
			// Kiểm tra trường "Biển số xe"
			if (licensePlate === "") {
				swal("Error", "Vui lòng nhập biển số xe!", "warning");
				return;
			}
			// Kiểm tra trường "Sức chứa"d
			if (capacity === "" || isNaN(capacity)) {
				swal("Error", "Vui lòng nhập sức chứa hợp lệ!", "warning");
				return;
			}

			// Nếu không có lỗi, cho phép gửi form
			document.getElementById("busForm").submit();
		}
		//Thời Gian
		function time() {
			var today = new Date();
			var weekday = new Array(7);
			weekday[0] = "Chủ Nhật";
			weekday[1] = "Thứ Hai";
			weekday[2] = "Thứ Ba";
			weekday[3] = "Thứ Tư";
			weekday[4] = "Thứ Năm";
			weekday[5] = "Thứ Sáu";
			weekday[6] = "Thứ Bảy";
			var day = weekday[today.getDay()];
			var dd = today.getDate();
			var mm = today.getMonth() + 1;
			var yyyy = today.getFullYear();
			var h = today.getHours();
			var m = today.getMinutes();
			var s = today.getSeconds();
			m = checkTime(m);
			s = checkTime(s);
			nowTime = h + " giờ " + m + " phút " + s + " giây";
			if (dd < 10) {
				dd = '0' + dd
			}
			if (mm < 10) {
				mm = '0' + mm
			}
			today = day + ', ' + dd + '/' + mm + '/' + yyyy;
			tmp = '<span class="date"> ' + today + ' - ' + nowTime + '</span>';
			document.getElementById("clock").innerHTML = tmp;
			clocktime = setTimeout("time()", "1000", "Javascript");

			function checkTime(i) {
				if (i < 10) {
					i = "0" + i;
				}
				return i;
			}
		}

		//Modal
		$("#show-emp").on("click", function() {
			$("#ModalUP").modal({
				backdrop : false,
				keyboard : false
			});
		});