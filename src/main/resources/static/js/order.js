let selectedSeatIds = [];
let seatIDInputvalue;
function selectSeat(seatElement) {
	const seatID = seatElement.getAttribute('data-seat-id');
	const seatIDInput = seatElement.querySelector('.seat-id-input');
	const isBooked = seatElement.getAttribute('data-is-booked') === 'true';

	if (isBooked) {
		alert("Ghế này đã được đặt!");
		return;
	}

	// Nếu ghế đã được chọn
	if (seatElement.classList.contains('seat-selected')) {
		// Bỏ chọn ghế
		seatElement.classList.remove('seat-selected');
		selectedSeatIds = [];
		selectedSeatIds = selectedSeatIds.filter(id => id !== seatID);
	} else {
		// Chọn ghế
		seatElement.classList.add('seat-selected');
		selectedSeatIds = [];
		selectedSeatIds.push(seatID); // Cập nhật selectedSeatId
		seatIDInputvalue=selectedSeatIds;
	}

	// Điền giá trị seatID vào thẻ input ẩn
	seatIDInput.value = selectedSeatIds;
	
}
function closeForm(routeElement) {
	const tripId = routeElement.getAttribute('data-trip-id');
	var seatContainerId = "#seatFormContainer-" + tripId;
	$(seatContainerId).hide();
	
}

function openForm(tripElement) {
	const tripId = tripElement.getAttribute('data-trip-id');
	var seatContainerId = "#seatFormContainer-" + tripId;
	$(seatContainerId).show();
	
}
function submitForm(event){
	// Handle form submission

		if (seatIDInputvalue.length > 0) {
			
		} else {
			alert("Vui lòng chọn ghế để tiếp tục đặt vế.");
			event.preventDefault();
		}
}

$(document).ready(function() {
	// Handle route link click
	$(".route-link").click(function(event) {
		event.preventDefault();
		var tripId = $(this).data("trip-id");
		var seatContainerId = "#seats-" + tripId;
		console.log(tripId);
		$.ajax({
			url: "/trips/" + tripId + "/seats",
			success: function(data) {
				$(seatContainerId).html(data);

			},
			error: function(xhr, status, error) {
				console.error("Error: " + error);
				alert("Failed to load seats.");
			}
		});
	});
});