$(document).ready(function() {
	// Handle route link click
	$(".trip-link").click(function(event) {
		event.preventDefault();
		var tripId = $(this).data("trip-id");
		var stationContainerId = "#station-" + tripId; // Create a unique ID
		console.log(tripId);
		$.ajax({
			url: "/manager/trips/" + tripId + "/stations",
			success: function(data) {
				$(stationContainerId).html(data); // Update the specific seat container
			},
			error: function(xhr, status, error) {
				console.error("Error: " + error);
				alert("Failed to load seats.");
			}
		});
	});
});