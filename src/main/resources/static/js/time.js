var _timeIsAbsolute = false;

function convertSecondsToHHMMSS(time) {
    if (!_timeIsAbsolute) {
        const date = moment.utc(time);
        const days = (date.format('x') / 86400000)>>0;
        return '+' + days + "d:" + date.hours() + "h:" + date.minutes() + "m:" + date.seconds() + "s";
    } else {
        time += configRepo.get(currentConfig).scenarioStartTime;
        return `${moment(time).format('L')} ${moment(time).format('LTS')}`;
    }
}

function toggleAbsolute() {
    _timeIsAbsolute = true;
    table.refreshTableData();
}

function toggleRelative() {
    _timeIsAbsolute = false;
    table.refreshTableData();
}