<%@ page import="pjwstk.tpo_6.mingle.Utils.CookieMap"%>
<%@ page import="pjwstk.tpo_6.mingle.Utils.JwtUtil"%>
<%@ page import="pjwstk.tpo_6.mingle.Utils.JwtToken"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
	CookieMap cookies = CookieMap.mapCookies(request.getCookies());
	JwtToken token = JwtUtil.parseToken(cookies.get("jwt").getValue()); String userId =
	token.getUserId(); String username = token.getUserName();
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8" />
		<title>Mingle</title>
		<link rel="stylesheet" href="/css/calendar.css" />
		<link rel="stylesheet" href="/css/app.css" />
		<script src="/js/jquery.min.js"></script>
		<script src="/js/app_navigator.js"></script>
		<script src="/js/app_http_engine.js"></script>
		<script src="/js/calendar.js"></script>
		<script src="/js/app.js"></script>
		<style>
			html {
				background-color: #11171a;
				overflow: hidden;
			}
			body {
				display: none;
			}
		</style>
	</head>
	<body>
		<div id="container" usrnm="<%= username %>" uid="<%= userId %>">
			<div id="chats-container">
				<div id="chat-groups-header">
					<div class="account-logo-container">
						<div class="account-logo">
							<div class="logo-placeholder">
								<div class="logo-placeholder-bg"></div>
								<div class="logo-placeholder-text"></div>
							</div>
						</div>
					</div>
					<div id="chats-search">
						<input
							type="text"
							id="chat-search-input"
							placeholder="@Username"
							autocomplete="off"
							autocomplete="off"
						/>
					</div>
				</div>
				<div id="chats">
					<div
						id="active-chat-groups"
						class="chat-list"
						data-chat-id=""
					><div id="empty-groups-list" class="fade-in">
						Nothing to see here yet...
					</div></div>
					<div id="chat-search-result-container" hidden>
						<div
							id="chat-search-result"
							class="chat-list fade-in"
						></div>
						<div id="empty-search-result" class="fade-in">
							Nothing to see here yet...
						</div>
					</div>
				</div>
			</div>
			<div id="active-chat-container">
				<div id="active-chat" class="fade-in" hidden>
					<div id="active-chat-header" data-chat-id="">
						<div id="active-chat-info-container">
							<div id="active-chat-info">
								<div id="active-chat-name"></div>
								<div id="active-chat-username"></div>
							</div>
							<div id="time-range-container">
								<div id="date-time-container">
									<div id="date-time-close">
										<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="100" height="100">
											<line x1="50" y1="10" x2="50" y2="90" stroke="black" stroke-width="15" stroke-linecap="round"/>
											<line x1="10" y1="50" x2="90" y2="50" stroke="black" stroke-width="15" stroke-linecap="round"/>
										</svg>
									</div>
								</div>
								<div class="datetime-picker">
									<div class="time-range-button-container">
										<button class="time-range-button from">
											<svg version="1.1" class="arrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 11.6 7.7">
												<path d="M5.8,7.7C5.4,7.7,5,7.5,4.7,7.2L0.3,2.4c-0.5-0.5-0.5-1.5,0-2S1.4,0,2,0l7.6,0c0.7,0,1.3-0.1,1.7,0.4 c0.5,0.5,0.5,1.4,0,1.9L6.9,7.2C6.6,7.6,6.2,7.7,5.8,7.7L5.8,7.7z"/>
											</svg>
											Time: from
										</button>
									</div>
									<calendar id="calendar-from"></calendar>
								</div>
								<div class="datetime-picker">
									<div class="time-range-button-container">
										<button class="time-range-button to">
											<svg version="1.1" class="arrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 11.6 7.7">
												<path d="M5.8,7.7C5.4,7.7,5,7.5,4.7,7.2L0.3,2.4c-0.5-0.5-0.5-1.5,0-2S1.4,0,2,0l7.6,0c0.7,0,1.3-0.1,1.7,0.4c0.5,0.5,0.5,1.4,0,1.9L6.9,7.2C6.6,7.6,6.2,7.7,5.8,7.7L5.8,7.7z"/>
											</svg>
											Time: to
										</button>
									</div>
									<calendar id="calendar-to"></calendar>
								</div>
							</div>
						</div>
					</div>
					<div id="active-chat-messages-container" class="">
						<div id="no-messages">Nothing to see here yet...</div>
						<div id="active-chat-messages">
						</div>
					</div>
					<div id="active-chat-input-area">
						<input
							type="text"
							id="active-chat-input"
							placeholder="Message"
							autocomplete="off"
						/>
						<div id="send-message-button-container">
							<div id="send-message-button">
								<img
									id="send-message-button-logo"
									src="/assets/logo/Logo_Icon.svg"
									alt="logo"
									class=""
								/>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="logout-button">
				<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 335 384"><path d="M334.42,191.74c0-43.16-.64-86.34.21-129.49A61.34,61.34,0,0,0,272.15-.23C217,.29,161.81-.1,106.63-.08c-11.49,0-19.1,6.58-19.69,16.87a17.7,17.7,0,0,0,16.92,19.1c1.66.11,3.33.06,5,.06q81.26,0,162.52,0c17.49,0,27,9.39,27,26.73q0,129.24,0,258.48c0,17.38-9.48,26.81-26.92,26.81q-82.51,0-165,0c-10.8,0-18.27,6.09-19.36,15.58-1.28,11.16,6.26,20,17.49,20.43.67,0,1.33,0,2,0q83.76,0,167.52,0c30.36-.06,55.51-21.73,59.78-51.28a77.16,77.16,0,0,0,.54-11Q334.46,256.73,334.42,191.74Z"/><path d="M63,210h5.35q66.75,0,133.51,0c13.94,0,22.63-11.57,18.33-24.14-2.54-7.43-9.31-11.86-18.38-11.87q-51,0-102,0H62c1.71-1.89,2.72-3.1,3.82-4.21C82.5,153,99.3,136.33,115.93,119.49c9.43-9.55,6.95-24.11-4.8-29.63-6.94-3.27-14.73-1.67-21,4.55q-34,33.87-67.91,67.83C16.83,167.66,11.38,173.05,6,178.53c-8,8.18-8.18,18.64-.18,26.67Q48,247.52,90.34,289.66c7.76,7.72,18.69,7.87,25.93.69s7.11-18.44-.71-26.32q-24.65-24.84-49.48-49.5a38.42,38.42,0,0,0-3.95-3.09Z"/></svg>
			</div>
		</div>
		<template id="chat-template">
			<div class="chat">
				<div class="chat-logo-container">
					<div class="chat-logo">
						<div class="logo-placeholder">
							<div class="logo-placeholder-bg"></div>
							<div class="logo-placeholder-text"></div>
						</div>
					</div>
				</div>
				<div class="chat-info">
					<div class="chat-info-header">
						<div class="chat-name"></div>
						<div class="chat-last-message-time"></div>
					</div>
					<div class="chat-last-message truncate"></div>
				</div>
			</div>
		</template>
		<template id="message-template">
			<div class="message-container">
				<div class="companion-logo-container">
					<div class="companion-logo">
						<div class="logo-placeholder">
							<div class="logo-placeholder-bg"></div>
							<div class="logo-placeholder-text"></div>
						</div>
					</div>
				</div>
				<div class="message">
					<div class="message-text"></div>
					<div class="message-time"></div>
				</div>
			</div>
		</template>
		<template id="chat-date-template">
			<div class="date-container">
				<div class="date">
					<div class="date-text"></div>
				</div>
			</div>
		</template>
		<template id="search-result-template">
			<div class="search-result" data-user-id="">
				<div class="search-result-logo-container">
					<div class="search-result-logo">
						<div class="logo-placeholder">
							<div class="logo-placeholder-bg"></div>
							<div class="logo-placeholder-text"></div>
						</div>
					</div>
				</div>
				<div class="search-result-info">
					<div class="search-result-info-header">
						<div class="search-result-name"></div>
					</div>
					<div class="search-result-username"></div>
				</div>
			</div>
		</template>
	</body>
</html>
