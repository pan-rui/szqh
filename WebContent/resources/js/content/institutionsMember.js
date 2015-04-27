define(function(require) {
	var Site = require('../comp/init.js');
	var form = $('#institutionMember_form');
	$('.submit_form').click(function() {
		form.submit();
	});
});