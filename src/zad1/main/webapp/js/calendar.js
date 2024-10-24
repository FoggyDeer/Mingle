class TimeCalendar{
    static id = 0;
    constructor(label, container, eventHandler) {
        this.id = TimeCalendar.id++;
        this.eventHandler = eventHandler;

        let calendar = document.createElement('div');
        calendar.classList.add('dropdown');
        calendar.id = `calendar-${this.id}`;
        calendar.innerHTML =   `<div class="tab-buttons">
                                    <div class="tab-button date-tab active">Date</div>
                                    <div class="tab-button time-tab">Time</div>
                                </div>
                                <div class="tab-content active date-content">
                                    <div class="calendar">
                                        <div class="calendar-header">
                                            <span class="prev-month">&#x25C0;</span>
                                            <span class="current-month"></span>
                                            <span class="next-month hidden">&#x25B6;</span>
                                        </div>
                                        <div class="calendar-grid">
                                        </div>
                                    </div>
                                </div>
                                <div class="tab-content time-content" >
                                    <div class="time-picker">
                                        <div class="time-scroll hour-scroll">
                                        </div>
                                        <div class="time-separator">:</div>
                                        <div class="time-scroll minute-scroll">
                                        </div>
                                    </div>
                                </div>`;
        
        this.DOMInstance = calendar;
        this.date = undefined;
        label.calendar = calendar;
        container.appendChild(calendar);
        label.addEventListener('click', function () {
            document.querySelectorAll('.dropdown').forEach(calendar => {
                if (calendar !== this.calendar) {
                    calendar.classList.remove('active');
                }
            });
            document.querySelectorAll('.time-range-button').forEach(button => {
                if (button !== this) {
                    button.classList.remove('active');
                }
            });
            this.calendar.classList.toggle('active');
            this.classList.toggle('active');
        });

        this.currentMonth = new Date().getMonth();
        this.currentYear = new Date().getFullYear();

        Object.defineProperty(this, 'monthNames', {
            value: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            writable: false,
            configurable: false
        });

        calendar.querySelector('.current-month').innerText = this.monthNames[new Date().getMonth()] + ' ' + new Date().getFullYear();

        Object.defineProperty(this, 'daysInMonth', {
            value: [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
            writable: false,
            configurable: false
        });

        this.DOMInstance.querySelector('.next-month').TimeCalendar = this;
        this.DOMInstance.querySelector('.prev-month').TimeCalendar = this;
        this.DOMInstance.querySelector('.prev-month').addEventListener('click', function() {
            this.TimeCalendar.DOMInstance.querySelector('.next-month').classList.remove('hidden');
            if (this.TimeCalendar.currentMonth === 0) {
                this.TimeCalendar.currentMonth = 11;
                this.TimeCalendar.currentYear--;
            } else {
                this.TimeCalendar.currentMonth--;
            }
            this.TimeCalendar.generateCalendar(this.TimeCalendar.currentMonth, this.TimeCalendar.currentYear, this.TimeCalendar);
        });

        this.DOMInstance.querySelector('.next-month').addEventListener('click', function() {

            if (this.TimeCalendar.currentMonth === 11) {
                this.TimeCalendar.currentMonth = 0;
                this.TimeCalendar.currentYear++;
            } else {
                this.TimeCalendar.currentMonth++;
            }
            
            let cmonth = new Date().getMonth();
            let cyear = new Date().getFullYear();
            if(this.TimeCalendar.currentMonth == cmonth && this.TimeCalendar.currentYear == cyear){
                this.TimeCalendar.DOMInstance.querySelector('.next-month').classList.add('hidden');
            }

            this.TimeCalendar.generateCalendar(this.TimeCalendar.currentMonth, this.TimeCalendar.currentYear, this.TimeCalendar);
        });

        

        this.DOMInstance.querySelector('.date-tab').addEventListener('click', function() {
            calendar.querySelector('.date-content').classList.add('active');
            calendar.querySelector('.date-content').classList.add('active');
            calendar.querySelector('.time-content').classList.remove('active');
            calendar.querySelector('.date-tab').classList.add('active');
            calendar.querySelector('.time-tab').classList.remove('active');
        });

        this.DOMInstance.querySelector('.time-tab').addEventListener('click', function() {
            calendar.querySelector('.time-content').classList.add('active');
            calendar.querySelector('.date-content').classList.remove('active');
            calendar.querySelector('.time-tab').classList.add('active');
            calendar.querySelector('.date-tab').classList.remove('active');
        });
        
        this.generateCalendar(this.currentMonth, this.currentYear, this);
        this.generateTimeOptions(this);
    }
    
    isLeapYear(year) {
        return ((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0);
    }

    generateCalendar(month, year, TimeCalendar) {
        const calendarGrid = TimeCalendar.DOMInstance.querySelector('.calendar-grid');
        calendarGrid.innerHTML = '<div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div><div>Sun</div>';
        const firstDay = new Date(year, month, 1).getDay();
        const daysInCurrentMonth = (month === 1 && TimeCalendar.isLeapYear(year)) ? 29 : TimeCalendar.daysInMonth[month];
        const daysInPrevMonth = (month === 0) ? TimeCalendar.daysInMonth[11] : (month === 1 && TimeCalendar.isLeapYear(year)) ? 29 : TimeCalendar.daysInMonth[month - 1];
        
        let prevMonthDays = (firstDay || 7) - 1;
        for (let i = daysInPrevMonth - prevMonthDays + 1; i <= daysInPrevMonth; i++) {
            calendarGrid.innerHTML += `<div class="disabled-date">${i}</div>`;
        }
        let cdate = new Date().getDate();
        let cmonth = new Date().getMonth();
        let cyear = new Date().getFullYear();
        for (let i = 1; i <= daysInCurrentMonth; i++) {
            if((TimeCalendar.currentYear > cyear) || (TimeCalendar.currentMonth > cmonth && cyear == TimeCalendar.currentYear) || (TimeCalendar.currentMonth == cmonth && cyear == TimeCalendar.currentYear && i > cdate)){
                calendarGrid.insertAdjacentHTML('beforeend', `<div class="disabled-date">${i}</div>`);
            } else {
                let dateCell = document.createElement('div');
                dateCell.classList.add('enabled-date');
                dateCell.innerText = i;
                dateCell.year = TimeCalendar.currentYear;
                dateCell.month = TimeCalendar.currentMonth;
                dateCell.date = i;
                dateCell.TimeCalendar = TimeCalendar;
                dateCell.addEventListener('click', TimeCalendar.handleSelectDate);
                calendarGrid.appendChild(dateCell);
            }
        }
        
        const remainingCells = 7 - (calendarGrid.children.length % 7);
        for (let i = 1; i <= remainingCells && remainingCells < 7; i++) {
            calendarGrid.insertAdjacentHTML('beforeend', `<div class="disabled-date">${i}</div>`);
        }

        TimeCalendar.DOMInstance.querySelector('.current-month').innerText = `${this.monthNames[month]} ${year}`;
    }

    handleSelectDate() {
        if (this.TimeCalendar.date === undefined || this.TimeCalendar.date === null) {
            this.TimeCalendar.date = new Date();
            this.TimeCalendar.date.setHours(0);
            this.TimeCalendar.date.setMinutes(0);
            this.TimeCalendar.date.setSeconds(0);
            this.TimeCalendar.date.setMilliseconds(0);
        }
        let obj = {
            type: 'date',
            year: this.year,
            month: this.month,
            date: this.date,
            calendar: this.TimeCalendar
        }
        this.TimeCalendar.date.setDate(this.date);
        this.TimeCalendar.date.setMonth(this.month);
        this.TimeCalendar.date.setFullYear(this.year);
        this.TimeCalendar.eventHandler(obj);
        this.TimeCalendar.DOMInstance.querySelector('.selected')?.classList.remove('selected');
        this.classList.add('selected');
    }

    generateTimeOptions(TimeCalendar) {
        const hourScroll = this.DOMInstance.querySelector('.hour-scroll');
        const minuteScroll = this.DOMInstance.querySelector('.minute-scroll');
        for (let i = 0; i < 24; i++) {
            let hourCell = document.createElement('div');
            hourCell.classList.add('time-cell');
            hourCell.innerText = i.toString().padStart(2, '0');
            hourCell.type = 'hours';
            hourCell.value = i;
            hourCell.TimeCalendar = TimeCalendar;
            hourCell.addEventListener('click', TimeCalendar.handleTimeSelect);
            hourScroll.appendChild(hourCell);
        }
        for (let i = 0; i < 60; i++) {
            let minuteCell = document.createElement('div');
            minuteCell.classList.add('time-cell');
            minuteCell.innerText = i.toString().padStart(2, '0');
            minuteCell.type = 'minutes';
            minuteCell.value = i;
            minuteCell.TimeCalendar = TimeCalendar;
            minuteCell.addEventListener('click', TimeCalendar.handleTimeSelect);
            minuteScroll.appendChild(minuteCell);
        }

        this.addScrollEvents(hourScroll, 24);
        this.addScrollEvents(minuteScroll, 60);
    }

    handleTimeSelect() {
        if (this.TimeCalendar.date === undefined) {
            this.TimeCalendar.date = new Date();
            this.TimeCalendar.date.setSeconds(0);
            this.TimeCalendar.date.setMilliseconds(0);
            if (this.type === 'hours')
                this.TimeCalendar.date.setMinutes(0);
        }
        let obj = {
            type: this.type,
            value: this.value,
            calendar: this.TimeCalendar
        }
        if (this.type === 'hours') {
            this.TimeCalendar.DOMInstance.querySelector('.hour-scroll').querySelector('.selected')?.classList.remove('selected');
            this.TimeCalendar.date.setHours(this.value);
        }
        if (this.type === 'minutes') {
            this.TimeCalendar.DOMInstance.querySelector('.minute-scroll').querySelector('.selected')?.classList.remove('selected');
            this.TimeCalendar.date.setMinutes(this.value);
        }
        this.TimeCalendar.eventHandler(obj);
        this.classList.add('selected');
    }

    addScrollEvents(element) {
        element.addEventListener('wheel', (e) => {
            if (e.deltaY > 0) {
                let firstChild = element.firstElementChild;
                element.removeChild(firstChild);
                element.appendChild(firstChild);
            } else {
                let lastChild = element.lastElementChild;
                element.removeChild(lastChild);
                element.prepend(lastChild);
            }
        });
    }
}