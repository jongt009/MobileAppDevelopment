var objects;

$(document).ready(function() {

	objects = loadObjects();

	showObjects();

	addEvents();

});

$(window).unload(function() {
	saveObjects();
});

function loadObjects() {

	if ( json = localStorage.getItem("objects")) {
		// Retrieve data from JSON
		returnValue = JSON.parse(json);
	} else {
		// No data stored
		returnValue = [];
	}

	return returnValue;
}

function saveObjects() {
	localStorage.setItem("objects", JSON.stringify(objects));
}

function showObjects() {
	listView = $('#objects');

	// Foreach loop
	for ( i = 0; i < objects.length; i++) {
		if (!objects[i]) {
			continue;
		}
		value = objects[i].value;

		if (!listView.find("li[name='" + value + "']").get(0)) {
			listItem = "<li name='" + value + "'>" + value + '</li>';
			listView.append(listItem);
		}
	}
	listView.listview();
	listView.listview("refresh");
}

function showDeleteObjects() {
	listView = $('#deleteobjects');
	console.log("showing"+listView.html());
	// Foreach loop
	for ( i = 0; i < objects.length; i++) {
		if (!objects[i]) {
			continue;
		}
		value = objects[i].value;

		if (!listView.find("li[delname='" + value + "']").get(0)) {			
			listItem = "<li class='deletable' delname='"+value+"'>" + value + '</li>';
			listView.append(listItem);
			console.log(listItem);
		}
	}
	listView.listview();
	listView.listview("refresh");
}

function addObject() {
	val = $('#input').val();
	if (val) {
		objects.push({
			value : val
		});
	}
	showObjects();

	$('#input').val('');
}

function removeObject(objectValue) {

	clearObjectLists();
	console.log(listView.find('li').length);

	result = [];
	currentIndex = 0;
	for ( i = 0; i < objects.length; i++) {
		object = objects[i];

		if (!object) {
			continue;
		}
		value = object.value;
		if (value !== objectValue && value) {
			result[currentIndex] = {value : value};
			currentIndex++;
		}
	}
	
	objects = result;
	showDeleteObjects();
	showObjects();
}

function clearObjectLists() {
	console.log("clean");
	listview = 	$('#objects');
	listView.find('li').remove();
	listview.html('');
	listView.listview("refresh");
	
	listview = $('#deleteobjects');
	listView.find('li').remove();
	listview.html('');
	listView.listview("refresh");
}

function addEvents() {
	$(document).on('click', "#inputButton", function() {
		addObject();
	});

	$('#input').on('keyup', function(e) {
		if (e.keyCode === 13) {
			addObject();
		}
	});
	
	$(document).on('click', "li", function() {
		value = $(this).attr('delname');
		if(value){
			removeObject(value);
		}
	});

	$(document).on("pageshow", "#deleteView", function() {
		showDeleteObjects();
	});
	
	$(document).on("pageshow", "#home", function() {
		showDeleteObjects();
	});
}