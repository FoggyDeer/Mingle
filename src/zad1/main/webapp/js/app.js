const chat_template_id = "chat-template";
const message_template_id = "message-template";
const search_result_template_id = "search-result-template";
const chat_date_template_id = "chat-date-template";
function whenAvailable(name, callback) {
	var interval = 10;
	window.setTimeout(function () {
		if (window[name]) {
			callback(window[name]);
		} else {
			whenAvailable(name, callback);
		}
	}, interval);
}
whenAvailable("AppEngine", function (t) {
	document.body.removeAttribute("style");

	AppEngine().getName({
		success: (response) => {
			window.name = response;
			window.username = getJwtPayload().username;
			window.userid = getJwtPayload().sub;

			let logo = document.querySelector(".account-logo");
			logo.querySelector(".logo-placeholder-bg").classList.add(
				`p${calcColorId(window.name)}`
			);
			logo.querySelector(".logo-placeholder-text").innerHTML =
				window.name[0].toUpperCase();
		},
	});

	$("#active-chat-input").keydown(function (event) {
		if (event.keyCode === 13) {
			sendMessage();
		}
	});

	$("#send-message-button").click(function () {
		if ($("#active-chat-input").val() === "") {
			return;
		}

		$(this).find("#send-message-button-logo").removeClass("rotate-scale");
		setTimeout(() => {
			$(this).find("#send-message-button-logo").addClass("rotate-scale");
		}, 10);
		sendMessage();
	});

	$("#chat-search-input").on("input", function () {
		let prevSearch = $(this).val();
		let search = $(this)
			.val(this.value.match(/[a-zA-Z0-9_][a-zA-Z0-9_\.\-]{0,24}/))
			.val();

		if (prevSearch !== search) return;

		let resultContainer = document.querySelector(
			"#chat-search-result-container"
		);
		let results = document.querySelector("#chat-search-result");

		if (search[0] === "@") search = search.substring(1);

		if (search === "") {
			document.querySelector("#empty-search-result").style.display = "";
			results.innerHTML = "";
			resultContainer.setAttribute("hidden", "");
			return;
		}

		resultContainer.removeAttribute("hidden");

		AppEngine().findUsers({
			pattern: search,
			success: (response) => {
				let responseObj = JSON.parse(response);
				results.innerHTML = "";
				if (responseObj.length === 0)
					document.querySelector(
						"#empty-search-result"
					).style.display = "block";
				else
					document.querySelector(
						"#empty-search-result"
					).style.display = "";

				responseObj.forEach((user) => {
					let chat = cloneTeemplate(search_result_template_id);
					chat.querySelector(".search-result").setAttribute(
						"data-user-id",
						user.id
					);
					chat.querySelector(".search-result-name").innerText =
						user.name;
					chat.querySelector(".search-result-username").innerText =
						user.username;
					chat.querySelector(".search-result").addEventListener(
						"click",
						handleOpenSearchChat
					);
					chat.querySelector(".logo-placeholder-bg").classList.add(
						`p${calcColorId(user.name)}`
					);
					chat.querySelector(".logo-placeholder-text").innerHTML =
						user.name[0].toUpperCase();

					chat.querySelector(".search-result").user = {};
					chat.querySelector(".search-result").user.id = user.id;
					chat.querySelector(".search-result").user.name = user.name;
					chat.querySelector(".search-result").user.username =
						user.username;

					results.appendChild(chat);
				});
			},
		});
	});

    
    document.querySelector("#date-time-close").addEventListener("click", function () { 
        document.querySelector("#date-time-close").classList.remove("visible");
        document.querySelector("#active-chat-input-area").classList.remove("hidden");
        document.querySelectorAll(".dropdown").forEach((dropdown) => { 
            dropdown.classList.remove("active");
            dropdown.querySelectorAll(".selected").forEach((selected) => { 
                selected.classList.remove("selected");
            });
        });
        document.querySelectorAll(".time-range-button").forEach((dropdown) => { 
            dropdown.classList.remove("active");
        });
        calendarFrom.date = null;
        calendarTo.date = null;
        window.doMessageFilter = false;
        getMessages(false, null, null);
    });

	document.querySelector("#logout-button").addEventListener("click", function () { 
		AppEngine().logout({
			success: () => { 
				AppNavigator.loadingPage();
			}
		});
	});

	getChats();
	setInterval(getChats, 5000);
	setInterval(getMessages, 5000);
});
var calendarFrom = null;
var calendarTo = null;
setTimeout(() => {
	calendarFrom = new TimeCalendar(
		document.querySelector(".time-range-button.from"),
		document.querySelector("#calendar-from"),
		handleTimeFilter
	);
	calendarTo = new TimeCalendar(
		document.querySelector(".time-range-button.to"),
		document.querySelector("#calendar-to"),
		handleTimeFilter
	);
}, 1000);

function handleTimeFilter(e) {
    document.querySelector("#active-chat-input-area").classList.add("hidden");
    document.querySelector("#date-time-close").classList.add("visible");
    window.doMessageFilter = true;
	getMessages(
		true,
		calendarFrom.date == undefined || calendarFrom.date === null
			? null
			: new Date(calendarFrom.date).toISOString().replace("Z", ""),
		calendarTo.date === undefined || calendarTo.date === null
			? null
			: new Date(calendarTo.date).toISOString().replace("Z", "")
	);
}

function sendMessage() {
	let text = $("#active-chat-input").val();
	if (!validateMessageText(text)) {
		return;
	}

	AppEngine().sendMessage({
		text: text,
		to: document.querySelector("#active-chat").chat.username,
		from: window.username,
		success: (response) => {
			$("#active-chat-input").val("");
			getMessages();
		},
	});
}

function getCookie(name) {
	const nameEQ = name + "=";
	const ca = document.cookie.split(";");
	for (let i = 0; i < ca.length; i++) {
		let c = ca[i];
		while (c.charAt(0) === " ") c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) === 0) {
			return c.substring(nameEQ.length, c.length);
		}
	}
	return null;
}

function base64UrlDecode(base64Url) {
	let base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
	while (base64.length % 4) {
		base64 += "=";
	}
	return atob(base64);
}

function decodeJWT(token) {
	const parts = token.split(".");
	if (parts.length !== 3) {
		throw new Error("Invalid JWT token");
	}

	const payload = base64UrlDecode(parts[1]);
	return JSON.parse(payload);
}

function getJwtPayload() {
	try {
		return decodeJWT(getCookie("jwt"));
	} catch (e) {
		return null;
	}
}

function getMessages(animate = false, timeFrom = null, timeTo = null) {
	let active_chat = document.querySelector("#active-chat");
	if (
		active_chat.hasAttribute("hidden") ||
		active_chat.chat === undefined ||
		active_chat.chat === null ||
		active_chat.chat.id === undefined ||
        active_chat.chat.id === null
        || (window.doMessageFilter == true && !animate)
	)
		return;

	let messages = document.querySelector("#active-chat-messages");

	let obj = {
		username: active_chat.chat.username,
		success: (response) => {
			let responseObj = JSON.parse(response);

			if (
				timeFrom == null &&
				timeTo === null &&
				responseObj.length != 0 &&
				messages.childElementCount > 0 &&
				responseObj[responseObj.length - 1].id ==
					messages.lastElementChild.querySelector(".message").id
			)
				return;
			messages.classList.remove("fade-in");
			messages.style.display = "";
			messages.innerHTML = "";
			let selfId = parseInt(getJwtPayload().sub);

			if (responseObj.length === 0)
				document.querySelector("#no-messages").style.display = "block";
			else document.querySelector("#no-messages").style.display = "";

            responseObj.forEach((messageInfo) => {
                if (messages.childElementCount == 0) {
                    let message = cloneTeemplate(chat_date_template_id);
                    message.querySelector(".date-text").innerText = extractDate(messageInfo.time);
                    messages.appendChild(message);
                }else if (messages.lastElementChild.date != extractDate(messageInfo.time)) {
                    let message = cloneTeemplate(chat_date_template_id);
                    message.querySelector(".date-text").innerText = extractDate(messageInfo.time);
                    messages.appendChild(message);
                }
				let message = cloneTeemplate(message_template_id);
				message.querySelector(".message").id = messageInfo.id;
				message.querySelector(".message").senderId =
					messageInfo.senderId;
				message.querySelector(".message-text").innerText =
					messageInfo.text;
				message.querySelector(".message-time").innerText = formatTime(
					messageInfo.time
                );
                message.querySelector(".message-container").date = extractDate(messageInfo.time);

				let sender =
					messageInfo.senderId === selfId ? "self" : "companion";
				message
					.querySelector(".message-container")
					.classList.add(sender);

				if (sender === "self") {
					message
						.querySelector(".logo-placeholder-bg")
						.classList.add(`p${calcColorId(window.name)}`);
					message.querySelector(".logo-placeholder-text").innerHTML =
						window.name[0].toUpperCase();
				} else {
					message
						.querySelector(".logo-placeholder-bg")
						.classList.add(
							`p${calcColorId(active_chat.chat.name)}`
						);
					message.querySelector(".logo-placeholder-text").innerHTML =
						active_chat.chat.name[0].toUpperCase();
				}

				messages.appendChild(message);
			});

			messages.style.display = "flex";
			if (messages.childElementCount > 0 && timeFrom == null && timeTo == null)
				messages.lastElementChild.classList.add("slide-in");
			if (animate) {
				setTimeout(() => {
					messages.classList.add("fade-in");
				}, 80);
			} else messages.classList.add("fade-in");
		},
	};

	if (timeFrom !== null) obj.timeFrom = timeFrom;
	if (timeTo !== null) obj.timeTo = timeTo;
	AppEngine().getMessages(obj);
}

function extractDate(dateString) { 
    let date = new Date(dateString)
    const options = {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      };
      
    return date.toLocaleDateString('en-GB', options).replace(/\//g, '.')
}

function getChats() {
	AppEngine().getChats({
		success: (response) => {
			let responseObj = JSON.parse(response);
			let active_chats = document.querySelector("#active-chat-groups");

			if (responseObj.length === 0)
				document.querySelector("#empty-groups-list").style.display =
					"block";
			else
				document.querySelector("#empty-groups-list").style.display = "";

			responseObj.forEach((chat_info) => {
				let isActive = $(
					'#active-chat-groups [data-chat-id="' + chat_info.id + '"]'
				).hasClass("active");
				$(
					'#active-chat-groups [data-chat-id="' + chat_info.id + '"]'
				).remove();

				let chat = cloneTeemplate(chat_template_id);
				chat.querySelector(".chat").setAttribute(
					"data-chat-id",
					chat_info.id
				);
				chat.querySelector(".chat-name").innerText = chat_info.name;
				chat.querySelector(".chat-last-message").innerText =
					chat_info.lastMessage;
				chat.querySelector(".chat-last-message-time").innerText =
					formatDateTime(chat_info.lastMessageTime);
				chat.querySelector(".logo-placeholder-bg").classList.add(
					`p${calcColorId(chat_info.name)}`
				);
				chat.querySelector(".logo-placeholder-text").innerHTML =
					chat_info.name[0].toUpperCase();
				chat.querySelector(".chat").addEventListener(
					"click",
					handleOpenChat
				);
				chat.querySelector(".chat").chat = {};
				chat.querySelector(".chat").chat.id = chat_info.id;
				chat.querySelector(".chat").chat.username = chat_info.username;
				chat.querySelector(".chat").chat.name = chat_info.name;
				if (isActive)
					chat.querySelector(".chat").classList.add("active");
				active_chats.append(chat);
			});
		},
	});
}

function initActiveChat(id, username, name) {
	let active_chat = document.querySelector("#active-chat");
	active_chat.chat = {};
	active_chat.chat.id = id;
	active_chat.chat.name = name;
	active_chat.chat.username = username.replace("@", "");
	active_chat.removeAttribute("hidden");

	document.querySelector("#active-chat-messages").innerText = "";
	document.querySelector("#active-chat-name").innerText = name;
	document.querySelector("#active-chat-username").innerText = "@" + username.replace("@", "");
}

function handleOpenChat() {
	if (this.classList.contains("active")) return;
	let active = document
		.querySelector("#chats-container")
		.querySelector(".active");
	if (active !== null) active.classList.remove("active");
	this.classList.add("active");
	initActiveChat(this.chat.id, this.chat.username, this.chat.name);
	getMessages(true);
}

function handleOpenSearchChat(event) {
	if (this.classList.contains("active")) return;
	let active = document
		.querySelector("#chats-container")
		.querySelector(".active");
	if (active !== null) active.classList.remove("active");
	this.classList.add("active");
	initActiveChat(this.user.id, this.user.username, this.user.name);
	getMessages(true);
}

function calcColorId(str) {
	let sum = 0;
	for (let i = 0; i < str.length; i++) {
		sum += str.charCodeAt(i);
	}
	return Math.abs(Math.floor((Math.cos(Math.pow(sum, 2)) * 14) % 10));
}

function formatDateTime(dateString) {
	const date = new Date(dateString);
	const now = new Date();

	const diffInSeconds = Math.floor((now - date) / 1000);
	const diffInMinutes = Math.floor(diffInSeconds / 60);
	const diffInHours = Math.floor(diffInMinutes / 60);
	const diffInDays = Math.floor(diffInHours / 24);
	const diffInWeeks = Math.floor(diffInDays / 7);
	const diffInMonths = Math.floor(diffInDays / 30);
	const diffInYears = Math.floor(diffInDays / 365);

	if (diffInDays < 1) {
		return date.toLocaleTimeString("en-GB", {
			hour: "2-digit",
			minute: "2-digit",
			hour12: false,
		});
	} else if (diffInDays < 7) {
		return `${diffInDays} ${diffInDays === 1 ? "day" : "days"} ago`;
	} else if (diffInDays < 30) {
		return `${diffInWeeks} ${diffInWeeks === 1 ? "week" : "weeks"} ago`;
	} else if (diffInDays < 365) {
		return `${diffInMonths} ${diffInMonths === 1 ? "month" : "months"} ago`;
	} else {
		return `${diffInYears} ${diffInYears === 1 ? "year" : "years"} ago`;
	}
}

function formatTime(dateStr) {
	let date = new Date(dateStr);

	let options = { hour: "2-digit", minute: "2-digit", hour12: false };
	let timeWithoutSeconds = date.toLocaleTimeString("en-GB", options);
	return timeWithoutSeconds;
}

function updateMessagePool() {
	AppEngine().updateMessagePool({
		activeChatId: $("#active-chat-header").attr("data-chat-id"),
		beforeSend: () => {},
		success: (response) => {
			let responseObj = JSON.parse(response);
			if (
				responseObj.hiddenChats === undefined ||
				responseObj.hiddenChats === null
			) {
				responseObj.hiddenChats.forEach((message) => {
					let isActive = $("#active-chat-groups").hasClass("active");

					$(
						'#active-chat-groups [data-user-id="' + message + '"]'
					).remove();
					let chatElement = cloneTeemplate(chat_template_id);
					chat.querySelector(".chat").setAttribute(
						"data-chat-id",
						message.chatId
					);
					chatElement.querySelector(".chat-name").innerText =
						message.text;
					chatElement.querySelector(
						".chat-last-message-time"
					).innerText = message.time;
					if (isActive)
						chatElement.querySelector(
							".chat-last-message"
						).innerText = message.lastMessage;
					document
						.querySelector("#active-chat-groups")
						.insertAdjacentElement("afterbegin", chatElement);
				});
			}
			let active_chat_messages = responseObj.activeChatMessages;
			if (
				active_chat_messages !== undefined &&
				active_chat_messages !== null
			) {
				active_chat_messages.forEach((message) => {
					let messageElement = document
						.getElementById("message-template")
						.content.cloneNode(true);
					messageElement.querySelector(".message").id = message.id;
					messageElement.querySelector(".message-text").innerText =
						message.text;
					messageElement.querySelector(".message-time").innerText =
						message.time;
					messageElement
						.querySelector(".message-container")
						.classList.add(message.from);
					$("#active-chat-messages").append(messageElement);
				});
			}
		},
	});
}

function validateMessageText(text) {
	if (text === "" || text === undefined || text === null) {
		return false;
	}
	return true;
}

function cloneTeemplate(selector) {
	return document.getElementById(selector).content.cloneNode(true);
}
